package lab1_203_06.uwaterloo.ca.lab2_203_06;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

import ca.uwaterloo.sensortoy.LineGraphView;

public class Lab2_203_06 extends AppCompatActivity {
    LineGraphView graph, smoothGraph;
    SensorEventListener accel;
    Vector<float[]> accelData = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_203_06);
        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_lab2_203_06);

        graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        ll.addView(graph);
        graph.setVisibility(View.VISIBLE);

        TextView dirLbl = makeLabel(ll, "");

        smoothGraph = new LineGraphView( getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        ll.addView(smoothGraph);
        smoothGraph.setVisibility(View.VISIBLE);


        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setText("Generate CSV Record for Acc. Sen.");
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (accelData.size() >= 100) {
                    genCSV();
                }
            }
        });

        //ACCEL

        makeLabel(ll, "The Accelerometer Reading is: ");
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        TextView accelSensorLbl = makeLabel(ll, "");
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accel = new MySensorEventListener(accelSensorLbl, accelData, graph, smoothGraph, dirLbl);
        sensorManager.registerListener(accel, accelSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    private TextView makeLabel(LinearLayout l, String text) {
        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(text);
        l.addView(tv1);
        return tv1;
    }

    private void genCSV() {
        if (!accelData.isEmpty()) {
            try {
                File file = new File("/storage/extSdCard/Android/data/lab1_203_06.uwaterloo.ca.lab1_203_06/files/Lab2_203_06", "accelReadings.csv");
                System.out.println(file.getAbsolutePath());
                if (file.createNewFile()) {
                    System.out.println("File is created!");
                } else {
                    System.out.println("File already exists.");
                }
                System.out.println(file.length());
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter writer = new PrintWriter(f);
                int i = 0;
                while (i < 100) {
                    float[] tmpData = accelData.elementAt(i);
                    writer.println(String.format("%f,%f,%f", tmpData[0], tmpData[1], tmpData[2]));
                    i++;

                }
                writer.flush();
                writer.close();
                f.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
