# Android 串口通讯 SerialPort

### 访问设备

定义 SerialPort.c 文件：

打开串口方法：

- path ：串口设备节点地址
- baudrate ： 波特率

```C
JNIEXPORT jobject JNICALL SerialPort_open
        (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
```

关闭串口：

```C
JNIEXPORT void JNICALL SerialPort_close
        (JNIEnv *env, jobject thiz)
```


使用 CMakeLists.txt 的方式编译 so 库。
[CMake 使用方法](https://developer.android.com/studio/projects/add-native-code.html?hl=zh-cn)

加载 so 库：

```java
static {
        System.loadLibrary("serial_port-lib");
    }
```
