package sig;

import java.util.List;

public class Board {
    List<BlockClump> blockData;
    int width;
    int height;
    public Board(int width,int height) {
        this.width=width;
        this.height=height;
        this.blockData = new ArrayList<BlockClump>();
    }
}
