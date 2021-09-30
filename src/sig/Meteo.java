package sig;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Meteo implements MouseListener,MouseMotionListener{
    public final static int SCREEN_WIDTH=640;
    public final static int SCREEN_HEIGHT=640;
    public static long FRAMECOUNT=0;
    public static double DRAWTIME=0;
    public static double GAMELOOPTIME=0;
    public static JFrame f;
    public static Board b;
    public static Random r;

    public final static long TIMEPERTICK = 16666667l;
    public static DebugMode DEBUG_DRAWING = DebugMode.MODE2;

    public static List<MouseQueue> MOUSE_QUEUE = new ArrayList<MouseQueue>();

    public static void runGameLoop() {
        FRAMECOUNT++;
        for (int i=0;i<MOUSE_QUEUE.size();i++) {
            MouseQueue mq = MOUSE_QUEUE.get(i);
            b.handleMouse(mq);
        }
        MOUSE_QUEUE.clear();
        b.run(FRAMECOUNT);
    }

    Meteo(JFrame f) {
        r = new Random(437210983125739812l);
        double[] val = {0,0,};
        b = new Board(SCREEN_WIDTH/2,SCREEN_HEIGHT/2,24,24,8,14,-0.065,5,4,-2,val);

        Panel p = new Panel();

        new Thread() {
            public void run(){
                while (true) {
                    long startTime = System.nanoTime();
                    runGameLoop();
                    p.repaint();
                    long endTime = System.nanoTime();
                    long diff = endTime-startTime;
                    if (diff>TIMEPERTICK) { //Took longer than 1/60th of a second. No sleep.
                        System.err.println("Frame Drawing took longer than "+TIMEPERTICK+"ns to calculate ("+diff+"ns total)!");
                    } else {
                        try {
                            long sleepTime = TIMEPERTICK - diff;
                            long millis = (sleepTime)/1000000;
                            int nanos = (int)(sleepTime-(((sleepTime)/1000000)*1000000));
                            //System.out.println("FRAME DRAWING: Sleeping for ("+millis+"ms,"+nanos+"ns) - "+(diff)+"ns");
                            DRAWTIME = (double)diff/1000000;
                            f.setTitle("Game Loop: "+DRAWTIME+"ms");
                            Thread.sleep(millis,nanos);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        f.getContentPane().addMouseListener(this);
        f.getContentPane().addMouseMotionListener(this);
        f.add(p);
        f.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Meteo Engine");        	
        new Meteo(f);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.CLICK,e));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.PRESS,e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.RELEASE,e));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.ENTER,e));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.EXIT,e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.DRAG,e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        MOUSE_QUEUE.add(new MouseQueue(MouseEventType.MOVE,e));
    }
}