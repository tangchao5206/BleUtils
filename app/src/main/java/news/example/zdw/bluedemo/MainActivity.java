package news.example.zdw.bluedemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import cn.me.com.blelib.ble.bean.BloodO2Bean;
import cn.me.com.blelib.ble.bean.WeightBean;
import cn.me.com.blelib.ble.bluetooth.BlueToothMessageListener;
import cn.me.com.blelib.ble.bluetooth.IBlueToothMessageCallBack;
import cn.me.com.blelib.ble.utils.ULog;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private BlueToothMessageListener btMsgListener;

    private TextView text;

    private Handler mHanlder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.txt);

        btMsgListener = new BlueToothMessageListener(this, btMsgCallBack);
        int respose = btMsgListener.startListenerMessage();//蓝牙初始化状态
        if (respose>0){
            ULog.showToast(this,"蓝牙初始化成功");
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

        @Override
        public void onReceiveMessage(Object data) {
            if (data instanceof WeightBean) {
                ULog.i(TAG, "receive data type is weight.");
                Message msg = mHanlder.obtainMessage();
                msg.obj = "tizhong" + ((WeightBean) data).getWeightData() + "";
                mHanlder.sendMessage(msg);
            } else if (data instanceof BloodO2Bean) {
                ULog.i(TAG, "receive data type is bloodO2.");
                Message msg = mHanlder.obtainMessage();
                msg.obj = "bloodo2" + ((BloodO2Bean) data).getBloodO2Data() + "%" + ((BloodO2Bean) data).getPulseData();
                mHanlder.sendMessage(msg);
            }  else {
                ULog.e(TAG, "receive undefined type.");
            }
        }
        //蓝牙断开连接
        @Override
        public int onDisConnected() {
		/*	btMsgListener.stopListenerMessage();
			btMsgListener = new BlueToothMessageListener(MainActivity.this, btMsgCallBack);
			btMsgListener.startListenerMessage();*/
            return 0;
        }
        //蓝牙连接成功
        @Override
        public void onConnected() {

        }
        //蓝牙写入数据
        @Override
        public void writeData(byte[] bs) {

            btMsgListener.writeLlsAlertLevel(bs);

        }

        @Override
        public void onReceiveAction(String action) {

        }

    };
}
