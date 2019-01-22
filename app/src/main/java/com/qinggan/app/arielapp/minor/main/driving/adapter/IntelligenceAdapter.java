package com.qinggan.app.arielapp.minor.main.driving.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.mui.TestActivity;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.utils.Tools;
import com.qinggan.app.arielapp.minor.music.MusicActivity;
import com.qinggan.app.arielapp.minor.music.MusicFragment;
import com.qinggan.app.arielapp.minor.music.imagecache.DoubleCache;
import com.qinggan.app.arielapp.minor.music.imagecache.ImageCache;
import com.qinggan.app.arielapp.minor.music.imagecache.ImageLoader;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.radio.FMActivity;
import com.qinggan.app.arielapp.minor.scenario.SceneActivity;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IntelligenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private IntegrationCore integrationCore;
    private Context context;
    private List<CardInfo> cardList;
    private FragmentManager fragmentManager;
    private ImageLoader mImageLoader;

    private boolean mHasInitHeightAndWidth = false;

    public IntelligenceAdapter(Context context, List<CardInfo> cardList, FragmentManager fragmentManager) {
        this.context = context;
        this.cardList = cardList;
        this.fragmentManager = fragmentManager;
        integrationCore = IntegrationCore.getIntergrationCore(context);

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.setImageCache(DoubleCache.getCacheInstance());
    }

    private static final int VIEW_TYPE_NORMAL = 0xA1;
    private static final int VIEW_TYPE_NOTICE = 0xA2;

    @Override
    public int getItemViewType(int position) {
        final CardInfo cardInfo = cardList.get(position);
        String cardId = cardInfo.getCardId();
        if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_NOTICE)) {
            return VIEW_TYPE_NOTICE;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                view = LayoutInflater.from(context).inflate(R.layout.intelligence_item_layout,
                        parent, false);
                return new MyViewHolder(view);
            case VIEW_TYPE_NOTICE:
                view = LayoutInflater.from(context).inflate(R.layout.intelligence_item_layout_notice,
                        parent, false);
                return new NoticeViewHolder(view);
        }

        return null;
    }

    private synchronized void initWidthAndHeight(View view){
        if (view == null) return;
        if (!mHasInitHeightAndWidth) {
            CardController cardController = integrationCore.getCardController();
            if (cardController != null) {
                int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
                Log.i("Brian_card","init card width =" + width +
                        "; init card Height =" + height);
                if (width != 0 && height != 0) {
                    cardController.setCardWidth(width);
                    cardController.setCardHeight(height);
                    mHasInitHeightAndWidth = true;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final CardInfo cardInfo = cardList.get(position);
        //赋值
        updateUI(holder, cardInfo);

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        MyViewHolder myHolder;
        if (holder instanceof MyViewHolder) {
            myHolder = (MyViewHolder)holder;
            initWidthAndHeight(myHolder.card_view);
        }
    }

    //赋值
    private void updateUI(final RecyclerView.ViewHolder holder, final CardInfo cardInfo) {
        if (cardInfo.getCardId().equalsIgnoreCase(CardController.CARD_TYPE_NOTICE))
            return;

        MyViewHolder myHolder = null;
        if (holder instanceof MyViewHolder) {
            myHolder = (MyViewHolder)holder;
        }
        if (null != cardInfo.getTitle()) {
            myHolder.title.setText(cardInfo.getTitle());
        } else {
            myHolder.title.setText("");
        }
        if (null != cardInfo.getSubTitle()) {
            myHolder.sub_title.setText(cardInfo.getSubTitle());
        } else {
            myHolder.sub_title.setText("");
        }
        if (null != cardInfo.getContent()) {
            myHolder.content.setText(cardInfo.getContent());
        } else {
            myHolder.content.setText("");
        }
        if (null != cardInfo.getSubContent()) {
            myHolder.subcontent.setText(cardInfo.getSubContent());
        } else {
            myHolder.subcontent.setText("");
        }
        if (null != cardInfo.getMessage()) {
            myHolder.message.setText(cardInfo.getMessage());
        } else {
            myHolder.message.setText("");
        }
        if (cardInfo.getNumber() > 0) {
            myHolder.number.setText(cardInfo.getNumber() + "");
            myHolder.number.setVisibility(View.VISIBLE);
        } else {
            myHolder.number.setVisibility(View.GONE);
        }
        //个性化判断
        final String cardId = cardInfo.getCardId();
        String statusStr = cardInfo.getType();
        int statusType = 0;
        try {
            statusType = Integer.valueOf(statusStr);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        int cadImg = 0;
        int iconImg = R.drawable.driving_mode_s_botton_search;
        int icon_left_img = 0;
        Bitmap albumCover = null;
        Bitmap musicBg = null;
        switch (cardId) {
            case CardController.CARD_TYPE_NAVI:
                cadImg = R.drawable.driving_mode_s_bottomborder_navi;
                if (statusType == CardController.CARD_NAVI_UNINSTALLED) {
                    iconImg = R.drawable.driving_mode_s_botton_search;
                } else {
                    iconImg = R.drawable.driving_mode_s_botton_navi;
                }

                break;
            case CardController.CARD_TYPE_MUSIC:
                cadImg = R.drawable.drivermode_bg_music;
                switch (statusType) {
                    case CardController.CARD_MUSIC_UNINSTALLED:
                        iconImg = R.drawable.driving_mode_s_botton_search;
                        break;
                    case CardController.CARD_MUSIC_FAVORITE:
                    case CardController.CARD_MUSIC_LOCAL_SONG:
                    case CardController.CARD_MUSIC_TOP:
                    case CardController.CARD_MUSIC_NEW:
                    case CardController.CARD_MUSIC_LAST_PLAY:
                        String key = cardInfo.getImageCacheKey();
                        if (key != null) {
                            albumCover = mImageLoader.getImageCache().get(key);
                        }

                        musicBg = integrationCore.getCardController().getMusicBlurBG();
                        iconImg = R.drawable.driving_mode_s_botton_play;
                        break;
                    case CardController.CARD_MUSIC_EMPTY_LIST:
                        iconImg = R.drawable.driving_mode_s_botton_playing;
                        break;
                }
                if (cardInfo.getRightIconImg() != 0) {
                    iconImg = cardInfo.getRightIconImg();
                }
                break;
            case CardController.CARD_TYPE_PHONE:
                iconImg = R.drawable.driving_mode_s_botton_search;
                cadImg = R.drawable.driving_mode_s_bottomborder_phone;
                switch (statusType) {
                    case CardController.CARD_PHONE_UNAUTHORIZATION:
                        iconImg = R.drawable.driving_mode_s_botton_search;
                        break;
                    case CardController.CARD_PHONE_MISSED_CALL:
                        iconImg = R.drawable.driving_mode_s_botton_phone;
                        break;
                    case CardController.CARD_PHONE_LAST_CALL:
                        iconImg = R.drawable.driving_mode_s_botton_phone;
                        break;
                    case CardController.CARD_PHONE_COLLECTION:
                        iconImg = R.drawable.driving_mode_s_botton_phone;
                        break;
                    case CardController.CARD_PHONE_SEARCH:
                        iconImg = R.drawable.driving_mode_s_botton_search;
                        break;
                }
                break;
            case CardController.CARD_TYPE_RADIO:

                iconImg = R.drawable.driving_mode_s_botton_scanning;
                //cadImg = R.drawable.driving_mode_s_bottomborder_radio;
                cadImg = R.drawable.driving_mode_s_bottomborder_car;
                switch (statusType) {
                    case CardController.CARD_RADIO_UNCONNECTED:
                        iconImg = R.drawable.driving_mode_s_botton_scanning;
                        break;
                    case CardController.CARD_RADIO_EMPTY_LIST:
                        iconImg = R.drawable.driving_mode_s_botton_scanning;
                        break;
                    case CardController.CARD_RADIO_LAST_PLAY:
                        iconImg = R.drawable.driving_mode_s_botton_play;
                        break;
                    case CardController.CARD_RADIO_COLLECTION:
                        iconImg = R.drawable.driving_mode_s_botton_play;
                        break;
                    case CardController.CARD_RADIO_SEARCH:
                        iconImg = R.drawable.driving_mode_s_botton_scanning;
                        break;

                }

                if (cardInfo.getRightIconImg() != 0) {
                    iconImg = cardInfo.getRightIconImg();
                }

                break;
            case CardController.CARD_TYPE_SCENARIO:
                cadImg = R.drawable.driving_mode_s_bottomborder_car;

                switch (statusType) {
                    case CardController.CARD_SCENARIO_NO_LOGIN:
                        iconImg = R.drawable.driving_mode_s_botton_window;
                        icon_left_img = R.drawable.driving_mode_s_icon_sunny;
                        break;
                    case CardController.CARD_SCENARIO_UNBINDED:
                        iconImg = R.drawable.driving_mode_s_botton_window;
                        icon_left_img = R.drawable.driving_mode_s_icon_sunny;
                        break;
                    case CardController.CARD_SCENARIO_RAIN:
                        iconImg = R.drawable.driving_mode_s_botton_window;
                        icon_left_img = R.drawable.driving_mode_s_icon_weather_rain;
                        break;
                    case CardController.CARD_SCENARIO_SNOW:
                        iconImg = R.drawable.driving_mode_s_botton_window;
                        icon_left_img = R.drawable.driving_mode_s_icon_weather_snow;
                        break;
                    case CardController.CARD_SCENARIO_BAD_AIR:
                        iconImg = R.drawable.driving_mode_s_botton_purify;
                        icon_left_img = R.drawable.driving_mode_s_icon_smog;
                        break;
                    case CardController.CARD_SCENARIO_TOO_COLD:
                        iconImg = R.drawable.driving_mode_s_botton_warm;
                        icon_left_img = R.drawable.driving_mode_s_icon_weather_snow;
                        break;
                    case CardController.CARD_SCENARIO_TOO_HOT:
                        iconImg = R.drawable.driving_mode_s_botton_cold;
                        icon_left_img = R.drawable.driving_mode_s_icon_hot;
                        break;
                    case CardController.CARD_SCENARIO_GOOD_AIR:
                        iconImg = R.drawable.driving_mode_s_botton_window;
                        icon_left_img = R.drawable.driving_mode_s_icon_sunny;
                        break;
                }

                break;
            case CardController.CARD_TYPE_NEWS:
                cadImg = R.drawable.driving_mode_s_bottom_border_news;
                if (cardInfo.getRightIconImg() != 0) {
                    iconImg = cardInfo.getRightIconImg();
                }
                break;

        }

        Bitmap bgBitmap = getBitmapForImgResourse(context, cadImg);
        if (cardInfo.getCardId().equalsIgnoreCase(CardController.CARD_TYPE_MUSIC)
                && musicBg != null) {
            myHolder.card_view.setBackground(new BitmapDrawable(musicBg));
        } else {
            //myHolder.card_view.setBackgroundResource(cadImg);
            if (bgBitmap != null) {
                myHolder.card_view.setBackground(new BitmapDrawable(bgBitmap));
            }
        }

        Bitmap rightIconBmp = getBitmapForImgResourse(context, iconImg);
        //myHolder.icion_right.setBackgroundResource(iconImg);
        if (rightIconBmp != null) {
            myHolder.icion_right.setBackground(new BitmapDrawable(rightIconBmp));
        }

        if (icon_left_img != 0) {
            myHolder.icon_left.setVisibility(View.VISIBLE);
            myHolder.icon_left.setImageResource(icon_left_img);
        } else if (albumCover != null) {
            myHolder.icon_left.setVisibility(View.VISIBLE);
            myHolder.icon_left.setImageBitmap(albumCover);
        } else {
            myHolder.icon_left.setVisibility(View.GONE);
        }
        //点击事件
        myHolder.left_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                integrationCore.clickCard(cardId, Integer.valueOf(cardInfo.getType()),
                        CardController.ACTION_CLICK_CARD, fragmentManager);
                EventBus.getDefault().post(new EventBusBean("heardStatus", "close"));

            }
        });
        myHolder.icion_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationCore.clickCard(cardId, Integer.valueOf(cardInfo.getType()),
                        CardController.ACTION_CLICK_ICON, fragmentManager);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {

        public NoticeViewHolder(View itemView) {
            super(itemView);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout card_view;
        private LinearLayout left_card;
        private TextView title;
        private TextView sub_title;
        private TextView content;
        private TextView subcontent;
        private TextView message;
        private ImageView icon_left;
        private ImageView icion_right;
        private TextView number;


        public MyViewHolder(View itemView) {
            super(itemView);
            left_card = itemView.findViewById(R.id.left_card);
            title = itemView.findViewById(R.id.title);
            sub_title = itemView.findViewById(R.id.sub_title);
            content = itemView.findViewById(R.id.content);
            subcontent = itemView.findViewById(R.id.subcontent);
            message = itemView.findViewById(R.id.message);
            icon_left = itemView.findViewById(R.id.icon_left);
            icion_right = itemView.findViewById(R.id.icion_right);
            card_view = itemView.findViewById(R.id.card_view);
            number = itemView.findViewById(R.id.number);
        }
    }


    //根据cardId 转 StageController.Stage
    private StageController.Stage toStage(String cardId) {
        StageController.Stage _stage = StageController.Stage.NAVIGATION;
        switch (cardId) {
            case CardController.CARD_TYPE_NAVI:
                _stage = StageController.Stage.NAVIGATION;
                break;
            case CardController.CARD_TYPE_MUSIC:
                _stage = StageController.Stage.MUSIC;
                break;
            case CardController.CARD_TYPE_PHONE:
                _stage = StageController.Stage.PHONE;
                break;
            case CardController.CARD_TYPE_RADIO:
                _stage = StageController.Stage.RADIO;
                break;
            case CardController.CARD_TYPE_SCENARIO:
                _stage = StageController.Stage.SCENARIO;
                break;
//            case CardController.CARD_TYPE_NEWS:
//                _stage=StageController.Stage.;
//                break;
            default:

                break;

        }
        return _stage;
    }

    /**
     * 大图片处理机制
     * 利用Bitmap 转存 R图片
     */
    public Bitmap getBitmapForImgResourse(Context mContext, int imgId){
        Bitmap result = null;
        try {
            InputStream is = mContext.getResources().openRawResource(imgId);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inSampleSize = 1;
            result = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (IOException e) {
            Log.i("Brian_ioexception", "====================== Brian start ========================");
            e.printStackTrace();
            Log.i("Brian_ioexception", "====================== Brian end ========================");
        }
        return result;
    }

}
