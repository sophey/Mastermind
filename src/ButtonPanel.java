import com.sun.org.apache.bcel.internal.generic.POP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ButtonPanel extends JPanel {

    private JButton submit;
    private JButton clear;
    private Game game;
    private int index;
    int[] sequence;
    public BoardPanel board;
    public JPanel guessSouth;
    private JPanel center;

    public ButtonPanel(Game g) {
        super(new GridLayout(2, 1));
        center = new JPanel(new GridLayout(1, g.getNumColors() + 1));
        game = g;
        initButtons();
        sequence = new int[game.getSeqLength()];
        index = 0;
        board = new BoardPanel(game);
        guessSouth = new JPanel(new GridLayout(1, game.getSeqLength()));
        add(center);
        add(guessSouth);
    }

    public void initButtons() {
        for (int i = 0; i < game.getNumColors(); i++) {
            JButton button = new ColorButton(Color.decode(SequenceView.indexcolors[i]));
            button.setName(i + "");
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (index < sequence.length) {
                        sequence[index++] = Integer.parseInt(button.getName());
                        guessSouth.add(new BallView(SequenceView.indexcolors[Integer.parseInt(button
                                .getName())]));
                    }
                    validate();
                    repaint();
                }
            });
            center.add(button);
        }
        submit = new JButton("SUBMIT");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (index == sequence.length) {
                    if (game.guess(sequence)) {
                        board.addSequences();
                        validate();
                        repaint();
                        JOptionPane.showMessageDialog(null, "You've won!");
                        System.exit(0);
                    } else {
                        board.addSequences();
                        validate();
                        repaint();
                    }
                }
                guessSouth.removeAll();
                System.out.println(Arrays.toString(sequence));
                sequence = new int[sequence.length];
                index = 0;

            }
        });
        clear = new JButton("CLEAR");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sequence = new int[sequence.length];
                index = 0;
                guessSouth.removeAll();
                validate();
                repaint();
            }
        });
        JButton ai = new JButton("Genetic");
        ai.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.guess();
                board.addSequences();
                repaint();
                validate();
            }
        });
        JPanel sc = new JPanel(new GridLayout(3, 1));
        sc.add(submit);
        sc.add(clear);
        sc.add(ai);
        center.add(sc);
    }


}
