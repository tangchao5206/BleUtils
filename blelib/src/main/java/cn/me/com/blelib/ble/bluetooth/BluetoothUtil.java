package cn.me.com.blelib.ble.bluetooth;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:38
 * 修改时间：
 * 修改备注：
 */
public class BluetoothUtil {

    //初始化监听消息返回结果
    public static final int START_DEVICE_SUCCESS      = 1;
    public static final int START_DEVICE_UNSUPPORTBLE = -1;
    public static final int START_DEVICE_UNSUPPORTBT  = -2;
    public static final int START_DEVICE_VERSON  = -3;
    public static final int START_DEVICE_ERROR        = -4;


    //设备类型(按顺序填写)
    public final static int DEVICETYPE_XUEYANGYI    = 0;
    public final static int DEVICETYPE_TIZHONGCHENG = 1;



    //设备标识((按上面设备类型顺序填写))
    public final static String DEVICENAMETAGS_XUEYANGYI    = "iChoice"; //血氧蓝牙设备名称
    public final static String DEVICENAMETAGS_TIZHONGCHENG = "eBody-Scale";


    //设备Service uuid(按上面设备类型顺序填写)
    public final static String[] DEVICE_SERVICE_UUID = {
            "ba11f08c-5f14-0b0d-1080-007cbe2f87ba",
            "0000fff0-0000-1000-8000-00805f9b34fb",
            ""
    };

    //设备通知uuid(按上面设备类型顺序填写)
    public final static String[][] NOTIFY_CHARACTERISTICSUUID = {
            {
                    "0000cd01-0000-1000-8000-00805f9b34fb",
                    "0000cd02-0000-1000-8000-00805f9b34fb",
                    "0000cd03-0000-1000-8000-00805f9b34fb",
                    "0000cd04-0000-1000-8000-00805f9b34fb"
            },
            {"0000ffe1-0000-1000-8000-00805f9b34fb"},

    };

    //设备写数据uuid (按上面设备类型顺序填写)
    public final static String[] WRITE_CHARACTERISTICSUUID = {
            "0000cd20-0000-1000-8000-00805f9b34fb",
            "0000fff3-0000-1000-8000-00805f9b34fb",
            ""
    };

    //
    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * 根据设备名称查询设备类型
     * @param deviceName
     * @return
     */
    public static int getDeviceTypeByDeviceName(String deviceName) {
        String[] deviceNames = {
                DEVICENAMETAGS_TIZHONGCHENG,
                DEVICENAMETAGS_XUEYANGYI

        };
        if (deviceName!=null){
            deviceName = deviceName.toLowerCase();
            for(int i = 0; i < deviceNames.length; i++) {
                if(deviceNames[i].toLowerCase().indexOf(deviceName) > -1) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * 返回设备通知uuid
     * @param deviceType
     * @return
     */
    public static String[] getNotifyUUIDsByDeviceType(int deviceType) {
        return NOTIFY_CHARACTERISTICSUUID[deviceType];
    }

    /**
     * 返回设备数据写入uuid
     * @param deviceType
     * @return
     */
    public static String getWriteUUIDByDeviceType(int deviceType) {
        return WRITE_CHARACTERISTICSUUID[deviceType];
    }

    /**
     * 返回设备service uuid
     * @param deviceType
     * @return
     */
    public static String getDeviceServiceUUID(int deviceType) {
        return DEVICE_SERVICE_UUID[deviceType];
    }
}
