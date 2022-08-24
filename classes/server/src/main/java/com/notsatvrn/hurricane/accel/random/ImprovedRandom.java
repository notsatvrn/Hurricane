package com.notsatvrn.hurricane.accel.random;

import com.notsatvrn.hurricane.util.MathUtil;
import com.notsatvrn.hurricane.accel.random.IImprovedRandom;

import java.util.Random;

public class ImprovedRandom extends Random implements IImprovedRandom {
    public ImprovedRandom() {
        reset(IImprovedRandom.createSeed());
    }

    public ImprovedRandom(long seed) {
        reset(seed);
    }

    @Override
    protected int next(int bits) {
        return (int)(nextLong() >>> (64 - bits));
    }

    @Override
    public int nextInt() {
        return MathUtil.long2int(nextLong());
    }

    @Override
    public double nextDouble() {
        return MathUtil.long2double(nextLong());
    }

    @Override
    public float nextFloat() {
        return MathUtil.long2float(nextLong());
    }

    @Override
    public boolean nextBoolean() {
        return MathUtil.long2boolean(nextLong());
    }

    @Override
    public void setSeed(long seed) {
        reset(seed);
    }

    @Override
    public void reset(long seed) {
    }

    public int[] nextInts(int n) {
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = nextInt();
        }
        return data;
    }

    public long[] nextLongs(int n) {
        long[] data = new long[n];
        for (int i = 0; i < n; i++) {
            data[i] = nextLong();
        }
        return data;
    }

    public boolean[] nextBooleans(int n) {
        boolean[] data = new boolean[n];
        for (int i = 0; i < n; i++) {
            data[i] = nextBoolean();
        }
        return data;
    }

    public float[] nextFloats(int n) {
        float[] data = new float[n];
        for (int i = 0; i < n; i++) {
            data[i] = nextFloat();
        }
        return data;
    }

    public double[] nextDoubles(int n) {
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = nextDouble();
        }
        return data;
    }

    public double[] nextGaussians(int n) {
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = nextGaussian();
        }
        return data;
    }
}

