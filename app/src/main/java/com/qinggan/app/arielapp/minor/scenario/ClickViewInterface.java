package com.qinggan.app.arielapp.minor.scenario;

import android.view.View;

/**
 * Created by fan on 2017/11/6.
 */

public interface ClickViewInterface {

    void OnClickPositionListener(View view, int... position);
    void OnClickContentListener(View view, String... content);


}
