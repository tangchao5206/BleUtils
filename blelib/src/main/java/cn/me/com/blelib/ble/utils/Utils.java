package cn.me.com.blelib.ble.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.DhcpInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:44
 * 修改时间：
 * 修改备注：
 */
public class Utils {

    private static final String TAG = "Utils";

    //版本名称，显示用
    private String versionName = "";
    //版本号，升级检查用
    private int versionCode = 0;

    private static Utils instance = null;

    private Utils(){}

    public static Utils getInstance() {
        if(instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    /**
     * 获取应用版本名称
     * @param ctx
     * @return
     */
    public String getVersionName(Context ctx) {
        if(versionName.equals("")) {
            getVersionInfo(ctx);
        }
        ULog.d(TAG, "getVersionName : " + versionName);
        return versionName;
    }

    /**
     * 获取应用版本号
     * @param ctx
     * @return
     */
    public int getVersionCode(Context ctx) {
        if(versionCode == 0) {
            getVersionCode(ctx);
        }
        ULog.d(TAG, "versionCode : " + versionCode);
        return versionCode;
    }

    /**
     * 获取应用版本信息
     * @param ctx
     */
    private void getVersionInfo(Context ctx) {
        try {
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo pdkInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            versionName = pdkInfo.versionName;
            versionCode = pdkInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            ULog.e(TAG, "getVersionInfo error! " + e.getMessage());
        }
    }

    /**
     * 检查ip是否连接的通
     * @param ip 目标ip地址
     * @return 目标ip地址是否连接通畅
     */
    public boolean isIpReachable(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            if (addr.isReachable(3000)) {
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            ULog.e(TAG, "getLoacalBitmap error : " + e.getMessage());
            return null;
        }
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    public Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                    int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        ULog.d(TAG, "videopath is : " + videoPath);
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        //		System.out.println("w"+bitmap.getWidth());
        //		System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    public Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Cursor cursor = cr.query(uri,new String[] { MediaStore.Video.Media._ID }, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));  //image id in image table.s

        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, MediaStore.Images.Thumbnails.MICRO_KIND, options);

        return bitmap;
    }

    /**
     * 通过文件名 获取视频的缩略图
     *
     * @param context
     * @param cr	 cr = getContentResolver();
     * @param testVideopath  全路径 "/mnt/sdcard/sidamingbu.mp4";
     * @return
     */
    public Bitmap getVideoThumbnail(Context context, String testVideopath) {
        // final String testVideopath = "/mnt/sdcard/sidamingbu.mp4";
        ContentResolver testcr = context.getContentResolver();
        String[] projection = { MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, };
        String whereClause = MediaStore.Video.Media.DATA + " = '" + testVideopath + "'";
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String videoPath = "";
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {

            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);

            do {
                _id = cursor.getInt(_idColumn);
                videoPath = cursor.getString(_dataColumn);
                System.out.println(_id + " " + videoPath);
            } while (cursor.moveToNext());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(testcr, _id, MediaStore.Images.Thumbnails.MICRO_KIND,
                options);
        return bitmap;
    }


    /**
     * 获取目录大小
     * @param file
     * @return
     */
    public double getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“kb”为单位
                double size = (double) file.length() / 1024;
                return size;
            }
        } else {
            ULog.e(TAG, "getDirSize error:" + file.getAbsolutePath() + " is not found.");
            return 0.0;
        }
    }

    /**
     * 获取应用程序接收总字节数，包括mobile 和 wifi
     * @return
     */
    int appUid = -1;
    public long getUidRxBytes(Context ctx) {
        if(appUid == -1) {
            try {
                appUid = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES).uid;
            } catch (PackageManager.NameNotFoundException e1) {
                ULog.e(TAG, "get application uid error: " + e1.getMessage());
            }
        }
        return TrafficStats.getUidRxBytes(appUid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
    }

    /**
     * 删除目录以及目录下的所有子目录及文件
     * @param dir	目录
     * @return		是否删除成功
     */
    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        if(dir.isFile()) {
            return dir.delete();
        }
        return true;
    }

    /**
     * 删除指定文件
     * @param file
     * @return
     */
    public boolean deleteFile(String file) {
        try {
            File delFile = new File(file);
            if(delFile.exists()) {
                return delFile.delete();
            }
        } catch (Exception e) {
            ULog.e(TAG, "delete File:" + file + "error, " + e.getMessage());
        }
        return false;
    }

    // 网关获取
    public String getGateWay(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        // dhcpInfo获取的是最后一次成功的相关信息，包括网关、ip等
        //return "dh_ip:" + FormatIP(dhcpInfo.ipAddress) + "\n" + "dh_gateway"+ FormatIP(dhcpInfo.gateway);
        //return FormatIP(dhcpInfo.gateway);
        return long2ip(dhcpInfo.gateway);
    }

    // IP地址转化为字符串格式
    @SuppressWarnings("deprecation")
    public String FormatIP(int IpAddress) {
        return Formatter.formatIpAddress(IpAddress);
    }

    private String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>24)&0xff)));
        return sb.toString();
    }

    /**
     * md5加密
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取HttpUtils对象
     */

  /*  HttpUtils httpUtils = null;
    int HTTP_CONNECT_TIMEOUT = 8000;

    private void initHttpUtils() {
        if(httpUtils == null) {
            httpUtils = new HttpUtils();
        }
    }
    public HttpUtils getHttpUtils() {
        initHttpUtils();
        httpUtils.configTimeout(HTTP_CONNECT_TIMEOUT);
        return httpUtils;
    }
    public HttpUtils getHttpUtils(int timeout) {
        initHttpUtils();
        httpUtils.configTimeout(timeout);
        return httpUtils;
    }*/
}
