package sig;

import java.awt.Graphics;
import java.awt.Color;

public class Block{
    BlockState state;
    int x,y; //Relative to its block clump
    final static BlockState[] STARTINGSTATES = {BlockState.BLUE,
        BlockState.GREEN,
        BlockState.ORANGE,
        BlockState.PURPLE,
        BlockState.RED,
        BlockState.WHITE,
        BlockState.YELLOW,};
    public Block(int x,int y) {
        this.x=x;
        this.y=y;
        state = STARTINGSTATES[(int)(Meteo.r.nextInt(1))];
    }
    @Override
    public String toString() {
        return "Block [state=" + state + ", x=" + x + ", y=" + y + "]";
    }
    public void draw(Graphics g, double x, double y, int block_width, int block_height,int launched) {
        if (Meteo.DEBUG_DRAWING==DebugMode.MODE0&&launched<=-1) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(state.getCol());
        }
        g.fill3DRect((int)x+this.x*block_width,(int)y-this.y*block_height, block_width, block_height, true);
    }
}