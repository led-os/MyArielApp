package com.qinggan.app.arielapp.session.media;

import com.qinggan.app.arielapp.iview.IMediaView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.media.MusicBean;

/**
 * Created by shuohuang on 18-5-9.
 */

public class QueryMediaSession implements IASRSession {
    private IMediaView iMediaView;

    @Override
    public void handleASRFeedback(DcsDataWrapper wrapper) {
        if (null != wrapper && wrapper.getDcsBean() != null) {
            if (iMediaView != null) {
                if (wrapper.getDcsBean() instanceof MusicBean) {
                    iMediaView.onShowMedia((MusicBean) wrapper.getDcsBean());
                }

            }
        }

    }

    public void registerOnShowListener(IMediaView mIMediaView) {
        iMediaView = mIMediaView;
    }
}
