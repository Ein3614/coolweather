package com.whut.ein3614.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.whut.ein3614.coolweather.gson.Forecast;
import com.whut.ein3614.coolweather.gson.Weather;
import com.whut.ein3614.coolweather.util.HttpUtil;
import com.whut.ein3614.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView tvTitle, tvUpdateTime, tvDegree, tvWeatherInfo, tvAQI, tvPM25,
            tvComfort, tvCarWash, tvSport;
    private LinearLayout forecastLayout;
    private ImageView ivBingPic;
    public SwipeRefreshLayout swipeRefresh;
    private Button btnNav;
    public DrawerLayout drawerLayout;

    public String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //系统状态栏透明化需要Android 5.0以上系统支持
        if (Build.VERSION.SDK_INT >= 21) {
            //获取当前活动的DecorView
            View decorView = getWindow().getDecorView();
            //setSystemUiVisibility()方法用于改变系统UI显示
            //传参 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE 表示活动的布局会显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initViews();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //背景图片地址
        String bingPicUrl = preferences.getString("bing_pic", null);
        if (bingPicUrl != null) {
            //有缓存的时候直接加载背景图片
            Glide.with(this).load(bingPicUrl).into(ivBingPic);
        } else {
            //无缓存时去服务器获取背景图片
            loadBingPic();
        }

        //天气的JSON数据
        String weatherJSON = preferences.getString("weather", null);
        if (weatherJSON != null) {
            //有缓存的时候直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherJSON);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherLayout.setVisibility(View.INVISIBLE);
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }

        btnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //设置下拉刷新事件
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (weatherId != null && !"".equals(weatherId))
                    requestWeather(weatherId);
            }
        });
    }

    private void initViews() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        tvTitle = (TextView) findViewById(R.id.tv_city_title);
        tvUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        tvDegree = (TextView) findViewById(R.id.tv_degree);
        tvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        tvAQI = (TextView) findViewById(R.id.tv_aqi);
        tvPM25 = (TextView) findViewById(R.id.tv_pm25);
        tvComfort = (TextView) findViewById(R.id.tv_comfort);
        tvCarWash = (TextView) findViewById(R.id.tv_car_wash);
        tvSport = (TextView) findViewById(R.id.tv_sport);
        ivBingPic = (ImageView) findViewById(R.id.iv_bing_pic);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //设置下拉刷新进度条颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        btnNav = (Button) findViewById(R.id.btn_nav);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequestByGet(requestBingPic, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取背景图片失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPicUrl = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("bing_pic", bingPicUrl);
                        editor.apply();
                        Glide.with(WeatherActivity.this).load(bingPicUrl).into(ivBingPic);
                    }
                });
            }
        });
    }

    /**
     * 根据weather_id查询城市天气情况，SharedPreferences存储
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=aa5dd4db0dcb4a3587219e97ed6b427f";
        HttpUtil.sendOkHttpRequestByGet(weatherUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherJSON = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(weatherJSON);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", weatherJSON);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        //每次请求天气信息的时候同时刷新背景图片
        loadBingPic();
    }

    /**
     * 处理并展示天气情况
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            tvTitle.setText(weather.basic.cityName);
            tvUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
            tvDegree.setText(weather.now.temperature + "℃");
            tvWeatherInfo.setText(weather.now.more.info);
            //预报
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
                TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
                TextView tvMax = (TextView) view.findViewById(R.id.tv_max);
                TextView tvMin = (TextView) view.findViewById(R.id.tv_min);
                tvDate.setText(forecast.date);
                tvInfo.setText(forecast.more.info);
                tvMax.setText(forecast.temperature.max + "℃");
                tvMin.setText(forecast.temperature.min + "℃");
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                tvAQI.setText(weather.aqi.city.aqi);
                tvPM25.setText(weather.aqi.city.pm25);
            }
            tvComfort.setText("舒适度：" + weather.suggestion.comfort.info);
            tvCarWash.setText("洗车指数：" + weather.suggestion.carWash.info);
            tvSport.setText("运动建议：" + weather.suggestion.sport.info);
            weatherLayout.setVisibility(View.VISIBLE);

            //启动定时任务
            Intent i = new Intent(this, AutoUpdateService.class);
            startService(i);
        } else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }
}
