/*
Amruta Deshmukh and Komal Patel
MainActivity.java
In Class 06
 */

package com.example.inclass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ListView;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    Button threadButton;
    Button btn_asynchtask;
    ExecutorService threadPool;
    Handler handler;
    ProgressBar progressBar;
    String TAG = "demo";
    SeekBar seekBar;
    ListView listviewresult;
    TextView txtprogress,txtprogresshappened,txtaverage;
    int seekValue;
    ArrayList<Double> result = new ArrayList<>();
    ArrayAdapter<Double> resultList;
    public int selectedProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        threadPool = Executors.newFixedThreadPool(2);
        threadButton = findViewById(R.id.btnThread);
        progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);
        txtprogress = findViewById(R.id.textView_complexity);
        txtprogresshappened = findViewById(R.id.textview_progressnumber);
        txtaverage = findViewById(R.id.textView_average);
        progressBar.setVisibility(View.INVISIBLE);
        btn_asynchtask = findViewById(R.id.btn_asynckTask);
        btn_asynchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoWorkAsynch().execute(selectedProgress);
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
            }
        });

        seekBar.setMax(20);
        seekValue = seekBar.getProgress();
        String x = Integer.toString(seekValue) + " times";
        Log.d("demo", "onCreate: "+x);
        TextView complexity = findViewById(R.id.textView_complexity);
        complexity.setText(x);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                txtprogress.setText(progress+" "+getResources().getString(R.string.seekbar_count));
                selectedProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

         listviewresult = findViewById(R.id.Listview_complexity);

        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPool.execute(new DoWork());
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return false;
            }
        });
    }

    public class DoWork implements Runnable{
        @Override
        public void run() {
            Log.d(TAG, "run: "+seekValue);
            for(int i=0;i<seekValue;i++) {
                double randomNumbers = HeavyWork.getNumber();
                Log.d(TAG, "run: ");
            }
            Log.d(TAG, "run: end");
        }
    }


    public class DoWorkAsynch extends AsyncTask<Integer, Double, ArrayList<Double>> {
        int i;
        Double average = 0.0;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(selectedProgress-1);
            progressBar.setProgress(0);//initially progress is 0
            txtprogresshappened.setText(0 +"/" +selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+" ");
            resultList =
                    new ArrayAdapter<Double>(MainActivity.this, android.R.layout.simple_list_item_1,android.R.id.text1, result);
            listviewresult.setAdapter(resultList);
        }

        @Override
        protected ArrayList<Double> doInBackground(Integer... integers) {
            int complexity_times = integers[0];
            for(i=0;i<complexity_times;i++) {
                Double num = HeavyWork.getNumber();
                publishProgress(num, Double.valueOf(i));
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            Double sum = 0.0;
            sum = sum + values [0];
            int progress =values[1].intValue();
            average = sum / (progress+1);
            progressBar.setProgress(progress);
            txtprogresshappened.setText(i +"/" +selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+" " +average);
            result.add(values[0]);
            resultList.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<Double> s) {
            threadButton.setEnabled(true);
            btn_asynchtask.setEnabled(true);
        }

    }
}
