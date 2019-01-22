package com.qinggan.app.arielapp.minor.integration;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.control.DcsAudioPlayerControl;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.app.voiceapi.nluresult.news.NewsStatusCallback;

public class PateoNewsCMD {

    private static final String TAG = PateoNewsCMD.class.getSimpleName();

    private Context mContext;

    public static final int STATUS_INVALID = -1;
    public static final int STATUS_PLAY = 1;
    public static final int STATUS_STOP = 2;

    private String mNewsTitle;
    private int mNewsStatus = STATUS_INVALID;
    private String mNewsToken;
    private DcsDataWrapper mNewsWrapper;

    public PateoNewsCMD(Context context) {
        mContext = context;
        NluResultManager.getInstance().setNewsStatusCallback(mNewsStatusCallback);
    }

    private NewsStatusCallback mNewsStatusCallback = new NewsStatusCallback() {
        @Override
        public void onNewStatus(int status) {
            Log.d(TAG,"--onNewStatus--status : " + status);

            if (mNewsStatus != status) {
                mNewsStatus = status;

                if (mNewsStatus == STATUS_PLAY) {
                    AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.NEWS);
                }
            }

            IntegrationCore.getIntergrationCore(mContext).getCardController()
                    .reloadNewsCard();
        }

        @Override
        public void onNewsTitle(String title) {
            Log.d(TAG,"--onNewsTitle--title : " + title);
            mNewsTitle = title;
            IntegrationCore.getIntergrationCore(mContext).getCardController()
                    .reloadNewsCard();
        }

        @Override
        public void onNewsToken(String token) {
            Log.d(TAG,"--onNewsToken--token : " + token);
            mNewsToken = token;
        }

        @Override
        public void onNewsWrapper(DcsDataWrapper wrapper) {
            mNewsWrapper = wrapper;
        }
    };

    public int getNewsStatus() {
        Log.d(TAG,"--getNewsStatus--");

        return mNewsStatus;
    }

    public String getNewsTitle() {
        Log.d(TAG,"--getNewsTitle--");
        return mNewsTitle;
    }

    public DcsDataWrapper getWrapper() {
        return mNewsWrapper;
    }

    public String getmNewsToken() {
        Log.d(TAG,"--getmNewsToken--");
        return mNewsToken;
    }

    public void setNewsPause() {
        Log.d(TAG,"--setNewsPause--");
        DcsAudioPlayerControl.getInstance().onPauseClicked(mNewsToken);
    }

    public void setNewsPlay() {
        Log.d(TAG,"--setNewsPlay--");

        boolean ret = AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.NEWS);
        if (ret) {
            DcsAudioPlayerControl.getInstance().onPlayClicked(mNewsToken);
        }
    }

    public void setNewsNext() {
        Log.d(TAG,"--setNewsNext--");
        boolean ret = AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.NEWS);
        if (ret) {
            DcsAudioPlayerControl.getInstance().onNextClicked(mNewsToken);
        }
    }

    public void setNewsPrev() {
        Log.d(TAG,"--setNewsPrev--");
        boolean ret = AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.NEWS);
        if (ret) {
            DcsAudioPlayerControl.getInstance().onPrevClicked(mNewsToken);
        }
    }

    private AudioPolicyManager.OnAudioPolicyListener mOnAudioPolicyListener  = new AudioPolicyManager.OnAudioPolicyListener() {
        private int status;
        @Override
        public boolean onPause() {
            status = mNewsStatus;
            setNewsPause();
            return false;
        }

        @Override
        public boolean onResume() {
            if (status == STATUS_PLAY) {
                setNewsPlay();
            }
            return false;
        }

        @Override
        public boolean onStop() {
            setNewsPause();
            return false;
        }
    };

}
