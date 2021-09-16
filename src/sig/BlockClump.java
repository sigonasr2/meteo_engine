package sig;

import java.util.List;

public class BlockClump {
    List<Block> blocks;
    double x,y; //the lower-left origin of this block clump. Every block positions relative to this.
    double yspd;
    public BlockClump(List<Block> blockList) {
        this.blocks = blockList;
    }
}
