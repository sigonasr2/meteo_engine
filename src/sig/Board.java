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
    public Board(int width, int height, double gravity, double launch_power, double max_rise_spd, double max_fall_spd,
            double[] combo_power_bonus) {
        this.width = width;
        this.height = height;
        this.gravity = gravity;
        this.launch_power = launch_power;
        this.max_rise_spd = max_rise_spd;
        this.max_fall_spd = max_fall_spd;
        this.combo_power_bonus = combo_power_bonus;
        this.blockData = new ArrayList<BlockClump>();

        List<Block> initialBlocks = new ArrayList<Block>();
        for (int x=0;x<width;x++) {
            for (int y=0;y<3;y++) {
                initialBlocks.add(new Block(x,y));
            }
        }

        BlockClump defaultClump = new BlockClump(initialBlocks,0,0,0);
        System.out.println(defaultClump);
    }
}
