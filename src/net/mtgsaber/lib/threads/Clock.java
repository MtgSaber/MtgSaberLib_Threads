package net.mtgsaber.lib.threads;

import java.util.LinkedList;
import java.util.Vector;

/**
 * @author MtgSaber
 * @version 0.0.1
 *
 * Cycles once per specified wait time, iterating over an internal collection of <interface>Tickable</interface>s.
 * <interface>Tickable</interface>s can be added via a buffer. They will be added on the next input buffer check step.
 * <interface>Tickable</interface>s are removed in the same manner. The clock will continue running until
 * <method>stop()</method> is called, and <field>running</field> is flipped to false.
 *
 * Each cycle consists of a Tick step, an input step, and an output step.
 * The steps operate as follows:
 *      - Tick:   Each element is ticked.
 *      - Input:  All elements of the add buffer are synchronously added to the internal collection,
 *                then the buffer is flushed.
 *      - Output: All elements of the remove buffer are synchronously removed from the internal collection,
 *                then the buffer is flushed.
 */
public class Clock implements Runnable {
    private final LinkedList<Tickable> ITEMS;
    private final Vector<Tickable> TO_ADD, TO_REM;
    private final long WAIT;
    private volatile boolean running;

    public Clock(long wait, Tickable... items) {
        WAIT = wait;
        ITEMS = new LinkedList<>();
        TO_REM = new Vector<>();
        TO_ADD = new Vector<>();
        for (Tickable item : items)
            ITEMS.add(item);
        running = true;
    }

    public void run() {
        while (running) {
            final long startTime = System.currentTimeMillis();
            synchronized (ITEMS) {
                for (Tickable item : ITEMS) {
                    if (running)
                        item.tick();
                    else
                        break;
                }
            }
            if (running)
                synchronized (TO_ADD) {
                    synchronized (ITEMS) {
                        ITEMS.addAll(TO_ADD);
                        TO_ADD.clear();
                    }
                }
            if (running)
                synchronized (TO_REM) {
                    synchronized (ITEMS) {
                        ITEMS.removeAll(TO_REM);
                        TO_REM.clear();
                    }
                }
            if (running) {
                final long procTime = System.currentTimeMillis();
                if (procTime > startTime + WAIT)
                    System.err.println("[WARNING]: Clock running " + (procTime-startTime-WAIT) + "ms behind!");
                while (System.currentTimeMillis() < startTime + WAIT && running) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void start() {
        running = true;
    }

    public void add(Tickable... items) {
        synchronized (TO_ADD) {
            for (Tickable item : items)
                TO_ADD.add(item);
        }
    }

    public void rem(Tickable... items) {
        synchronized (TO_REM) {
            for (Tickable item : items)
                TO_REM.add(item);
        }
    }

    public void clear() {
        synchronized (TO_REM) {
            synchronized (ITEMS) {
                TO_REM.addAll(ITEMS);
            }
        }
    }
}
