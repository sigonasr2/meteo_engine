package sig;

public class Block{
    BlockState state;
    int x,y; //Relative to its block clump
    final static BlockState[] STARTINGSTATES = {BlockState.BLUE,
        BlockState.GREEN,
        BlockState.ORANGE,
        BlockState.PURPLE,
        BlockState.RED,
        BlockState.WHITE,
        BlockState.YELLOW,};
    public Block(int x,int y) {
        this.x=x;
        this.y=y;
        state = STARTINGSTATES[(int)(Math.random()*STARTINGSTATES.length)];
    }
    @Override
    public String toString() {
        return "Block [state=" + state + ", x=" + x + ", y=" + y + "]";
    }
}