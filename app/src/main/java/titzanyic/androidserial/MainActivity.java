package titzanyic.androidserial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import titzanyic.library.SerialPort;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt = findViewById(R.id.sample_text);
        txt.setText(SerialPort.getJniTest()); //test your jni config is ok,you need remove jni



    }

}
