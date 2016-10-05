package com.iblogstreet.batteryshow.utils;

import android.util.Log;

/**
 * 项目名称：CJPADT
 * 类描述：
 * 创建人：Gent Liu
 * 创建时间：2016/8/12 9:53
 * 修改人：Gent Liu
 * 修改时间：2016/8/12 9:53
 * 修改备注：
 */
public class Loger {
    public static final boolean SHOW_LOG_INFO = true;

    public static void e(String tag, String log)
    {
        if (SHOW_LOG_INFO) Log.e(tag, log);
    }

    public static void i(String tag, String log)
    {
        if (SHOW_LOG_INFO) Log.i(tag, log);
    }

    public static void d(String tag, String log)
    {
        if (SHOW_LOG_INFO) Log.d(tag, log);
    }

    public static void v(String tag, String log)
    {
        if (SHOW_LOG_INFO) Log.v(tag, log);
    }

    public static void w(String tag, String log)
    {
        if (SHOW_LOG_INFO) Log.w(tag, log);
    }


    public static void  printHexString(String tag,  byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuilder.append(hex);
        }
        e(tag, stringBuilder.toString());
    }
}

