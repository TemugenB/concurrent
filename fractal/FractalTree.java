import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FractalTree extends Canvas {
    /* Variables with class-wide visibility */
    private static boolean slowMode;
    volatile boolean running = true;
    AtomicInteger tasks = new AtomicInteger(1);
    ArrayBlockingQueue<LineInfo> drawQueue = new ArrayBlockingQueue<>(1024);
    ExecutorService executor = Executors.newFixedThreadPool(128);
    List<LineInfo> lines = new ArrayList<>();

    record LineInfo(int x1, int y1, int x2, int y2, Color color) {}

    /* Recursive function for calculating all drawcalls for the fractal tree */
    public void makeFractalTree(int x, int y, int angle, int height) {

        if (slowMode) {
            try {Thread.sleep(100);}
            catch (InterruptedException ie) { return; }
        }

        if (height == 0) return;

        int x2 = x + (int)(Math.cos(Math.toRadians(angle)) * height * 8);
        int y2 = y + (int)(Math.sin(Math.toRadians(angle)) * height * 8);

        try {
            drawQueue.put(new LineInfo(x, y, x2, y2, height < 5 ? Color.GREEN : Color.BLACK));
        } catch (InterruptedException ie) {
            return;
        }
        tasks.incrementAndGet();
        executor.submit(() -> {
            makeFractalTree(x2, y2, angle-20, height-1);
            if (tasks.decrementAndGet() == 0) {
                synchronized (this) {
                    this.notify();
                }
            }
        });
        makeFractalTree(x2, y2, angle+20, height-1);
    }

    /* Code for EDT */
    /* Must only contain swing code (draw things on the screen) */
    /* Must not contain calculations (do not use math and compute libraries here) */
    /* No need to understand swing, a simple endless loop that draws lines is enough */
    @Override
    public void paint(Graphics g) {
        if (!running) {
            for (LineInfo line : lines) {
                g.setColor(line.color());
                g.drawLine(line.x1(), line.y1(), line.x2(), line.y2());
            }
        }
        try {
            while(running) {
                LineInfo line = drawQueue.poll(50, TimeUnit.MILLISECONDS);
                if (line != null) {
                    g.setColor(line.color());
                    g.drawLine(line.x1(), line.y1(), line.x2(), line.y2());
                    lines.add(line);
                }
            }
        } catch (InterruptedException ie) {}
        System.out.println("Exiting");
    }

    /* Code for main thread */
    public static void main(String args[]) throws InterruptedException {

        /* Parse args */
        slowMode = args.length != 0 && Boolean.parseBoolean(args[0]);
        slowMode = true;
        /* Initialize graphical elements and EDT */
        FractalTree tree = new FractalTree();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setVisible(true);
        frame.add(tree);

        /* Log success as last step */        
        tree.executor.submit(() -> {
            tree.makeFractalTree(390, 480, -90, 10);
            if (tree.tasks.decrementAndGet() == 0) {
                synchronized (tree) {
                    tree.notify();
                }
            }
        });
        synchronized (tree) {
            while (tree.tasks.get() > 0) {
                tree.wait(100);
            }
        }
        while (!tree.drawQueue.isEmpty()) {
            Thread.sleep(100);
        }
        tree.running = false;
        tree.executor.shutdownNow();
        System.out.println("Main has finished");
    }
}
