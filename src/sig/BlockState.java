package sig;

import java.awt.Color;

public enum BlockState {
    RED(Color.RED),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    PURPLE(Color.MAGENTA),
    WHITE(Color.WHITE),
    IGNITED(Color.BLACK);

    Color col;

    BlockState(Color col) {
        this.col=col;
    }
   

    public Color getCol() {
        return col;
    }

    public void setCol(Color col) {
        this.col = col;
    }
}
