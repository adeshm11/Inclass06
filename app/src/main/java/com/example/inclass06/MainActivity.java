package com.example.inclass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button threadButton;
    Button btn_asynchtask;
    ExecutorService threadPool;
    Handler handler;
    ProgressBar progressBar;
    SeekBar seekBar;
    ArrayList<Double> threadArray = new ArrayList<>(20);
    ArrayAdapter threadAdapter;
    ArrayAdapter blankadapter;
    ListView listviewresult;
    TextView txtprogress,txtprogresshappened,txtaverage;
    ArrayList<Double> result = new ArrayList<>();
    ArrayAdapter<Double> resultList;
    int arraySize;
    Double sum = 0.0;
    public int selectedProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadPool = Executors.newFixedThreadPool(2);
        threadButton = findViewById(R.id.btnThread);
        progressBar = findViewById(R.id.progressBar);seekBar = findViewById(R.id.seekBar);
        txtprogress = findViewById(R.id.textView_complexity);
        progressBar.setVisibility(View.GONE);
        txtprogresshappened = findViewById(R.id.tvProgress);
        txtaverage = findViewById(R.id.textView_average);
        progressBar.setVisibility(View.INVISIBLE);
        btn_asynchtask = findViewById(R.id.btn_asynckTask);
        listviewresult = findViewById(R.id.Listview_complexity);

        btn_asynchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.clear();
                threadArray.clear();
                new DoWorkAsynch().execute(selectedProgress);
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
                seekBar.setEnabled(false);
            }
        });
        seekBar.setMax(20);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                txtprogress.setText(progress+" "+getResources().getString(R.string.seekbar_count));
                selectedProgress = progress;
                if(progress == 0){
                    result.clear();
                    threadArray.clear();
                    threadButton.setEnabled(false);
                    btn_asynchtask.setEnabled(false);
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.select_complexity), Toast.LENGTH_LONG).show();
                }
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

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

                switch (msg.what){
                    case DoWorkThread.STATUS_START:
                        progressBar.setProgress(0);
                        progressBar.setMax(selectedProgress);
                        break;

                    case DoWorkThread.STATUS_PROGRESS:

                        Double number = msg.getData().getDouble(DoWorkThread.NUMBER_KEY);
                        if(threadArray.size()<selectedProgress) {
                            threadArray.add(number);
                            progressBar.setProgress(threadArray.size());
                        }

                        for (double element: threadArray) {
                            threadAdapter = new ArrayAdapter<Double>(MainActivity.this, android.R.layout.simple_list_item_1,
                                    android.R.id.text1,threadArray);
                            listviewresult.setAdapter(threadAdapter);
                            threadAdapter.notifyDataSetChanged();
                        }
                        int size = threadArray.size();
                        if(size<=selectedProgress) {
                            arraySize = size;
                            sum = sum + number ;
                            Double average = sum / (size);
                            txtprogresshappened.setText(arraySize+getResources().getString(R.string.slash)+selectedProgress);

                            txtaverage.setText(getResources().getString(R.string.average)+" "+average);
                        }
                        break;

                    case DoWorkThread.STATUS_END:
                        threadButton.setEnabled(true);
                        seekBar.setEnabled(true);
                        btn_asynchtask.setEnabled(true);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
                return false;
            }
        });
        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadArray.clear();
                result.clear();
                listviewresult.setAdapter(blankadapter);
                listviewresult.isEnabled();
                progressBar.setVisibility(View.VISIBLE);
                if(selectedProgress == 0){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.select_complexity), Toast.LENGTH_LONG).show();
                }
                txtprogresshappened.setText(getResources().getString(R.string.zero)+getResources().getString(R.string.slash)+
                        selectedProgress);
                txtaverage.setText(getResources().getString(R.string.average)+" "+getResources().getString(R.string.avg_initial));
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
                seekBar.setEnabled(false);
                threadPool.execute(new DoWorkThread());
            }
        });
    }

    public class DoWorkThread implements Runnable{
        double randomNumber;
        static final String NUMBER_KEY="NUMBER";
        static final int STATUS_START = 0;
        static final int STATUS_PROGRESS = 1;
        static final int STATUS_END = 2;
        static final String PROGRESS_KEY = "PROGRESS";

        @Override
        public void run() {
            HeavyWork heavyWork = new HeavyWork();
            Bundle bundle = new Bundle();
            Message startMessage = new Message();
            startMessage.what = STATUS_START;
            bundle.putDouble(PROGRESS_KEY,selectedProgress);
            startMessage.setData(bundle);
            handler.sendMessage(startMessage);

            for(int i=0;i<selectedProgress;i++) {
                Message progressMessage = new Message();
                randomNumber = heavyWork.getNumber();
                bundle.putDouble(NUMBER_KEY, (double) randomNumber);
                progressMessage.what = STATUS_PROGRESS;
                progressMessage.setData(bundle);
                handler.sendMessage(progressMessage);
            }

            Message endMessage = new Message();
            endMessage.what = STATUS_END;
            handler.sendMessage(endMessage);
        }
    }

    public class DoWorkAsynch extends AsyncTask<Integer, Double, ArrayList<Double>> {
        int i;
        Double average = 0.0;
        Double sum = 0.0;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(selectedProgress);
            progressBar.setProgress(0);
            txtprogresshappened.setText(getResources().getString(R.string.zero)+getResources().getString(R.string.slash)+
                    selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+" "+getResources().getString(R.string.avg_initial));
            resultList =
                    new ArrayAdapter<Double>(MainActivity.this, android.R.layout.simple_list_item_1,android.R.id.text1, result);
            listviewresult.setAdapter(resultList);
            sum = 0.0;
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
            sum = sum + values [0];
            Integer progress =values[1].intValue();
            average = sum / (progress+1);
            progressBar.setProgress(progress+1);
            txtprogresshappened.setText(i +getResources().getString(R.string.slash) +selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+" " +average);
            result.add(values[0]);
            listviewresult.setAdapter(resultList);
            resultList.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<Double> s) {
            threadButton.setEnabled(true);
            btn_asynchtask.setEnabled(true);
            seekBar.setEnabled(true);
        }
    }
}