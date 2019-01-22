package com.qinggan.app.arielapp.minor.main.navigation;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.utils.CoordinateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dingqb on 2018/12/11.
 */
public class StreetUrl extends AsyncTask<Integer, Integer, Boolean> {
    private String panoUrl = "https://api.map.baidu.com/panorama/v2?";
    private String mStreetId;
    private String mName;
    private int mLon;
    private int mLat;

    private StreetCallback mCallBack;

    public interface StreetCallback {
        void onStreetUrl(String street_id, String url);
    }

    public StreetUrl(String name, int lon, int lat, StreetCallback callback) {
        mName = name;
        mLon = lon;
        mLat = lat;
        mCallBack = callback;
    }

    public StreetUrl(String streetId, StreetCallback callback) {
        mStreetId = streetId;
        mCallBack = callback;
    }

    @Override
    protected Boolean doInBackground(Integer... arg0) {
        Thread.currentThread().setName("FetchStreetUrlAsyncTask");
        if (!TextUtils.isEmpty(mStreetId)) {
            return true;
        }

        long t = SystemClock.uptimeMillis();
        String poi_url = "https://api.map.baidu.com/place/v2/search?query=" + mName
                + "&page_size=1&location="
                + String.valueOf(mLat / 1e5f) + "," + String.valueOf(mLon / 1e5f)
                + "&coord_type=2&radius=100&output=json&ak=sbH2GesVaHO9eRXDefjfdecAtURbmO33";
        try {
            OkHttpClient client = HttpClient.getOkHttpClient();
            Request request = new Request.Builder()
                    .url(poi_url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("results")) {
                    JSONArray jsonArray = jsonObject.optJSONArray("results");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject jsonPoi = (JSONObject) jsonArray.get(0);
                        if (jsonPoi != null && jsonPoi.has("uid")) {
                            mStreetId = jsonPoi.optString("uid");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("xiaohf",
                "FetchStreetUrlAsyncTask cost " + (SystemClock.uptimeMillis() - t) + "ms");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        String url;
        if (mStreetId != null) {
            url = panoUrl + "width=640&height=300&poiid="
                    + mStreetId + "&fov=180&ak=sbH2GesVaHO9eRXDefjfdecAtURbmO33";
//                url = "https://mapsv0.bdimg"
//                        + ".com/?qt=pr3dpoi&width=600&height=400&watermark=&fovy=60&quality=80"
//                        + "&uid=" + mStreetId;
        } else {
            double[] bd09 = CoordinateUtil.gcj02tobd09(mLon / 1e5f, mLat / 1e5f);
            String loaction = String.valueOf(bd09[0]) + "," + String.valueOf(bd09[1]);
            url =panoUrl + "width=640&height=300&location="
                    + loaction + "&fov=180&ak=sbH2GesVaHO9eRXDefjfdecAtURbmO33";
        }

        if (mCallBack != null) {
            mCallBack.onStreetUrl(mStreetId, url);
        }
    }
}
