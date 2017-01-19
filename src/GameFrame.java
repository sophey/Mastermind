import javax.swing.*;

public class GameFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mastermind");
        // set size
        frame.setSize(690, 690);

        Game g = new Game(4, 6);

        frame.add(new GameView(g));

        // exit normally on closing the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // show frame
        frame.setVisible(true);
    }

}
