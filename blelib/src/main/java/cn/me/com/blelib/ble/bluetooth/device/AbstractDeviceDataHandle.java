package cn.me.com.blelib.ble.bluetooth.device;


import cn.me.com.blelib.ble.bluetooth.IBlueToothMessageCallBack;
import cn.me.com.blelib.ble.utils.ULog;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:36
 * 修改时间：
 * 修改备注：
 */
public abstract class AbstractDeviceDataHandle {
    //日志标签
    protected String TAG = "AbstractDeviceDataHandle";

    //设备类型
    protected int deviceType = -1;

    //ui数据回传
    IBlueToothMessageCallBack msgCallBack;

    //上一次接收数据时间(过滤重复数据的时候用)
    protected long lastReceiveDataMillis = 0;

    //上一次接收数据内容(过滤重复数据的时候用)
    protected String lastReceiveData = "";

    public AbstractDeviceDataHandle(IBlueToothMessageCallBack callback) {
        this.msgCallBack = callback;
    }

    /**
     * 处理数据
     * @param data
     */
    public abstract void handlerData(String data);

    /**
     * 设备连接成功后
     */
    public abstract void onDeviceConnected();

    /**
     * 检查是否接收重复消息（1秒内接收的消息；2、消息内容同缓存的内容一样）
     * @param data
     * @return
     */
    protected boolean isTheSameData(String data) {
        if(System.currentTimeMillis() - lastReceiveDataMillis <1000
                && data.equals(lastReceiveData)) {
            lastReceiveDataMillis = System.currentTimeMillis();
            ULog.i(TAG, "接收到重复消息内容!");
            return true;
        }
        lastReceiveDataMillis = System.currentTimeMillis();
        lastReceiveData = data;
        return false;
    }

    /**
     * 下发消息到设备中
     * @param bs
     */
    protected void sendDataToDevice(byte[] bs) {
        this.msgCallBack.writeData(bs);
    }

    /**
     * 发送消息到用户
     * @param data
     */
    protected void notifyDataToUser(Object data) {

        this.msgCallBack.onReceiveMessage(data);
    }
    /**
     * 发送状态到用户
     * @param data
     */
    public void notifyActionToUser(String  data) {

        this.msgCallBack.onReceiveAction(data);
    }
}
