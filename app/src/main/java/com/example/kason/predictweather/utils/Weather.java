package com.example.kason.predictweather.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.kason.predictweather.view.MainActivity.APPKEY;

/**
 * Created by Kason on 2017/10/19.
 */

public class Weather {
    public static void getData(Activity activity, Handler handler, String city, String cityId, String cityCode) {
        new InitData(activity, handler, city, cityId, cityCode).execute();
    }

    public static JSONObject weatherJson;

    private static class InitData extends AsyncTask {
        private String city, cityId, cityCode;
        private Handler handler;
        private ProgressDialog progressDialog;
        private Activity activity;

        public InitData(Activity activity, Handler handler, String city, String cityId, String cityCode) {
            this.activity = activity;
            this.handler = handler;
            this.city = city;
            this.cityId = cityId;
            this.cityCode = cityCode;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Object o) {
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL("https://way.jd.com/jisuapi/weather?city=" + city + "&cityid=" + cityId + "&citycode=" + cityCode + "&appkey=" + APPKEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                InputStream is = conn.getInputStream();
                if (conn.getResponseCode() == 200) {
                    int len;
                    byte[] bytes = new byte[1024];
                    String str;
                    StringBuffer stringBuffer = new StringBuffer();

                    while ((len = is.read(bytes)) != -1) {
                        str = new String(bytes, 0, len);
                        stringBuffer.append(str);
                    }
                    JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                    String resultJson = jsonObject.getString("result");
                    weatherJson = new JSONObject(resultJson);
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = weatherJson;
                    handler.sendMessage(msg);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
