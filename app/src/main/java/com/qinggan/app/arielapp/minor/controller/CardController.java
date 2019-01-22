package com.qinggan.app.arielapp.minor.controller;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BKMusicActivity;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.VoiceDcsActivity;
import com.qinggan.app.arielapp.VoiceNewsActivity;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.integration.MusicContacts;
import com.qinggan.app.arielapp.minor.integration.PateoFMCMD;
import com.qinggan.app.arielapp.minor.integration.PateoNewsCMD;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.BdMapUIcontrol;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.navigation.bean.BaiduUiControlEvent;
import com.qinggan.app.arielapp.minor.main.utils.InterceptorProxyUtils;
import com.qinggan.app.arielapp.minor.music.MusicActivity;
import com.qinggan.app.arielapp.minor.music.imagecache.DoubleCache;
import com.qinggan.app.arielapp.minor.music.imagecache.ImageCache;
import com.qinggan.app.arielapp.minor.music.imagecache.ImageLoader;
import com.qinggan.app.arielapp.minor.phone.CallLogManager;
import com.qinggan.app.arielapp.minor.phone.bean.EventBusCardInfo;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneContactsActivity;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.minor.radio.FMActivity;
import com.qinggan.app.arielapp.minor.scenario.SceneActivity;
import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.minor.utils.ShardPreUtils;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.utils.AppManager;
import com.qinggan.app.arielapp.utils.RSBlurProcess;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.tencent.qqmusic.third.api.contract.Data;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by brian on 18-11-29.
 */

public class CardController implements IntegrationCore.MusicListener, ImageLoader.AlbumDownloadCallback {
    private Context mContext;

    public static final String CARD_TYPE_NAVI = "navigation";
    public static final String CARD_TYPE_MUSIC = "music";
    public static final String CARD_TYPE_PHONE = "phone";
    public static final String CARD_TYPE_RADIO = "radio";
    public static final String CARD_TYPE_SCENARIO = "scenario";
    public static final String CARD_TYPE_NEWS = "news";
    public static final String CARD_TYPE_NOTICE = "notice";

    public static final int CARD_NAVI_UNINSTALLED = 0;
    public static final int CARD_NAVI_BACK_HOME = 1;
    public static final int CARD_NAVI_GO_COMPANY = 2;
    public static final int CARD_NAVI_PRESET = 3;
    public static final int CARD_NAVI_COLLECTION = 4;
    public static final int CARD_NAVI_NO_RECOMMENED = 5;

    public static final int CARD_PHONE_UNAUTHORIZATION = 0;
    public static final int CARD_PHONE_MISSED_CALL = 1;
    public static final int CARD_PHONE_LAST_CALL = 2;
    public static final int CARD_PHONE_COLLECTION = 3;
    public static final int CARD_PHONE_SEARCH = 4;

    public static final int CARD_RADIO_UNCONNECTED = 0;
    public static final int CARD_RADIO_EMPTY_LIST = 1;
    public static final int CARD_RADIO_LAST_PLAY = 2;
    public static final int CARD_RADIO_COLLECTION = 3;
    public static final int CARD_RADIO_SEARCH = 4;

    public static final int CARD_SCENARIO_NO_LOGIN = 0;
    public static final int CARD_SCENARIO_UNBINDED = 1;
    public static final int CARD_SCENARIO_RAIN = 2;
    public static final int CARD_SCENARIO_SNOW = 3;
    public static final int CARD_SCENARIO_BAD_AIR = 4;
    public static final int CARD_SCENARIO_TOO_COLD = 5;
    public static final int CARD_SCENARIO_TOO_HOT = 6;
    public static final int CARD_SCENARIO_GOOD_AIR = 7;

    public static final int CARD_MUSIC_UNINSTALLED = 0;
    public static final int CARD_MUSIC_FAVORITE = 1;
    public static final int CARD_MUSIC_LOCAL_SONG = 2;
    public static final int CARD_MUSIC_TOP = 3;
    public static final int CARD_MUSIC_LAST_PLAY = 4;
    public static final int CARD_MUSIC_EMPTY_LIST = 5;
    public static final int CARD_MUSIC_NEW = 6;

    public static final int CARD_NEWS_ONE_SHOT = 0;

    public static final int WEATHER_TYPE_RAIN = 0;
    public static final int WEATHER_TYPE_SNOW = 1;
    public static final int WEATHER_TYPE_TOO_HOT = 2;
    public static final int WEATHER_TYPE_TOO_COLD = 3;
    public static final int WEATHER_TYPE_BAD_AIR = 4;
    public static final int WEATHER_TYPE_GOOD_AIR = 5;

    private FragmentManager mFragmentManager;

    private static final String FileName = "music_save.xml";

    public static final String PACKAGE_NAME_BAIDU_MAP = "com.baidu.BaiduMap";
    private static final String PACKAGE_NAME_QQ_MUSIC = "com.tencent.qqmusic";
    private static final String PACKAGE_NAME_ARIELAPP = "com.qinggan.app.arielapp";

    private final LatLng mDefaultOrigin = new LatLng(32.058784, 118.757749);
    private String mDefaultFrom = "石榴财智中心";

    private Handler mHandler = new Handler();

    private SimpleStopTimeoutListener mTimeoutListener;

    private ImageLoader mImageLoader;

    private Bitmap musicBlurBG;
    private int mCardWidth = 0;
    private int mCardHeight = 0;

    public void setCardWidth(int cardWidth){
        mCardWidth = cardWidth;
    }

    public void setCardHeight(int cardHeight){
        mCardHeight = cardHeight;
    }

    public Bitmap getMusicBlurBG(){
        return musicBlurBG;
    }

    public void setTimeoutListener(SimpleStopTimeoutListener timeoutListener){
        mTimeoutListener = timeoutListener;
    }

    public CardController(Context context, IntegrationCore integrationCore) {
        mContext = context;
        EventBus.getDefault().register(this);
        mIntergrationCore = integrationCore;
        setMusicListener();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.setImageCache(DoubleCache.getCacheInstance());
        mImageLoader.addDownloadCallback(this);
    }

