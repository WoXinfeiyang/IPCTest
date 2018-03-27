package com.fxj.ipctest.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.fxj.ipctest.R;
import com.fxj.ipctest.utils.IPCConstants;

public class MessengerActivity extends Activity implements View.OnClickListener {
    private static String TAG=MessengerActivity.class.getSimpleName()+"0327";

    private Messenger mServiceMessenger;

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IPCConstants.MSG_FROM_SERVICE:
                    Log.i(TAG,"replyMsg from service:"+msg.getData().getString("reply"));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Messenger getReplyMsgFromService=new Messenger(new MessengerHandler());

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"onServiceConnected");
            mServiceMessenger=new Messenger(service);

            Message msg=Message.obtain(null, IPCConstants.MSG_FROM_CLIENT);
            Bundle bundle=new Bundle();
            bundle.putString("msg","Hello,msg from client");
            msg.setData(bundle);
            msg.replyTo=getReplyMsgFromService;/*给Message设置回复Messenger*/
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
            mServiceMessenger=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        findViewById(R.id.btn_bind_messenger_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_messenger_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_bind_messenger_service:
                bindService(new Intent(MessengerActivity.this,MessengerService.class),connection,BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_messenger_service:
                unbindService(connection);
                break;
        }
    }

}
