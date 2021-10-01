package sig;

import java.util.Arrays;

public class CollisionRangeIdentifier {
    int index;
    int[] range;
    CollisionRangeIdentifier(int index,int[] range) {
        this.index=index;
        this.range=range;
    }
    @Override
    public String toString() {
        return "CollisionRangeIdentifier [index=" + index + ", range=" + Arrays.toString(range) + "]";
    }
}
