package com.qinggan.app.arielapp.minor.main.navigation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 2018/12/21. 显示poi详情界面的地图
 */
public class NaviMapViewLine {
    TextureMapView mMapView;
    BaiduMap mBaidumap = null;
    DistanceTimeCallBack mCallBack;
    // 设置起终点信息，
    PlanNode stNode;
    PlanNode enNode;
    private UiSettings mUiSettings;
    public void initMapView(TextureMapView mMapView, LatLng start, LatLng end, DistanceTimeCallBack callBack) {
        this.mMapView = mMapView;
        stNode = PlanNode.withLocation(start);// 39.9152108931,116.4039006839
        enNode = PlanNode.withLocation(end);// 39.9301757891,116.4402815578
        this.mCallBack = callBack;
        mBaidumap = mMapView.getMap();
        mUiSettings = mBaidumap.getUiSettings();
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        //地图上比例尺
        mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);
        //控制缩放
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setOverlookingGesturesEnabled(false);
        mMapView.setEnabled(false);
        initMapLine();
    }

    /**
     * 发起路线规划搜索示例
     */
    private void initMapLine() {
        // 重置浏览节点的路线数据
        mBaidumap.clear();
        // 实际使用中请对起点终点城市进行正确的设定
        // 初始化搜索模块，注册事件监听
        // 搜索相关
        RoutePlanSearch mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    if (result.getRouteLines().size() >= 1) {
                        // 数据回调
                        mCallBack.getRouteLineData(result.getRouteLines().get(0));
                        // 显示导航路线-默认第一条的数据
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
                        mBaidumap.setOnMarkerClickListener(overlay);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    } else {
                        Log.d("route result", "结果数<0");
                        return;
                    }

                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        // 设置开启路况相关信息
        drivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
        mSearch.drivingSearch((drivingRoutePlanOption).from(stNode).to(enNode));
    }
    // 释放资源
    public void release() {
        if (mMapView != null) {
//            mMapView.onDestroy();
        }
    }

    interface DistanceTimeCallBack {
        void getRouteLineData(RouteLine data);
    }

    /**
     * 用于显示一条驾车路线的overlay，自3.4.0版本起可实例化多个添加在地图中显示，当数据中包含路况数据时，则默认使用路况纹理分段绘制
     */
    public class DrivingRouteOverlay extends OverlayManager {

        private DrivingRouteLine mRouteLine = null;
        boolean focus = false;

        /**
         * 构造函数
         *
         * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
         */
        public DrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public final List<OverlayOptions> getOverlayOptions() {
            if (mRouteLine == null) {
                return null;
            }

            List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
            // step node
            if (mRouteLine.getAllStep() != null
                    && mRouteLine.getAllStep().size() > 0) {

                for (DrivingRouteLine.DrivingStep step : mRouteLine.getAllStep()) {
                    Bundle b = new Bundle();
                    b.putInt("index", mRouteLine.getAllStep().indexOf(step));

                    // 去掉航线上的方向箭头图标
//                    if (step.getEntrance() != null) {
//                        overlayOptionses.add((new MarkerOptions())
//                                .position(step.getEntrance().getLocation())
//                                .anchor(0.5f, 0.5f)
//                                .zIndex(10)
//                                .rotate((360 - step.getDirection()))
//                                .extraInfo(b)
//                                .icon(BitmapDescriptorFactory
//                                        .fromAssetWithDpi("Icon_line_node.png")));
//                    }
                    // 最后路段绘制出口点
                    if (mRouteLine.getAllStep().indexOf(step) == (mRouteLine
                                                                          .getAllStep().size() - 1) && step.getExit() != null) {
                        overlayOptionses.add((new MarkerOptions())
                                .position(step.getExit().getLocation())
                                .anchor(0.5f, 0.5f)
                                .zIndex(10)
                                .icon(BitmapDescriptorFactory
                                        .fromAssetWithDpi("Icon_line_node.png")));

                    }
                }
            }

            if (mRouteLine.getStarting() != null) {
                overlayOptionses.add((new MarkerOptions())
                        .position(mRouteLine.getStarting().getLocation())
                        .icon(getStartMarker() != null ? getStartMarker() :
                                BitmapDescriptorFactory
                                        .fromAssetWithDpi("Icon_start.png")).zIndex(10));
            }
            if (mRouteLine.getTerminal() != null) {
                overlayOptionses
                        .add((new MarkerOptions())
                                .position(mRouteLine.getTerminal().getLocation())
                                .icon(getTerminalMarker() != null ? getTerminalMarker() :
                                        BitmapDescriptorFactory
                                                .fromAssetWithDpi("Icon_end.png"))
                                .zIndex(10));
            }
            // poly line
            if (mRouteLine.getAllStep() != null
                    && mRouteLine.getAllStep().size() > 0) {

                List<DrivingRouteLine.DrivingStep> steps = mRouteLine.getAllStep();
                int stepNum = steps.size();

                List<LatLng> points = new ArrayList<LatLng>();
                ArrayList<Integer> traffics = new ArrayList<Integer>();
                int totalTraffic = 0;
                for (int i = 0; i < stepNum; i++) {
                    if (i == stepNum - 1) {
                        points.addAll(steps.get(i).getWayPoints());
                    } else {
                        points.addAll(steps.get(i).getWayPoints().subList(0, steps.get(i).getWayPoints().size() - 1));
                    }

                    totalTraffic += steps.get(i).getWayPoints().size() - 1;
                    if (steps.get(i).getTrafficList() != null && steps.get(i).getTrafficList().length > 0) {
                        for (int j = 0; j < steps.get(i).getTrafficList().length; j++) {
                            traffics.add(steps.get(i).getTrafficList()[j]);
                        }
                    }
                }

//            Bundle indexList = new Bundle();
//            if (traffics.size() > 0) {
//                int raffic[] = new int[traffics.size()];
//                int index = 0;
//                for (Integer tempTraff : traffics) {
//                    raffic[index] = tempTraff.intValue();
//                    index++;
//                }
//                indexList.putIntArray("indexs", raffic);
//            }
                boolean isDotLine = false;

                if (traffics != null && traffics.size() > 0) {
                    isDotLine = true;
                }
                PolylineOptions option = new PolylineOptions().points(points).textureIndex(traffics)
                        .width(20).dottedLine(isDotLine).focus(true)
                        .color(getLineColor() != 0 ? getLineColor() : Color.argb(178, 0, 78, 255)).zIndex(0);
                if (isDotLine) {
                    option.customTextureList(getCustomTextureList());
                }
                overlayOptionses.add(option);
            }
            return overlayOptionses;
        }

        /**
         * 设置路线数据
         *
         * @param routeLine 路线数据
         */
        public void setData(DrivingRouteLine routeLine) {
            this.mRouteLine = routeLine;
        }

        /**
         * 覆写此方法以改变默认起点图标
         *
         * @return 起点图标        */
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        /**
         * 覆写此方法以改变默认绘制颜色
         *
         * @return 线颜色
         */
        public int getLineColor() {
            return 0;
        }

        public List<BitmapDescriptor> getCustomTextureList() {
            ArrayList<BitmapDescriptor> list = new ArrayList<BitmapDescriptor>();
            list.add(BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png"));
            list.add(BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png"));
            list.add(BitmapDescriptorFactory.fromAsset("Icon_road_yellow_arrow.png"));
            list.add(BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png"));
            list.add(BitmapDescriptorFactory.fromAsset("Icon_road_nofocus.png"));
            return list;
        }

        /**
         * 覆写此方法以改变默认终点图标
         *
         * @return 终点图标
         */
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }

        /**
         * 覆写此方法以改变默认点击处理
         *
         * @param i 线路节点的 index
         *
         * @return 是否处理了该点击事件
         */
        public boolean onRouteNodeClick(int i) {
            if (mRouteLine.getAllStep() != null
                    && mRouteLine.getAllStep().get(i) != null) {
                Log.i("baidumapsdk", "DrivingRouteOverlay onRouteNodeClick");
            }
            return false;
        }

        @Override
        public final boolean onMarkerClick(Marker marker) {
            for (Overlay mMarker : mOverlayList) {
                if (mMarker instanceof Marker && mMarker.equals(marker)) {
                    if (marker.getExtraInfo() != null) {
                        onRouteNodeClick(marker.getExtraInfo().getInt("index"));
                    }
                }
            }
            return true;
        }

        @Override
        public boolean onPolylineClick(Polyline polyline) {
            boolean flag = false;
            for (Overlay mPolyline : mOverlayList) {
                if (mPolyline instanceof Polyline && mPolyline.equals(polyline)) {
                    // 选中
                    flag = true;
                    break;
                }
            }
            setFocus(flag);
            return true;
        }

        public void setFocus(boolean flag) {
            focus = flag;
            for (Overlay mPolyline : mOverlayList) {
                if (mPolyline instanceof Polyline) {
                    // 选中
                    ((Polyline) mPolyline).setFocus(flag);

                    break;
                }
            }

        }

    }

}
