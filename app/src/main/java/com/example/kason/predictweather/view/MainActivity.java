package com.example.kason.predictweather.view;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.example.kason.predictweather.R;
import com.example.kason.predictweather.adapter.RecDailyAdapter;
import com.example.kason.predictweather.adapter.RecHourlyAdapter;
import com.example.kason.predictweather.bean.JsonBean;
import com.example.kason.predictweather.utils.City;
import com.example.kason.predictweather.utils.JsonFileReader;
import com.example.kason.predictweather.utils.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static String APPKEY = "c063cc71ea0e3234558e89148747c1de";

    private LinearLayout ll_location;
    private TextView tv_city_name;
    public static SwipeRefreshLayout swipeRefresh;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private List<List<String>> options2Items = new ArrayList<>();
    private List<List<List<String>>> options3Items = new ArrayList<>();

    private AssetManager assets;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String city = (String) msg.obj;
                    Iterator<Map.Entry<String, HashMap<String, String>>> iterator = City.CITY.entrySet().iterator();
                    StringBuffer stringBuffer = new StringBuffer();
                    while (iterator.hasNext()) {
                        Map.Entry<String, HashMap<String, String>> entry = iterator.next();
                        String key = entry.getKey();
                        HashMap<String, String> value = entry.getValue();
                        stringBuffer.append(key + ",");
                    }
                    Weather.getData(MainActivity.this, handler, city, City.getCityID(city), City.getCityCode(city));
                    break;
                case 2:
                    JSONObject jsonOb = (JSONObject) msg.obj;
                    swipeRefresh.setRefreshing(false);
                    try {
                        JSONObject result = new JSONObject(jsonOb.getString("result"));
                        Log.e("handleMessage", "handleMessage: " + result);
                        city = result.getString("city");
                        String updatetime = result.getString("updatetime");
                        String date = result.getString("date");
                        String week = result.getString("week");
                        String temp = result.getString("temp");
                        String weather = result.getString("weather");
                        String img = result.getString("img");
                        String winddirect = result.getString("winddirect");
                        String windpower = result.getString("windpower");
                        String humidity = result.getString("humidity");
                        String windspeed = result.getString("windspeed");

                        tv_city_name.setText(city + "市");
                        tv_updatetime.setText(updatetime);
                        tv_date.setText(date);
                        tv_week.setText(week);
                        tv_temp.setText(temp + "°");
                        tv_weather.setText(weather);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(assets.open(img + ".png"));
                            iv_img.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        tv_winddirect.setText(winddirect);
                        tv_windpower.setText(windpower);
                        tv_humidity.setText(humidity);
                        tv_windspeed.setText(windspeed);
                        if (rec_hourly.getAdapter() == null && rec_daily.getAdapter() == null) {
                            rec_hourly.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.HORIZONTAL));
                            rec_hourly.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                            rec_hourly.setAdapter(new RecHourlyAdapter(MainActivity.this));

                            rec_daily.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.HORIZONTAL));
                            rec_daily.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                            rec_daily.setAdapter(new RecDailyAdapter(MainActivity.this));
                        } else {
                            RecHourlyAdapter recHourAdapter = (RecHourlyAdapter) rec_hourly.getAdapter();
                            RecDailyAdapter recDailyAdapter = (RecDailyAdapter) rec_daily.getAdapter();
                            recHourAdapter.upDate();
                            recDailyAdapter.upDate();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    cityName = sp.getString("city", "null");
                    if (!cityName.equals("null")) {
                        onRefresh();
                        Toast.makeText(MainActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "请选择你的城市", Toast.LENGTH_LONG).show();
                        showPickerView();
                    }
                    break;
            }

        }
    };

    private TextView tv_updatetime;
    private TextView tv_date;
    private TextView tv_week;
    private TextView tv_temp;
    private TextView tv_weather;
    private ImageView iv_img;
    private TextView tv_winddirect;
    private TextView tv_windpower;
    private TextView tv_humidity;
    private TextView tv_windspeed;
    private ImageView tv_day_night;

    private String cityName;
    private SharedPreferences sp;

    private RecyclerView rec_hourly;
    private RecyclerView rec_daily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setListener();
        initJsonData();
        City.Init(this, handler);
        sp = getSharedPreferences("City", MODE_PRIVATE);
        assets = getAssets();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
                t.setToNow(); // 取得系统时间。
                int hour = t.hour;    // 0-23
                try {
                    URL url = null;
                    if (hour > 6 && hour <= 18) {
                        url = new URL("http://m.weather.com.cn/img/d0.gif");
                    } else {
                        url = new URL("http://m.weather.com.cn/img/n0.gif");
                    }
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    InputStream is = conn.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    tv_day_night.setImageBitmap(bitmap);
                                }
                            }
                    );
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Timer().schedule(timerTask, 0, 60000);
    }


    private void initView() {
        tv_city_name = (TextView) findViewById(R.id.tv_city_name);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ll_location = (LinearLayout) findViewById(R.id.ll_location);

        tv_updatetime = (TextView) findViewById(R.id.tv_updatetime);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_week = (TextView) findViewById(R.id.tv_week);
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        tv_weather = (TextView) findViewById(R.id.tv_weather);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        tv_winddirect = (TextView) findViewById(R.id.tv_winddirect);
        tv_windpower = (TextView) findViewById(R.id.tv_windpower);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        tv_windspeed = (TextView) findViewById(R.id.tv_windspeed);
        tv_day_night = (ImageView) findViewById(R.id.tv_day_night);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.GRAY);

        rec_hourly = (RecyclerView) findViewById(R.id.rec_hourly);
        rec_daily = (RecyclerView) findViewById(R.id.rec_daily);
    }

    private void setListener() {
        ll_location.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        Message message = handler.obtainMessage();
        message.obj = cityName;
        message.what = 1;
        message.sendToTarget();
    }

    @Override
    public void onClick(View view) {
        if (view == ll_location) {
            showPickerView();
        }
    }

    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                tv_city_name.setText(options2Items.get(options1).get(options2));
                Message message = handler.obtainMessage();
                cityName = options2Items.get(options1).get(options2).replace("市", "");
                message.obj = cityName;
                message.what = 1;
                message.sendToTarget();
                sp.edit().putString("city", cityName).commit();
                //                                mTvAddress.setText(text);
            }
        }).setTitleText("")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.GRAY)
                .setContentTextSize(13)
                .setOutSideCancelable(false)
                .build();
        //          pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        //        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }


    private void initJsonData() {   //解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        //  获取json数据
        String JsonData = JsonFileReader.getJson(this, "province_data.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            List<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            List<List<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
}
