package com.qinggan.app.arielapp.utils;

import java.text.DecimalFormat;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-3]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ByteUtil {

    /**
     * 字节转
     *
     * @param byteSize
     * @return
     */
    public static String getSize(int byteSize) {
        //获取到的size为：1705230
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (byteSize / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(byteSize / (float) GB) + "G";
        } else if (byteSize / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(byteSize / (float) MB) + "M";
        } else if (byteSize / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(byteSize / (float) KB) + "K";
        } else {
            resultSize = byteSize + "B";
        }
        return resultSize;
    }
}
