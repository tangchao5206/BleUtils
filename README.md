# BleUtils
安卓低功耗蓝牙ble快速上手

最近项目中用到蓝牙ble的需求，于是把蓝牙代码整合起来，方便调用。
 第一次传代码到github,不足之处，希望大家多支持支持  


功能特点：  

1.简洁明了，蓝牙业务与ui充分解耦


项目会一直维护，发现问题欢迎提出~  会第一时间修复哟~  qq:852234130  希望用得着的朋友点个start，你们的支持才是我继续下去的动力，在此先谢过~  
蓝牙操作主要封装在blelib中， BlueToothMessageListener为主要蓝牙相关操作处理类，通过IBlueToothMessageCallBack回调数据。
       

#3.代码中如何使用
  
1.蓝牙操作类初始化：
 btMsgListener = BlueToothMessageListener.create(this).setCallback(btMsgCallBack)
                .setServiceUUid(DEVICE_SERVICE_UUID) //必须要
                .setNotifyUUid(NOTIFY_CHARACTERISTICSUUID)//必须要
                .setReadUUid(READ_CHARACTERISTICSUUID)
                .setWriteUUid(WRITE_CHARACTERISTICSUUID)
                .setBleName(DEVICE_NAME_SPO2) //蓝牙名称和mac地址二选一
                .setBleMac(DEVICE_MAC_SPO2);
    
  2.  //检测手机能否支持蓝牙ble，支持就开始
  
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


		

3.数据回调<br>
    
  
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
        //发现服务
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
