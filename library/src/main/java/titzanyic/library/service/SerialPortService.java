package titzanyic.library.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;

import titzanyic.library.ReceiveCallBackAidl;
import titzanyic.library.SerialPort;
import titzanyic.library.SerialPortAidl;


public class SerialPortService extends Service {



    //read data
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;


    private static final String TAG = SerialPortService.class.getSimpleName();



    private RemoteCallbackList<ReceiveCallBackAidl> mCallbacks = new RemoteCallbackList<>();

    private SerialPortAidl.Stub mBinder = new SerialPortAidl.Stub() {

        @Override
        public boolean isTaskRunning() throws RemoteException {
            return false;
        }

        @Override
        public void stopRunningTask() throws RemoteException {

        }

        @Override
        public void registerCallback(ReceiveCallBackAidl receiveCallback) throws RemoteException {
            Log.d(TAG, "registerCallback: receiveCallback");
            if (receiveCallback != null) {
                mCallbacks.register(receiveCallback);
            }

        }

        @Override
        public void unregisterCallback(ReceiveCallBackAidl receiveCallback) throws RemoteException {
            Log.d(TAG, "unregisterCallback: ");
            if (receiveCallback != null) {
                mCallbacks.unregister(receiveCallback);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        startReadThread();

    }

    /**
     * 开启接收线程
     */
    private void startReadThread() {
        mSerialPort = SerialPort.getSerialPort();
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    /**
     * 关闭接收线程
     */
    private void stopReadThread() {
        Log.d(TAG, "stopReadThread:");
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        SerialPort.getSerialPort().closeSerialPort();
        mSerialPort = null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * 接收到数据回调
     */
    private void receiveData(byte[] buffer) {
        String receive_data = new String(buffer, 0, buffer.length);
        final int len = mCallbacks.beginBroadcast();
        if (len == 0 ) {
            stopReadThread();
            return;
        }
        for (int i = 0; i < len; i++) {
            try {
                Log.d(TAG, "receiveData: getBroadcastItem receiveData");
                mCallbacks.getBroadcastItem(i).receiveData(receive_data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }

    byte[] tempByte = null;

    /**
     * 接收数据线程
     */
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                int size = 0;
                byte[] buffer = null;
                if (mInputStream == null) {
                    return;
                }
                try {
                    buffer = new byte[mInputStream.available()];
                    size = mInputStream.read(buffer);
                    if (size>0) {
                        if (isJsonObject(buffer)) {
                            receiveData(buffer);
                            tempByte = null;
                        }else {
                            if (null == tempByte) {
                                tempByte = buffer;
                            }else {
                                tempByte = byteAppend(tempByte,buffer);
                                if (isJsonObject(tempByte)) {
                                    receiveData(tempByte);
                                    tempByte = null;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbacks.kill();
        stopReadThread();
    }

    private boolean isJsonObject(byte[] buffer) {
        String receive_data = new String(buffer, 0, buffer.length);
        Log.d(TAG, "isJsonObject: jsonStr = " + receive_data);
        boolean isJson = false;
        try {
            new JSONObject(receive_data);
            isJson = true;
        } catch (JSONException e) {
            e.printStackTrace();
            isJson = false;
        }
        Log.d(TAG, "isJsonObject: is status " + isJson);
        if (!isJson && isAllJson(receive_data)) {
            Log.d(TAG, "isJsonObject: clear tempbyte");
            tempByte = null;
        }
        return isJson;
    }

    public  boolean isAllJson(String str) {
        int count = 0;
        int offset = 0;
        while((offset = str.indexOf("}", offset)) != -1){
            offset = offset + "}".length();
            count++;
        }
        if (count < 2) {
            return false;
        }else {
            return true;
        }
    }

    public static byte[] byteAppend(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
