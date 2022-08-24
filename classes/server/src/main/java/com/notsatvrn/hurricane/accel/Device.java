package com.notsatvrn.hurricane.accel;

import com.notsatvrn.hurricane.accel.random.ImprovedRandom;

public interface Device {
  public void destroy();

  public ImprovedRandom random();
  
  public ImprovedRandom random(long seed);

  public String getType();
}

