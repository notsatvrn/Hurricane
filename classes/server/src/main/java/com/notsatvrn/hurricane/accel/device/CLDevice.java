package com.notsatvrn.hurricane.accel.device;

import com.notsatvrn.hurricane.accel.random.ImprovedRandom;
import com.notsatvrn.hurricane.accel.random.CLRandom;
import com.notsatvrn.hurricane.accel.DeviceUtil;
import com.notsatvrn.hurricane.accel.Device;

import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clCreateContext;

import org.jocl.cl_command_queue;
import org.jocl.cl_device_id;
import org.jocl.cl_context;

public class CLDevice implements Device {
  public cl_device_id device = null;
  public cl_context ctx = null;
  public cl_command_queue queue = null;

  public CLDevice(cl_device_id dev) {
    this.device = dev;
    this.ctx = clCreateContext(
      DeviceUtil.ctxProp, 1, new cl_device_id[]{dev},
      null, null, null);
    this.queue = clCreateCommandQueueWithProperties(
      this.ctx, dev, DeviceUtil.queueProp, null);
  }

  public void destroy() {
    clReleaseCommandQueue(this.queue);
    clReleaseContext(this.ctx);
  }

  public ImprovedRandom random() {
    return new CLRandom(this);
  }

  public ImprovedRandom random(long seed) {
    return new CLRandom(this, seed);
  }

  public String getType() {
    return "OpenCL";
  }
}
