package com.qinggan.app.arielapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <格式校验>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class FormatUtil {

    /**
     * 是否是手机格式 1开头,长度11位数字结尾
     *
     * @param mobile
     * @return
     */
    public static boolean isPhone(String mobile) {
        String regex = "^1\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(mobile);
        return m.matches();
    }

    /**
     * 是否是合法身份证
     *
     * @param cardId 15位数字或18位全数字或者17位数字+一个字母
     * @return
     */
    public static boolean isCardId(String cardId) {
        String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(cardId);
        return m.matches();
    }

}
