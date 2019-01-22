package com.qinggan.app.arielapp.ui.widget.code;

/**
 * <验证码输入监听>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-28]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface InputCompleteListener {
    void inputComplete(String content);

    void deleteContent();
}
