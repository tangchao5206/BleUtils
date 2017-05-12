package cn.me.com.blelib.ble.bluetooth;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:40
 * 修改时间：
 * 修改备注：
 */
public interface IBlueToothMessageCallBack {
    /**
     * 接收到数据
     */
    void onReceiveMessage(Object obj);

    /**
     * 断开连接
     * @return
     */
    int onDisConnected();

    //蓝牙设备连接成功
    void onConnected();
    /**
     * 写入数据
     * @param bs
     */
    void writeData(byte[] bs);
   /* *
     * 接收到状态*/

    void onReceiveAction(String action);

}
