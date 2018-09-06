package com.whut.ein3614.coolweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * 类描述：
 * 创建人：Created by Administrator on 2018/9/6.
 * 修改人：
 * 修改时间：
 */
public class Province extends LitePalSupport{
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
