package com.qinggan.app.arielapp.ui.nav;

public class NavAddressBean {
    private String mAddress;
    private String mName;

    public NavAddressBean(String address, String name){
        this.mAddress = address;
        this.mName = name;
    }

    public String getmAddress(){
        return this.mAddress;
    }

    public void setmAddress(String address){
        this.mAddress = address;
    }

    public String getmName(){
        return this.mName;
    }

    public void setmName(String name){
        this.mName = name;
    }
}
