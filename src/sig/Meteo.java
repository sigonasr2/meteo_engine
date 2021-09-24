package sig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Meteo {
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

    public static void runGameLoop() {
        FRAMECOUNT++;
        b.run(FRAMECOUNT);
    }

    public static void main(String[] args) {        	
        r = new Random(437210983125739812l);
        double[] val = {0,0,};
        b = new Board(SCREEN_WIDTH/2,SCREEN_HEIGHT/2,24,24,8,14,-0.065,1,4,-2,val);

        JFrame f = new JFrame("Meteo Engine");
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

        f.add(p);
        f.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}