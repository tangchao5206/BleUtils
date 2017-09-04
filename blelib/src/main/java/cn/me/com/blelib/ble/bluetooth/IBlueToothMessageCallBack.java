package cn.me.com.blelib.ble.bluetooth;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:40
 * 修改时间：
 * 修改备注：
 */
public interface IBlueToothMessageCallBack {



    /**
     * 断开连接
     * @return
     */
    void onDisConnected();

    //蓝牙设备连接成功
    void onConnected();

    void onConnectFail();
    /**
     * 写入数据
     * @param bs
     */
    void writeData(byte[] bs);


    void onServicesDiscovered();

    void onDescriptorWriteSuccess();


    void onDescriptorWrite();


    void onCharacteristicRead(String data);
    /**
     * 接收到数据
     */
    void  onReceiveMessage(String data);


}
