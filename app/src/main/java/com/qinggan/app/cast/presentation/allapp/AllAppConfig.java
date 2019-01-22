package com.qinggan.app.cast.presentation.allapp;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.PresentationManager;
import com.qinggan.app.cast.presentation.BasePresentation;
import com.qinggan.app.cast.presentation.fm.FMPresentation;
import com.qinggan.app.cast.presentation.music.MusicPresentation;
import com.qinggan.app.cast.presentation.nav.NavPresentation;
import com.qinggan.app.cast.presentation.tel.TelPresentation;
import com.qinggan.app.cast.presentation.vehicle.VehiclePresentation;

/**
 * <allapp>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public enum AllAppConfig {
    VEHICLE(R.string.p_vehicle, R.mipmap.ic_launcher) {
        @Override
        public BasePresentation getPresentation() {
            return new VehiclePresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
        }
    },
    NAV(R.string.p_nav, R.mipmap.ic_launcher) {
        @Override
        public BasePresentation getPresentation() {
            return new NavPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
        }
    },
    MUSIC(R.string.p_music, R.mipmap.ic_launcher) {
        @Override
        public BasePresentation getPresentation() {
            return new MusicPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
        }
    },
    TEL(R.string.p_tel, R.mipmap.ic_launcher) {
        @Override
        public BasePresentation getPresentation() {
            return new TelPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
        }
    },
    FM(R.string.p_fm, R.mipmap.ic_launcher) {
        @Override
        public BasePresentation getPresentation() {
            return new FMPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
        }
    },
    WECHAT(R.string.p_wechat, R.mipmap.ic_launcher);
    public int nameId, iconId;

    AllAppConfig(int nameId, int iconId) {
        this.iconId = iconId;
        this.nameId = nameId;
    }

    public BasePresentation getPresentation() {
        return null;
    }

}
