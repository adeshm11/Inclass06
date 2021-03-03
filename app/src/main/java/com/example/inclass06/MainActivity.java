package com.example.inclass06;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public int selectedProgress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_asynchtask = findViewById(R.id.btn_asynckTask);
        btn_asynchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new DoWorkAsynch().execute(selectedProgress);
            }
        });

        SeekBar seekBar =findViewById( R.id.seekBar);
        seekBar.setMax(20);
        int seekValue = seekBar.getProgress();
        String x = Integer.toString(seekValue) + " times";
        Log.d("demo", "onCreate: "+x);
        TextView complexity = findViewById(R.id.textView_complexity);
        complexity.setText(x);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                complexity.setText(String.valueOf(progress) + " times");
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
        ArrayAdapter<Integer> resultadapter = new ArrayAdapter<>();
        //listviewresult.setAdapter();
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


