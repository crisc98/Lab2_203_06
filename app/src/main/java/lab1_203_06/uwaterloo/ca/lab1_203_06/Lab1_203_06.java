package lab1_203_06.uwaterloo.ca.lab1_203_06;

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
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;

import ca.uwaterloo.sensortoy.LineGraphView;

public class Lab1_203_06 extends AppCompatActivity {
    LineGraphView graph, smoothGraph;
    SensorEventListener light,rot,mag,accel;
    Vector<float[]> accelData = new Vector<>();
    TextView dirLbl;

    //Vector<Vector<Float>> axisData= new Vector<>();
//    Vector<Float> x= new Vector<>();
//    Vector<Float> y= new Vector<>();
//    Vector<Float> z= new Vector<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1_203_06);
        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_lab1_203_06);

//        axisData.add(x);
//        axisData.add(y);
//        axisData.add(z);

        graph = new LineGraphView( getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        ll.addView(graph);
        graph.setVisibility(View.VISIBLE);

        TextView dirLbl = makeLabel(ll, "");

//        smoothGraph = new LineGraphView( getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
//        ll.addView(smoothGraph);
//        smoothGraph.setVisibility(View.VISIBLE);

        Button resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setText("Clear Record-High Data");
        resetBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                clearAllRecords();
            }
        });
        resetBtn.setBackgroundColor(Color.BLUE);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setText("Generate CSV Record for Acc. Sen.");
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(accelData.size()>=100) {
                    genCSV();
                }
            }
        });

        makeLabel(ll, "The Light Sensor Reading is: ");

        //LIGHT

        TextView lightSensorLbl = makeLabel(ll, "");
        makeLabel(ll, "The Record-High Light Sensor Reading is: ");
        TextView highLightSensorLbl = makeLabel(ll, "");
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        light = new MySensorEventListener(lightSensorLbl,highLightSensorLbl);
        sensorManager.registerListener(light, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //ACCEL

        makeLabel(ll, "The Accelerometer Reading is: ");

        TextView accelSensorLbl = makeLabel(ll, "");
        makeLabel(ll, "The Record-High Accelerometer Reading is: ");
        TextView highAccelSensorLbl = makeLabel(ll, "");
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accel = new MySensorEventListener(accelSensorLbl,highAccelSensorLbl);
        sensorManager.registerListener(accel, accelSensor, SensorManager.SENSOR_DELAY_GAME);

        //MAG

        makeLabel(ll, "The Magnetic Sensor Reading is: ");

        TextView magSensorLbl = makeLabel(ll, "");
        makeLabel(ll, "The Record-High Magnetic Sensor Reading is: ");
        TextView highMagSensorLbl = makeLabel(ll, "");
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mag = new MySensorEventListener(magSensorLbl,highMagSensorLbl);
        sensorManager.registerListener(mag, magSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //ROTATION

        makeLabel(ll, "The Rotational Sensor Reading is: ");

        TextView rotSensorLbl = makeLabel(ll, "");
        makeLabel(ll, "The Record-High Rotational Sensor Reading is: ");
        TextView highRotSensorLbl = makeLabel(ll, "");
        Sensor rotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        rot = new MySensorEventListener(rotSensorLbl,highRotSensorLbl);
        sensorManager.registerListener(rot, rotSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void clearAllRecords(){
        MySensorEventListener tmpLight = (MySensorEventListener) light;
        MySensorEventListener tmpAccel = (MySensorEventListener) accel;
        MySensorEventListener tmpRot = (MySensorEventListener) rot;
        MySensorEventListener tmpMag = (MySensorEventListener) mag;
        clearRecords(tmpLight);
        clearRecords(tmpAccel);
        clearRecords(tmpRot);
        clearRecords(tmpMag);

    }
    private void clearRecords(MySensorEventListener senLis){
        senLis.clearHigh();
    }
    private TextView makeLabel(LinearLayout l, String text){
        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(text);
        l.addView(tv1);
        return tv1;
    }
    private void genCSV(){
            if (!accelData.isEmpty()) {
                try {
                    File file = new File("/storage/extSdCard/Android/data/lab1_203_06.uwaterloo.ca.lab1_203_06/files/Lab1_203_06", "accelReadings.csv");
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
//    private float avg(Vector<Vector<Float>> values, int axis, int start, int length){
//        Vector<Float> axisValues;
//        axisValues=values.elementAt(axis);
//        Log.d("Check", "Check");
//
//        if (start+length>=axisValues.size()){
//            System.out.println("ERROR");
//            return -1111111;
//        }
//        float sum=0;
//        for (int i=start;i<start+length;i++){
//            sum+=axisValues.elementAt(i);
//        }
//        return sum/length;
//    }
//
//    public String getGesture(){
//        System.out.println("Hello");
//        if(axisData.elementAt(2).size()>15){
//
//            float[] axisDis ={0,0};
//            System.out.println("Hello");
//            for (int i=0;i<2;i++) {
//                axisDis[i] = avg(axisData, i, axisData.elementAt(i).size() - 6, 5) - avg(axisData, i, axisData.elementAt(i).size() - 11, 5);
//            }
//            if(axisDis[0]>=10){
//                return "RIGHT";
//            }
//            if(axisDis[0]<=-10){
//                return "LEFT";
//            }
//            if(axisDis[1]>=10){
//                return "UP";
//            }
//            if(axisDis[1]<=-10){
//                return "DOWN";
//            }
//
//        }
//        return "";
//    }

    class MySensorEventListener implements SensorEventListener {
        TextView output, high;
        float lightHigh, accelHigh, magHigh, rotHigh;
        float[] accelHighSet, magHighSet, rotHighSet, smoothValues;
        float alpha = 0.15f;



        public MySensorEventListener(TextView outputView, TextView highView) {
            output = outputView;
            high = highView;
        }
        public void clearHigh(){
            lightHigh = 0;
            accelHigh = 0;
            magHigh = 0;
            rotHigh = 0;
        }
        public void onAccuracyChanged(Sensor s, int i) {

        }

//
//        public float[] lowPass(float[] input, float[] output){
//            if ( output == null ) return input;
//            for ( int i=0; i<input.length; i++ ) {
//                output[i] = output[i] + alpha * (input[i] - output[i]);
//            }
//            return output;
//        }
//

        public void onSensorChanged(SensorEvent se) {
            if (se.sensor.getType() == Sensor.TYPE_LIGHT) {
                output.setText(""+se.values[0]+"\n");
                if (Math.abs(se.values[0])>Math.abs(lightHigh)){
                    lightHigh=se.values[0];
                }
                high.setText(""+lightHigh+"\n");
            }
            if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
              //  dirLbl.setText(getGesture());
                String s = String.format("(%.2f, %.2f, %.2f)", se.values[0],se.values[1], se.values[2]);
                output.setText(s+"\n");
                float[] tmpData = {se.values[0],se.values[1],se.values[2]};
//                axisData.elementAt(0).add(se.values[0]);
//                axisData.elementAt(1).add(se.values[1]);
//                axisData.elementAt(2).add(se.values[2]);
                accelData.add(tmpData);
                float accelSum = (Math.abs(se.values[0])+Math.abs(se.values[1])+Math.abs(se.values[2]));
                if(accelSum>accelHigh){
                    accelHigh=accelSum;
                    accelHighSet= new float[3];
                    accelHighSet[0]=se.values[0];
                    accelHighSet[1]=se.values[1];
                    accelHighSet[2]=se.values[2];
                }
                smoothValues=accelData.elementAt(0);
                graph.addPoint(se.values);
              //  smoothGraph.addPoint(lowPass(se.values,smoothValues));
                String h = String.format("(%.2f, %.2f, %.2f)",accelHighSet[0], accelHighSet[1], accelHighSet[2]);
                high.setText(h+"\n");
            }
            if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                String s = String.format("(%.2f, %.2f, %.2f)", se.values[0],se.values[1], se.values[2]);
                output.setText(s+"\n");
                float magSum = (Math.abs(se.values[0])+Math.abs(se.values[1])+Math.abs(se.values[2]));
                if(magSum>magHigh){
                    magHigh=magSum;
                    magHighSet = new float[3];
                    magHighSet[0]=se.values[0];
                    magHighSet[1]=se.values[1];
                    magHighSet[2]=se.values[2];
                }
                String h = String.format("(%.2f, %.2f, %.2f)",magHighSet[0], magHighSet[1], magHighSet[2]);
                high.setText(h+"\n");
            }
            if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                String s = String.format("(%.2f, %.2f, %.2f)", se.values[0],se.values[1], se.values[2]);
                output.setText(s+"\n");
                float rotSum = (Math.abs(se.values[0])+Math.abs(se.values[1])+Math.abs(se.values[2]));
                if(rotSum>rotHigh){
                    rotHigh=rotSum;
                    rotHighSet= new float[3];
                    rotHighSet[0]=se.values[0];
                    rotHighSet[1]=se.values[1];
                    rotHighSet[2]=se.values[2];
                }
                String h = String.format("(%.2f, %.2f, %.2f)",rotHighSet[0], rotHighSet[1], rotHighSet[2]);
                high.setText(h+"\n");
            }
        }
    }
}
