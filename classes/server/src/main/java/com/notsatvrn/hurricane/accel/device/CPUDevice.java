package com.notsatvrn.hurricane.accel.device;

import com.notsatvrn.hurricane.accel.random.ImprovedRandom;
import com.notsatvrn.hurricane.accel.random.CPURandom;
import com.notsatvrn.hurricane.accel.Device;

public class CPUDevice implements Device {
  public CPUDevice() {
  }

  public void destroy() {
  }

  public ImprovedRandom random() {
    return new CPURandom();
  }

  public ImprovedRandom random(long seed) {
    return new CPURandom(seed);
  }

  public String getType() {
    return "CPU";
  }
}

