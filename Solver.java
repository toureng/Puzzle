import java.util.*;

/**
 * Реализация интерфейса <tt>ConundrumSolver</tt>.
 * Задача решается следующим образом:
 * 1. Для текущего состояния расчитываются возможные перестановки и добовляются в очередь.
 * 2. Если текущее состояние целевое, то расчитывается путь от конечного состояния в начальное, разворачивается
 * и конвертируется в последовательность цифр, представляющих ответ на задачу.
 * 3. Если текущее состояние не целевое, то для каждой перестановки, которая не была проработана ранее, повторяются
 * пп. 1-3
 *
 * @author Дмитрий Панин
 */
public class Solver implements ConundrumSolver {

    /**
     * Массив степеней числа десять.
     */
    private static final int[] POWS = {1, 10, 100, 1000, 10_000, 100_000, 1_000_000, 10_000_000};

    /**
     * Идентификатор целевого состояния.
     */
    private static final int FINAL_STATE_ID = 12340567;

    /**
     * Карта, в которой хранятся уже проработанные состояния.
     */
    private HashMap<Integer, Vertex> graph;

    /**
     * Очередь в которую добавлябтся состояния для проработки.
     */
    private LinkedList<Vertex> queue;

    /**
     * Основной метод.
     *
     * @param initialState начальное состояние
     * @return результат решения задачи
     */
    @Override
    public int[] resolve(int[] initialState) {
        Vertex initialVertex = init(initialState);
        ArrayList<Vertex> path = buildPath(initialVertex);
        return convertPathToActionChain(path);
    }

    /**
     * Метод строит путь от начального состояния к конечному. В цикле из очереди извлекается состояние (начиная
     * с начального), для него просчитываются возможные перестановки и те, которые еще не содержаться в карте graph
     * добавлябтся в graph и в queue. Цикл заканчивается когда найдено конечное состояние. Далее строится список
     * состояний от конечного к начальному и разворачивается.
     *
     * @param initialVertex начальное состояние
     * @return список состояний от начального к конечному
     */
    private ArrayList<Vertex> buildPath(Vertex initialVertex) {
        queue.offer(initialVertex);
        Vertex currentVertex;
        do {
            currentVertex = queue.poll();
            int[][] edges = getPossiblePermutations(currentVertex.getState());
            for (int[] edge : edges) {
                int id = getStateId(edge);
                Vertex anotherVertex;
                if (!graph.containsKey(id)) {
                    anotherVertex = new Vertex(edge, id, currentVertex);
                    graph.put(id, anotherVertex);
                    queue.offer(anotherVertex);
                }
            }

        } while (currentVertex.getId() != FINAL_STATE_ID);

        ArrayList<Vertex> res = new ArrayList<>(32);
        for (Vertex vertex = graph.get(FINAL_STATE_ID); vertex != null; vertex = vertex.getParent()) {
            res.add(vertex);
        }
        Collections.reverse(res);
        return res;
    }

    /**
     * Метод конвертирует последовательность состояний в последовательность шагов (чисел),
     * представляющих решение задачи.
     *
     * @param path список состояний
     * @return массив чисел
     */
    private int[] convertPathToActionChain(ArrayList<Vertex> path) {
        int[] res = new int[path.size() - 1];
        for (int i = 0; i < path.size() - 1; i++) {
            res[i] = getAction(path.get(i), path.get(i + 1));
        }
        return res;
    }

    /**
     * Метод примает два состояния и возвращает число, на ячейку с которым нужно переместить пустую ячейку, чтобы
     * получить из одного состояния другое.
     *
     * @param from исходное состояние
     * @param to   состояние после перестановки
     * @return число, характеризующее ячейку
     */
    private int getAction(Vertex from, Vertex to) {
        return from.getState()[getZeroPosition(to.getState())];
    }

