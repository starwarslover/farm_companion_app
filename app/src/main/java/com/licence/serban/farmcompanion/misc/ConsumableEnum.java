package com.licence.serban.farmcompanion.misc;

/**
 * Created by Serban on 05.03.2017.
 */

public enum ConsumableEnum {
  SEED, CHEMICAL, FERTILISER, FUEL;

  public static ConsumableEnum get(int idx) {
    switch (idx) {
      case 0:
        return SEED;
      case 1:
        return CHEMICAL;
      case 2:
        return FERTILISER;
      case 3:
        return FUEL;
      default:
        return null;
    }
  }
}
