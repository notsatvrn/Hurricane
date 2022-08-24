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
import com.notsatvrn.hurricane.accel.device.CLDevice;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Arrays;

import lombok.NonNull;

public class CLRandom extends ImprovedRandom implements IImprovedRandom {
    public long x = IImprovedRandom.createSeed();
    public long y = 362436069L;
    public long z = 521288629L;
    public long w = 88675123L;
    public long v = 5783321L;
    public long d = 6615241L;

    private static final String[] kernelStringMulti = new String[]{"""
    __kernel void nextLongs(__global const long long *id, __global long long *od) {
        long long t, x, y, z, w, v, d, n;
        int gid = get_global_id(0);
        x = id[gid];
        y = id[gid+1];
        z = id[gid+2];
        w = id[gid+3];
        v = id[gid+4];
        d = id[gid+5];
        n = id[gid+6];
        
        for (int i = 0; i < n; ++i) {
            t = (x ^ (x >> 2));
            x = y;
            y = z;
            z = w;
            w = v;
            v = (v ^ (v << 4)) ^ (t ^ (t << 1));
            od[gid+6+i] = (d += 362437) + v;
        }

        od[gid] = x;
        od[gid+1] = y;
        od[gid+2] = z;
        od[gid+3] = w;
        od[gid+4] = v;
        od[gid+5] = d;
    }
    """};
    private static final String[] kernelStringSingle = new String[]{"""
    __kernel void nextLong(__global const long long *id, __global long long *od) {
        long long t, x, v;
        int gid = get_global_id(0);
        x = id[gid];
        v = id[gid+4];
        
        t = (x ^ (x >> 2));
        od[gid] = id[gid+1];
        od[gid+1] = id[gid+2];
        od[gid+2] = id[gid+3];
        od[gid+3] = v;
        v = (v ^ (v << 4)) ^ (t ^ (t << 1));
        t = (od[gid+5] = id[gid+5] + 362437) + v;

        od[gid+4] = v;
    }
    """};
    private CLDevice device = null;
    private cl_program programMulti = null;
    private cl_program programSingle = null;
    private static final long size7 = Sizeof.cl_long * 7;
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
        this.programMulti = clCreateProgramWithSource(this.device.ctx,
            1, kernelStringMulti, null, null);
        clBuildProgram(this.programMulti, 0, null, null, null, null);
        this.programSingle = clCreateProgramWithSource(this.device.ctx,
            1, kernelStringSingle, null, null);
        clBuildProgram(this.programSingle, 0, null, null, null, null);
    }

    @Override
    public long nextLong() {
        long[] inData = new long[]{this.x, this.y, this.z, this.w, this.v, this.d};
        long[] outData = new long[7];

        cl_mem inputMemAddress = clCreateBuffer(this.device.ctx,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            size6, Pointer.to(inData), null);
        cl_mem outputMemAddress = clCreateBuffer(this.device.ctx,
            CL_MEM_READ_WRITE,
            size7, null, null);

        cl_kernel kernel = clCreateKernel(this.programSingle, "nextLong", null);

        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(inputMemAddress));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(outputMemAddress));

        clEnqueueNDRangeKernel(this.device.queue, kernel, 1, null,
            new long[]{1}, null, 0, null, null);

        clEnqueueReadBuffer(this.device.queue, outputMemAddress, CL_TRUE, 0,
            Sizeof.cl_long, Pointer.to(outData), 0, null, null);

        clReleaseMemObject(inputMemAddress);
        clReleaseMemObject(outputMemAddress);
        clReleaseKernel(kernel);

        this.x = outData[0];
        this.y = outData[1];
        this.z = outData[2];
        this.w = outData[3];
        this.v = outData[4];
        this.d = outData[5];

        return outData[6];
    }

    @Override
    public long[] nextLongs(int n) {
        int ol = 6 + n;
        long outSize = Sizeof.cl_long * ol;

        long[] inData = new long[]{this.x, this.y, this.z, this.w, this.v, this.d, n};
        long[] outData = new long[ol];
        long[] resultData = new long[n];

        cl_mem inputMemAddress = clCreateBuffer(this.device.ctx,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            size7, Pointer.to(inData), null);
        cl_mem outputMemAddress = clCreateBuffer(this.device.ctx,
            CL_MEM_READ_WRITE,
            outSize, null, null);

        cl_kernel kernel = clCreateKernel(this.programMulti, "nextLongs", null);

        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(inputMemAddress));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(outputMemAddress));

        clEnqueueNDRangeKernel(this.device.queue, kernel, 1, null,
            new long[]{n}, null, 0, null, null);

        clEnqueueReadBuffer(this.device.queue, outputMemAddress, CL_TRUE, 0,
            outSize, Pointer.to(outData), 0, null, null);

        clReleaseMemObject(inputMemAddress);
        clReleaseMemObject(outputMemAddress);
        clReleaseKernel(kernel);

        this.x = outData[0];
        this.y = outData[1];
        this.z = outData[2];
        this.w = outData[3];
        this.v = outData[4];
        this.d = outData[5];

        return Arrays.copyOfRange(outData, 7, ol);

        /*for (i = 6; i < ol; i++) {
            resultData[i] = output_data[i];
        }

        return resultData;*/
    }

    public void destroy() {
        clReleaseProgram(this.programMulti);
        clReleaseProgram(this.programSingle);
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

