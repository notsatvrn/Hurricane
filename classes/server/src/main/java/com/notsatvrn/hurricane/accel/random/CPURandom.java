package com.notsatvrn.hurricane.accel.random;

import com.notsatvrn.hurricane.accel.random.IImprovedRandom;
import com.notsatvrn.hurricane.accel.random.ImprovedRandom;

public class CPURandom extends ImprovedRandom implements IImprovedRandom {
    public long x = IImprovedRandom.createSeed();
    public long y = 362436069L;
    public long z = 521288629L;
    public long w = 88675123L;
    public long v = 5783321L;
    public long d = 6615241L;

    public CPURandom() {
        super();
    }

    public CPURandom(long seed) {
        super(seed);
    }

    public long nextLong() {
        long t = (x ^ (x >> 2));
        x = y;
        y = z;
        z = w;
        w = v;
        v = (v ^ (v << 4)) ^ (t ^ (t << 1));
        return (d += 362437) + v;
    }

    public void reset(long seed) {
        x = seed;
        y = 362436069L;
        z = 521288629L;
        w = 88675123L;
        v = 5783321L;
        d = 6615241L;
    }
}

