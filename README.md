# BleUtils
安卓低功耗蓝牙ble快速上手

最近项目中用到蓝牙ble的需求，于是把蓝牙代码整合起来，方便调用。
 第一次传代码到github,不足之处，希望大家多支持支持  


功能特点：  

1.简洁明了，蓝牙业务与ui充分解耦


项目会一直维护，发现问题欢迎提出~  会第一时间修复哟~  qq:852234130  希望用得着的朋友点个start，你们的支持才是我继续下去的动力，在此先谢过~  

       

#3.代码中如何使用
  
1.在blelib里BluetoothUtil类里配置蓝牙uuid，蓝牙设备名称（一般蓝牙协议文档上回明确给出相应的uuid）
    
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
	


2.蓝牙辅助类初始化
  
 // btMsgListener = new BlueToothMessageListener(this, btMsgCallBack);
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


3.蓝牙回调数据对应蓝牙设备的处理（这个要根据你自己的蓝牙协议去解析）
       在blelib里 BloodO2DataHandle类里是我对我公司血氧设备收到数据的处理类
		

4.数据回调
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
