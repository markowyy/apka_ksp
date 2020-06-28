package com.example.apka_ksp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Chart extends AppCompatActivity {
    String[] temp = new String[1000];
    String value = "0";
    int nr = 0;




    private Socket socket;
    {
        try {
            socket = IO.socket("https://ksp-project.azurewebsites.net/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    Viewport viewport;
    Calendar calendar = Calendar.getInstance();
    GraphView graph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        socket.connect();
        socket.emit("confirmation", true);
// we get graph view instance
        graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return "+ " + super.formatLabel(value*3, isValueX) + " s";
                } else {
                    // show currency for y values

                    return super.formatLabel(value*3, isValueX) + "\u2103";
                }
            }
        });
        graph.addSeries(series);
        // customize a little bit viewport

        viewport = graph.getViewport();
       viewport.computeScroll();
        viewport.setScalable(true);
        viewport.setScrollable(true);
        //viewport.setScalableY(true);
        //viewport.setScrollableY(true);
        viewport.setYAxisBoundsManual(false);
        viewport.setMinY(0);
        viewport.setMaxX(20);
        viewport.setMinX(0);
        viewport.setMaxY(35);



        socket.on("currentValue", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];

                        try {
                            value = data.getString("value");
                           // Log.i("i", value);
                            if(nr==3) {
                                onResume(value);
                                nr=0;
                            }
                            nr++;
                            //

                        } catch (JSONException e) {
                            return;
                        }
                        //updateText(value);


                    }

                });

            }
        });




}

    protected void onResume(final String value) {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 1; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry(value);
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    private void addEntry(final String value) {


        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                series.appendData(new DataPoint(lastX++, Integer.parseInt(value)), true, 30);
                viewport.scrollToEnd();}


        });
        // here, we choose to display max 10 points on the viewport and we scroll to end

    }


}


