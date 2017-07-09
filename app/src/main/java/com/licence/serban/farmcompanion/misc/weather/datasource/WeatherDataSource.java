package com.licence.serban.farmcompanion.misc.weather.datasource;

import com.google.android.gms.maps.model.LatLng;
import com.licence.serban.farmcompanion.misc.weather.models.WeatherCondition;

import java.util.List;

public interface WeatherDataSource {
  interface Callback<T> {
    void onSuccess(T arg);

    void onError();
  }

  void getCurrentCondition(LatLng latLng, Callback<WeatherCondition> callback);

  void getForecast(LatLng latLng, Callback<List<WeatherCondition>> callback);
}
