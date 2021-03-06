package sig;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Rectangle;
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
    int attack_counter=0;

    BlockClump clumpClickId;
    Block clickBlock;

    final static BlockState[] STARTINGSTATES = {BlockState.BLUE,
        BlockState.GREEN,
        BlockState.ORANGE,
        BlockState.PURPLE,
        BlockState.RED,
        BlockState.WHITE,
        BlockState.YELLOW,};
	
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
    }
    public void run(long frames) {
    	if (frames%20==0) {
    		blockData.add(new BlockClump(Arrays.asList(new Block((int)(Meteo.r.nextInt(width)),0)),0,590,0,width,-1));
    	}

        outerloop:
        for (int i=0;i<blockData.size();i++) {
            BlockClump blocks = blockData.get(i);
        	DestroyOutsideBlocks(blocks);
            if (checkForMatches(blocks)) {continue;}
        	double FUTURE_FALL_POSITION = blocks.y+blocks.yspd+gravity;
            List<CollisionRangeIdentifier> ranges = new ArrayList<CollisionRangeIdentifier>();
            for (int k=0;k<blocks.collisionColumnRanges.length;k++) {
                int[] range = blocks.collisionColumnRanges[k];
                ranges.add(new CollisionRangeIdentifier(k, range));
            }
            ranges = ranges.stream().sorted((a,b)->a.range[0]-b.range[0]).collect(Collectors.toList());
            //System.out.println(ranges);
        	for (int l=0;l<ranges.size();l++) {
                int x=ranges.get(l).index;
        		if (blocks.collisionColumnRanges[x][0]!=-1) {
        			for (int j=0;j<blockData.size();j++) {
                        BlockClump blocks2 = blockData.get(j);
        				if (!blocks.equals(blocks2)&&blocks2.collisionColumnRanges[x][1]!=-1) {
                            if (blocks.yspd>0) {
                                if (blocks2.y+(blocks2.collisionColumnRanges[x][0])*block_height>FUTURE_FALL_POSITION+(blocks.collisionColumnRanges[x][1])*block_height) {
                                    if (FUTURE_FALL_POSITION+(blocks.collisionColumnRanges[x][1])*block_height>blocks2.y+(blocks2.collisionColumnRanges[x][0])*block_height) {
                                        CombineAToBFromBelow(blocks,blocks2);
                                        HandleBlockLand(blocks, x, blocks2.y+(blocks2.collisionColumnRanges[x][0])*block_height-blocks.maxBlockHeight*block_height);
                                        continue outerloop;
                                    }
                                }
                            } else {
                                if (FUTURE_FALL_POSITION+(blocks.collisionColumnRanges[x][0])*block_height<=0) { //Handle reaching the ground.
                                    HandleBlockLand(blocks, x, 0);
                                    continue outerloop;
                                } else
                                if (blocks2.y<FUTURE_FALL_POSITION+(blocks.collisionColumnRanges[x][0])*block_height) {
                                    if (FUTURE_FALL_POSITION+(blocks.collisionColumnRanges[x][0])*block_height<blocks2.y+(blocks2.collisionColumnRanges[x][1]+1)*block_height) {
                                        HandleBlockLand(blocks, x, blocks2.y+(blocks2.collisionColumnRanges[x][1]+1)*block_height);
                                        System.out.println(ranges);
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
        MergeAllGroundedClumps();
    	if (blockClumpDeleteList.size()>0) {
    		blockData.removeAll(blockClumpDeleteList);
    		blockClumpDeleteList.clear();
    	}
    	if (blockClumpAddList.size()>0) {
    		blockData.addAll(blockClumpAddList);
    		blockClumpAddList.clear();
    	}
    }
    private void DestroyOutsideBlocks(BlockClump blocks) {
    	if (blocks.yspd>0) {
			for (int[] range : blocks.collisionColumnRanges) {
				if (range[1]*block_height+blocks.y>block_height*height) {
					List<Block> removedBlocks = blocks.getBlocks().stream().filter((block)->block.y*block_height+blocks.y>block_height*height).collect(Collectors.toList());
                    RemoveBlocks(blocks,removedBlocks.toArray(new Block[removedBlocks.size()]));
				}
			}
    	}
	}
    private void RemoveBlocks(BlockClump bc,Block...blocks) {
        bc.removeBlock(blocks);
        if (bc.getBlocks().size()==0) {
            blockClumpDeleteList.add(bc);
        }
    }
	private void MergeAllGroundedClumps() {
        List<BlockClump> groundedClumps = blockData.stream().filter((cl)->cl.y==0).collect(Collectors.toList());
        if (groundedClumps.size()>1) {
            BlockClump base = groundedClumps.remove(0);
            for (int i=0;i<groundedClumps.size();i++) {
                BlockClump bc = groundedClumps.get(i);
                base.addBlock(bc.getBlocks().toArray(new Block[bc.getBlocks().size()]));
            }
            blockClumpDeleteList.addAll(groundedClumps);
        }
    }
    private boolean checkForMatches(BlockClump blocks) {
        //Start from one block and work our way across, seeing if we can make a match of 3 or more. Go to the next row, repeat. Then do the columns. Once all blocks marked for ignition, ignite them and send them.
        //Lowest block is used as the block clump starting point.
        List<Block> markedBlocks = new ArrayList<Block>();
        for (int y=0;y<blocks.maxBlockHeight;y++) {
            //System.out.println(blocks.getSortedBlocksOnRow(y));
            //System.out.println(blocks.getSortedBlocksOnRow(y));
            List<Block> blockList = blocks.getSortedBlocksOnRow(y);
            //System.out.println(" "+blockList);
            addAllToListUnique(markedBlocks,FindMatches(blockList));
        }
        for (int x=0;x<width;x++) {
            List<Block> blockList = blocks.getSortedBlocksOnCol(x);
            addAllToListUnique(markedBlocks,FindMatches(blockList));
        }
        if (markedBlocks.size()>0) {
            int minY=Integer.MAX_VALUE;
            List<Block> newClumpBlocks = new ArrayList<Block>();
            newClumpBlocks.addAll(markedBlocks);
            for (int i=0;i<markedBlocks.size();i++) {
                Block b = markedBlocks.get(i);
                b.state = BlockState.IGNITED;
                //All blocks above marked blocks now join the clump.
                newClumpBlocks.addAll(blocks.getSortedBlocksOnCol(b.x).stream().filter((block)->!newClumpBlocks.contains(block)&&block.y>b.y).collect(Collectors.toList()));
                minY=Math.min(minY,b.y);
            }
            //For now just get rid of them.
            RemoveBlocks(blocks,newClumpBlocks.toArray(new Block[newClumpBlocks.size()]));
            for (int i=0;i<newClumpBlocks.size();i++) {
                Block b = newClumpBlocks.get(i);
                b.y-=minY;
            }
            blockClumpAddList.add(
                new BlockClump(newClumpBlocks, blocks.x, blocks.y+minY*block_height, launch_power, width, 120)
            );
        }
        return markedBlocks.size()>0;
    }
    private void addAllToListUnique(List<Block> list, List<Block> listToAddFrom) {
		list.addAll(listToAddFrom.stream().filter((block)->!list.contains(block)).collect(Collectors.toList()));
	}
	private List<Block> FindMatches(List<Block> blockList) {
        List<Block> markedBlocks = new ArrayList<Block>();
        List<Block> tempMarkedBlocks = new ArrayList<Block>();
        if (blockList.size()==0) {return markedBlocks;}
        BlockState col = blockList.get(0).state;
        int matches= 1;
        int prevX = blockList.get(0).x;
        int prevY = blockList.get(0).y;
        while (blockList.size()>0) {
            Block currentBlock = blockList.get(0);
            if (Math.abs(currentBlock.x-prevX)==1||Math.abs(currentBlock.y-prevY)==1) {
                if (col!=BlockState.IGNITED&&currentBlock.state==col) {
                    matches++;
                    tempMarkedBlocks.add(blockList.remove(0));
                } else {
                    if (matches>=3) {
                        markedBlocks.addAll(tempMarkedBlocks);
                    }
                    matches=1;
                    col=currentBlock.state;
                    tempMarkedBlocks.clear();
                    tempMarkedBlocks.add(blockList.remove(0));
                }
            } else {
                if (matches>=3) {
                    markedBlocks.addAll(tempMarkedBlocks);
                }
                matches=1;
                col=currentBlock.state;
                tempMarkedBlocks.clear();
                tempMarkedBlocks.add(blockList.remove(0));
            }
            prevX=currentBlock.x;
            prevY=currentBlock.y;
        }
        if (matches>=3) {
            markedBlocks.addAll(tempMarkedBlocks);
        }
        return markedBlocks;
    }
	private void CombineAToB(BlockClump A, BlockClump B) {
        for (int i=0;i<A.getBlocks().size();i++) { 
            Block b = A.getBlocks().get(i);
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
        if (blocks.launched<=0) {
            for (int i=0;i<blocks.getBlocks().size();i++) {
                Block b = blocks.getBlocks().get(i);
                if (b.state==BlockState.IGNITED) {
                    b.state=STARTINGSTATES[(int)(Meteo.r.nextInt(3))];
                }
            }
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

        for (int i=0;i<blockData.size();i++) {
            BlockClump bc = blockData.get(i);
            bc.drawBlocks(g,DRAW_STARTX,DRAW_STARTY,block_width,block_height,clickBlock);
        }
        g.setColor(Color.BLACK);
        g.fillRoundRect(DRAW_STARTX, DRAW_STARTY+block_height, DRAW_ENDX-DRAW_STARTX, 3, 3, 1);
        BlockClump.drawDebugBlockClumps(g,DRAW_STARTX,DRAW_STARTY,block_width,block_height,blockData);
        if (Meteo.DEBUG_DRAWING!=DebugMode.OFF) {
            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(blockData.size()),4,Meteo.SCREEN_HEIGHT-20);
        }
    }
    public void handleMouse(MouseQueue mq) {
        MouseEvent e = mq.ev;
        switch (mq.type) {
            case CLICK:
                //System.out.println("Clicked: "+e.getPoint());
                break;
            case DRAG:
                //System.out.println("Dragged: "+e.getPoint());
                if (clumpClickId!=null&&clickBlock!=null) {
                    List<Block> adjacentBlocks = clumpClickId.getBlocks().stream().filter((block)->Math.abs(clickBlock.y-block.y)==1&&clickBlock.x==block.x).collect(Collectors.toList());
                    for (Block b : adjacentBlocks) {
                        if (new Rectangle(0,b.draw_y,Meteo.SCREEN_WIDTH,block_height).contains(e.getPoint())) {
                            int newY = b.y;
                            b.y=clickBlock.y;
                            clickBlock.y = newY;
                            break;
                        }
                    }
                }
                break;
            case ENTER:
                //System.out.println("Entered: "+e.getPoint());
                break;
            case EXIT:
                //System.out.println("Exited: "+e.getPoint());
                break;
            case MOVE:
                //System.out.println("Moved: "+e.getPoint());
                break;
            case PRESS:
                //System.out.println("Pressed: "+e.getPoint());
                //Adjust Y coordinate based on where the board is positioned.
                clickBlock=null;
                clumpClickId=null;
                outer:
                for (int i=0;i<blockData.size();i++) {
                    BlockClump bc = blockData.get(i);
                    for (int j=0;j<bc.getBlocks().size();j++) {
                        Block b = bc.getBlocks().get(j);
                        if (new Rectangle(b.draw_x,b.draw_y,block_width,block_height).contains(e.getPoint())) {
                            clickBlock=b;
                            clumpClickId=bc;
                            break outer;
                        }
                    }
                }
                break;
            case RELEASE:
                //System.out.println("Released: "+e.getPoint());
                break;
            default:
                break;

        }
    }
}
