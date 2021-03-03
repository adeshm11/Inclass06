package com.example.inclass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
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
    TextView txtprogress;
    int seekValue;

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

        btn_asynchtask = findViewById(R.id.btn_asynckTask);
        btn_asynchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoWorkAsynch().execute(selectedProgress);
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
        ListView listviewresult = findViewById(R.id.Listview_complexity);
//        ArrayAdapter<Integer> resultadapter = new ArrayAdapter<>();
        //listviewresult.setAdapter();
        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPool.execute(new DoWork());
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


    public class DoWorkAsynch extends AsyncTask<Integer,Integer,Double> {
        double number;
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Average");
            progressDialog.setMax(selectedProgress);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("TAG", "onProgressUpdate: "+values[0]);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected Double doInBackground(Integer... params) {
            ArrayList<Double> result = new ArrayList<>();
            int complexity_times = params[0];
            for(int i=0;i<complexity_times;i++){
                  result.add(HeavyWork.getNumber());
                  publishProgress(i);
            }
            Log.d("TAG", "doInBackground: "+result);
            return null;
        }
    }
    }


