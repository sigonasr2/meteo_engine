package sig;

import java.awt.event.MouseEvent;

public class MouseQueue {
    MouseEventType type;
    MouseEvent ev;
    MouseQueue(MouseEventType type,MouseEvent ev) {
        this.type=type;
        this.ev=ev;
    }
}
