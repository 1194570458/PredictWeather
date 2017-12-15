package com.example.kason.predictweather.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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

public class RecDailyAdapter extends RecyclerView.Adapter<RecDailyAdapter.MyHolder> {
    private Activity activity;
    private ArrayList<ArrayList> DailyJson;

    public RecDailyAdapter(Activity activity) {
        this.activity = activity;
        DailyJson = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(weatherJson.getString("result"));
            JSONArray hourly = result.getJSONArray("daily");
            for (int i = 0; i < hourly.length(); i++) {
                JSONObject jsonObject = new JSONObject(hourly.get(i) + "");
                ArrayList<String> list = new ArrayList();
                list.add(jsonObject.getString("date"));
                list.add(jsonObject.getString("sunrise"));
                list.add(jsonObject.getString("week"));
                list.add(jsonObject.getString("sunset"));
                list.add(jsonObject.getString("night"));
                list.add(jsonObject.getString("day"));
                DailyJson.add(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upDate() {
        DailyJson = new ArrayList();
        try {
            JSONObject result = new JSONObject(weatherJson.getString("result"));
            JSONArray hourly = result.getJSONArray("daily");
            for (int i = 0; i < hourly.length(); i++) {
                JSONObject jsonObject = new JSONObject(hourly.get(i) + "");
                ArrayList<String> list = new ArrayList();
                list.add(jsonObject.getString("date"));
                list.add(jsonObject.getString("sunrise"));
                list.add(jsonObject.getString("week"));
                list.add(jsonObject.getString("sunset"));
                list.add(jsonObject.getString("night"));
                list.add(jsonObject.getString("day"));
                DailyJson.add(list);
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myHolder = new MyHolder(LayoutInflater.from(activity).inflate(R.layout.item_daily, null, false));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        ArrayList list = DailyJson.get(position);
        InputStream is = null;
        holder.tv_date.setText(list.get(0) + "");
        holder.tv_week.setText(list.get(2) + "");
        JSONObject night = null;
        JSONObject day = null;
        try {
            night = new JSONObject(list.get(4) + "");
            day = new JSONObject(list.get(5) + "");
            holder.tv_night_templow.setText(night.getString("templow") + "°");
            is = activity.getAssets().open(night.getString("img") + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.iv_night_img.setImageBitmap(bitmap);
            holder.tv_night_windpower.setText(night.getString("windpower"));

            holder.tv_day_templow.setText(day.getString("temphigh") + "°");
            is = activity.getAssets().open(day.getString("img") + ".png");
            bitmap = BitmapFactory.decodeStream(is);
            holder.iv_day_img.setImageBitmap(bitmap);
            holder.tv_day_windpower.setText(night.getString("windpower"));
        } catch (JSONException e) {
            e.printStackTrace();
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
    }

    class MyHolder extends ViewHolder {

        TextView tv_date;
        TextView tv_week;
        TextView tv_night_templow;
        ImageView iv_night_img;
        TextView tv_night_windpower;
        TextView tv_day_templow;
        ImageView iv_day_img;
        TextView tv_day_windpower;

        public MyHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_week = itemView.findViewById(R.id.tv_week);
            tv_night_templow = itemView.findViewById(R.id.tv_night_templow);
            iv_night_img = itemView.findViewById(R.id.iv_night_img);
            tv_night_windpower = itemView.findViewById(R.id.tv_night_windpower);
            tv_day_templow = itemView.findViewById(R.id.tv_day_templow);
            iv_day_img = itemView.findViewById(R.id.iv_day_img);
            tv_day_windpower = itemView.findViewById(R.id.tv_day_windpower);
        }
    }

    @Override
    public int getItemCount() {
        return DailyJson.size();
    }
}
