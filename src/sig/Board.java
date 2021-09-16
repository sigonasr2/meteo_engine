package sig;

import java.util.ArrayList;
import java.util.List;

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
            for (int y=0;y<3;y++) {
                initialBlocks.add(new Block(x,y));
            }
        }

        BlockClump defaultClump = new BlockClump(initialBlocks,0,0,0);
        System.out.println(defaultClump);
    }
}