    private IntegrationCore mIntergrationCore;

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void handleEvent(EventBusCardInfo event) {
        mPhoneCardInfo = event.getCardInfo();
        mIntergrationCore.cardInfoCallback(CARD_TYPE_PHONE);
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getEvent(EventBusBean event) {
        String type = event.getType();
        if(type.equals("setElement")){
            Log.i("Alan","CardController len=-="+event.getLen()+"=-=time=-="+event.getElapsed_time());
        }

        if (TextUtils.isEmpty(event.getLen()) ||
                TextUtils.isEmpty(event.getElapsed_time())){
            Log.i("Brian_card", "calculate rest time error.");
            return;
        }

        int cardType = Integer.parseInt(mNaviCardInfo.getType());
        switch(cardType){
            case CARD_NAVI_BACK_HOME:
            case CARD_NAVI_COLLECTION:
            case CARD_NAVI_GO_COMPANY:
            case CARD_NAVI_PRESET:
                Log.i("Brian_card", "update navigation card.");
                mNaviCardInfo.setSubContent(event.getElapsed_time());
                mIntergrationCore.cardInfoCallback(CARD_TYPE_NAVI);
                break;
        }
    }
    /**
     * public static CardController getCardController(Context context) {
     * synchronized (mLock) {
     * if (mCardController == null) {
     * mCardController = new CardController(context);
     * }
     * }
     * return mCardController;
     * }
     */

    public void setMusicListener() {
        mIntergrationCore.setMusicListener(this);
    }

    private CardInfo mNaviCardInfo;
    private CardInfo mMusicCardInfo;
    private CardInfo mPhoneCardInfo;
    private CardInfo mRadioCardInfo;
    private CardInfo mScenarioCardInfo;
    private CardInfo mNewsCardInfo;
    private CardInfo mNoticeCardInfo;

    public CardInfo getCardInfo(String cardType) {
        if (cardType.equalsIgnoreCase(CARD_TYPE_NAVI)) {
            return mNaviCardInfo;
        } else if (cardType.equalsIgnoreCase(CARD_TYPE_MUSIC)) {
            return mMusicCardInfo;
        } else if (cardType.equalsIgnoreCase(CARD_TYPE_PHONE)) {
            return mPhoneCardInfo;
        } else if (cardType.equalsIgnoreCase(CARD_TYPE_RADIO)) {
            return mRadioCardInfo;
        } else if (cardType.equalsIgnoreCase(CARD_TYPE_SCENARIO)) {
            return mScenarioCardInfo;
        } else if (cardType.equalsIgnoreCase(CARD_TYPE_NEWS)) {
            return mNewsCardInfo;
        } else if (cardType.equalsIgnoreCase(CARD_TYPE_NOTICE)) {
            return mNoticeCardInfo;
        }

        return null;
    }

    public void restoreCardInfo() {
        List<CardInfo> cardInfos = mIntergrationCore.readCardInfo();
        for (CardInfo cardInfo : cardInfos) {
            if (cardInfo == null) {
                break;
            }

            String id = cardInfo.getCardId();
            if (id.equalsIgnoreCase(CARD_TYPE_NAVI)) {
                mNaviCardInfo = cardInfo;
            } else if (id.equalsIgnoreCase(CARD_TYPE_MUSIC)) {
                mMusicCardInfo = cardInfo;
            } else if (id.equalsIgnoreCase(CARD_TYPE_PHONE)) {
                mPhoneCardInfo = cardInfo;
            } else if (id.equalsIgnoreCase(CARD_TYPE_RADIO)) {
                mRadioCardInfo = cardInfo;
            } else if (id.equalsIgnoreCase(CARD_TYPE_SCENARIO)) {
                mScenarioCardInfo = cardInfo;
            } else if (id.equalsIgnoreCase(CARD_TYPE_NEWS)) {
                mNewsCardInfo = cardInfo;
            } else if (id.equalsIgnoreCase(CARD_TYPE_NOTICE)) {
                mNoticeCardInfo = cardInfo;
            }
        }
    }

    public void saveCardInfos() {
        List<CardInfo> cardInfos = new ArrayList<>();
        cardInfos.add(mNaviCardInfo);
        cardInfos.add(mMusicCardInfo);
        cardInfos.add(mPhoneCardInfo);
        cardInfos.add(mRadioCardInfo);
        cardInfos.add(mScenarioCardInfo);
        cardInfos.add(mNewsCardInfo);
        cardInfos.add(mNoticeCardInfo);
        mIntergrationCore.updateCardInfo(cardInfos);
    }

    public void initCardInfos() {
        reloadNaviCard();
        reloadMusicCard();
        reloadRadioCard();
        reloadScenarioCard();
        initPhoneCard();
        reloadNewsCard();
        reloadNoticeCard();
    }

    public void reloadNoticeCard() {
        mNoticeCardInfo = new CardInfo();
        mNoticeCardInfo.setCardId(CARD_TYPE_NOTICE);
    }

    public void reloadNewsCard(){
        mNewsCardInfo = new CardInfo();
        mNewsCardInfo.setCardId(CARD_TYPE_NEWS);

        int status = mIntergrationCore.mPateoNewsCMD.getNewsStatus();
        String content = mIntergrationCore.mPateoNewsCMD.getNewsTitle();
        mNewsCardInfo.setTitle(mContext.getString(R.string.title_news));
        mNewsCardInfo.setContent(content);
        mNewsCardInfo.setType(CARD_NEWS_ONE_SHOT + "");

        if (status == PateoNewsCMD.STATUS_PLAY) {
            mNewsCardInfo.setPlayOn(true);
            mNewsCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
        } else if (status == PateoNewsCMD.STATUS_STOP) {
            mNewsCardInfo.setPlayOn(false);
            mNewsCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
        }

        mIntergrationCore.cardInfoCallback(CARD_TYPE_NEWS);
    }

    public void reloadNaviCard() {
        mNaviCardInfo = new CardInfo();
        mNaviCardInfo.setCardId(CARD_TYPE_NAVI);
        if (!isBaiduMapInstalled()) {
            ArielLog.logController(ArielLog.LEVEL_INFO, "Navigation--CardController-reloadNaviCard",
                    "baidu map is not installed..");
            setNaviInfo(CARD_NAVI_UNINSTALLED + "",
                    mContext.getString(R.string.title_navigation),
                    mContext.getString(R.string.subtitle_install),
                    mContext.getString(R.string.content_install_baidu_map),
                    "",
                    "");
            return;
        }

        NaviInfo presetDestination = getPresetDestination();
        if (presetDestination != null) {
            setNaviInfo(CARD_NAVI_PRESET + "",
                    mContext.getString(R.string.title_guess),
                    mContext.getString(R.string.subtitle_preset_dest),
                    presetDestination.getName(),
                    "",
                    presetDestination.getDisplayName());
            return;
        }

        boolean isAm = mIntergrationCore.judgeIsAM();

        NaviInfo home = getHomeDestionation();
        if (!isAm && home != null) {
            mNaviCardInfo.setType(CARD_NAVI_BACK_HOME + "");
            setNaviInfo(CARD_NAVI_BACK_HOME + "",
                    mContext.getString(R.string.title_guess),
                    "",
                    mContext.getString(R.string.content_back_home),
                    "",
                    home.getDisplayName());
            return;
        }

        NaviInfo company = getCompanyDestination();
        if (isAm && company != null) {
            setNaviInfo(CARD_NAVI_GO_COMPANY + "",
                    mContext.getString(R.string.title_guess),
                    "",
                    mContext.getString(R.string.content_go_company),
                    "",
                    company.getDisplayName());
            return;
        }

        NaviInfo firstCollect = getFirstCollectDestination();
        if (firstCollect != null) {
            setNaviInfo(CARD_NAVI_COLLECTION + "",
                    mContext.getString(R.string.title_guess),
                    mContext.getString(R.string.subtitle_collection_dest),
                    firstCollect.getName(),
                    "",
                    firstCollect.getDisplayName());
            return;
        }

        mNaviCardInfo.setType(CARD_NAVI_NO_RECOMMENED + "");
        setNaviInfo(CARD_NAVI_NO_RECOMMENED + "",
                mContext.getString(R.string.title_search_dest),
                "",
                mContext.getString(R.string.content_where_to_go),
                "",
                "");
    }

    public void reloadMusicCard() {
        mMusicCardInfo = new CardInfo();
        mMusicCardInfo.setCardId(CARD_TYPE_MUSIC);

        if (!isQQMusicInstalled()) {
            ArielLog.logController(ArielLog.LEVEL_INFO, "Music--CardController--reloadMusicCard",
                    "qq music is not installed..");
            setMusicInfo(CARD_MUSIC_UNINSTALLED + "",
                    mContext.getString(R.string.title_music),
                    mContext.getString(R.string.subtitle_install),
                    mContext.getString(R.string.content_install_qq_music),
                    "",
                    "");
            return;
        }

        CardInfo musicInfo = null;

        musicInfo = getCurrentMusic();
        if (musicInfo != null) {
            mMusicCardInfo = musicInfo;
            mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
            return;
        }

        musicInfo = getLastPlayMusic();
        if (musicInfo == null) {
            setMusicInfo(CARD_MUSIC_EMPTY_LIST + "",
                    mContext.getString(R.string.title_music),
                    "",
                    mContext.getString(R.string.content_music_empty_list),
                    "",
                    "");
        } else {
            mMusicCardInfo = musicInfo;
            mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
        }
    }

    private CardInfo getCurrentMusic(){
        CardInfo cardInfo = null;
        Data.Song currentSong = mIntergrationCore.getCurrentPlayingSong();

        if (currentSong == null) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--getCurrentMusic",
                    "No current song, perhaps qq music is not play on.");
        } else {
            cardInfo = new CardInfo();
            cardInfo.setCardId(CARD_TYPE_MUSIC);
            cardInfo.setTitle(mContext.getString(R.string.title_music));
            //TODO:Need to change by folder type
            cardInfo.setType(CARD_MUSIC_LAST_PLAY + "");

            String songName = currentSong.getTitle();
            String folderName = "";
            mIntergrationCore.getKnownFolderByTitle(songName);
            ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--CardController--getCurrentMusic",
                    "Folder name is " + folderName);
            cardInfo.setSubTitle(folderName);

