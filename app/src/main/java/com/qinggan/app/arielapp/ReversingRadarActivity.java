package com.qinggan.app.arielapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qinggan.qinglink.bean.RadarInfo;



public class ReversingRadarActivity extends BaseActivity {
    //    /**
//     * 传感器无故障
//     */
//    public static int NO_ERROR = 0 ;
//    /**
//     * 传感器有故障
//     */
//    public static int ERROR = 1;
//
//    /**
//     * 未发现障碍物
//     */
//    public static int NO_OBSTACLE_DETECTED = 0 ;
//    /**
//     * 障碍物距离小于40cm
//     */
//    public static int DISTANCE_OF_RRM_IS_LESS_THAN_40CM = 1;
//    /**
//     * 障碍物距离在40cm-100cm之间
//     */
//    public static int DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM = 2;
//    /**
//     * 障碍物距离在100cm-150cm之间
//     */
//    public static int DISTANCE_RANGE_OF_RRM_IS_FROM_100CM_TO_150CM = 3;

    private int mLeftFrontSersorErrorFlag;
    private int mRightFrontSersorErrorFlag;
    private int mLeftRearSersorErrorFlag;
    private int mRightRearSersorErrorFlag;
    private int mLeftRearMiddleSersorErrorFlag;
    private int mRightRearMiddleSersorErrorFlag;

    //
    private int mLeftFrontSersorDistance;
    private int mRightFrontSersorDistance;

    private int mLeftRearSersorDistance;
    private int mRightRearSersorDistance;

    private int mLeftRearMiddleSersorDistance;
    private int mRightMiddleRearSersorDistance;

    private LocalBroadcastManager localBroadcastManager;

    private final static String TAG = "ReversingRadarActivity";
    private ImageView leftFront;
    private ImageView rightFront;

    private ImageView leftRearSersor;
    private ImageView rightRearSersor;

    private ImageView leftMiddle;
    private ImageView rightMiddle;

    private RadarInfo mRadarInfo;
    View view;

