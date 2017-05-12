package cn.me.com.blelib.ble.utils;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:43
 * 修改时间：
 * 修改备注：
 */
public class DensityUtil {

    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context activity) {
        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context activity) {
        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
