package cn.me.com.blelib.ble.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.UUID;

import cn.me.com.blelib.ble.bluetooth.device.AbstractDeviceDataHandle;
import cn.me.com.blelib.ble.bluetooth.device.BloodO2DataHandle;
import cn.me.com.blelib.ble.bluetooth.device.WeightDataHandle;
import cn.me.com.blelib.ble.utils.ULog;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/6/30 14:37
 * 修改时间：
 * 修改备注：
 */
@SuppressLint("NewApi")
public class BlueToothMessageListener {
    //tag
    private static final String TAG = "BlueToothMessageListener";
    //activity
    private Context                   mContext;
    //回调对象
    private IBlueToothMessageCallBack btMsgCallBack;
    //是否扫描
    private boolean mScanning = false;

    //adapter
    private BluetoothAdapter mBluetoothAdapter;
    //当前测量设备类型
    private int currDeviceType ;

    //当前连接设备
    private BluetoothDevice currConnectDevice = null;
    private BluetoothGatt mBluetoothGatt;

    //数据处理
    private AbstractDeviceDataHandle deviceDataHandler;

    //蓝牙设备监听端口
    String[]             deviceReadNotifyUUIDs = null;
    int                  deviceReadNotifyIndex = 0;
    BluetoothGattService btService             = null;
    private Handler mHandler=new Handler();





    public BlueToothMessageListener(Context activity, IBlueToothMessageCallBack msgCallBack) {
        this.mContext = activity;
        this.btMsgCallBack = msgCallBack;

    }

    /**
     * 开始监听蓝牙消息
     * @return 0成功，其他失败
     */
    public int startListenerMessage() {


        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return BluetoothUtil.START_DEVICE_UNSUPPORTBLE;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {

            // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
            BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }else {
            return BluetoothUtil.START_DEVICE_VERSON;
        }

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            return BluetoothUtil.START_DEVICE_UNSUPPORTBT;
        }

        // 如果蓝牙没有打开，则直接打开
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }


        scanLeDevice(true); //开始扫描周围蓝牙设备


        return BluetoothUtil.START_DEVICE_SUCCESS;
    }

    /**
     * 关闭蓝牙消息监听
     */
    public void stopListenerMessage() {

        scanLeDevice(false);

        mScanning=true;
        if(currConnectDevice != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            currConnectDevice = null;
        }
    }

    @SuppressWarnings("unused")
    private void restartListenerMessage() {
        stopListenerMessage();
        startListenerMessage();
    }

    //扫描设备
    public void scanLeDevice(boolean enable) {
        if (enable) {
            mScanning = true;

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            ULog.d(TAG,"onscan");
            //  BluetoothDevice  name=eBody-Scale address=BC:6A:29:26:97:5E
            ULog.d(TAG,"BluetoothDevice  name=" + device.getName() + " address=" + device.getAddress());
            //  BluetoothDevice  name=eBody-Scale address=BC:6A:29:26:97:5E
            //根据蓝牙名称或者mac地址找到对应的蓝牙设备
             if (BluetoothUtil.DEVICENAMETAGS_XUEYANGYI.equals(device.getName())) {
                deviceDataHandler = new BloodO2DataHandle(btMsgCallBack);
                currDeviceType = BluetoothUtil.DEVICETYPE_XUEYANGYI;
                currConnectDevice = device;
                mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
                 if (mScanning) {
                     scanLeDevice(false);
                 }
            }
            else if (BluetoothUtil.DEVICENAMETAGS_TIZHONGCHENG.equals(device.getName())) {
                deviceDataHandler = new WeightDataHandle(btMsgCallBack);
                currDeviceType = BluetoothUtil.DEVICETYPE_TIZHONGCHENG;
                currConnectDevice = device;
                mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
                 if (mScanning) {
                     scanLeDevice(false);
                 }
            }



        }
    };

    //连接回调信息
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //  deviceDataHandler.notifyActionToUser("connected");
                //连接状态改变为连接成功
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //连接状态改变为未连接
                deviceDataHandler.notifyActionToUser("no connected");
                //断开后延时一秒再扫描连接
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice(true);
                    }
                }, 1000);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //发现服务后的回调

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //搜寻设备完毕，写入数据特征到设备中

                if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                    return;
                }
                btService = mBluetoothGatt.getService(UUID.fromString(BluetoothUtil.getDeviceServiceUUID(currDeviceType)));
                if (btService == null) {
                    return;
                }

                //btService.getCharacteristic(uuid)


                //数据通知uuid列表
                deviceReadNotifyUUIDs = BluetoothUtil.getNotifyUUIDsByDeviceType(currDeviceType);
                deviceReadNotifyIndex = 0;


                BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID.fromString(deviceReadNotifyUUIDs[0]));
                if (characteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BluetoothUtil.CLIENT_CHARACTERISTIC_CONFIG));
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                    mBluetoothGatt.readCharacteristic(characteristic);
                }

            } else {
                ULog.w(TAG, "onServicesDiscovered received: " + status);
            }
        }


        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor gattDescriptor, int status) {
            if (deviceReadNotifyUUIDs.length - 1 == deviceReadNotifyIndex) {
                //这里通知前端，设备连接成功
                deviceDataHandler.onDeviceConnected();
                deviceDataHandler.notifyActionToUser("connected");

            } else {
                deviceReadNotifyIndex++;
                BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID.fromString(deviceReadNotifyUUIDs[deviceReadNotifyIndex]));
                if (characteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BluetoothUtil.CLIENT_CHARACTERISTIC_CONFIG));
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                    mBluetoothGatt.readCharacteristic(characteristic);
                }
            }
        }

        ;

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //读取到数据
            ULog.i(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ULog.i("TAG", characteristic.getValue().toString());

            }
        }

        /**
         * 返回数据。
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 数据
            String str = "";
            for (int i = 0; i < characteristic.getValue().length; i++) {
                str = str + (characteristic.getValue()[i] & 0xff) + " ";   //变成int字符串数据，方便处理
              //  str = str + (String.format("%02X ",characteristic.getValue()[i] & 0xff)) + " ";//这里也可变为16进制数据，方便对协议
            }
            ULog.i(TAG, str);
            deviceDataHandler.handlerData(str);

        }
    };

    /**
     * 写入数据到设备
     * @param bb
     */
    public void writeLlsAlertLevel(byte[] bb) {

        if (btService == null) {
            ULog.e(TAG, "link loss Alert service not found!");
            return;
        }
        BluetoothGattCharacteristic alertLevel = btService.getCharacteristic(UUID.fromString(BluetoothUtil.getWriteUUIDByDeviceType(currDeviceType)));

        if (alertLevel == null) {
            ULog.e(TAG, "link loss Alert Level charateristic not found!");
            return;
        }

        boolean status = false;
        int storedLevel = alertLevel.getWriteType();
        ULog.d(TAG, "storedLevel() - storedLevel=" + storedLevel);
        alertLevel.setValue(bb);
        alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        status = mBluetoothGatt.writeCharacteristic(alertLevel);
        ULog.d(TAG, "writeLlsAlertLevel() - status=" + status);
    }
}
