package sig;

import javax.swing.JPanel;
import java.awt.Graphics;

public class Panel extends JPanel{

    Panel() {
        this.setSize(Meteo.SCREEN_WIDTH,Meteo.SCREEN_HEIGHT);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Meteo.b.drawBoard(g);
    }
}
