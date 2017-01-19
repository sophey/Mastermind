import java.util.Arrays;

public class Board {

    private int[] sequence; // actual sequence
    private int numColors;

    public Board(int seqLength, int numColors) {
        this.numColors = numColors;
        sequence = createRandSequence(seqLength);
        System.out.println("Sequence: " + Arrays.toString(sequence));
    }

    public Board(int[] sequence, int numColors) {
        this.sequence = sequence;
        this.numColors = numColors;
    }

    private int[] createRandSequence(int seqLength) {
        int[] seq = new int[seqLength];
        for (int i = 0; i < seqLength; i++) {
            seq[i] = (int) (Math.random() * numColors);
        }
        return seq;
    }

    public int getNumColors() {
        return numColors;
    }

    public int[] getSequence() {
        return sequence;
    }

    public static int numCorrectColor(int[] guess, int[] cor, int numColors) {
        int correct = 0;
        for (int i = 0; i < numColors; i++) {
            int count = 0;
            for (int j = 0; j < guess.length; j++) {
                if (guess[j] == i)
                    count++;
            }
            int count2 = 0;
            for (int k = 0; k < cor.length; k++) {
                if (cor[k] == i)
                    count2++;
            }
            correct += Math.min(count, count2);
            if (correct == cor.length)
                break;
        }
        return correct;
    }

    public static int numCorrectColorPosition(int[] guess, int[] cor) {
        int count = 0;
        for (int i = 0; i < guess.length; i++) {
            if (guess[i] == cor[i])
                count++;
        }
        return count;
    }

    public int numCorrectColor(int[] guess) {
        return numCorrectColor(guess, sequence, numColors);
    }

    public int numCorrectColorPosition(int[] guess) {
        return numCorrectColorPosition(guess, sequence);
    }

    public boolean hasWon(int[] guess) {
        if (guess.length != sequence.length) {
            System.out.println("Wrong length");
            return false;
        }
//        System.out.println("Number of Correct Colors: " + numCorrectColor(guess));
//        System.out.println("Number of correct colors in correct positions: " + numCorrectColorPosition(guess));
        return numCorrectColorPosition(guess) == sequence.length;
    }

}
