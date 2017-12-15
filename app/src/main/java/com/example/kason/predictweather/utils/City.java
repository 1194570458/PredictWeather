package com.example.kason.predictweather.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.example.kason.predictweather.view.MainActivity.APPKEY;

/**
 * Created by Kason on 2017/10/18.
 * 初始化城市
 */

public class City {
    public static HashMap<String, HashMap<String, String>> CITY;
    private static InitCity initCity;
    static int flat = 0;

    public static String getCityID(String city) {
        return CITY.get(city).get("cityid");
    }

    public static String getCityCode(String city) {
        return CITY.get(city).get("citycode");
    }

    public static void Init(Activity activity, Handler handler) {
        if (initCity == null) {
            initCity = (InitCity) new InitCity(activity, handler).execute(APPKEY);
        }
        if (flat == 1) {
            initCity.cancel(true);

        }
    }

    static class InitCity extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;
        private Activity activity;
        private Handler handler;

        public InitCity(Activity activity, Handler handler) {
            this.activity = activity;
            this.handler = handler;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(activity, "", "正在初始化...");
            progressDialog.setCancelable(false);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        flat = 1;
                        Init(activity, handler);
                        progressDialog.dismiss();
                    }
                    return false;
                }
            });

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Toast.makeText(activity, "初始化完成", Toast.LENGTH_SHORT).show();
            Message msg = new Message();
            msg.what = 3;
            handler.sendMessage(msg);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Toast.makeText(activity, "初始化取消", Toast.LENGTH_SHORT).show();
        }


        @Override
        protected Void doInBackground(String... strings) {
            StringBuffer URLpath = new StringBuffer();
            URLpath.append("https://way.jd.com/jisuapi/weather1");
            URLpath.append("?appkey=");
            URLpath.append(APPKEY);
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(URLpath.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                is = conn.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                String str = null;
                StringBuffer context = new StringBuffer();
                if (conn.getResponseCode() == 200) {
                    while ((len = is.read(bytes)) != -1) {
                        str = new String(bytes, 0, len);
                        context.append(str);
                    }
                    JSONObject jsonObject = new JSONObject(context.toString());
                    if (jsonObject.getString("code").equals("10000")) {
                        String result = jsonObject.getString("result");
                        JSONArray resultArray = new JSONObject(result).getJSONArray("result");
                        CITY = new HashMap<>();
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonResult = resultArray.getJSONObject(i);
                            String city = jsonResult.getString("city");
                            String cityid = jsonResult.getString("cityid");
                            String citycode = jsonResult.getString("citycode");
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("citycode", citycode);
                            hashMap.put("cityid", cityid);
                            CITY.put(city, hashMap);
                        }
                    }
                }

            } catch (MalformedURLException e) {
                Toast.makeText(activity, "初始化失败，请检测权限", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(activity, "初始化失败，请检测IO流", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(activity, "初始化失败，请检测Json格式", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
