package com.licence.serban.farmcompanion.misc.weather.datasource;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.licence.serban.farmcompanion.misc.weather.models.WeatherCondition;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OpenWeatherMapClient implements WeatherDataSource {
  private static final String BASE_URL = "http://api.openweathermap.org/data/2.5";
  private static final String API_KEY = "c38f96d25686835cef6d15bfbfb37b89";

  @Override
  public void getCurrentCondition(LatLng latLng, final Callback<WeatherCondition> callback) {
    AsyncHttpClient httpClient = new AsyncHttpClient();

    RequestParams params = new RequestParams();
    params.add("lat", String.valueOf(latLng.latitude));
    params.add("lon", String.valueOf(latLng.longitude));
    params.add("units", "metric");
    params.add("APPID", API_KEY);

    httpClient.get(String.format("%s/%s", BASE_URL, "weather"), params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        callback.onSuccess(parseWeatherJson(response));
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
        Log.e("error", e.getMessage());
        callback.onError();
      }
    });
  }

  @Override
  public void getForecast(LatLng latLng, final Callback<List<WeatherCondition>> callback) {
    final AsyncHttpClient httpClient = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    params.add("lat", String.valueOf(latLng.latitude));
    params.add("lon", String.valueOf(latLng.longitude));
    params.add("units", "metric");
    params.add("APPID", API_KEY);

    httpClient.get(String.format("%s/%s", BASE_URL, "forecast"), params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        callback.onSuccess(parseWeatherForecastJson(response));
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
//        Log.e("err", e.getMessage());
        callback.onError();
      }
    });
  }

  private List<WeatherCondition> parseWeatherForecastJson(JSONObject object) {
    List<WeatherCondition> forecast = new ArrayList<>();

    try {
      String city = object.getJSONObject("city").getString("name");
      JSONArray forecastArray = object.getJSONArray("list");
      for (int i = 1; i < forecastArray.length(); i++) {
        if (i == 6) {
          break;
        }
        JSONObject weatherObj = forecastArray.getJSONObject(i);
        WeatherCondition condition = new WeatherCondition();

        condition.setCity(city);
        condition.setType(getConditionType(weatherObj.getJSONArray("weather").getJSONObject(0).getString("icon")));

        JSONObject mainObject = weatherObj.getJSONObject("main");

        condition.setTemp(mainObject.getDouble("temp"));
        condition.setTempMin(mainObject.getDouble("temp_min"));
        condition.setTempMax(mainObject.getDouble("temp_max"));

        condition.setDate(new SimpleDateFormat("HH:mm:ss").parse(weatherObj.getString("dt_txt").split(" ")[1]));
        forecast.add(condition);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return forecast;
  }

  private WeatherCondition parseWeatherJson(JSONObject object) {
    WeatherCondition condition = new WeatherCondition();

    try {
      condition.setCity(object.getString("name"));

      JSONObject weatherObj = object.getJSONArray("weather").getJSONObject(0);
      condition.setType(getConditionType(weatherObj.getString("icon")));

      JSONObject mainObject = object.getJSONObject("main");

      condition.setTemp(mainObject.getDouble("temp"));
      condition.setTempMin(mainObject.getDouble("temp_min"));
      condition.setTempMax(mainObject.getDouble("temp_max"));

      condition.setDate(new Date(object.getLong("dt") * 1000));

    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

    return condition;
  }

  private WeatherCondition.ConditionType getConditionType(String iconName) {
    if (iconName.startsWith("01")) {
      return WeatherCondition.ConditionType.CLEAR;
    } else if (iconName.startsWith("02")) {
      return WeatherCondition.ConditionType.FEW_CLOUDS;
    } else if (iconName.startsWith("03") || iconName.startsWith("04")) {
      return WeatherCondition.ConditionType.CLOUDS;
    } else if (iconName.startsWith("09")) {
      return WeatherCondition.ConditionType.SHOWER;
    } else if (iconName.startsWith("10")) {
      return WeatherCondition.ConditionType.RAIN;
    } else if (iconName.startsWith("11")) {
      return WeatherCondition.ConditionType.STORM;
    } else if (iconName.startsWith("13")) {
      return WeatherCondition.ConditionType.SNOW;
    } else if (iconName.startsWith("50")) {
      return WeatherCondition.ConditionType.MIST;
    }

    return WeatherCondition.ConditionType.CLEAR;
  }
}
