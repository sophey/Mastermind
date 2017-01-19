import javax.swing.*;
import java.awt.*;

public class BallView extends JComponent {

    private Color color;

    public BallView(String c) {
        color = Color.decode(c);
    }

    public void paintComponent(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        int min = Math.min(width, height);
        int diameter = min * 2 / 3;
        g.setColor(color);
        g.fillOval(min / 6, min / 6, diameter, diameter);
    }

}
