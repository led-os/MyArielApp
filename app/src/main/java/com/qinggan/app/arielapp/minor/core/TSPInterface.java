package com.qinggan.app.arielapp.minor.core;

import com.qinggan.app.arielapp.user.Bean.AddressBean;

/**
 * Created by brian on 18-10-30.
 */

public interface TSPInterface {

    void getVehicleDetailInfo();
    void getFavorAddressList();
    void addFavorAddress(AddressBean addressBean);
    void addFavorAddress(AddressBean addressBean, boolean isNotifyUI);
    void updateFavorAddress(AddressBean addressBean);
    void updateFavorAddress(AddressBean addressBean, boolean isNotifyUI);
    void delFavorAddress(String sid);
    void delFavorAddress(String sid, boolean isNotifyUI);
}
