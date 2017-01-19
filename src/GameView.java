import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {

    private Game game;

    public GameView(Game g) {
        super(new BorderLayout());
        game = g;
        ButtonPanel bp = new ButtonPanel(game);
        add(bp.board, BorderLayout.CENTER);
        add(bp, BorderLayout.SOUTH);
    }

}
