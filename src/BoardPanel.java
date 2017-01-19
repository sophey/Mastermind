import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

public class BoardPanel extends Box {

    private Game game;

    public BoardPanel(Game g) {
        super(BoxLayout.Y_AXIS);
        game = g;
        addSequences();
    }

    public void addSequences() {
        this.removeAll();
        LinkedList<int[]> seq = game.getSequenceLL();
        Iterator<int[]> it = game.getColorCorrectLL().iterator();
        for (int[] s : seq) {
            add(new SequenceView(s, it.next()));
        }
        repaint();
        validate();
    }


}
