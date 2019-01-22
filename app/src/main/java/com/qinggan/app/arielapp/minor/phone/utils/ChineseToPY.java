package com.qinggan.app.arielapp.minor.phone.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import com.qinggan.app.arielapp.R;

public class ChineseToPY {

    public static StringBuffer sb = new StringBuffer();

    /**
     * get first PinYin letter
     */
    public static String getPinYinFirstLetter(String str) {
        sb.setLength(0);
        char c = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * get all String PinYin letter
     */
    public static String getAllPinYinFirstLetter(String chines) {

        sb.setLength(0);
        char[] chars = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < chars.length; i++ ){
            if(chars[i]>128) {
                try{
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(chars[i],defaultFormat)[0].charAt(0));
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public static int getPinYinImageResource(Boolean isBlue, String str) {
        switch (str.toLowerCase()) {
            case "a":
                return isBlue? R.drawable.phone_buttun_bule_a:R.drawable.phone_buttun_gray_a;
            case "b":
                return isBlue? R.drawable.phone_buttun_bule_b:R.drawable.phone_buttun_gray_b;
            case "c":
                return isBlue? R.drawable.phone_buttun_bule_c:R.drawable.phone_buttun_gray_c;
            case "d":
                return isBlue? R.drawable.phone_buttun_bule_d:R.drawable.phone_buttun_gray_d;
            case "e":
                return isBlue? R.drawable.phone_buttun_bule_e:R.drawable.phone_buttun_gray_e;
            case "f":
                return isBlue? R.drawable.phone_buttun_bule_f:R.drawable.phone_buttun_gray_f;
            case "g":
                return isBlue? R.drawable.phone_buttun_bule_g:R.drawable.phone_buttun_gray_g;
            case "h":
                return isBlue? R.drawable.phone_buttun_bule_h:R.drawable.phone_buttun_gray_h;
            case "i":
                return isBlue? R.drawable.phone_buttun_bule_i:R.drawable.phone_buttun_gray_i;
            case "j":
                return isBlue? R.drawable.phone_buttun_bule_j:R.drawable.phone_buttun_gray_j;
            case "k":
                return isBlue? R.drawable.phone_buttun_bule_k:R.drawable.phone_buttun_gray_k;
            case "l":
                return isBlue? R.drawable.phone_buttun_bule_l:R.drawable.phone_buttun_gray_l;
            case "m":
                return isBlue? R.drawable.phone_buttun_bule_m:R.drawable.phone_buttun_gray_m;
            case "n":
                return isBlue? R.drawable.phone_buttun_bule_n:R.drawable.phone_buttun_gray_n;
            case "o":
                return isBlue? R.drawable.phone_buttun_bule_o:R.drawable.phone_buttun_gray_o;
            case "p":
                return isBlue? R.drawable.phone_buttun_bule_p:R.drawable.phone_buttun_gray_p;
            case "q":
                return isBlue? R.drawable.phone_buttun_bule_q:R.drawable.phone_buttun_gray_q;
            case "r":
                return isBlue? R.drawable.phone_buttun_bule_r:R.drawable.phone_buttun_gray_r;
            case "s":
                return isBlue? R.drawable.phone_buttun_bule_s:R.drawable.phone_buttun_gray_s;
            case "t":
                return isBlue? R.drawable.phone_buttun_bule_t:R.drawable.phone_buttun_gray_t;
            case "u":
                return isBlue? R.drawable.phone_buttun_bule_u:R.drawable.phone_buttun_gray_u;
            case "v":
                return isBlue? R.drawable.phone_buttun_bule_v:R.drawable.phone_buttun_gray_v;
            case "w":
                return isBlue? R.drawable.phone_buttun_bule_w:R.drawable.phone_buttun_gray_w;
            case "x":
                return isBlue? R.drawable.phone_buttun_bule_x:R.drawable.phone_buttun_gray_x;
            case "y":
                return isBlue? R.drawable.phone_buttun_bule_y:R.drawable.phone_buttun_gray_y;
            case "z":
                return isBlue? R.drawable.phone_buttun_bule_z:R.drawable.phone_buttun_gray_z;
        }
        return isBlue? R.drawable.phone_buttun_bule_wen:R.drawable.phone_buttun_gray_wen;
    }
}
