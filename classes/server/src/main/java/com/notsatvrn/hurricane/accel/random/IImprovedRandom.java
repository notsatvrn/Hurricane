package com.notsatvrn.hurricane.accel.random;

import java.util.concurrent.atomic.AtomicLong;

public interface IImprovedRandom {
    static final long multiplier = 0x5DEECE66DL;
    static final long mask = (1L << 48) - 1;
    static long seedUniquifier() {
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 1181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next)) {
                return next;
            }
        }
    }
    static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
    static long initialScramble(long seed) {
        return (seed ^ multiplier) & mask;
    }

    static long createSeed() {
        return initialScramble(seedUniquifier() ^ System.nanoTime());
    }

    default public void destroy() {
    }

    default public void reset() {
        reset(createSeed());
    }

    public void reset(long seed);

    public int[] nextInts(int n);

    public long[] nextLongs(int n);

    public boolean[] nextBooleans(int n);

    public float[] nextFloats(int n);

    public double[] nextDoubles(int n);

    public double[] nextGaussians(int n);
}
