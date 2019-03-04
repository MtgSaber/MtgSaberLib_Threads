import net.mtgsaber.lib.threads.Clock;
import net.mtgsaber.lib.threads.Tickable;

import java.util.Random;

public class Clock_Test {
    public static void main(String[] args) {
        test2();
    }

    private static void test1() {
        System.out.println("---- BEGIN TEST 1 ----");
        final long start = System.currentTimeMillis();
        Tickable tck1 = () -> System.out.println("[" + (System.currentTimeMillis()-start) + "ms]: Foo");
        Tickable tck2 = () -> System.out.println("[" + (System.currentTimeMillis()-start) + "ms]: Bar");
        Clock clk1 = new Clock(250, tck1);
        Clock clk2 = new Clock(500, tck2);
        Thread clk1Thread = new Thread(clk1);
        Thread clk2Thread = new Thread(clk2);
        clk1Thread.start();
        try {
            Thread.sleep(125);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        clk2Thread.start();
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        clk1.stop();
        clk2.stop();
    }

    private static class Test2_Tickable implements Tickable {
        private final String NAME;
        private final long START_TIME = System.currentTimeMillis();

        Test2_Tickable(String NAME) {
            this.NAME = NAME;
        }

        @Override
        public void tick() {
            System.out.println("[" + (System.currentTimeMillis()-START_TIME) + "ms]: " + NAME);
        }
    }

    private static void test2() {
        System.out.println("---- INIT TEST 2 ----");

        final Random RNG = new Random();
        final int[] DELAYS = new int[]{
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
                RNG.nextInt(2000) + 1000,
        };

        final Test2_Tickable[] TICKABLES = new Test2_Tickable[]{
                new Test2_Tickable("A"),
                new Test2_Tickable("B"),
                new Test2_Tickable("C"),
                new Test2_Tickable("D"),
                new Test2_Tickable("E"),
                new Test2_Tickable("F"),
                new Test2_Tickable("G"),
                new Test2_Tickable("H")
        };

        final Clock CLK_1 = new Clock(500);
        final Thread CLK_1_THREAD = new Thread(CLK_1);

        System.out.println("---- DELAY TIMES ----");
        for (int i=0; i<DELAYS.length; i++)
            System.out.println("Delay " + i + ": " + DELAYS[i] + "ms");

        System.out.println("---- BEGIN TEST 2 ----");
        CLK_1_THREAD.start();
        for (int i=0; i<DELAYS.length; i++) {
            CLK_1.add(TICKABLES[i]);
            try {
                Thread.sleep(DELAYS[i]);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("** ALL TICKABLES ADDED **");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("** WAITED FOR 10 SECONDS **");
        for (int i=0; i<DELAYS.length; i++) {
            CLK_1.rem(TICKABLES[i]);
            try {
                Thread.sleep(DELAYS[DELAYS.length-1-i]);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        CLK_1.stop();
        System.out.println("---- TEST 2 COMPLETE ----");
    }
}
