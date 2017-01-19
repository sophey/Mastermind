import java.util.*;

public class Game {

    private LinkedList<int[]> sequenceLL;
    private LinkedList<int[]> colorCorrectLL;
    private int numGuesses;
    private int numColors;
    private int seqLength;
    private Board board;
    public final int MAX_GEN = 10;
    public final int POP_SIZE = 150;
    private Random r = new Random();

    public Game(int seqLength, int numColors) {
        sequenceLL = new LinkedList<>();
        colorCorrectLL = new LinkedList<>();
        numGuesses = 0;
        this.numColors = numColors;
        this.seqLength = seqLength;
        board = new Board(seqLength, numColors);
    }

    public Game(Board b) {
        sequenceLL = new LinkedList<>();
        colorCorrectLL = new LinkedList<>();
        numGuesses = 0;
        numColors = b.getNumColors();
        seqLength = b.getSequence().length;
        board = b;
    }

    public boolean guess(int[] sequence) {
        numGuesses++;
        int corCol = board.numCorrectColor(sequence);
        int corPos = board.numCorrectColorPosition(sequence);
        sequenceLL.add(sequence);
        colorCorrectLL.add(new int[]{corCol, corPos});
//        System.out.println("Number of guesses: " + getNumGuesses());
//        System.out.println("Number of Correct Colors: " + corCol);
//        System.out.println("Number of correct colors in correct positions: " + corPos);
        if (corPos == sequence.length)
            return true;
        return false;
    }

    public int getNumGuesses() {
        return numGuesses;
    }

    public LinkedList<int[]> getSequenceLL() {
        return sequenceLL;
    }

    public LinkedList<int[]> getColorCorrectLL() {
        return colorCorrectLL;
    }

    public int getNumColors() {
        return numColors;
    }

    public int getSeqLength() {
        return seqLength;
    }

    public int guess() {
        LinkedList<List<Integer>> guesses = new LinkedList<>();
        int i = 1;
        // fixed initial guess g
        List<Integer> g = Arrays.asList(1, 1, 2, 3);
        System.out.println("guess: " + g);
        while (!guess(toIntArray(g))) {
            guesses.add(g);
            i++;
            int h = 1;
            ArrayList<List<Integer>> eligible = new ArrayList<>();
            Map<List<Integer>, Integer> population = new HashMap<>();
            // initialize population
            int totalFitness = 0;
            while (population.size() < POP_SIZE) {
                List<Integer> tempList = Arrays.asList(r.nextInt(getNumColors()), r.nextInt(getNumColors()), r
                        .nextInt(getNumColors()), r.nextInt(getNumColors()));
                if (!population.containsKey(tempList)) {
                    int[] tempArr = toIntArray(tempList);
                    int j = 1;
                    Iterator<int[]> it = getColorCorrectLL().iterator();
                    int fitness = 0;
                    for (List<Integer> guess : guesses) {
                        int[] guessArr = toIntArray(guess);
                        int[] corColPos = it.next();
                        fitness += Math.abs(Board.numCorrectColor(tempArr, guessArr, getNumColors()) -
                                corColPos[0])
                                + Math.abs(Board.numCorrectColorPosition(tempArr, guessArr) - corColPos[1]) + guessArr
                                .length * (j - 1);
                        j++;
                    }
                    population.put(tempList, fitness);
                    totalFitness += fitness;
                }
            }
            while (h <= MAX_GEN) {
                // generate new population
                Map<List<Integer>, Integer> newPopulation = new HashMap<>();
                int newTotalFitness = 0;
                while (newPopulation.size() < POP_SIZE) {
                    List<Integer>[] parents = chooseParents(population, totalFitness);
                    List<Integer>[] children;
                    // 50% chance 1-point crossover, 50% 2-point
                    if (r.nextDouble() < .5) {
                        children = onePtCross(parents);
                    } else {
                        children = twoPtCross(parents);
                    }
                    // mutations
                    if (r.nextDouble() <= .03) {
                        children[0] = mutate(children[0]);
                    }
                    if (r.nextDouble() <= .03) {
                        children[1] = mutate(children[1]);
                    }
                    // permutation
                    if (r.nextDouble() <= 0.03) {
                        children[0] = permute(children[0]);
                    }
                    if (r.nextDouble() <= 0.03) {
                        children[1] = permute(children[1]);
                    }
                    if (r.nextDouble() <= 0.02) {
                        children[0] = invert(children[0]);
                    }
                    if (r.nextDouble() <= 0.02) {
                        children[1] = invert(children[1]);
                    }
                    for (int k = 0; k < 2; k++) {
                        int[] tempArr = toIntArray(children[k]);
                        int j = 1;
                        Iterator<int[]> it = getColorCorrectLL().iterator();
                        int fitness = 0;
                        for (List<Integer> guess : guesses) {
                            int[] guessArr = toIntArray(guess);
                            int[] corColPos = it.next();
                            fitness += Math.abs(Board.numCorrectColor(tempArr, guessArr, getNumColors()) -
                                    corColPos[0])
                                    + Math.abs(Board.numCorrectColorPosition(tempArr, guessArr) - corColPos[1]) +
                                    guessArr
                                            .length * (j - 1);
                            j++;
                        }
                        if (!newPopulation.containsKey(children[k])) {
                            newTotalFitness += fitness;
                            newPopulation.put(children[k], fitness);
                        }
                    }
                }
                population = newPopulation;
                totalFitness = newTotalFitness;
                Iterator<List<Integer>> it = population.keySet().iterator();
                while (it.hasNext()) {
                    List<Integer> lst = it.next();
                    if (isEligible(lst, guesses)) {
                        eligible.add(lst);
                    }
                }
                h++;
            }
            if (eligible.size() > 0) {
                int rand = r.nextInt(eligible.size());
                g = eligible.get(rand);
                System.out.println("guess: " + g);
            } else {
                i--;
            }
        }
        return i;
    }

