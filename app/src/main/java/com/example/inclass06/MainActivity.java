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
    TextView p;
    ArrayList<Double> threadArray = new ArrayList<>(20);
    ArrayAdapter threadAdapter;
    ListView listviewresult;
    TextView txtprogress,txtprogresshappened,txtaverage;
    ArrayList<Double> result = new ArrayList<>();
    ArrayAdapter<Double> resultList;
    int arraySize;
    Double average = 0.0;

    public int selectedProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadPool = Executors.newFixedThreadPool(2);
        threadButton = findViewById(R.id.btnThread);
        progressBar = findViewById(R.id.progressBar);seekBar = findViewById(R.id.seekBar);
        txtprogress = findViewById(R.id.textView_complexity);
        p = findViewById(R.id.tvProgress);
        progressBar.setVisibility(View.GONE);
        txtprogresshappened = findViewById(R.id.tvProgress);
        txtaverage = findViewById(R.id.textView_average);
        progressBar.setVisibility(View.INVISIBLE);
        btn_asynchtask = findViewById(R.id.btn_asynckTask);
        listviewresult = findViewById(R.id.Listview_complexity);

        btn_asynchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoWorkAsynch().execute(selectedProgress);
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
            }
        });

        seekBar.setMax(20);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                threadArray.clear();
                txtprogress.setText(progress+" "+getResources().getString(R.string.seekbar_count));

                selectedProgress = progress;
                Log.d(TAG, "onProgressChanged: "+selectedProgress);

                for(int i=0;i<selectedProgress;i++){
                    threadPool.execute(new DoWorkThread());
                }

                handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        progressBar.setProgress(0);
                        progressBar.setMax(20);
                        progressBar.setProgress(progressBar.getProgress()+1);
                        threadArray.add((Double) msg.obj);
                        Double sum = 0.0;
                        int size = threadArray.size();
                        arraySize = size;
                        sum = ((Double) msg.obj).doubleValue()+sum;
                        Double average = sum / (size);
                        Log.d(TAG, "handleMessage: "+arraySize+threadArray);

                        if(!threadButton.isEnabled()){
                            threadAdapter = new ArrayAdapter<Double>(MainActivity.this, android.R.layout.simple_list_item_1,
                                    android.R.id.text1,threadArray);
                            listviewresult.setAdapter(threadAdapter);
                            threadAdapter.notifyDataSetChanged();
//                                    txtaverage.setText(average+"");
                        }
                        return false;
                    }
                });

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                threadButton.setEnabled(true);
                btn_asynchtask.setEnabled(true);
            }
        });
        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPool.execute(new DoWorkThread());

                progressBar.setVisibility(View.VISIBLE);
                p.setText(arraySize+getResources().getString(R.string.slash)+selectedProgress);
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
            }
        });
    }

    public class DoWorkThread implements Runnable{
        double randomNumber;

        @Override
        public void run() {
            HeavyWork heavyWork = new HeavyWork();
            randomNumber = heavyWork.getNumber();

            Message message = new Message();
            message.obj = randomNumber;
            handler.sendMessage(message);
        }
    }

    public class DoWorkAsynch extends AsyncTask<Integer, Double, ArrayList<Double>> {
        int i;
        Double average = 0.0;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(selectedProgress-1);
            txtprogresshappened.setText(0 +"/" +selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+"0.0");
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
            Integer progress =values[1].intValue();
            average = sum / (progress+1);
            progressBar.setProgress(progress);
            txtprogresshappened.setText(i +"/" +selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+" " +average);
            result.add(values[0]);
            listviewresult.setAdapter(resultList);
            resultList.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<Double> s) {
            threadButton.setEnabled(true);
            btn_asynchtask.setEnabled(true);
        }
    }
}