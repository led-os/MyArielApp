package com.qinggan.app.cast.window.view;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.cast.ArielCastManager;
import com.qinggan.app.cast.PresentationManager;
import com.qinggan.app.cast.presentation.lock.LockPresentation;
import com.qinggan.app.cast.receiver.model.ArielCastPower;
import com.qinggan.app.cast.touch.CastTouchDispatch;
import com.qinggan.app.cast.widgets.BatteryView;
import com.qinggan.app.cast.window.ViewSizeConstant;
import com.qinggan.app.cast.window.WindowViewType;
import com.qinggan.app.cast.window.view.model.ControlWindowModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <控制中心>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class DefaultWindowView extends CastWindowView {

    private static final int CONTROL_INVISIBLE = 0;
    private static final int CONTROL_FM_MUSIC = 1;
    private static final int CONTROL_VISIBLE = 2;

    /**
     * 当前
     */
    private static int controlStatus = CONTROL_INVISIBLE;

    public DefaultWindowView() {
        super(WindowViewType.DEFAULT);
        this.setSize(ViewSizeConstant.FULL_SCREEN, ViewSizeConstant.FULL_SCREEN)
                .setCancelable(false).setContentView(R.layout.cast_main_window_view);
    }

    private LinearLayout mVoiceLl, mMenuLl, mControlLl, mControl_FM_Ll;
    private RelativeLayout mStatusRl;
    private BatteryView mBatteryView;
    private Button mLockScreenBtn, mExitBtn, mPrivateMode;

    private int[] voicePosition = new int[]{0, 0, 0, 0};
    private int[] menuPosition = new int[]{0, 0, 0, 0};
    private int[] controlPosition = new int[]{0, 0, 0, 0};
    private int[] controlFmPosition = new int[]{0, 0, 0, 0};

    /**
     * 起始偏移
     */
    private float transY = 0;

    @Override
    public void onViewInit() {
        super.onViewInit();
        EventBus.getDefault().register(this);

        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(LOG_TAG, "onLayoutChange");
                int[] location = new int[2];
                mVoiceLl.getLocationOnScreen(location);
                int voiceLeft = location[0];
                int voiceTop = location[1];
                int voiceRight = voiceLeft + mVoiceLl.getMeasuredWidth();
                int voiceBottom = voiceTop + mVoiceLl.getMeasuredHeight();
                voicePosition = new int[]{voiceLeft, voiceTop, voiceRight, voiceBottom};
                mControlLl.getLocationOnScreen(location);
                int controlLeft = location[0];
                int controlTop = location[1];
                int controlRight = controlLeft + mControlLl.getMeasuredWidth();
                int controlBottom = controlTop + mControlLl.getMeasuredHeight();
                controlPosition = new int[]{controlLeft, controlTop, controlRight, controlBottom};
                mMenuLl.getLocationOnScreen(location);
                int menuLeft = location[0];
                int menuTop = location[1];
                int menuRight = menuLeft + mMenuLl.getMeasuredWidth();
                int menuBottom = menuTop + mMenuLl.getMeasuredHeight();
                menuPosition = new int[]{menuLeft, menuTop, menuRight, menuBottom};

                mControl_FM_Ll.getLocationOnScreen(location);
                int fmLeft = location[0];
                int fmTop = location[1];
                int fmRight = fmLeft + mControl_FM_Ll.getMeasuredWidth();
                int fmBottom = fmTop + mControl_FM_Ll.getMeasuredHeight();
                controlFmPosition = new int[]{fmLeft, fmTop, fmRight, fmBottom};
            }
        });

        mMenuLl = rootView.findViewById(R.id.menu_ll);
        mStatusRl = rootView.findViewById(R.id.status_rl);
        mBatteryView = rootView.findViewById(R.id.horizontalBattery);
        mVoiceLl = rootView.findViewById(R.id.voice_ll);
        mControlLl = rootView.findViewById(R.id.control_ll);
        mControl_FM_Ll = rootView.findViewById(R.id.control_music_fm_ll);
        mPrivateMode = rootView.findViewById(R.id.control_private_mode);
        mLockScreenBtn = rootView.findViewById(R.id.control_close_screen);
        mExitBtn = rootView.findViewById(R.id.control_exit);
        mPrivateMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "mPrivateMode onClick");
            }
        });
        mLockScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "mLockScreenBtn onClick");
                LockPresentation lockPresentation = new LockPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
                PresentationManager.getInstance().showPresentation(lockPresentation);
            }
        });
        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "mExitBtn onClick");
                ArielCastManager.getInstance().stopCast();
            }
        });
        //下拉控制
        rootView.setOnTouchListener(new View.OnTouchListener() {
            float downY;
            boolean control;
            private int mode = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mode = 0;
                    control = false;
                    if (event.getY() <= CastTouchDispatch.CONTROL_RSP_Y && controlStatus == CONTROL_INVISIBLE) {
                        mode = 1;
                        control = true;
                        if (mControl_FM_Ll.getVisibility() == View.INVISIBLE) {
                            transY = mControlLl.getTranslationY() + mControl_FM_Ll.getMeasuredHeight();
                        } else {
                            transY = mControlLl.getTranslationY();
                        }
                        downY = event.getY();
                    } else if (event.getY() < mControl_FM_Ll.getMeasuredHeight() && controlStatus == CONTROL_FM_MUSIC) {
                        mode = 2;
                        control = true;
                        transY = mControlLl.getTranslationY();
                        downY = event.getY();
                    } else if (controlStatus == CONTROL_VISIBLE) {
                        mode = 3;
                        control = true;
                        transY = mControlLl.getTranslationY();
                        downY = event.getY();
                    }
                }
                //move
                if (control && event.getAction() == MotionEvent.ACTION_MOVE) {
                    float targetTransY = transY + event.getY() - downY;
                    if (targetTransY < 0) {
                        targetTransY = 0;
                    }
                    if (targetTransY > mControlLl.getMeasuredHeight()) {
                        targetTransY = mControlLl.getMeasuredHeight();
                    }
                    mControlLl.setTranslationY(targetTransY);
                }
                //up
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mode == 1) {
                        if (mControl_FM_Ll.getVisibility() == View.INVISIBLE) {
                            if (mControlLl.getTranslationY() > mControlLl.getMeasuredHeight() / 2) {
                                //下拉超过控制中心1/2 显示控制中心
                                showOrHideControlAnimate(true);
                            } else {
                                //缩回去
                                showOrHideControlAnimate(false);
                            }
                        } else {
                            if (mControlLl.getTranslationY() < mControl_FM_Ll.getMeasuredHeight() / 2) {
                                //下拉不超过音乐fm的一半,缩回去
                                showOrHideFMAnimate(false);
                            } else if (mControlLl.getTranslationY() >= mControl_FM_Ll.getMeasuredHeight() / 2 && mControlLl.getTranslationY() < mControlLl.getMeasuredHeight() / 2) {
                                //显示音乐fm
                                showOrHideFMAnimate(true);
                            } else {
                                showOrHideControlAnimate(true);
                            }
                        }
                    } else if (mode == 2) {
                        if (mControlLl.getTranslationY() < mControl_FM_Ll.getMeasuredHeight() / 2) {
                            showOrHideFMAnimate(false);
                        } else if (mControlLl.getTranslationY() >= mControl_FM_Ll.getMeasuredHeight() / 2 && mControlLl.getTranslationY() < mControlLl.getMeasuredHeight() / 2) {
                            showOrHideFMAnimate(true);
                        } else {
                            showOrHideControlAnimate(true);
                        }
                    } else if (mode == 3) {
                        if (mControlLl.getTranslationY() >= mControlLl.getMeasuredHeight() / 2) {
                            showOrHideControlAnimate(true);
                        } else if (mControl_FM_Ll.getVisibility() == View.VISIBLE) {
                            if (mControlLl.getTranslationY() >= mControl_FM_Ll.getMeasuredHeight()) {
                                showOrHideFMAnimate(true);
                            } else showOrHideControlAnimate(false);
                        }else {
                            showOrHideControlAnimate(false);
                        }
                    }
                }
                return false;
            }
        });
    }

    public void hideControlCenter() {
        mControlLl.setTranslationY(mControlLl.getMeasuredHeight() != 0 ? -mControlLl.getMeasuredHeight() : -500);
    }

    public void showOrHideVoice(boolean show) {
        mVoiceLl.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showOrHideFM(boolean show) {
        mControl_FM_Ll.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void showOrHideMenu(boolean show) {
        mMenuLl.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showOrHideStatusBar(boolean show) {
        mStatusRl.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean touchInWindowArea(float x, float y) {
        //控制中心全部显示
        if (controlStatus == CONTROL_VISIBLE) {
            Log.d(LOG_TAG, "controlStatus == CONTROL_VISIBLE");
            return true;
        }
        //控制中心部分显示
        if (controlStatus == CONTROL_FM_MUSIC) {
            Log.d(LOG_TAG, "controlStatus fm or music visible");
            if (x >= controlFmPosition[0] && x <= controlFmPosition[2] && y >= 0 && y <= (controlFmPosition[3] - controlFmPosition[1])) {
                Log.d(LOG_TAG, "in controlStatus fm or music area");
                return true;
            }
        }
        if (x >= voicePosition[0] && x <= voicePosition[2] && y >= voicePosition[1] && y <= voicePosition[3]) {
            Log.d(LOG_TAG, "in voice area");
            return true;
        }
        //是否在菜单区域
        if (mMenuLl.getVisibility() == View.VISIBLE) {
            Log.d(LOG_TAG, "menu is visible");
            if (x >= menuPosition[0] && x <= menuPosition[2] && y >= menuPosition[1] && y <= menuPosition[3]) {
                Log.d(LOG_TAG, "in menu area");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewRemove() {
        super.onViewRemove();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWindowContentChange(ControlWindowModel windowContent) {
        Log.d(LOG_TAG, "onWindowContentChange windowContent:" + windowContent.toString());
        switch (windowContent.getWindowContent()) {
            case MENU:
                showOrHideMenu(windowContent.isShow());
                break;
            case TEL:
                break;
            case VOICE:
                showOrHideVoice(windowContent.isShow());
                break;
            case FM_MUSIC:
                boolean show = windowContent.isShow();
                showOrHideFM(show);
                if (show) {
                    Log.d(LOG_TAG, "FM_MUSIC show controlStatus:" + controlStatus);
                    if (controlStatus == CONTROL_INVISIBLE) {
                        //下拉下来
                        showOrHideFMAnimate(true);
                        controlStatus = CONTROL_FM_MUSIC;
                    }
                } else {
                    Log.d(LOG_TAG, "FM_MUSIC hide controlStatus:" + controlStatus);
                    if (controlStatus == CONTROL_FM_MUSIC) {
                        showOrHideFMAnimate(false);
                        controlStatus = CONTROL_INVISIBLE;
                    }
                }
                break;
            case CONTROL:
                break;
            case STATUS_BAR:
                showOrHideStatusBar(windowContent.isShow());
                break;
        }
    }

    /**
     * 下拉fm
     *
     * @param pull
     */
    private void showOrHideFMAnimate(boolean pull) {
        Log.d(LOG_TAG, "showOrHideFMAnimate,pull:" + pull);
        if (pull) {
            ObjectAnimator transYAnim = ObjectAnimator.ofFloat(mControlLl, "translationY", mControl_FM_Ll.getMeasuredHeight());
            transYAnim.setDuration(200);
            transYAnim.start();
        } else {
            ObjectAnimator transYAnim = ObjectAnimator.ofFloat(mControlLl, "translationY", 0);
            transYAnim.setDuration(200);
            transYAnim.start();
        }
    }

    private void showOrHideControlAnimate(boolean pull) {
        Log.d(LOG_TAG, "showOrHideControlAnimate,pull:" + pull);
        if (pull) {
            ObjectAnimator transYAnim = ObjectAnimator.ofFloat(mControlLl, "translationY", mControlLl.getMeasuredHeight());
            transYAnim.setDuration(200);
            transYAnim.start();
        } else {
            ObjectAnimator transYAnim = ObjectAnimator.ofFloat(mControlLl, "translationY", 0);
            transYAnim.setDuration(200);
            transYAnim.start();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doPowerChange(ArielCastPower arielCastPower) {
        //电池电量变化
        Log.d(LOG_TAG, "doPowerChange:arielCastPower:" + arielCastPower);
        mBatteryView.setPower(arielCastPower.getPower());
    }
}
