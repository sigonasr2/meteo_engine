package sig;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    List<BlockClump> blockData;
    int width;
    int height;
    double gravity;
    double launch_power;
    double max_rise_spd;
    double max_fall_spd;
    double[] combo_power_bonus;
    int x,y;
    int block_width,block_height;
    double vspeed;
	
	List<BlockClump> blockClumpDeleteList = new ArrayList<BlockClump>();
	List<BlockClump> blockClumpAddList = new ArrayList<BlockClump>();
	
    public Board(int centerX,int centerY,int block_width,int block_height,int boardWidth, int boardHeight, double gravity, double launch_power, double max_rise_spd, double max_fall_spd,
            double[] combo_power_bonus) {
        this.x=centerX;
        this.y=centerY;
        this.block_width=block_width;
        this.block_height=block_height;
        this.width = boardWidth;
        this.height = boardHeight;
        this.gravity = gravity;
        this.launch_power = launch_power;
        this.max_rise_spd = max_rise_spd;
        this.max_fall_spd = max_fall_spd;
        this.combo_power_bonus = combo_power_bonus;
        this.blockData = new ArrayList<BlockClump>();

        List<Block> initialBlocks = new ArrayList<Block>();
        for (int x=0;x<boardWidth;x++) {
            for (int y=0;y<(int)(Math.random()*12);y++) {
            	initialBlocks.add(new Block(x,y));
            }
        }

        BlockClump defaultClump = new BlockClump(initialBlocks,0,260,0,width,120);
        
        List<Block> initialBlocks2 = new ArrayList<Block>();
        for (int x=0;x<boardWidth;x++) {
            for (int y=0;y<(int)(Math.random()*12);y++) {
            	initialBlocks2.add(new Block(x,y));
            }
        }
        BlockClump defaultClump2 = new BlockClump(initialBlocks2,0,540,0,width,120);

        blockData.add(defaultClump);
        blockData.add(defaultClump2);
    }
    public void run(long frames) {
    	if (frames%100==0) {
    		blockData.add(new BlockClump(Arrays.asList(new Block((int)(Math.random()*width),0)),0,590,0,width,-1));
    	}

        outerloop:
        for (BlockClump blocks : blockData) {
            if (checkForMatches(blocks)) {continue;}
        	double FUTURE_FALL_POSITION = blocks.y+blocks.yspd+gravity;
        	for (int x=0;x<width;x++) {
        		if (blocks.collisionColumnRanges[x][0]!=-1) {
        			for (BlockClump blocks2 : blockData) {
        				if (!blocks.equals(blocks2)&&blocks2.collisionColumnRanges[x][1]!=-1) {
        					if (FUTURE_FALL_POSITION<blocks2.y) {
	        					if (FUTURE_FALL_POSITION+blocks.collisionColumnRanges[x][1]*block_height>blocks2.y+(blocks2.collisionColumnRanges[x][0]+1)*block_height) {
	        						HandleBlockLand(blocks, x, blocks2.y+(blocks2.collisionColumnRanges[x][0]+1)*block_height);
	        						continue outerloop;
	        					}
        					} else {
        						if (FUTURE_FALL_POSITION+blocks.collisionColumnRanges[x][0]*block_height<blocks2.y+(blocks2.collisionColumnRanges[x][1]+1)*block_height) {
	        						HandleBlockLand(blocks, x, blocks2.y+(blocks2.collisionColumnRanges[x][1]+1)*block_height);
                                    if (blocks.launched==-1) {
                                        CombineAToB(blocks,blocks2);
                                        blocks.launched=-2;
                                    }
	        						continue outerloop;
	        					}
        					}
        				}
        			}
        		}
        	}
            if (FUTURE_FALL_POSITION>0) {
                blocks.yspd=Math.max(blocks.yspd+gravity,max_fall_spd);
                blocks.y+=blocks.yspd;
            } else {
                //We have hit the bottom.
                HandleBlockLand(blocks, x, 0);
            }
	        //System.out.println(blocks.y);
        }
    	if (blockClumpDeleteList.size()>0) {
    		blockData.removeAll(blockClumpDeleteList);
    		blockClumpDeleteList.clear();
    	}
    	if (blockClumpAddList.size()>0) {
    		blockData.addAll(blockClumpAddList);
    		blockClumpAddList.clear();
    	}
    }
    private boolean checkForMatches(BlockClump blocks) {
        //Start from one block and work our way across, seeing if we can make a match of 3 or more. Go to the next row, repeat. Then do the columns. Once all blocks marked for ignition, ignite them and send them.
        //Lowest block is used as the block clump starting point.
        for (int y=0;y<blocks.maxBlockHeight;y++) {
            System.out.println(blocks.getSortedBlocksOnRow(y));
        }
        for (int x=0;x<width;x++) {
            System.out.println(blocks.getSortedBlocksOnCol(x));
        }
        return false;
    }
	private void CombineAToB(BlockClump A, BlockClump B) {
        for (Block b : A.getBlocks()) { 
            b.y = B.collisionColumnRanges[b.x][1]+1;
            B.addBlock(b);
        }
        blockClumpDeleteList.add(A);
    }
    private void HandleBlockLand(BlockClump blocks, int x, double yset) {
		blocks.yspd=0;
		blocks.y=yset;
		if (blocks.launched>0) {
            blocks.launched--;
		} else 
		if (blocks.launched==0) {
            SplitBlockClump(blocks);
        }
	}
    private void SplitBlockClump(BlockClump blocks) {
		for (int x=0;x<width;x++) {
			if (blocks.collisionColumnRanges[x][0]!=-1) {
				final int column=x;
				blockClumpAddList.add(
						new BlockClump(
							blocks.getBlocks().stream().filter((block)->block.x==column).collect(Collectors.toList()),
							0,blocks.y,blocks.yspd,width,blocks.launched-1)
						);
			}
		}
		blockClumpDeleteList.add(blocks);
	}
	public void drawBoard(Graphics g) {
        final int DRAW_STARTX = (int)(x - block_width*((double)width/2));
        final int DRAW_STARTY = (int)(y + block_height*((double)height/2));
        final int DRAW_ENDX = (int)(x + block_width*((double)width/2));

        for (BlockClump bc : blockData) {
            bc.drawBlocks(g,DRAW_STARTX,DRAW_STARTY,block_width,block_height);
        }
        g.setColor(Color.BLACK);
        g.fillRoundRect(DRAW_STARTX, DRAW_STARTY+block_height, DRAW_ENDX-DRAW_STARTX, 3, 3, 1);
        BlockClump.drawDebugBlockClumps(g,DRAW_STARTX,DRAW_STARTY,block_width,block_height,blockData);
    }
}
