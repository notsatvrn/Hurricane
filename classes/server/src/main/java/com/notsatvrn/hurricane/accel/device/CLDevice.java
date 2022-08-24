package com.notsatvrn.hurricane.accel.device;

import com.notsatvrn.hurricane.accel.random.ImprovedRandom;
import com.notsatvrn.hurricane.accel.random.CLRandom;
import com.notsatvrn.hurricane.accel.DeviceUtil;
import com.notsatvrn.hurricane.accel.Device;

import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clGetDeviceInfo;

import static org.jocl.CL.CL_DEVICE_VERSION;
import static org.jocl.CL.CL_DEVICE_NAME;

import org.jocl.cl_command_queue;
import org.jocl.cl_device_id;
import org.jocl.cl_context;
import org.jocl.Pointer;

public class CLDevice implements Device {
  public cl_device_id device = null;
  public cl_context ctx = null;
  public cl_command_queue queue = null;
  public String name = "";
  public float version = 2.0f;

  public CLDevice(cl_device_id dev) {
    this.device = dev;
    this.ctx = clCreateContext(
      DeviceUtil.ctxProp, 1, new cl_device_id[]{dev},
      null, null, null);
    this.queue = clCreateCommandQueueWithProperties(
      this.ctx, dev, DeviceUtil.queueProp, null);
    this.name = getCLData(CL_DEVICE_NAME);
    this.version = getCLVersion();
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

  public float getCLVersion() {
    String deviceVersion = getCLData(CL_DEVICE_VERSION);
    String versionString = deviceVersion.substring(7, 10);
    float version = Float.parseFloat(versionString);
    return version;
  }

  public String getCLData(int param) {
    long[] size = new long[1];
    clGetDeviceInfo(this.device, param, 0, null, size);

    byte[] buffer = new byte[(int)size[0]];
    clGetDeviceInfo(this.device, param, buffer.length, Pointer.to(buffer), null);

    return new String(buffer, 0, buffer.length-1);
  }
}
