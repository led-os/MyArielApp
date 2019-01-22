package com.qinggan.app.cast.window.view.model;

import com.qinggan.app.cast.window.view.WindowContent;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-18]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ControlWindowModel {

    WindowContent windowContent;
    boolean show;
    BaseControlModel model;

    public ControlWindowModel(WindowContent windowContent, boolean show) {
        this.windowContent = windowContent;
        this.show = show;
    }

    public ControlWindowModel(WindowContent windowContent, boolean show, BaseControlModel model) {
        this.windowContent = windowContent;
        this.show = show;
        this.model = model;
    }

    public BaseControlModel getModel() {
        return model;
    }

    public void setModel(BaseControlModel model) {
        this.model = model;
    }

    public WindowContent getWindowContent() {
        return windowContent;
    }

    public void setWindowContent(WindowContent windowContent) {
        this.windowContent = windowContent;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "ControlWindowModel:windowContent:" + windowContent + ",show:" + show;
    }
}
