package com.licence.serban.farmcompanion.misc.weather;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.misc.weather.models.WeatherCondition;

public class WeatherHelper {
  public static String getTemperatureString(double temp) {
    return String.format("%.0f°C", temp);
  }

  public static int getIconForCondition(WeatherCondition weatherCondition) {
    switch (weatherCondition.getType()) {
      case CLEAR:
        return R.drawable.icon_w_s;
      case FEW_CLOUDS:
        return R.drawable.icon_w_cs;
      case CLOUDS:
        return R.drawable.icon_w_c;
      case SHOWER:
        return R.drawable.icon_w_cd;
      case RAIN:
        return R.drawable.icon_w_cr;
      case STORM:
        return R.drawable.icon_w_ch;
      default:
        return R.drawable.icon_w_cs;
    }
  }
}
