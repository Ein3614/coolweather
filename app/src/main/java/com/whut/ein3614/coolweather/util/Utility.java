package com.whut.ein3614.coolweather.util;

import com.whut.ein3614.coolweather.db.City;
import com.whut.ein3614.coolweather.db.County;
import com.whut.ein3614.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类描述：解析JSON数据（网络数据-->数据库）
 * 创建人：Created by Administrator on 2018/9/6.
 * 修改人：
 * 修改时间：
 */
public class Utility {
    /**
     * 解析处理省级json数据
     * */
    public static boolean handleProvinceResponse(String response){
        try {
            JSONArray array = new JSONArray(response);
            for(int i=0;i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                Province province = new Province();
                province.setProvinceName(object.getString("name"));
                province.setProvinceCode(object.getInt("id"));
                province.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 解析处理市级json数据
     * */
    public static boolean handleCityResponse(String response,int provinceId){
        try {
            JSONArray array = new JSONArray(response);
            for(int i=0;i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                City city = new City();
                city.setCityCode(object.getInt("id"));
                city.setCityName(object.getString("name"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 解析处理县级json数据
     * */
    public static boolean handleCountyResponse(String response,int cityId){
        try {
            JSONArray array = new JSONArray(response);
            for(int i=0;i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                County county = new County();
                county.setCityId(cityId);
                county.setCountyName(object.getString("name"));
                county.setWeatherId(object.getString("weather_id"));
                county.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
