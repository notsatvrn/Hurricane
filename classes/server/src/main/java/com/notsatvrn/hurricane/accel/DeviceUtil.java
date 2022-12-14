package com.notsatvrn.hurricane.accel;

import com.notsatvrn.hurricane.accel.Device;
import com.notsatvrn.hurricane.accel.device.CLDevice;
import com.notsatvrn.hurricane.accel.device.CPUDevice;
import com.notsatvrn.hurricane.config.HurricaneConfig;
import com.notsatvrn.hurricane.accel.random.ImprovedRandom;

import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clGetDeviceIDs;

import org.jocl.cl_platform_id;
import org.jocl.cl_device_id;
import org.jocl.cl_context_properties;
import org.jocl.cl_queue_properties;
import org.jocl.cl_kernel;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class DeviceUtil {
  public static final Logger LOGGER = LogUtils.getLogger();
  public static ObjectArrayList<Device> devices = new ObjectArrayList();
  public static int deviceCount = 0;
  public static Int2ObjectOpenHashMap<Device> deviceConfig = new Int2ObjectOpenHashMap();
  public static cl_context_properties ctxProp = null;
  public static cl_queue_properties queueProp = null;

  public static void populateDevices() {
    devices.add(new CPUDevice());
    deviceCount++;

    int[] npa = new int[1];
    clGetPlatformIDs(0, null, npa);
    int np = npa[0];

    cl_platform_id[] pa = new cl_platform_id[np];
    clGetPlatformIDs(np, pa, null);
    cl_platform_id p = pa[0];

    ctxProp = new cl_context_properties();
    ctxProp.addProperty(CL_CONTEXT_PLATFORM, p);

    int[] nda = new int[1];
    clGetDeviceIDs(p, CL_DEVICE_TYPE_ALL, 0, null, nda);
    int nd = nda[0];

    cl_device_id[] da = new cl_device_id[nd];
    clGetDeviceIDs(p, CL_DEVICE_TYPE_ALL, nd, da, null);

    queueProp = new cl_queue_properties();
    
    for (int i = 0; i < da.length; i++) {
      devices.add(new CLDevice(da[i]));
    }
  }

  public static void configureDevices() {
    int dc = devices.size();

    if (HurricaneConfig.acceleratedRandom) {
      LOGGER.info("[Hurricane] Testing accelerated RNG.");

      long rngBestTime = Long.MAX_VALUE;
      int rngBestDeviceID = 0;
      Device rngBestDevice = null;

      int rngNums = HurricaneConfig.acceleratedRandomThresh + 1;

      for (int i = 0; i < dc; i++) {
        Device device = devices.get(i);
        LOGGER.info("[Hurricane] Testing RNG on device " + Integer.toString(i) + " (" + device.getType() + ") (" + Integer.toString(rngNums) + " random 64-bit integers)...");
        ImprovedRandom rng = device.random();

        long start = System.nanoTime();
        rng.nextLongs(rngNums);
        long end = System.nanoTime();
        long result = end - start;
        LOGGER.info("[Hurricane] Took " + Long.toString(result) + " nanoseconds.");
        if (result < rngBestTime) {
          rngBestTime = result;
          rngBestDeviceID = i;
          rngBestDevice = device;
        }
        rng.destroy();
      }

      LOGGER.info("[Hurricane] Best RNG device: " + Integer.toString(rngBestDeviceID) + " (" + rngBestDevice.getType() + ").");

      deviceConfig.put(0, rngBestDevice);
    } else {
      LOGGER.info("[Hurricane] Using non-accelerated RNG.");
      deviceConfig.put(0, devices.get(0));
    }
  }

  public static void destroyDevices() {
    for (int i = 0; i < devices.size(); i++) {
      devices.get(i).destroy();
    } 
  }

  public static Device best(byte task) {
    return deviceConfig.get(task);
  }
}

