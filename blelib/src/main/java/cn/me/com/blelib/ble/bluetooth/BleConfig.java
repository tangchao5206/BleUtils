package cn.me.com.blelib.ble.bluetooth;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:38
 * 修改时间：
 * 修改备注：
 */
public abstract class BleConfig {

    //初始化监听消息返回结果
    public static final int START_DEVICE_SUCCESS      = 1;
    public static final int START_DEVICE_UNSUPPORTBLE = -1;
    public static final int START_DEVICE_UNSUPPORTBT  = -2;
    public static final int START_DEVICE_VERSON  = -3;
    public static final int START_DEVICE_ERROR        = -4;

    //
    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

   /* *//**
     * 根据设备名称查询设备类型
     * @param deviceName
     * @return
     *//*
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
*/

    /**
     * 返回设备通知uuid
     * @param deviceType
     * @return
     */
    public abstract String[] getNotifyUUIDsByDeviceType(int deviceType);

    /**
     * 返回设备数据写入uuid
     * @param deviceType
     * @return
     */
    public abstract String getWriteUUIDByDeviceType(int deviceType) ;


    /**
     * 返回设备service uuid
     * @param deviceType
     * @return
     */
    public abstract String getDeviceServiceUUID(int deviceType) ;

}