    /**
     * Метод подготавливает класс к работе.
     *
     * @param initialState массив, описывающий начальное состояние
     * @return екземпляр класса Vertex, описывающий начальное состояние
     */
    private Vertex init(int[] initialState) {
        graph = new HashMap<>(40320);
        queue = new LinkedList<>();
        int initialVertexId = getStateId(initialState);
        Vertex initialVertex = new Vertex(initialState, initialVertexId, null);
        graph.put(initialVertexId, initialVertex);
        return initialVertex;

    }

    /**
     * Метод расчитывает возможные перестановки.
     *
     * @param state массив, описывающий состояние
     * @return массив массивов с возможными перестановками
     */
    private int[][] getPossiblePermutations(int[] state) {
        int zeroPosition = getZeroPosition(state);
        int[][] res = null;
        switch (zeroPosition) {
            case 0:
                res = new int[2][8];
                res[0] = swap(state, 0, 1);
                res[1] = swap(state, 0, 2);
                break;
            case 1:
                res = new int[3][8];
                res[0] = swap(state, 1, 0);
                res[1] = swap(state, 1, 2);
                res[2] = swap(state, 1, 3);
                break;
            case 2:
                res = new int[3][8];
                res[0] = swap(state, 2, 0);
                res[1] = swap(state, 2, 1);
                res[2] = swap(state, 2, 5);
                break;
            case 3:
                res = new int[3][8];
                res[0] = swap(state, 3, 1);
                res[1] = swap(state, 3, 4);
                res[2] = swap(state, 3, 6);
                break;
            case 4:
                res = new int[2][8];
                res[0] = swap(state, 4, 3);
                res[1] = swap(state, 4, 5);
                break;
            case 5:
                res = new int[3][8];
                res[0] = swap(state, 5, 2);
                res[1] = swap(state, 5, 4);
                res[2] = swap(state, 5, 7);
                break;
            case 6:
                res = new int[2][8];
                res[0] = swap(state, 6, 3);
                res[1] = swap(state, 6, 7);
                break;
            case 7:
                res = new int[2][8];
                res[0] = swap(state, 7, 5);
                res[1] = swap(state, 7, 6);
                break;
        }

        return res;
    }

    /**
     * Метод делает копию входящего массива и меняет в копии елементы местами.
     *
     * @param source исходный массив
     * @param i      индекс 1
     * @param j      индекс 2
     * @return новый массив с поменянными элементами
     */
    private int[] swap(int[] source, int i, int j) {
        int[] res = Arrays.copyOf(source, source.length);
        int tmp = res[i];
        res[i] = res[j];
        res[j] = tmp;
        return res;
    }

    /**
     * Метод находит индекс нулевого элемента
     *
     * @param state входящий массив
     * @return индекс нулевого элемента
     */
    private int getZeroPosition(int[] state) {
        int res = -1;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0)
                res = i;
        }
        return res;
    }

    /**
     * Метод расчитывает идентификатор состояния. Идентификатор используется в карте graph в качестве ключа.
     *
     * @param state массив состояния
     * @return идентификатор состояния
     */
    private static int getStateId(int[] state) {
        int res = 0;
        for (int i = 0; i < state.length; i++) {
            res += state[i] * POWS[POWS.length - 1 - i];
        }
        return res;
    }

    /**
     * Вспомогательный класс описывающий состояние. В целях увеличения скорости работы методы equals() и hashCode()
     * максимально упрощены.
     */
    private static class Vertex {

        /**
         * Массив, описывающий состояние.
         */
        private int[] state;

        /**
         * Идентификатор состояния.
         */
        private int id;

        /**
         * Ссылка на родительское состояние. Нужна для восстановления пути перестановок, который необходимо пройти,
         * чтобы получить из конечного состояния начальное.
         */
        private Vertex parent;

        public Vertex(int[] state, int id, Vertex parent) {
            this.state = state;
            this.id = id;
            this.parent = parent;
        }

        public int[] getState() {
            return state;
        }

        public int getId() {
            return id;
        }

        public Vertex getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            Vertex vertex = (Vertex) o;
            return id == vertex.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

}