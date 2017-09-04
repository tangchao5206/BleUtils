package cn.me.com.blelib.ble.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    private BluetoothGatt mBluetoothGatt;


    //蓝牙设备监听端口
    String[]             deviceReadNotifyUUIDs = null;
    int                  deviceReadNotifyIndex = 0;
    BluetoothGattService btService             = null;
    private Handler mHandler =new Handler();

    private boolean isScan=true;
    private  boolean isScanByName;
    private String serviceUUid;
    private String[] notifyUUid;
    String writeUUid;
    String readUUid;
    private String name="iChoice";
    private String mac="08:7C:BE:2F:87:BA";
    private long SCAN_PERIOD=10000;

    public static BlueToothMessageListener create(Activity activity) {
        return new BlueToothMessageListener(activity);
    }


    public BlueToothMessageListener(Context activity ) {
        this.mContext = activity;

    }
    public BlueToothMessageListener  setCallback(IBlueToothMessageCallBack callback){
        this.btMsgCallBack=callback;
        return this;
    }
    public BlueToothMessageListener  setScanTime(Long time){
        this.SCAN_PERIOD=time;
        return this;
    }
    public BlueToothMessageListener  setServiceUUid(String serviceUUid){
        this.serviceUUid=serviceUUid;
        return this;
    }
    public BlueToothMessageListener  setNotifyUUid(String[] notifyUUid){
        this.notifyUUid=notifyUUid;
        return this;
    }
    public BlueToothMessageListener  setReadUUid(String readUUid){
        this.readUUid=readUUid;
        return this;
    }
    public BlueToothMessageListener  setWriteUUid(String writeUUid){
        this.writeUUid=writeUUid;
        return this;
    }
    public BlueToothMessageListener  setBleName(String name){
        this.name=name;
        return this;
    }
    public BlueToothMessageListener  setBleMac(String mac){
        this.mac=mac;
        return this;
    }
    public int checkBle(){

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return BleConfig.START_DEVICE_UNSUPPORTBLE;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {

            // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
            BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }else {
            return BleConfig.START_DEVICE_VERSON;
        }

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            return BleConfig.START_DEVICE_UNSUPPORTBT;
        }

        // 如果蓝牙没有打开，则直接打开
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        return BleConfig.START_DEVICE_SUCCESS;
    }

    /**
     * 开始监听蓝牙消息
     * @return 0成功，其他失败
     */
    public void startListener(boolean isScanByName) {
        this.isScanByName=isScanByName;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanLeDevice(true);
            }
        }, 100);//开始扫描周围蓝牙设备
        isScan=true;


    }

    /**
     * 关闭蓝牙消息监听
     */
    public void stopListenerMessage() {
        mScanning=true;
        isScan=false;
        if (mBluetoothAdapter!=null)
         scanLeDevice(false);

        if (mBluetoothGatt!=null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt = null;
        }


    }

    @SuppressWarnings("unused")
    public void restartListenerMessage() {
        stopListenerMessage();
        startListener(isScanByName);

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

    //扫描设备
  /*  public void scanLeDevice( boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }*/


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            ULog.d(TAG,"onscan");
            //  BluetoothDevice  name=eBody-Scale address=BC:6A:29:26:97:5E
            ULog.d(TAG,"BluetoothDevice  name=" + device.getName() + " address=" + device.getAddress());
            //  BluetoothDevice  name=eBody-Scale address=BC:6A:29:26:97:5E
            //根据蓝牙名称或者mac地址找到对应的蓝牙设备
            if (isScanByName){
                if (name.equals(device.getName())) {
                    ULog.d(TAG,"find_device_by_name");
                    mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
                    if (mScanning) {
                        scanLeDevice(false);
                    }

                }
            }else {
                if (mac.equals(device.getAddress())) {
                    ULog.d(TAG,"find_device_by_mac");
                    mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
                    if (mScanning) {
                        scanLeDevice(false);
                    }

                }
            }





        }
    };


    //连接回调信息
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //连接成功判断
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                ULog.d(TAG,"connected");
                btMsgCallBack.onConnected();
                mBluetoothGatt.discoverServices();
                // 连接断开判断
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                ULog.d(TAG,"onDisConnected");
                btMsgCallBack.onDisConnected();
                gatt.close();

                //连接断开延时一秒后继续扫秒蓝牙连接
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isScan)
                scanLeDevice(true);


            } else if (status != BluetoothGatt.GATT_SUCCESS) { // 连接失败判断
                ULog.d(TAG,"onConnectFail");
                btMsgCallBack.onConnectFail();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isScan)
                    scanLeDevice(true);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            btMsgCallBack.onServicesDiscovered();
            //发现服务后的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //搜寻设备完毕，写入数据特征到设备中

                if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                    return;
                }
                if (serviceUUid==null){
                    ULog.i(TAG," serviceUUid is null");
                    return;
                }
                btService = mBluetoothGatt.getService(UUID.fromString(serviceUUid));
                if (btService == null) {
                    gatt.disconnect(); //找不到服务重新连接
                    return;
                }
                ULog.d(TAG,"findServices");

                if (notifyUUid==null){
                    ULog.i(TAG," notifyUUid is null");
                    return;
                }

                //数据通知uuid列表
                deviceReadNotifyIndex = 0;

                BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID.fromString(notifyUUid[0]));
                if (characteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BleConfig.CLIENT_CHARACTERISTIC_CONFIG));
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                  //  mBluetoothGatt.readCharacteristic(characteristic);
                }

            } else {
                ULog.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor gattDescriptor, int status) {

            btMsgCallBack.onDescriptorWrite();
            if (status != 0) {
                ULog.d(TAG, "onDescriptorWrite, status not 0, do disconnect.");
                //状态码出错，先断开连接然后再重新连接
                gatt.disconnect();
            }else {
                if (notifyUUid.length - 1 == deviceReadNotifyIndex) {
                    //这里通知前端，设备连接成功
                    btMsgCallBack.onDescriptorWriteSuccess();
                    ULog.d(TAG,"onDescriptorWriteSuccess");


                } else {
                    deviceReadNotifyIndex++;
                    BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID.fromString(notifyUUid[deviceReadNotifyIndex]));
                    if (characteristic != null) {
                        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BleConfig.CLIENT_CHARACTERISTIC_CONFIG));
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(descriptor);
                        }
                        mBluetoothGatt.readCharacteristic(characteristic);
                    }
                }
            }

        }



        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //读取到数据

            ULog.i(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ULog.i("TAG", characteristic.getValue().toString());
                btMsgCallBack.onCharacteristicRead(characteristic.getValue().toString());

            }
        }

        /**
         * 返回数据。
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 数据
            ULog.d(TAG,"onCharacteristicChanged");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < characteristic.getValue().length; i++) {
              //  sb.append (characteristic.getValue()[i] & 0xff).append(" ") ;   //变成int字符串数据，方便处理
               //这里也可变为16进制数据，方便对协议
                sb.append (String.format("%02X ",characteristic.getValue()[i] & 0xff)).append(" ") ;
            }

            btMsgCallBack.onReceiveMessage(sb.toString());

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
        if (writeUUid==null){
            ULog.i(TAG,"writeUUid is null");
            return;
        }
        BluetoothGattCharacteristic alertLevel = btService.getCharacteristic(UUID.fromString(writeUUid));

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
