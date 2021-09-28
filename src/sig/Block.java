package sig;

import java.awt.Graphics;
import java.awt.Color;

public class Block{
    BlockState state;
    int x,y; //Relative to its block clump
    int draw_x,draw_y;
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
        state = STARTINGSTATES[(int)(Meteo.r.nextInt(3))];
    }
    @Override
    public String toString() {
        return "Block [state=" + state + ", x=" + x + ", y=" + y + "]";
    }
    public void draw(Graphics g, double x, double y, int block_width, int block_height, int launched, boolean selected) {
        if (Meteo.DEBUG_DRAWING==DebugMode.MODE0&&launched<=-1) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(state.getCol());
        }

        draw_x=(int)x+this.x*block_width;
        draw_y=(int)y-this.y*block_height;

        g.fill3DRect(draw_x, draw_y, block_width, block_height, true);
        
        if (selected) {
            g.setColor(Color.RED);
            for (int i=0;i<2;i++) {
                g.drawRect(draw_x+i-1,draw_y+i-1,block_width-1,block_height-1);
            }
        }
    }
}