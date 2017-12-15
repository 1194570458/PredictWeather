package com.example.kason.predictweather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import static com.example.kason.predictweather.view.MainActivity.swipeRefresh;

/**
 * Created by Kason on 2017/10/20.
 */

public class MyScrollView extends ScrollView {

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                swipeRefresh.setEnabled(false);
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                swipeRefresh.setEnabled(true);
                requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                swipeRefresh.setEnabled(false);
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                swipeRefresh.setEnabled(true);
                requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.onTouchEvent(event);
    }
}
