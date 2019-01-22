package com.qinggan.app.arielapp.WheelControl.Observer;

import com.qinggan.app.arielapp.WheelControl.Listener.MusicControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.NaviControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.PhoneCallControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.RadioControlListener;

public interface IWheelControlOb {
    void handleEvents(int keycode,int keyaction);
    void setMusicListener(MusicControlListener musicListener);
    void setNaviListener(NaviControlListener naviListener);
    void setRadioListener(RadioControlListener radioListener);
    void setPhoneListener(PhoneCallControlListener phoneListener);
    void destroyResource();
}