    private List<Integer>[] chooseParents(Map<List<Integer>, Integer> population, int totalFitness) {
        List<Integer>[] ret = new List[2];
        for (int i = 0; i < 2; i++) {
            int rand = r.nextInt(totalFitness);
            int fit = 0;
            List<Integer> lst = null;
            Iterator<List<Integer>> it = population.keySet().iterator();
            while (fit <= rand) {
                lst = it.next();
                fit += population.get(lst);
            }
            ret[i] = lst;
        }
        return ret;
    }

    private List<Integer>[] onePtCross(List<Integer>[] parents) {
        int crossPt = r.nextInt(parents[0].size() - 1) + 1;
        List<Integer> c1 = new ArrayList<>();
        List<Integer> c2 = new ArrayList<>();
        for (int i = 0; i < parents[0].size(); i++) {
            if (i <= crossPt) {
                c1.add(parents[0].get(i));
                c2.add(parents[1].get(i));
            } else {
                c1.add(parents[1].get(i));
                c2.add(parents[0].get(i));
            }
        }
        return new List[]{c1, c2};
    }

    private List<Integer>[] twoPtCross(List<Integer>[] parents) {
        int crossPt1 = r.nextInt(parents[0].size() - 1) + 1;
        int crossPt2;
        do {
            crossPt2 = r.nextInt(parents[0].size() - 1) + 1;
        } while (crossPt2 != crossPt1);
        int min = Math.min(crossPt1, crossPt2);
        int max = Math.max(crossPt1, crossPt2);
        List<Integer> c1 = new ArrayList<>();
        List<Integer> c2 = new ArrayList<>();
        for (int i = 0; i < parents[0].size(); i++) {
            if (i <= min || i > max) {
                c1.add(parents[0].get(i));
                c2.add(parents[1].get(i));
            } else {
                c1.add(parents[1].get(i));
                c2.add(parents[0].get(i));
            }
        }
        return new List[]{c1, c2};
    }

    private List<Integer> mutate(List<Integer> child) {
        int mutatePt = r.nextInt(child.size());
        int randCol = r.nextInt(getNumColors());
        child.set(mutatePt, randCol);
        return child;
    }

    private List<Integer> permute(List<Integer> child) {
        int perm1 = r.nextInt(child.size());
        int perm2 = r.nextInt(child.size());
        int temp = child.get(perm2);
        child.set(perm2, child.get(perm1));
        child.set(perm1, temp);
        return child;
    }

    private List<Integer> invert(List<Integer> child) {
        int inv1 = r.nextInt(child.size());
        int inv2 = r.nextInt(child.size());
        int min = Math.min(inv1, inv2);
        int max = Math.max(inv1, inv2);
        int j = 0;
        for (int i = min; i < (max - min) / 2 + min; i++) {
            int temp = child.get(max - j);
            child.set(max - j, child.get(i));
            child.set(i, temp);
            j++;
        }
        return child;
    }

    private boolean isEligible(List<Integer> lst, LinkedList<List<Integer>> guesses) {
        int[] arr = toIntArray(lst);
        int[] guessArr = toIntArray(guesses.getFirst());
        int x = Math.abs(Board.numCorrectColor(arr, guessArr, getNumColors()) - getColorCorrectLL()
                .getFirst()[0]);
        int y = Math.abs(Board.numCorrectColorPosition(arr, guessArr) - getColorCorrectLL().getFirst()[1]);
        Iterator<int[]> it = getColorCorrectLL().iterator();
        for (List<Integer> guess : guesses) {
            guessArr = toIntArray(guess);
            int[] g = it.next();
            if (x != Math.abs(Board.numCorrectColor(arr, guessArr, getNumColors()) - g[0]) || y != Math.abs
                    (Board.numCorrectColorPosition(arr, guessArr)) - g[1])
                return false;
        }
        return true;
    }


    private int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);
        return ret;
    }


    public static void initGame() {
        Scanner s = new Scanner(System.in);
        System.out.print("How long should the sequence be? ");
        int length = s.nextInt();
        System.out.print("How many colors? ");
        int colors = s.nextInt();
        long start = System.nanoTime();
        System.out.print("How many iterations? ");
        int it = s.nextInt();
        double guessCount = 0.;
        for (int i = 0; i < it; i++) {
            Board b = new Board(4, 6);
            Game g = new Game(b);
            guessCount += g.guess();
        }
        System.out.println("Average number of guesses: " + guessCount / it);
        long end = System.nanoTime();
        System.out.println("Time: " + ((end - start) / 1000000000.));
    }

    public static void main(String[] args) {
        Game.initGame();
    }

}