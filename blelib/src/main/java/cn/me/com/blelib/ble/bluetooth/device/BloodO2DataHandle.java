package cn.me.com.blelib.ble.bluetooth.device;

import cn.me.com.blelib.ble.bean.BloodO2Bean;
import cn.me.com.blelib.ble.bluetooth.BluetoothUtil;
import cn.me.com.blelib.ble.bluetooth.IBlueToothMessageCallBack;
import cn.me.com.blelib.ble.utils.ULog;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/10/27 14:25
 * 修改时间：
 * 修改备注：
 */
public class BloodO2DataHandle extends AbstractDeviceDataHandle {

    private BloodO2Bean bloodO2Bean;

    public BloodO2DataHandle(IBlueToothMessageCallBack callback) {
        super(callback);
        this.deviceType = BluetoothUtil.DEVICETYPE_TIZHONGCHENG;
        this.TAG = "BloodO2DataHandle";
        bloodO2Bean = new BloodO2Bean();
    }

    @Override
    public void handlerData(String data) {
        ULog.i(TAG, "receive data : " + data);

        //这里解析收过来的数据
        if (super.isTheSameData(data)) {
            return;
        }
        String[] datas = data.split(" ");

        if(datas.length == 6) {
            if(Integer.parseInt(datas[4]) > 0) {

                bloodO2Bean.setBloodO2Data(Integer.parseInt(datas[3]));

                bloodO2Bean.setPulseData(Integer.parseInt(datas[4]));

                this.notifyDataToUser(bloodO2Bean);
            }
        }
    }

    @Override
    public void onDeviceConnected() {
        ULog.i(TAG, "bloodo2 device connected.");
	/*	bloodO2Bean.setBloodO2Data(0);
		bloodO2Bean.setPulseData(0);
		this.notifyDataToUser(bloodO2Bean);*/

        msgCallBack.onConnected();
        //蓝牙连接成功后发送命令
        final byte[] datas = {(byte)0xaa, (byte)0x55, (byte)0x04, (byte)0xb1, 0x00, 0x00, (byte)0xb5};
        sendDataToDevice(datas);
    }
}
