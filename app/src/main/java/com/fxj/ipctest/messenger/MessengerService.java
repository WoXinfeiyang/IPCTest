package com.fxj.ipctest.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.fxj.ipctest.utils.IPCConstants;

public class MessengerService extends Service {
    private static final String TAG=MessengerService.class.getSimpleName()+"0327";
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IPCConstants.MSG_FROM_CLIENT:
                    Log.i(TAG,"msg from client:"+msg.getData().getString("msg"));

                    /*给Cient发回复消息*/
                    Messenger replyToClientMessenger=msg.replyTo;
                    Message replyMsg=Message.obtain(null,IPCConstants.MSG_FROM_SERVICE);
                    Bundle bundle=new Bundle();
                    bundle.putString("reply","service给client的回复消息");
                    replyMsg.setData(bundle);
                    try {
                        replyToClientMessenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private Messenger messenger=new Messenger(new MessengerHandler());

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"MessengerService.onBind");
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"MessengerService.onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"MessengerService.onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
