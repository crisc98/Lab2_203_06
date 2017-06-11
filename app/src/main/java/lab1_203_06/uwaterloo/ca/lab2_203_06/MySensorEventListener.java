package lab1_203_06.uwaterloo.ca.lab2_203_06;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import ca.uwaterloo.sensortoy.LineGraphView;

public class MySensorEventListener implements SensorEventListener{
    //Declares class-wide fields
    LineGraphView graph, smoothGraph;
    Vector<float[]> accelData;
    TextView output, dirLbl;
    float[] smoothValues;
    float alpha = 0.15f;
    Vector<Float> x= new Vector<>();
    Vector<Float> y= new Vector<>();
    Vector<Float> z= new Vector<>();
    String direction = "NONE";
    boolean peaked =false;
    Timer timer = new Timer();

    //Threshold constant
    final float HOLD = alpha*50;

    //Constructor for MySensorEventListener: Takes a  current data TextView, a Vector, two LineGraphViews, and and a direction label text view.
    //Assigns the parameters to their respective class-wide fields
    public MySensorEventListener(TextView outputView, Vector data, LineGraphView graph, LineGraphView smooth, TextView dirLbl) {
        output = outputView;
        accelData = data;
        this.graph = graph;
        smoothGraph = smooth;
        this.dirLbl=dirLbl;
    }

    public void onAccuracyChanged(Sensor s, int i) {

    }

    //Method that applies low pass filter to an array of data inputs and stores them in an array of data outputs
    public float[] lowPass(float[] input, float[] output){
        if (output == null) {
            return input;
        }
        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }

    //Actions to be executed every time the sensor detects a new data point
    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            if(getAgitated()&&!peaked){
                direction=getGesture();
            }
            dirLbl.setText(direction);
            String s = String.format("(%.2f, %.2f, %.2f)", se.values[0],se.values[1], se.values[2]);
            output.setText(s+"\n");
            float[] tmpData = {se.values[0],se.values[1],se.values[2]};
                x.add(se.values[0]);
                y.add(se.values[1]);
                z.add(se.values[2]);
            accelData.add(tmpData);
            smoothValues=accelData.elementAt(0);
            graph.addPoint(se.values);
            smoothGraph.addPoint(lowPass(se.values,smoothValues));

        }

    }

    //A method that returns the average of the values in a float vector from a starting index to a certain length after
    private float avg(Vector<Float> axisValues, int start, int length){
        Log.d("Check", "Check");

        if (start+length>=axisValues.size()){
            Log.e("Error", "OutOfBounds");
            return -1111111;
        }
        float sum=0;
        for (int i=start;i<start+length;i++){
            sum+=axisValues.elementAt(i);
        }
        return sum/length;
    }

    //A method that returns which Gesture the device was subjected to based on the averages of the accelerometer data.
    //Returns a specific direction when the average accel data on that axis exceeds the threshold constant
    public String getGesture(){
        if(x.size()>15){

            float[] axisDis = new float[3];

            axisDis[0] = avg(x, x.size() - 6, 5);
            axisDis[1] = avg(y, y.size() - 6, 5);
            axisDis[2] = avg(z, z.size() - 6, 5);

            if(axisDis[0]<=(-1*HOLD)){
                peak();
                return "LEFT";
            }
            else if(axisDis[0]>=HOLD){
                peak();
                return "RIGHT";
            }
            else if(axisDis[1]<=(-1*HOLD)||axisDis[2]<=(-1*HOLD)){
                peak();
                return "DOWN";
            }
            else if(axisDis[1]>=HOLD||axisDis[2]>=HOLD){
                peak();
                return "UP";
            }

        }
        return "";
    }

    //A method that returns whether or not the device has been agitated or the threshold has been passed on some axis
    private boolean getAgitated(){
        if(x.size()>15){

            float[] axisDis = new float[3];

            axisDis[0] = avg(x, x.size() - 6, 5);
            axisDis[1] = avg(y, y.size() - 6, 5);
            axisDis[2] = avg(z, z.size() - 6, 5);

            if(axisDis[0]<=(-1*HOLD)||axisDis[0]>=HOLD||axisDis[1]<=(-1*HOLD)||axisDis[2]<=(-1*HOLD)||axisDis[1]>=HOLD||axisDis[2]>=HOLD) {
                return true;
            }

         }
        return false;
    }

    //A method that sets the state of the gesture to peaked for .5 seconds and then sets returns to a listening state
    private void peak(){
        peaked=true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                peaked=false;
                this.cancel();

            }
        },500);
    }
}



