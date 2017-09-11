// SerialPortAidl.aidl
package titzanyic.library;
import titzanyic.library.ReceiveCallBackAidl;

interface SerialPortAidl {
    boolean isTaskRunning();
    void stopRunningTask();
    void registerCallback( ReceiveCallBackAidl receiveCallback);
    void unregisterCallback( ReceiveCallBackAidl receiveCallback);
}
