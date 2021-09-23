package sig;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;

public class BlockClump {
    private List<Block> blocks;
    double x,y; //the lower-left origin of this block clump. Every block positions relative to this.
    double yspd;
    int[][] collisionColumnRanges;
    int launched = 120; /*
    	Negative is for when block clumps are divided into smaller columns for re-sorting.
     	Positive is used for how much landing launch time before being split and falling.*/
 
    public BlockClump(List<Block> blockList, double x, double y, double startspd, int width, int launched) {
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
        this.launched=launched;
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
            b.draw(g,originX+x*block_width,originY-y,block_width,block_height,launched);
        }
    }
    public void drawClumpOutlines(Graphics g, int originX, int originY, int block_width, int block_height) {
        if (Meteo.DEBUG_DRAWING==DebugMode.MODE0) {
            g.setColor(new Color(0,255,0,128));
            for (int i=0;i<collisionColumnRanges.length;i++) {
                if (collisionColumnRanges[i][0]!=-1) {
                    g.drawRect((int)(x+i*block_width)+originX,(int)(originY-y-(block_height*(collisionColumnRanges[i][1]-collisionColumnRanges[i][0]))),block_width,block_height*(collisionColumnRanges[i][1]-collisionColumnRanges[i][0]+1));
                    g.drawRect((int)(x+i*block_width)+originX+1,(int)(originY+1-y-(block_height*(collisionColumnRanges[i][1]-collisionColumnRanges[i][0]))),block_width,block_height*(collisionColumnRanges[i][1]-collisionColumnRanges[i][0]+1));
                }
            }
        }
    }
    public void drawClumpDots(Graphics g, int originX, int originY, int block_width, int block_height) {
        if (Meteo.DEBUG_DRAWING==DebugMode.MODE0) {
            g.setColor(Color.RED);
            g.drawOval((int)x+originX,(int)-y+originY,2,2);
        }
    }

    public List<Block> getBlocks() {
		return blocks;
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
    public static void drawDebugBlockClumps(Graphics g, int originX, int originY, int block_width, int block_height, List<BlockClump> blockData) {
        for (BlockClump bc : blockData) {
            bc.drawClumpOutlines(g,originX,originY,block_width,block_height);
        }
        for (BlockClump bc : blockData) {
            bc.drawClumpDots(g,originX,originY,block_width,block_height);
        }
    }
}
