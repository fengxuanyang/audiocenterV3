package com.ragentek.homeset.audiocenter.model.bean;

import java.io.Serializable;

/**
 * Created by xuanyang.feng on 2017/3/16.
 */

public class TagDetail implements Serializable {
    private String name;
    private int icon;
    private int categoryID;
    private String keywords;
    private int radioType;//for radio
    private int province;//for radio
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }


    public int getRadioType() {
        return radioType;
    }

    public void setRadioType(int radioType) {
        this.radioType = radioType;
    }



    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }



    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public String getName() {

        return name;
    }

    public int getIcon() {
        return icon;
    }


}
