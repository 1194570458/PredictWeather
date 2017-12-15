package com.example.kason.predictweather.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kason.predictweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.kason.predictweather.utils.Weather.weatherJson;

/**
 * Created by Kason on 2017/10/19.
 */

public class RecHourlyAdapter extends RecyclerView.Adapter<RecHourlyAdapter.MyHolder> {
    private Activity activity;
    private ArrayList<ArrayList> hourlyJson;

    public RecHourlyAdapter(Activity mainActivity) {
        this.activity = mainActivity;
        hourlyJson = new ArrayList();
        try {
            JSONObject result = new JSONObject(weatherJson.getString("result"));
            JSONArray hourly = result.getJSONArray("hourly");
            for (int i = 0; i < hourly.length(); i++) {
                JSONObject jsonObject = new JSONObject(hourly.get(i) + "");
                ArrayList<String> list = new ArrayList<>();
                list.add(jsonObject.getString("temp"));
                list.add(jsonObject.getString("img"));
                list.add(jsonObject.getString("weather"));
                list.add(jsonObject.getString("time"));
                hourlyJson.add(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upDate() {
        hourlyJson = new ArrayList();
        try {
            JSONObject result = new JSONObject(weatherJson.getString("result"));
            JSONArray hourly = result.getJSONArray("hourly");
            for (int i = 0; i < hourly.length(); i++) {
                JSONObject jsonObject = new JSONObject(hourly.get(i) + "");
                ArrayList<String> list = new ArrayList<>();
                list.add(jsonObject.getString("temp"));
                list.add(jsonObject.getString("img"));
                list.add(jsonObject.getString("weather"));
                list.add(jsonObject.getString("time"));
                hourlyJson.add(list);
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myHolder = new MyHolder(LayoutInflater.from(activity).inflate(R.layout.item_hourly, null, false));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        ArrayList list = hourlyJson.get(position);
        holder.tv_temp.setText(list.get(0) + "°");
        InputStream is = null;
        try {
            is = activity.getAssets().open(list.get(1) + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.iv_img.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.tv_weather.setText((String) list.get(2));
        holder.tv_time.setText((String) list.get(3));
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_temp;
        ImageView iv_img;
        TextView tv_weather;
        TextView tv_time;

        public MyHolder(View itemView) {
            super(itemView);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            iv_img = itemView.findViewById(R.id.iv_img);
            tv_weather = itemView.findViewById(R.id.tv_weather);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

    @Override
    public int getItemCount() {
        return hourlyJson.size();
    }
}
