# AndroidSerial-
android serial port communication

# Android 串口通讯 SerialPort

如今，智能设备层出不穷。不少智能设备采用 Android 系统作为操作系统，需要控制或者与各种传感器获取数据，大部分就是通过串口通讯。通讯主要涉及两块，一块是接受数据，一块是发送数据。

根据 **Linux 一切皆文件** 的说法。串口设备我们当做一个文件，通过 JAVA 调用 C 去访问 Linux 的设备节点。那么 C 文件需要完成的功能就是打开和关闭设备。并且可以传入参数，比如：串口设备节点号、波特率等信息。

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

### Java 中获取设备