    @Override
    protected void initView() {
        try{
            getSwipeBackLayout().setEnableGesture(true);
            initWm();
            leftFront = (ImageView)view.findViewById(R.id.left_front);
            leftMiddle = (ImageView)view.findViewById(R.id.left_middle);
            leftRearSersor = (ImageView)view.findViewById(R.id.left_rear_sersor);

            rightFront = (ImageView)view.findViewById(R.id.right_front);
            rightMiddle = (ImageView)view.findViewById(R.id.right_middle);
            rightRearSersor = (ImageView)view.findViewById(R.id.right_rear_sersor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initWm(){
        try{
            //创建View
            view = LayoutInflater.from(mContext).inflate(R.layout.reversing_radar, null);
            LinearLayout layout = (LinearLayout)findViewById(R.id.reversing_layout);
            layout.addView(view);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void initListener() {

    }

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;

    @Override
    protected void initData() {
        try{
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            intentFilter = new IntentFilter();
            intentFilter.addAction("com.qinggan.app.arielapp.radar_close");
            localReceiver=new LocalReceiver();

            localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            mRadarInfo = (RadarInfo)getIntent().getSerializableExtra("radarInfo");
            showRadarInfo();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            removeViewAndDestroy();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.reversing;
    }


    @Override
    public void onBackPressed() {
        removeViewAndDestroy();
    }

    private void removeViewAndDestroy(){
        moveTaskToBack(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mRadarInfo = (RadarInfo)intent.getSerializableExtra("radarInfo");
        showRadarInfo();
    }

    private void showRadarInfo(){
        initRadarStatus();
        showLr();
        showRr();
        showLs();
        showRs();
        showLm();
        showRm();
    }


    private void initRadarStatus(){
        try{
            mLeftFrontSersorDistance = mRadarInfo.getLeftFrontSersorDistance();
            mRightFrontSersorDistance = mRadarInfo.getRightFrontSersorDistance();

            mLeftRearSersorDistance = mRadarInfo.getLeftRearSersorDistance();
            mRightRearSersorDistance = mRadarInfo.getRightRearSersorDistance();

            mLeftRearMiddleSersorDistance = mRadarInfo.getLeftRearMiddleSersorDistance();
            mRightMiddleRearSersorDistance = mRadarInfo.getRightMiddleRearSersorDistance();


            mLeftFrontSersorErrorFlag = mRadarInfo.getLeftFrontSersorErrorFlag();
            mRightFrontSersorErrorFlag = mRadarInfo.getRightFrontSersorErrorFlag();
            mLeftRearSersorErrorFlag = mRadarInfo.getLeftRearSersorErrorFlag();
            mRightRearSersorErrorFlag = mRadarInfo.getRightRearSersorErrorFlag();
            mLeftRearMiddleSersorErrorFlag = mRadarInfo.getLeftRearMiddleSersorErrorFlag();
            mRightRearMiddleSersorErrorFlag = mRadarInfo.getRightRearMiddleSersorErrorFlag();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showLr(){
        try {
            if(mLeftFrontSersorErrorFlag == RadarInfo.NO_ERROR){
                if(mLeftFrontSersorDistance == RadarInfo.NO_OBSTACLE_DETECTED){
                    leftFront.setImageResource(R.drawable.left_front0);
                }else if (mLeftFrontSersorDistance == RadarInfo.DISTANCE_OF_RRM_IS_LESS_THAN_40CM){
                    leftFront.setImageResource(R.drawable.left_front2);
                }else if(mLeftFrontSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM){
                    leftFront.setImageResource(R.drawable.left_front1);
                }
            }else if(mLeftFrontSersorErrorFlag == RadarInfo.ERROR){
                leftFront.setImageResource(R.drawable.left_front_error);
            }else{
                leftFront.setImageResource(R.drawable.left_front_unknow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void showRr(){
        try {
            if(mRightFrontSersorErrorFlag == RadarInfo.NO_ERROR){
                if(mRightFrontSersorDistance == RadarInfo.NO_OBSTACLE_DETECTED){
                    rightFront.setImageResource(R.drawable.right_front0);
                }else if (mRightFrontSersorDistance == RadarInfo.DISTANCE_OF_RRM_IS_LESS_THAN_40CM){
                    rightFront.setImageResource(R.drawable.right_front2);
                }else if(mRightFrontSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM){
                    rightFront.setImageResource(R.drawable.right_front1);
                }
            }else if(mRightFrontSersorErrorFlag == RadarInfo.ERROR){
                rightFront.setImageResource(R.drawable.right_front_error);
            }else{
                rightFront.setImageResource(R.drawable.right_front_unknow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showLs(){
        try {
            if(mLeftRearSersorErrorFlag == RadarInfo.NO_ERROR){
                if(mLeftRearSersorDistance == RadarInfo.NO_OBSTACLE_DETECTED){
                    leftRearSersor.setImageResource(R.drawable.left_rear_sersor0);
                }else if (mLeftRearSersorDistance == RadarInfo.DISTANCE_OF_RRM_IS_LESS_THAN_40CM){
                    leftRearSersor.setImageResource(R.drawable.left_rear_sersor2);
                }else if(mLeftRearSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM){
                    leftRearSersor.setImageResource(R.drawable.left_rear_sersor1);
                }
            }else if(mLeftRearSersorErrorFlag == RadarInfo.ERROR){
                leftRearSersor.setImageResource(R.drawable.left_rear_error);
            }else{
                leftRearSersor.setImageResource(R.drawable.left_rear_unknow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showRs(){
        try{
            if(mRightRearSersorErrorFlag == RadarInfo.NO_ERROR){
                if(mRightRearSersorDistance == RadarInfo.NO_OBSTACLE_DETECTED){
                    rightRearSersor.setImageResource(R.drawable.right_rear_sersor0);
                }else if (mRightRearSersorDistance == RadarInfo.DISTANCE_OF_RRM_IS_LESS_THAN_40CM){
                    rightRearSersor.setImageResource(R.drawable.right_rear_sersor2);
                }else if(mRightRearSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM){
                    rightRearSersor.setImageResource(R.drawable.right_rear_sersor1);
                }
            }else if(mRightRearSersorErrorFlag == RadarInfo.ERROR){
                rightRearSersor.setImageResource(R.drawable.right_rear_error);
            }else{
                rightRearSersor.setImageResource(R.drawable.right_rear_unknow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showLm(){
        try {
            if(mLeftRearMiddleSersorErrorFlag == RadarInfo.NO_ERROR){
                if(mLeftRearMiddleSersorDistance == RadarInfo.NO_OBSTACLE_DETECTED){
                    leftMiddle.setImageResource(R.drawable.left_middle0);
                }else if (mLeftRearMiddleSersorDistance == RadarInfo.DISTANCE_OF_RRM_IS_LESS_THAN_40CM){
                    leftMiddle.setImageResource(R.drawable.left_middle3);
                }else if(mLeftRearMiddleSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM){
                    leftMiddle.setImageResource(R.drawable.left_middle2);
                }else if(mLeftRearMiddleSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_100CM_TO_150CM){
                    leftMiddle.setImageResource(R.drawable.left_middle1);
                }
            }else if(mLeftRearMiddleSersorErrorFlag == RadarInfo.ERROR){
                leftMiddle.setImageResource(R.drawable.left_rear_middle_error);
            }else{
                leftMiddle.setImageResource(R.drawable.left_rear_middle_unknow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showRm(){
        try {
            if(mRightRearMiddleSersorErrorFlag == RadarInfo.NO_ERROR){
                if(mRightMiddleRearSersorDistance == RadarInfo.NO_OBSTACLE_DETECTED){
                    rightMiddle.setImageResource(R.drawable.right_middle0);
                }else if (mRightMiddleRearSersorDistance == RadarInfo.DISTANCE_OF_RRM_IS_LESS_THAN_40CM){
                    rightMiddle.setImageResource(R.drawable.right_middle3);
                }else if(mRightMiddleRearSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_40CM_TO_100CM){
                    rightMiddle.setImageResource(R.drawable.right_middle2);
                }else if(mRightMiddleRearSersorDistance == RadarInfo.DISTANCE_RANGE_OF_RRM_IS_FROM_100CM_TO_150CM){
                    rightMiddle.setImageResource(R.drawable.right_middle1);
                }
            }else if(mRightRearMiddleSersorErrorFlag == RadarInfo.ERROR){
                rightMiddle.setImageResource(R.drawable.right_middle_rear_error);
            }else{
                rightMiddle.setImageResource(R.drawable.right_middle_rear_unknow);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            localBroadcastManager.unregisterReceiver(localReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
