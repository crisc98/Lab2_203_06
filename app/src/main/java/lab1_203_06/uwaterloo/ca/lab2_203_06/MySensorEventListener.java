package lab1_203_06.uwaterloo.ca.lab2_203_06;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.Vector;

import ca.uwaterloo.sensortoy.LineGraphView;

/**
 * Created by Cristiano Chelotti on 5/18/2017.
 */

public class MySensorEventListener implements SensorEventListener{
    LineGraphView graph, smoothGraph;
    Vector<float[]> accelData;
    TextView output, dirLbl;
    float[] smoothValues;
    float alpha = 0.15f;
    Vector<Float> x= new Vector<>();
    Vector<Float> y= new Vector<>();
    Vector<Float> z= new Vector<>();
    String direction = "NONE";
    final float HOLD = alpha*50;



    public MySensorEventListener(TextView outputView, Vector data, LineGraphView graph, LineGraphView smooth, TextView dirLbl) {
        output = outputView;
        accelData = data;
        this.graph = graph;
        smoothGraph = smooth;
        this.dirLbl=dirLbl;
    }

    public void onAccuracyChanged(Sensor s, int i) {

    }


    public float[] lowPass(float[] input, float[] output){
        if (output == null) {
            return input;
        }
        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }


    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            if(getGesture()!=""){
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
//            cleanup(x);
//            cleanup(y);
//            cleanup(z);
//            cleanup(accelData);

        }

    }
    private void cleanup(Vector v){
        if(v.size()>100){
            v.subList(100,v.size()).clear();
        }
    }
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

    public String getGesture(){
        if(x.size()>15){

            float[] axisDis = new float[3];

            axisDis[0] = avg(x, x.size() - 6, 5);
            axisDis[1] = avg(y, y.size() - 6, 5);
            axisDis[2] = avg(z, z.size() - 6, 5);

            if(axisDis[0]<=(-1*HOLD)){
                return "RIGHT";
            }
            else if(axisDis[0]>=HOLD){
                return "LEFT";
            }
            else if(axisDis[1]<=(-1*HOLD)||axisDis[2]<=(-1*HOLD)){
                return "UP";
            }
            else if(axisDis[1]>=HOLD||axisDis[2]>=HOLD){
                return "DOWN";
            }

        }
        return "";
    }
}



