package news.example.zdw.bluedemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import cn.me.com.blelib.ble.bluetooth.BlueToothMessageListener;
import cn.me.com.blelib.ble.bluetooth.IBlueToothMessageCallBack;
import cn.me.com.blelib.ble.utils.ULog;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private BlueToothMessageListener btMsgListener;

    private TextView text;

    private Handler mHanlder;


    //设备标识((按上面设备类型顺序填写))
    public final static String DEVICE_NAME_SPO2    = "iChoice"; //血氧蓝牙设备名称uuid

    //设备mac地址
    public final static String DEVICE_MAC_SPO2    = "08:7C:BE:2F:87:BA"; //血氧蓝牙mac地址



    //设备Service uuid
    public final static String DEVICE_SERVICE_UUID = "ba11f08c-5f14-0b0d-1080-007cbe2f87ba";  //血氧服务id

    //设备通知uuid  //血氧通知uuid(一般设备是一个，这里有四个，看协议决定)
    public final static String[] NOTIFY_CHARACTERISTICSUUID = {

                    "0000cd01-0000-1000-8000-00805f9b34fb",
                    "0000cd02-0000-1000-8000-00805f9b34fb",
                    "0000cd03-0000-1000-8000-00805f9b34fb",
                    "0000cd04-0000-1000-8000-00805f9b34fb"
            };


    //设备写数据uuid (按上面设备类型顺序填写)
    public final static String WRITE_CHARACTERISTICSUUID =  "0000cd20-0000-1000-8000-00805f9b34fb";  //血氧写uuid

    public final static String READ_CHARACTERISTICSUUID =  "0000cd21-0000-1000-8000-00805f9b34fb";  //血氧读uuid


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.txt);
        btMsgListener = BlueToothMessageListener.create(this).setCallback(btMsgCallBack)
                .setServiceUUid(DEVICE_SERVICE_UUID) //必须要
                .setNotifyUUid(NOTIFY_CHARACTERISTICSUUID)//必须要
                .setReadUUid(READ_CHARACTERISTICSUUID)
                .setWriteUUid(WRITE_CHARACTERISTICSUUID)
                .setBleName(DEVICE_NAME_SPO2) //蓝牙名称和mac地址二选一
                .setBleMac(DEVICE_MAC_SPO2);

        //检测手机能否支持蓝牙ble
        int respose = btMsgListener.checkBle();
        if (respose>0){
            ULog.showToast(this,"蓝牙初始化成功");
            btMsgListener.startListener(true);  //true 代表通过蓝牙名字搜索蓝牙，false代表通过mac地址搜索蓝牙

        }else if (respose==-1){
            ULog.showToast(this,"当前手机不支持蓝牙ble");

        }else if (respose==-2){
            ULog.showToast(this,"当前手机不支持蓝牙");

        }else if (respose==-3){
            ULog.showToast(this,"安卓系统版本必须大于4.3");
        }

        mHanlder = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                text.setText(msg.obj.toString());
            }
        };
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (btMsgListener!=null){
            btMsgListener.stopListenerMessage();
            btMsgListener=null;
        }


    }


    /**
     *回调
     */
    IBlueToothMessageCallBack btMsgCallBack = new IBlueToothMessageCallBack() {




        //蓝牙断开连接
        @Override
        public void onDisConnected() {

        }


        //蓝牙连接成功
        @Override
        public void onConnected() {

        }
        //蓝牙连接失败
        @Override
        public void onConnectFail() {

        }

        //蓝牙写入数据
        @Override
        public void writeData(byte[] bs) {

            btMsgListener.writeLlsAlertLevel(bs);

        }

        @Override
        public void onServicesDiscovered() {

        }
        //通知uuid全部通知完毕后回调
        @Override
        public void onDescriptorWriteSuccess() {
            final byte[] datas = {(byte)0xaa, (byte)0x55, (byte)0x04, (byte)0xb1, 0x00, 0x00, (byte)0xb5};
            btMsgListener.writeLlsAlertLevel(datas);
        }

        @Override
        public void onDescriptorWrite() {

        }
        //读特征值
        @Override
        public void onCharacteristicRead(String data) {

        }
        //接收到的数据
        @Override
        public void onReceiveMessage(final String data) {
            ULog.d(TAG,"receive data  "+data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText(data);
                }
            });
        }


    };
}
