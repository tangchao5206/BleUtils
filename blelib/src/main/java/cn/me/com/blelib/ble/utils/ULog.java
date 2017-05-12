package cn.me.com.blelib.ble.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:44
 * 修改时间：
 * 修改备注：
 */
public class ULog {

    /** 正式上线是设为false */
    private static boolean testMode = true;

    public static void v(String tag, String msg) {
        if (testMode) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (testMode) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (testMode) {
            Log.i(tag, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (testMode) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (testMode) {
            Log.e(tag, msg);
        }
    }

    public static void showToast(Context context, String str) {
        if (str != null) {

            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}
