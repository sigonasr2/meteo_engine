package sig;

import java.awt.Graphics;
import java.util.List;

public class BlockClump {
    List<Block> blocks;
    double x,y; //the lower-left origin of this block clump. Every block positions relative to this.
    double yspd;
    public BlockClump(List<Block> blockList, double x, double y, double startspd) {
        this.blocks = blockList;
        this.x=x;
        this.y=y;
        this.yspd=startspd;
    }
    @Override
    public String toString() {
        return "BlockClump [blocks=" + blocks + ", x=" + x + ", y=" + y + ", yspd=" + yspd + "]";
    }
    public void drawBlocks(Graphics g, int originX, int originY, int block_width, int block_height) {
        for (Block b : blocks) {
            b.draw(g,originX-x*block_width,originY-y*block_height,block_width,block_height);
        }
    }
}
