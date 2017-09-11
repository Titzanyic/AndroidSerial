package titzanyic.androidserial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;

import titzanyic.library.SerialPort;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt = findViewById(R.id.sample_text);
        txt.setText(SerialPort.getJniTest()); //test your jni config is ok,you need remove jni


        //send data
        byte[] sendData = new byte[]{(byte) 0x81, (byte) 0x81,0x43, (byte) 0xFE, 0x23, (byte) 0x00,0x67, (byte) 0xFE};
        try {
            SerialPort.getSerialPort().getOutputStream().write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
