package com.licence.serban.farmcompanion.misc;

/**
 * Created by Serban on 05.03.2017.
 */

public enum ConsumableEnum {
  SEMINTE, IERBICIDE, FERTILIZATORI, COMBUSTIBIL;

  public static ConsumableEnum get(int idx) {
    switch (idx) {
      case 0:
        return SEMINTE;
      case 1:
        return IERBICIDE;
      case 2:
        return FERTILIZATORI;
      case 3:
        return COMBUSTIBIL;
      default:
        return null;
    }
  }
}
