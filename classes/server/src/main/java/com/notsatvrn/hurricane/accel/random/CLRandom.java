package com.notsatvrn.hurricane.accel.random;

import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clSetKernelArg;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;

import org.jocl.Sizeof;
import org.jocl.Pointer;
import org.jocl.cl_mem;
import org.jocl.cl_kernel;
import org.jocl.cl_context;
import org.jocl.cl_program;

import com.notsatvrn.hurricane.accel.random.IImprovedRandom;
import com.notsatvrn.hurricane.accel.random.ImprovedRandom;
import com.notsatvrn.hurricane.config.HurricaneConfig;
import com.notsatvrn.hurricane.accel.device.CLDevice;
import com.notsatvrn.hurricane.util.MathUtil;

import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;

public class CLRandom extends ImprovedRandom implements IImprovedRandom {
    private long x = IImprovedRandom.createSeed();
    private long y = 362436069L;
    private long z = 521288629L;
    private long w = 88675123L;
    private long v = 5783321L;
    private long d = 6615241L;

    private static final String[] kernelString = new String[]{"""
    __kernel void nextLong(__global long *id, __global long *ov) {
        int gid = get_global_id(0);
        long x = id[gid];
        long v = id[gid+4]; 
        long t = (x ^ (x >> 2));
        id[gid] = id[gid+1];
        id[gid+1] = id[gid+2];
        id[gid+2] = id[gid+3];
        id[gid+3] = v;
        v = (v ^ (v << 4)) ^ (t ^ (t << 1));
        ov[gid] = (id[gid+5] = id[gid+5] + 362437) + v;

        id[gid+4] = v;
    }
    """};
    private CLDevice device = null;
    private cl_program program = null;
    private static final long size6 = Sizeof.cl_long * 6;

    public CLRandom(@NonNull CLDevice dev) {
        super();
        setupDevice(dev);
    }

    public CLRandom(@NonNull CLDevice dev, long seed) {
        super(seed);
        setupDevice(dev);
    }

    public void setupDevice(@NonNull CLDevice dev) {
        this.device = dev;
        this.program = clCreateProgramWithSource(this.device.ctx,
            1, kernelString, null, null);
        clBuildProgram(this.program, 0, null, null, null, null);
    }

    // CPU RNG is always faster for small data operations, such as single increments
    @Override
    public long nextLong() {
        long t = (x ^ (x >> 2));
        x = y;
        y = z;
        z = w;
        w = v;
        v = (v ^ (v << 4)) ^ (t ^ (t << 1));
        return (d += 362437) + v;
    }

    @Override
    public long[] nextLongs(int n) {
        long[] outData = new long[n];

        if (n < HurricaneConfig.acceleratedRandomThresh) {
            for (int i = 0; i < n; i++) {
                outData[i] = nextLong();
            }
            return outData;
        }

        long outSize = Sizeof.cl_long * n;

        long[] inData = new long[]{this.x, this.y, this.z, this.w, this.v, this.d};

        cl_mem inputMemAddress = clCreateBuffer(this.device.ctx,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            size6, Pointer.to(inData), null);
        cl_mem outputMemAddress = clCreateBuffer(this.device.ctx,
            CL_MEM_READ_WRITE,
            outSize, null, null);

        cl_kernel kernel = clCreateKernel(this.program, "nextLong", null);

        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(inputMemAddress));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(outputMemAddress));

        clEnqueueNDRangeKernel(this.device.queue, kernel, 1, null,
            new long[]{n}, null, 0, null, null);

        clEnqueueReadBuffer(this.device.queue, outputMemAddress, CL_TRUE, 0,
            outSize, Pointer.to(outData), 0, null, null);

        clReleaseMemObject(inputMemAddress);
        clReleaseMemObject(outputMemAddress);
        clReleaseKernel(kernel);

        this.x = inData[0];
        this.y = inData[1];
        this.z = inData[2];
        this.w = inData[3];
        this.v = inData[4];
        this.d = inData[5];

        return outData;
    }

    public int[] nextInts(int n) {
        long[] longs = nextLongs(n);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = MathUtil.long2int(longs[i]);
        }
        return data;
    }

    public boolean[] nextBooleans(int n) {
        long[] longs = nextLongs(n);
        boolean[] data = new boolean[n];
        for (int i = 0; i < n; i++) {
            data[i] = MathUtil.long2boolean(longs[i]);
        }
        return data;
    }

    public float[] nextFloats(int n) {
        long[] longs = nextLongs(n);
        float[] data = new float[n];
        for (int i = 0; i < n; i++) {
            data[i] = MathUtil.long2float(longs[i]);
        }
        return data;
    }

    public double[] nextDoubles(int n) {
        long[] longs = nextLongs(n);
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = MathUtil.long2double(longs[i]);
        }
        return data;
    }

    public void destroy() {
        clReleaseProgram(this.program);
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

