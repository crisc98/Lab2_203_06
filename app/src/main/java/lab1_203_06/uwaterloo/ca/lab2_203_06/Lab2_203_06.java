package lab1_203_06.uwaterloo.ca.lab2_203_06;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    //Declaring class-wide fields
    LineGraphView graph, smoothGraph;
    SensorEventListener accel;
    Vector<float[]> accelData = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_203_06);

        //Declares and initializes the linear layout used in the application
        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_lab2_203_06);

        //Initializes the accelerometer graph for the raw readings
        graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        ll.addView(graph);
        graph.setVisibility(View.VISIBLE);

        //Initializes a blank label to display the direction of the gesture
        TextView dirLbl = makeLabel(ll, "");
        dirLbl.setTextSize(32);

        //Initializes an accelerometer graph for the filtered readings
        smoothGraph = new LineGraphView( getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        ll.addView(smoothGraph);
        smoothGraph.setVisibility(View.VISIBLE);

        //Creates and initializes a save button that will be used to generate the csv for the filtered readings
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

        //Creates a label that indicates the current accelerometer reading
        makeLabel(ll, "The Accelerometer Reading is: ");

        //Registers and assigns listeners and managers to the linear accelerometer
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        TextView accelSensorLbl = makeLabel(ll, "");
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accel = new MySensorEventListener(accelSensorLbl, accelData, graph, smoothGraph, dirLbl);
        sensorManager.registerListener(accel, accelSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    //A method that creates a label with specific text and adds it to a specified layout
    private TextView makeLabel(LinearLayout l, String text) {
        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(text);
        l.addView(tv1);
        return tv1;
    }


    //A method that generates a CSV with the accelerometer data (last 100). Creates a new file if necessary. Overwrites otherwise
    private void genCSV() {
        if (!accelData.isEmpty()) {
            try {
                File file = new File("/storage/extSdCard/Android/data/lab1_203_06.uwaterloo.ca.lab1_203_06/files/Lab2_203_06", "lab_2_readings.csv");
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



