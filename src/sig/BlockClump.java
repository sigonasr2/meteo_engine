package sig;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Color;

public class BlockClump {
    private List<Block> blocks = new ArrayList<Block>();
    double x,y; //the lower-left origin of this block clump. Every block positions relative to this.
    double yspd;
    int[][] collisionColumnRanges;
    int maxBlockHeight=0; //Gives the height in blocks. So if the top is 0, this should be 1.
    int launched = 120; /*
    	Negative is for when block clumps are divided into smaller columns for re-sorting. 0=Ready for split. -1=Ready for merging. -2=Merged (Dead clump)
     	Positive is used for how much landing launch time before being split and falling.*/
 
    public BlockClump(List<Block> blockList, double x, double y, double startspd, int width, int launched) {
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
        maxBlockHeight=0;

        for (int i=0;i<collisionColumnRanges.length;i++) {
            collisionColumnRanges[i] = new int[]{-1,-1};
        }
        for (Block b : blocks) {updateBlockCollisionRangeWithBlock(b);}
    }
    public void addBlock(Block...blocks) {
        //Adds the block to the strucutre. Update collision column ranges to reflect the new bounds.
        for (Block b : blocks) 
        {
            this.blocks.add(b);
            updateBlockCollisionRangeWithBlock(b);
        }
    }
    public void removeBlock(Block...blocks) {
        for (Block b : blocks) {
            this.blocks.remove(b);
        }
        updateBlockCollision();
    }
    public void drawBlocks(Graphics g, int originX, int originY, int block_width, int block_height) {
        for (Block b : blocks) {
            b.draw(g,originX+x,originY-y,block_width,block_height,launched);
            if (Meteo.DEBUG_DRAWING==DebugMode.MODE2) {
                g.setColor(Color.BLACK);
                g.drawString(Integer.toString(maxBlockHeight),(int)x+b.x*block_width+originX+4,(int)-y-b.y*block_height+originY+16);
            }
        }
    }
    public void drawClumpOutlines(Graphics g, int originX, int originY, int block_width, int block_height) {
        if (Meteo.DEBUG_DRAWING!=DebugMode.OFF) {
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
        if (Meteo.DEBUG_DRAWING!=DebugMode.OFF) {
            g.setColor(Color.RED);
            g.drawOval((int)x+originX,(int)-y+originY,2,2);
        }
    }

    public List<Block> getBlocks() {
		return blocks;
	}
    public List<Block> getSortedBlocksOnRow(int row) {
        return getBlocks().stream().filter((block)->block.y==row).sorted((b1,b2)->b1.x-b2.x).collect(Collectors.toList());
    }
    public List<Block> getSortedBlocksOnCol(int col) {
        return getBlocks().stream().filter((block)->block.x==col).sorted((b1,b2)->b1.y-b2.y).collect(Collectors.toList());
    }
	private void updateBlockCollisionRangeWithBlock(Block b) {
        if (collisionColumnRanges[b.x][0]==-1||collisionColumnRanges[b.x][0]>b.y) {
            collisionColumnRanges[b.x][0]=b.y;
        }
        if (collisionColumnRanges[b.x][1]==-1||collisionColumnRanges[b.x][1]<b.y) {
            collisionColumnRanges[b.x][1]=b.y;
        }
        maxBlockHeight=Math.max(maxBlockHeight,b.y+1);
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
