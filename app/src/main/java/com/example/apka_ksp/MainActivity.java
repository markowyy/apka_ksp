package com.example.apka_ksp;

import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.LinearGauge;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
Button btn;
TextView temp;


private Socket socket;

    {
        try {
            socket = IO.socket("https://ksp-project.azurewebsites.net/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket.connect();
        socket.emit("confirmation", true);
        btn = (Button)findViewById(R.id.button2) ;
        //nickname = (EditText) findViewById(R.id.nickname);
        temp = (TextView) findViewById(R.id.tempid);


        socket.on("currentValue", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String value;

                        try {
                            value = data.getString("value");
                            Log.i("i", value);
                        } catch (JSONException e) {
                            return;
                        }
                        //updateText(value);
                        temp.setText(value + "\u2103");

                    }
                });
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i  = new Intent(MainActivity.this,Chart.class);
                    startActivity(i);
                }
            }
        );
    }

}

