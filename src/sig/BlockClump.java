package sig;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockClump {
    private List<Block> blocks;
    double x,y; //the lower-left origin of this block clump. Every block positions relative to this.
    double yspd;
    int[][] collisionColumnRanges;
    int launched = -1; /*
    	Negative is for when block clumps are divided into smaller columns for re-sorting.
     	Positive is used for how much landing launch time before being split and falling.*/
 
    public BlockClump(List<Block> blockList, double x, double y, double startspd, int width) {
    	this.blocks = new ArrayList<Block>();
    	this.blocks.addAll(blockList);
        collisionColumnRanges = new int[width][];

        for (int i=0;i<width;i++) {
            collisionColumnRanges[i] = new int[]{-1,-1};
        }

        addBlock(blockList.toArray(new Block[blockList.size()]));
        this.x=x;
        this.y=y;
        this.yspd=startspd;
    }
    public void updateBlockCollision() {
        //Call this whenever the block structure changes. This will define what the top and bottom positions
        //of each vertical column are for faster collision checking.
        collisionColumnRanges = new int[collisionColumnRanges.length][];

        for (int i=0;i<collisionColumnRanges.length;i++) {
            collisionColumnRanges[i] = new int[]{-1,-1};
        }
        for (Block b : blocks) {updateBlockCollisionRangeWithBlock(b);}
    }
    public void addBlock(Block...blocks) {
        //Adds the block to the strucutre. Update collision column ranges to reflect the new bounds.
        for (Block b : blocks) {updateBlockCollisionRangeWithBlock(b);}
    }
    public void drawBlocks(Graphics g, int originX, int originY, int block_width, int block_height) {
        for (Block b : blocks) {
            b.draw(g,originX+x*block_width,originY-y,block_width,block_height);
        }
    }

    private void updateBlockCollisionRangeWithBlock(Block b) {
        if (collisionColumnRanges[b.x][0]==-1||collisionColumnRanges[b.x][0]>b.y) {
            collisionColumnRanges[b.x][0]=b.y;
        }
        if (collisionColumnRanges[b.x][1]==-1||collisionColumnRanges[b.x][1]<b.y) {
            collisionColumnRanges[b.x][1]=b.y;
        }
    }
    
    @Override
    public String toString() {
        return "BlockClump [blocks=" + blocks + ", x=" + x + ", y=" + y + ", yspd=" + yspd + "]";
    }
}
