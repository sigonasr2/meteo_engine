package sig;

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
}