            cardInfo.setContent(currentSong.getTitle());
            cardInfo.setMessage(currentSong.getSinger().getTitle());

            if (mIntergrationCore.getMusicStatus() == MusicContacts.PLAYSTARTED) {
                cardInfo.setPlayOn(true);
                cardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
            } else {
                cardInfo.setPlayOn(false);
                cardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
            }

            String coverKey = currentSong.getId();
            cardInfo.setImageCacheKey(coverKey);
            Bitmap coverBitmap = mImageLoader.getImageCache().get(coverKey);
            if (coverBitmap == null) {
                mImageLoader.downloadWithoutDisplay(currentSong.getAlbum().getCoverUri(), coverKey);
            } else {
                if (mCardWidth != 0 && mCardHeight != 0) {
                    musicBlurBG = getRoundedCornerBitmap(Bitmap.createScaledBitmap(
                            RSBlurProcess.createRSBitmap(mContext, coverBitmap),
                            mCardWidth, mCardHeight, true), 20);
                }
            }

            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--getCurrentMusic",
                    "current song name is " + currentSong.getTitle());
        }

        return cardInfo;
    }


    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private CardInfo getLastPlayMusic() {
        CardInfo cardInfo = null;

        String songName = ShardPreUtils.getInstance(FileName).getStringValue("lastSong_Title");
        String songId = ShardPreUtils.getInstance(FileName).getStringValue("lastSong_Id");
        String coverUri = ShardPreUtils.getInstance(FileName).getStringValue("lastSong_Album_CoverUri");
        String singerName = ShardPreUtils.getInstance(FileName).getStringValue("lastSong_Singer_Title");
        String coverKey = songId;

        if (!TextUtils.isEmpty(songName)) {
            cardInfo = new CardInfo();
            cardInfo.setCardId(CARD_TYPE_MUSIC);
            cardInfo.setType(CARD_MUSIC_LAST_PLAY + "");

            cardInfo.setTitle(mContext.getString(R.string.title_music));
            cardInfo.setSubTitle(mContext.getString(R.string.subtitle_last_play));
            cardInfo.setContent(songName);
            cardInfo.setMessage(singerName);

            if (mIntergrationCore.getMusicStatus() == MusicContacts.PLAYSTARTED) {
                //cardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
                cardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
                cardInfo.setPlayOn(true);
            } else {
                //cardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
                cardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
                cardInfo.setPlayOn(false);
            }

            Data.Song currentSong = mIntergrationCore.getCurrentPlayingSong();
            cardInfo.setImageCacheKey(coverKey);
            Bitmap coverBitmap = mImageLoader.getImageCache().get(coverKey);
            if (coverBitmap == null && currentSong != null) {
                mImageLoader.downloadWithoutDisplay(currentSong.getAlbum().getCoverUri(), coverKey);
            }

            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--getLastPlayMusic",
                    "Last play song name is " + songName);
        }

        return cardInfo;
    }

    public void reloadRadioCard() {
        mRadioCardInfo = new CardInfo();
        mRadioCardInfo.setCardId(CARD_TYPE_RADIO);

        if (!isRadioConnected()) {
            setRadioInfo(CARD_RADIO_UNCONNECTED + "",
                    mContext.getString(R.string.title_radio),
                    "",
                    mContext.getString(R.string.content_no_connect),
                    "",
                    mContext.getString(R.string.message_open_radio));
            return;
        }

        if (getRadioList() == null) {
            setRadioInfo(CARD_RADIO_EMPTY_LIST + "",
                    mContext.getString(R.string.title_search_radio),
                    "",
                    mContext.getString(R.string.content_no_radio),
                    "",
                    "");
            return;
        }

        /**
        CardInfo lastRadio = getLastRadioInfo();
        if (lastRadio != null) {
            mRadioCardInfo = lastRadio;
        } else {
            setRadioInfo(CARD_RADIO_EMPTY_LIST + "",
                    mContext.getString(R.string.title_search_radio),
                    "",
                    mContext.getString(R.string.content_no_radio),
                    "",
                    "");
        }*/
        float currentFrequency = mIntergrationCore.mPateoFMCMD.getCurrentFrequency();
        int radioType = mIntergrationCore.mPateoFMCMD.getRadioType();
        int radioStatus = mIntergrationCore.mPateoFMCMD.getRadioStatus();

        String type = "";
        String title = "";
        String content = "";
        String message = "";

        if (currentFrequency == 0) {
            type = CARD_RADIO_LAST_PLAY + "";
            title = mContext.getString(R.string.title_radio);
            content = mContext.getString(R.string.content_no_radio);
        } else {
            type = CARD_RADIO_LAST_PLAY + "";
            title = mContext.getString(R.string.title_radio);
            //String radioTypeStr = radioType == 1? "FM":"AM";
            String radioTypeStr = "FM";
            content = radioTypeStr + " " + currentFrequency;
        }

        //mRadioCardInfo.setType(type);
        mRadioCardInfo.setTitle(title);
        mRadioCardInfo.setContent(content);

        boolean playOn = radioStatus == PateoFMCMD.STATUS_ON;
        if (playOn) {
            mRadioCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
        } else {
            mRadioCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
        }
        mRadioCardInfo.setPlayOn(playOn);

        ArielLog.logController(ArielLog.LEVEL_DEBUG, "Radio--CardController--reloadRadioCard",
                "Radio card info is " + mRadioCardInfo);
        setRadioInfo(type, title, "", content, "", message);
    }

    public void reloadScenarioCard() {
        mScenarioCardInfo = new CardInfo();
        mScenarioCardInfo.setCardId(CARD_TYPE_SCENARIO);

        int weatherType = getCurrentWeather();

        if (!isUserLogin()) {
            setScenarioInfo(CARD_SCENARIO_NO_LOGIN + "",
                    mContext.getString(R.string.title_weather),
                    mContext.getString(R.string.subtitle_car),
                    getWeahterStr(weatherType),
                    "",
                    mContext.getString(R.string.message_have_no_login_in));
            return;
        }

        if (!hasBinded()) {
            setScenarioInfo(CARD_SCENARIO_UNBINDED + "",
                    mContext.getString(R.string.title_weather),
                    mContext.getString(R.string.subtitle_car),
                    getWeahterStr(weatherType),
                    "",
                    mContext.getString(R.string.message_have_not_bind_car));
            return;
        }

        String type = "";
        String content = "";
        String message = "";
        switch (weatherType) {
            case WEATHER_TYPE_RAIN:
                type = CARD_SCENARIO_RAIN + "";
                content = mContext.getString(R.string.content_rain_on);
                message = mContext.getString(R.string.message_open_mode_rain_and_snow);
                break;
            case WEATHER_TYPE_SNOW:
                type = CARD_SCENARIO_SNOW + "";
                content = mContext.getString(R.string.content_snow_on);
                message = mContext.getString(R.string.message_open_mode_rain_and_snow);
                break;
            case WEATHER_TYPE_TOO_HOT:
                type = CARD_SCENARIO_TOO_HOT + "";
                content = mContext.getString(R.string.content_too_hot);
                message = mContext.getString(R.string.message_open_one_key_cool);
                break;
            case WEATHER_TYPE_TOO_COLD:
                type = CARD_SCENARIO_TOO_COLD + "";
                content = mContext.getString(R.string.content_too_cold);
                message = mContext.getString(R.string.message_open_one_key_warm);
                break;
            case WEATHER_TYPE_BAD_AIR:
                type = CARD_SCENARIO_BAD_AIR + "";
                content = mContext.getString(R.string.content_air_fouls);
                message = mContext.getString(R.string.message_open_mode_fog_and_haze);
                break;
            case WEATHER_TYPE_GOOD_AIR:
                type = CARD_SCENARIO_GOOD_AIR + "";
                content = mContext.getString(R.string.content_nice_weather);
                message = mContext.getString(R.string.message_open_window);
                break;
        }
        setScenarioInfo(type,
                mContext.getString(R.string.title_weather),
                mContext.getString(R.string.subtitle_car),
                content,
                "",
                message);
    }

    private void initPhoneCard() {
        mPhoneCardInfo = new CardInfo();
        mPhoneCardInfo.setCardId(CardController.CARD_TYPE_PHONE);
        mPhoneCardInfo.setTitle(mContext.getString(R.string.title_phone));

        mPhoneCardInfo.setType(CardController.CARD_PHONE_SEARCH + "");
        mPhoneCardInfo.setSubTitle(mContext.getString(R.string.subtitle_search));
        mPhoneCardInfo.setContent(mContext.getString(R.string.content_call_to_somebody));
        mPhoneCardInfo.setMessage("");
        mPhoneCardInfo.setNumber(0);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            mPhoneCardInfo.setType(CardController.CARD_PHONE_UNAUTHORIZATION + "");
            mPhoneCardInfo.setSubTitle("");
            mPhoneCardInfo.setContent(mContext.getString(R.string.content_authorization));
        }

        CallLogManager.getInstance(mContext).syncCallLog();
    }

    private boolean hasBinded() {
        if (ArielApplication.getmUserInfo() != null
                && !TextUtils.isEmpty(ArielApplication.getmUserInfo().getVin())) {
            return true;
        }
        return false;
    }

    private String getWeahterStr(int weatherType) {
        String result = "";
        switch (weatherType) {
            case WEATHER_TYPE_RAIN:
                result = mContext.getString(R.string.content_rain_on);
                break;
            case WEATHER_TYPE_SNOW:
                result = mContext.getString(R.string.content_snow_on);
                break;
            case WEATHER_TYPE_TOO_HOT:
                result = mContext.getString(R.string.content_too_hot);
                break;
            case WEATHER_TYPE_TOO_COLD:
                result = mContext.getString(R.string.content_too_cold);
                break;
            case WEATHER_TYPE_BAD_AIR:
                result = mContext.getString(R.string.content_air_fouls);
                break;
            case WEATHER_TYPE_GOOD_AIR:
                result = mContext.getString(R.string.content_nice_weather);
                break;
        }
        return result;
    }

    private int getCurrentWeather() {
        return WEATHER_TYPE_RAIN;
    }

    public void setScenarioInfo(String type, String title, String subTitle,
                                String content, String subContent, String message) {
        if (!TextUtils.isEmpty(type)) {
            mScenarioCardInfo.setType(type);
        }

        if (!TextUtils.isEmpty(title)) {
            mScenarioCardInfo.setTitle(title);
        }

        if (!TextUtils.isEmpty(subTitle)) {
            mScenarioCardInfo.setSubTitle(subTitle);
        }

        if (!TextUtils.isEmpty(content)) {
            mScenarioCardInfo.setContent(content);
        }

        if (!TextUtils.isEmpty(subContent)) {
            mScenarioCardInfo.setSubContent(subContent);
        }

        if (!TextUtils.isEmpty(message)) {
            mScenarioCardInfo.setMessage(message);
        }

        mIntergrationCore.cardInfoCallback(CARD_TYPE_SCENARIO);
    }

    public void setRadioInfo(String type, String title, String subTitle,
                             String content, String subContent, String message) {
        if (!TextUtils.isEmpty(type)) {
            mRadioCardInfo.setType(type);
        }

        if (!TextUtils.isEmpty(title)) {
            mRadioCardInfo.setTitle(title);
        }

        if (!TextUtils.isEmpty(subTitle)) {
            mRadioCardInfo.setSubTitle(subTitle);
        }

        if (!TextUtils.isEmpty(content)) {
            mRadioCardInfo.setContent(content);
        }

        if (!TextUtils.isEmpty(subContent)) {
            mRadioCardInfo.setSubContent(subContent);
        }

        if (!TextUtils.isEmpty(message)) {
            mRadioCardInfo.setMessage(message);
        }

        mIntergrationCore.cardInfoCallback(CARD_TYPE_RADIO);
    }


    public void setMusicInfo(String type, String title, String subTitle,
                             String content, String subContent, String message) {
        if (!TextUtils.isEmpty(type)) {
            mMusicCardInfo.setType(type);
        }

        if (!TextUtils.isEmpty(title)) {
            mMusicCardInfo.setTitle(title);
        }

        if (!TextUtils.isEmpty(subTitle)) {
            mMusicCardInfo.setSubTitle(subTitle);
        }

        if (!TextUtils.isEmpty(content)) {
            mMusicCardInfo.setContent(content);
        }

        if (!TextUtils.isEmpty(subContent)) {
            mMusicCardInfo.setSubContent(subContent);
        }

        if (!TextUtils.isEmpty(message)) {
            mMusicCardInfo.setMessage(message);
        }

        mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
    }

    public void setNaviInfo(String type, String title, String subTitle,
                            String content, String subContent, String message) {
        if (!TextUtils.isEmpty(type)) {
            mNaviCardInfo.setType(type);
        }

        if (!TextUtils.isEmpty(title)) {
            mNaviCardInfo.setTitle(title);
        }

        if (!TextUtils.isEmpty(subTitle)) {
            mNaviCardInfo.setSubTitle(subTitle);
        }

        if (!TextUtils.isEmpty(content)) {
            mNaviCardInfo.setContent(content);
        }

        if (!TextUtils.isEmpty(subContent)) {
            mNaviCardInfo.setSubContent(subContent);
        }

        if (!TextUtils.isEmpty(message)) {
            mNaviCardInfo.setMessage(message);
        }

        mIntergrationCore.cardInfoCallback(CARD_TYPE_NAVI);
    }

    private boolean isUserLogin() {
        if (ArielApplication.getmUserInfo() != null) {
            return true;
        }
        return false;
    }

    private NaviInfo getPresetDestination() {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setIsPreset("true");

        List<NaviInfo> list = (List<NaviInfo>) (List) mIntergrationCore.
                queryDestInfo(naviInfo, mContext, NaviInfo.class.getName());

        if (list == null) {
            return null;
        }

        if (list.size() != 0) {
            for (NaviInfo info : list) {
                return info;
            }
        }

        return null;
    }

    private NaviInfo getHomeDestionation() {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setName("家");
        List<NaviInfo> list = (List<NaviInfo>) (List) mIntergrationCore.
                queryDestInfo(naviInfo, mContext, NaviInfo.class.getName());

        if (list == null){
            return null;
        }

        if (list.size() != 0){
            for (NaviInfo temp : list) {
                return temp;
            }
        }
        return null;
    }

    private NaviInfo getCompanyDestination() {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setName("公司");
        List<NaviInfo> list = (List<NaviInfo>) (List) mIntergrationCore.
                queryDestInfo(naviInfo, mContext, NaviInfo.class.getName());

        if (list == null){
            return null;
        }

        if (list.size() != 0){
            for (NaviInfo temp : list) {
                return temp;
            }
        }

        return null;
    }

    private NaviInfo getFirstCollectDestination() {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setName("收藏");
        List<NaviInfo> list = (List<NaviInfo>) (List) mIntergrationCore.
                queryDestInfo(naviInfo, mContext, NaviInfo.class.getName());

        if (list == null){
            Log.i("Brian", "query collection poi is null");
            return null;
        }

        if (list.size() != 0){
            for (NaviInfo temp : list) {
                Log.i("Brian", temp.getName());
                return temp;
            }
        }

        Log.i("Brian", "query collection poi list size is 0");
        return null;
    }

    public boolean isBaiduMapInstalled() {
        return mIntergrationCore.isSomeAppInstalled(PACKAGE_NAME_BAIDU_MAP, mContext);
    }

    private boolean isQQMusicInstalled() {
        return mIntergrationCore.isSomeAppInstalled(PACKAGE_NAME_QQ_MUSIC, mContext);
    }

    private boolean isRadioConnected() {
        return mIntergrationCore.mPateoFMCMD.isRadioConnected();
    }

    private List<String> getRadioList() {
        List<String> result = mIntergrationCore.mPateoFMCMD.getFrequencyList();
        if (result.size() == 0) {
            return null;
        }
        return result;
    }

    private int getRadioType() {
        return mIntergrationCore.mPateoFMCMD.getRadioType();
    }

    private CardInfo getLastRadioInfo() {
        //TODO:
        return null;
    }

    @Override
    public void onBack(ArrayList list,boolean state) {
        //TODO:
    }

    @Override
    public void onBackFolder(ArrayList list) {
        //TODO:
    }

    @Override
    public void songChange() {
        Data.Song currentSong = mIntergrationCore.getCurrentMusic();
        if (currentSong == null) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--songChange",
                    "get no music when song change.");
            return;
        }

        if (mMusicCardInfo == null) {
            mMusicCardInfo = new CardInfo();
        }

        String key = mMusicCardInfo.getImageCacheKey();
        if (key.contains(currentSong.getId())) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--songChange",
                    "key equals. ignore song change.");
            return;
        }

        mMusicCardInfo.setTitle(mContext.getString(R.string.title_music));
        mMusicCardInfo.setType(CARD_MUSIC_LAST_PLAY+"");
        mMusicCardInfo.setSubTitle("");
        mMusicCardInfo.setContent(currentSong.getTitle());
        mMusicCardInfo.setSubContent("");
        mMusicCardInfo.setMessage(currentSong.getSinger().getTitle());
        mMusicCardInfo.setPlayOn(true);

        String coverKey = currentSong.getId();
        mMusicCardInfo.setImageCacheKey(coverKey);
        Bitmap coverBitmap = mImageLoader.getImageCache().get(coverKey);
        if (coverBitmap == null) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--songChange",
                    "bit map is null, so go to download.");
            mImageLoader.downloadWithoutDisplay(currentSong.getAlbum().getCoverUri(), coverKey);
        } else {
            if (mCardWidth != 0 && mCardHeight != 0) {
                musicBlurBG = getRoundedCornerBitmap(Bitmap.createScaledBitmap(
                        RSBlurProcess.createRSBitmap(mContext, coverBitmap),
                        mCardWidth, mCardHeight, true), 20);
            }
        }

        mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
    }

    public static final int ACTION_CLICK_ICON = 0;
    public static final int ACTION_CLICK_CARD = 1;

    public void clickIntent(String cardId, int cardType, int actionType, FragmentManager fragmentManager) {
        if (mFragmentManager == null&&null!=fragmentManager) {
            mFragmentManager = fragmentManager;
        }
        //this.mFragmentManager=fragmentManager;
        if (cardId.equalsIgnoreCase(CARD_TYPE_NAVI)) {
            clickNaviIntent(cardType, actionType);
        } else if (cardId.equalsIgnoreCase(CARD_TYPE_MUSIC)) {
            clickMusicIntent(cardType, actionType);
        } else if (cardId.equalsIgnoreCase(CARD_TYPE_RADIO)) {
            clickRadioIntent(cardType, actionType);
        } else if (cardId.equalsIgnoreCase(CARD_TYPE_SCENARIO)) {
            clickScenarioIntent(cardType, actionType);
        } else if (cardId.equalsIgnoreCase(CARD_TYPE_PHONE)) {
            clickPhoneIntent(cardType, actionType);
        } else if (cardId.equalsIgnoreCase(CARD_TYPE_NEWS)) {
            clickNewsIntent(cardType, actionType);
        }
    }

    private void clickNaviIntent(int cardType, int actionType) {
        if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING && cardType != CARD_NAVI_UNINSTALLED) {
            EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_SHOW_NAVI));
            return;
        }
        switch (actionType) {
            case ACTION_CLICK_ICON:
                triggerNaviIconBycardType(cardType);
                break;
            case ACTION_CLICK_CARD:
                triggerNaviCardBycardType(cardType);
                break;
        }
    }

    private void clickRadioIntent(int cardType, int actionType) {
        switch (actionType) {
            case ACTION_CLICK_ICON:
                triggerRadioIconBycardType(cardType);
                break;
            case ACTION_CLICK_CARD:
                triggerRadioCardBycardType(cardType);
                break;
        }
    }

    private void clickMusicIntent(int cardType, int actionType) {
        switch (actionType) {
            case ACTION_CLICK_ICON:
                triggerMusicIconBycardType(cardType);
                break;
            case ACTION_CLICK_CARD:
                triggerMusicCardBycardType(cardType);
                break;
        }
    }

    private void clickScenarioIntent(int cardType, int actionType) {
        switch (actionType) {
            case ACTION_CLICK_ICON:
                triggerScenarioIconBycardType(cardType);
                break;
            case ACTION_CLICK_CARD:
                triggerScenarioCardBycardType(cardType);
                break;
        }
    }

    private void clickPhoneIntent(int cardType, int actionType) {
        switch (actionType) {
            case ACTION_CLICK_ICON:
                triggerPhoneIconBycardType(cardType);
                break;
            case ACTION_CLICK_CARD:
                triggerPhoneCardBycardType(cardType);
                break;
        }
    }

    private void clickNewsIntent(int cardType, int actionType) {
        switch (actionType) {
            case ACTION_CLICK_ICON:
                triggerNewsIconBycardType(cardType);
                break;
            case ACTION_CLICK_CARD:
                triggerNewsCardBycardType(cardType);
                break;
        }
    }

    private void triggerNaviIconBycardType(int cardType) {
        LatLng carPoi = getCarPoiFromTSP();
        switch (cardType) {
            case CARD_NAVI_UNINSTALLED:
                goToInstallSomeApp(PACKAGE_NAME_BAIDU_MAP);
                break;
            case CARD_NAVI_BACK_HOME:
                NaviInfo home = getHomeDestionation();
                planRoute(home, mDefaultOrigin, mDefaultFrom);
                break;
            case CARD_NAVI_GO_COMPANY:
                NaviInfo company = getCompanyDestination();
                planRoute(company, mDefaultOrigin, mDefaultFrom);
                break;
            case CARD_NAVI_PRESET:
                NaviInfo preset = getPresetDestination();
                planRoute(preset, mDefaultOrigin, mDefaultFrom);
                break;
            case CARD_NAVI_COLLECTION:
                NaviInfo collect = getFirstCollectDestination();
                planRoute(collect, mDefaultOrigin, mDefaultFrom);
                break;
            case CARD_NAVI_NO_RECOMMENED:
                gotoNavigationActivity();
                break;
        }
    }

    private LatLng getCarPoiFromTSP(){
        //TODO:
        return null;
    }

    private void planRoute(NaviInfo destNaviInfo, LatLng carPoi, String poiName){
        if (destNaviInfo == null) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Navigation--CardController--planRoute",
                    "Home is null, when navi card type is home");
            return;
        }

        //TODO： Get Car info from TSP
        if (carPoi == null) {
            return;
        }

        LatLng dest = null;
        String destLat = destNaviInfo.getPoiLat();
        String destLno = destNaviInfo.getPoiLno();

        if (destLat == null || destLno == null) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Navigation--CardController--planRoute",
                    "destLat is null or destLno is null");
            return;
        }

        dest = new LatLng(Double.parseDouble(destNaviInfo.getPoiLat()),
                Double.parseDouble(destNaviInfo.getPoiLno()));

        mIntergrationCore.planRoute(mContext, poiName, dest, carPoi);
    }

    /**
     * 驾驶模式下点击导航卡片的触发行为
     * @param cardType 卡片的形态
     */
    private void triggerNaviCardBycardType(int cardType) {
        switch (cardType) {
            case CARD_NAVI_UNINSTALLED:
                goToInstallSomeApp(PACKAGE_NAME_BAIDU_MAP);
                break;
            case CARD_NAVI_BACK_HOME:
            case CARD_NAVI_GO_COMPANY:
            case CARD_NAVI_PRESET:
            case CARD_NAVI_COLLECTION:
            case CARD_NAVI_NO_RECOMMENED:
                gotoNavigationActivity();
                break;
        }
    }

    private void triggerRadioIconBycardType(int cardType) {
        switch (cardType) {
            case CARD_RADIO_UNCONNECTED:
                break;
            case CARD_RADIO_EMPTY_LIST:
//                mIntergrationCore.mPateoFMCMD.doAutoScan();
                break;
            case CARD_RADIO_LAST_PLAY:
                mIntergrationCore.playOrStopRadio();
                break;
            case CARD_RADIO_COLLECTION:
            case CARD_RADIO_SEARCH:
                gotoFMActivity();
                break;
        }
    }

    private void triggerRadioCardBycardType(int cardType) {
        switch (cardType) {
            case CARD_RADIO_UNCONNECTED:
                //TODO:
                break;
            case CARD_RADIO_EMPTY_LIST:
            case CARD_RADIO_LAST_PLAY:
            case CARD_RADIO_COLLECTION:
            case CARD_RADIO_SEARCH:
                gotoFMActivity();
                break;
        }
    }

    private void triggerMusicIconBycardType(int cardType) {
        switch (cardType) {
            case CARD_MUSIC_UNINSTALLED:
                goToInstallSomeApp(PACKAGE_NAME_QQ_MUSIC);
                break;
            case CARD_MUSIC_FAVORITE:
            case CARD_MUSIC_LOCAL_SONG:
            case CARD_MUSIC_TOP:
            case CARD_MUSIC_LAST_PLAY:
                mIntergrationCore.playOrPauseMusic();
                break;
            case CARD_MUSIC_EMPTY_LIST:
                /**LocalFragmentManager.getInstance().showSubFragment(mFragmentManager,
                        LocalFragmentManager.FragType.MUSIC, R.id.main_content_view);*/
                startMusicActivity();
                break;
        }
    }

    private void triggerMusicCardBycardType(int cardType) {
        switch (cardType) {
            case CARD_MUSIC_UNINSTALLED:
                goToInstallSomeApp(PACKAGE_NAME_QQ_MUSIC);
                break;
            case CARD_MUSIC_FAVORITE:
            case CARD_MUSIC_LOCAL_SONG:
            case CARD_MUSIC_TOP:
            case CARD_MUSIC_LAST_PLAY:
            case CARD_MUSIC_EMPTY_LIST:
//                LocalFragmentManager.getInstance().showSubFragment(mFragmentManager,
//                        LocalFragmentManager.FragType.MUSIC, R.id.main_content_view);
                startMusicActivity();
                break;
        }
    }

    void startMusicActivity(){
        Intent intent = new Intent(mContext, BKMusicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    private void triggerScenarioIconBycardType(int cardType) {
        switch (cardType) {
            case CARD_SCENARIO_NO_LOGIN:
                //TODO:
                break;
            case CARD_SCENARIO_UNBINDED:
                //TODO:
                break;
            case CARD_SCENARIO_RAIN:
                //TODO:
                break;
            case CARD_SCENARIO_SNOW:
                //TODO:
                break;
            case CARD_SCENARIO_BAD_AIR:
                //TODO:
                break;
            case CARD_SCENARIO_TOO_COLD:
                //TODO:
                break;
            case CARD_SCENARIO_TOO_HOT:
                //TODO:
                break;
            case CARD_SCENARIO_GOOD_AIR:
                //TODO:
                break;
        }
    }

    private void triggerScenarioCardBycardType(int cardType) {
        switch (cardType) {
            case CARD_SCENARIO_NO_LOGIN:
                LoginActivity.startAction(mContext);
                break;
            case CARD_SCENARIO_UNBINDED:
                InterceptorProxyUtils.isAviliableUserVehicle();
                break;
            case CARD_SCENARIO_RAIN:
            case CARD_SCENARIO_SNOW:
            case CARD_SCENARIO_BAD_AIR:
            case CARD_SCENARIO_TOO_COLD:
            case CARD_SCENARIO_TOO_HOT:
            case CARD_SCENARIO_GOOD_AIR:
                /**LocalFragmentManager.getInstance().showSubFragment(mFragmentManager,
                        LocalFragmentManager.FragType.QINGJING, R.id.main_content_view);*/
                gotoScenarioActivity();
                break;
        }
    }

    private void gotoScenarioActivity(){
        Intent intent = new Intent(mContext, SceneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    private void triggerPhoneIconBycardType(int cardType) {
        Intent intent;
        switch (cardType) {
            case CARD_PHONE_UNAUTHORIZATION:
                toSelfSetting(mContext);
                break;
            case CARD_PHONE_MISSED_CALL:
            case CARD_PHONE_LAST_CALL:
            case CARD_PHONE_COLLECTION:
                startDial();
                break;
            case CARD_PHONE_SEARCH:
                gotoPhoneContactsActivity();
                break;
            default:
                gotoPhoneMainActivity();
        }
    }

    private void triggerPhoneCardBycardType(int cardType) {
        switch (cardType) {
            case CARD_PHONE_UNAUTHORIZATION:
                toSelfSetting(mContext);
                break;
            case CARD_PHONE_MISSED_CALL:
            case CARD_PHONE_LAST_CALL:
            case CARD_PHONE_COLLECTION:
                gotoPhoneMainActivity();
                break;
            case CARD_PHONE_SEARCH:
                gotoPhoneContactsActivity();
                break;
            default:
                gotoPhoneMainActivity();
        }
    }

    private void startDial() {
        String number = mPhoneCardInfo.getMessage();
        if(number != null) {
            CallUtils.startCallByPhoneNumber(mContext, number);
            CallUtils.startInCallUI(mContext, number, mPhoneCardInfo.getContent());
        }
    }

    private void gotoPhoneContactsActivity() {
        Intent intent = new Intent(mContext, PhoneContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    private void gotoPhoneMainActivity() {
        Intent intent = new Intent(mContext, PhoneMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    private void gotoNavigationActivity() {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    private void gotoFMActivity() {
        Intent intent = new Intent(mContext, FMActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);

        mIntergrationCore.mPateoFMCMD.playCurrent();
    }

    private void gotoNewsActivity(){
        if (mIntergrationCore.mPateoNewsCMD.getWrapper() == null) {
            return;
        }
        Intent intent = new Intent(mContext, VoiceNewsActivity.class);
        intent.putExtra("dcsWrapper", mIntergrationCore.mPateoNewsCMD.getWrapper());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    /*
     * start app self setting
     */
    public static void toSelfSetting(Context context) {
        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(mIntent);
    }

    private void triggerNewsIconBycardType(int cardType) {
        switch(cardType) {
            case CARD_NEWS_ONE_SHOT:
                if (mIntergrationCore.mPateoNewsCMD.getNewsStatus() == PateoNewsCMD.STATUS_PLAY) {
                    //TODO: stop news, no need to do play operation because the card will disappear when news stop.
                    mIntergrationCore.mPateoNewsCMD.setNewsPause();
                }
                break;
        }
    }

    private void triggerNewsCardBycardType(int cardType) {
        switch(cardType){
            case CARD_NEWS_ONE_SHOT:
                gotoNewsActivity();
                break;
        }
    }

    public String getVendor(){
        String[] vendorInfo = Build.FINGERPRINT.split("/");
        if(vendorInfo != null){
            return vendorInfo[0];
        }
        return null;
    }

    @Override
    public void onAlbumDownloadSuccess(String key) {
        Data.Song currentSong = mIntergrationCore.getCurrentMusic();
        if ((currentSong.getId()).equalsIgnoreCase(key)){
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Music--CardController--onAlbumDownloadSuccess",
                    "Current music match right.");
            reloadMusicCard();
        }
    }

    @Override
    public void onAlbumDownloadFailed(String key) {
        Log.i("Brian-Download", "onAlbumDownloadFailed");
    }

    public class AlbumGetService extends IntentService {

        public AlbumGetService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {

        }
    }

    public void goToInstallSomeApp(String packageName){
        String vendor = getVendor();
        ArielLog.logController(ArielLog.LEVEL_DEBUG, "CardController--goToInstallSomeApp",
                "vendor = " + vendor);
        Uri uri = Uri.parse("market://details?id=" + packageName + "&caller=" + PACKAGE_NAME_ARIELAPP);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String marketPackage = "";

        switch (vendor) {
            case "OPPO":
                marketPackage = "com.oppo.market";
                break;
            case "Xiaomi":
                marketPackage = "com.xiaomi.market";
                break;
            case "Huawei":
            case "HUAWEI"://[BUG:L0001M-483] ADDED BY brianchen@pateo.com.cn, at 2018.12.05
                marketPackage = "com.huawei.appmarket";
                break;
            case "vivo":
                marketPackage = "com.bbk.appstore";
                break;
        }

        if (mIntergrationCore.isSomeAppInstalled(marketPackage, mContext)) {
            intent.setPackage(marketPackage);
        }

        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Brian",
                    "=================== start catching exception ===================");
            //[BUG:L0001M-483] ADDED BY brianchen@pateo.com.cn, at 2018.12.05 BEGIN
            Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_user_no_market),
                    Toast.LENGTH_SHORT).show();
            //[BUG:L0001M-483] ADDED BY brianchen@pateo.com.cn, at 2018.12.05 END
            e.printStackTrace();
            ArielLog.logController(ArielLog.LEVEL_DEBUG, "Brian",
                    "=================== end catching exception ===================");
        }

        mContext.startActivity(intent);
    }

    /**
    public void receiveAppInstallOrRemove(String packageName){
        switch(packageName){
            case PACKAGE_NAME_QQ_MUSIC:
                reloadMusicCard();
                break;
            case PACKAGE_NAME_BAIDU_MAP:
                reloadNaviCard();
                break;
        }
    }*/

    @Override
    public void playStateChanged() {
        if (mMusicCardInfo == null) {
            ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--CardController--playStateChanged",
                    "music info is null");
            return;
        }
        int state = mIntergrationCore.getMusicStatus();
        if (state == MusicContacts.PLAYSTARTED) {
            //mMusicCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
            mMusicCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_playing);
            mMusicCardInfo.setPlayOn(true);
            mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
        //} else if (state == MusicContacts.PLAYPAUSED){
            //mMusicCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
        } else if (state == MusicContacts.PLAYPAUSING || state == MusicContacts.PLAYSTOPPED
                || state == MusicContacts.PLAYPAUSED){
            mMusicCardInfo.setRightIconImg(R.drawable.driving_mode_s_botton_play);
            mMusicCardInfo.setPlayOn(false);
            mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
            if (mTimeoutListener != null) {
                mTimeoutListener.timeoutExitCard(CARD_TYPE_MUSIC);
            }
        }/** else if (state == MusicContacts.PLAYPAUSED) {
            if (mTimeoutListener != null) {
                mTimeoutListener.timeoutExitCard(CARD_TYPE_MUSIC);
            }
        }*/
    }

    public void radioSimpleCardTimeout(){
        if (mTimeoutListener != null) {
            mTimeoutListener.timeoutExitCard(CARD_TYPE_RADIO);
        }
    }


    @Override
    public void favStateChange() {

    }

    @Override
    public void playListChange() {

    }

    @Override
    public void playModeChange() {

    }

    public interface SimpleStopTimeoutListener{
        void timeoutExitCard(String cardId);
    }

    @Override
    public void codeState(boolean state) {

    }

    @Override
    public void forSpecialLocalCode(String cmd, int ret) {

    }

    @Override
    public void backFolderName(String name) {
        if (mMusicCardInfo == null) {
            return;
        }

        mMusicCardInfo.setSubTitle(name);
        mIntergrationCore.cardInfoCallback(CARD_TYPE_MUSIC);
    }
}
