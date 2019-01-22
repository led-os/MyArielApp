package com.qinggan.app.arielapp.minor.main.driving.helper;

public class DefaultItemTouchHelper extends YolandaItemTouchHelper {
    private DefaultItemTouchHelpCallback itemTouchHelpCallback;
    public DefaultItemTouchHelper(DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        super(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        itemTouchHelpCallback = (DefaultItemTouchHelpCallback) getCallback();
    } /**
 * 设置是否可以被拖拽
 *
 * @param canDrag 是true，否false
 */ public void setDragEnable(boolean canDrag) { itemTouchHelpCallback.setDragEnable(canDrag); }
 /**
 * 设置是否可以被滑动
 *
 * @param canSwipe 是true，否false
 */ public void setSwipeEnable(boolean canSwipe) { itemTouchHelpCallback.setSwipeEnable(canSwipe); } }
