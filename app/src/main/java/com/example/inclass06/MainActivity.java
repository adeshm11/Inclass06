package com.example.inclass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    ListView listView;
    ArrayList<Double> threadArray = new ArrayList<>(20);
    ArrayAdapter threadAdapter;
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
        p = findViewById(R.id.tvProgress);


        progressBar.setVisibility(View.GONE);
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
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                txtprogress.setText(progress+" "+getResources().getString(R.string.seekbar_count));
                selectedProgress = progress;

                for(int i=0;i<progress;i++){
                    threadPool.execute(new DoWorkThread());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

         listviewresult = findViewById(R.id.Listview_complexity);
         Log.d(TAG, "onCreate: result"+result);

        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPool.execute(new DoWorkThread());
                progressBar.setVisibility(View.VISIBLE);
                p.setText(""+getResources().getString(R.string.slash)+selectedProgress);
                btn_asynchtask.setEnabled(false);
                threadButton.setEnabled(false);
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                threadArray.add((Double) msg.obj);

//                ArrayList<Double> receivedMessage = (ArrayList<Double>) msg.getData().get(DoWorkThread.RANDOM_ARRAY);
//                Log.d(TAG, "handleMessage: "+receivedMessage);

                threadAdapter = new ArrayAdapter<Double>(MainActivity.this, android.R.layout.simple_list_item_1,
                        android.R.id.text1,threadArray);

                listView = findViewById(R.id.Listview_complexity);
                listView.setAdapter(threadAdapter);
                threadAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    public class DoWorkThread implements Runnable{

        ArrayList<Double> randomArray = new ArrayList<Double>();
        double randomNumber;
        static final String RANDOM_ARRAY = "RANDOM_ARRAY";

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
        double number;
        ProgressDialog progressDialog;
        int i,avg;
        Double average = 0.0;
        //ArrayList<Double> res =

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
            Double sum = 0.0;
            int complexity_times = integers[0];
            for(i=0;i<complexity_times;i++){
                Double num = HeavyWork.getNumber();
                result.add(num);
                progressBar.setProgress(i);
                sum = sum + num;
                average = sum/(i+1);
                publishProgress(average,Double.valueOf(i));
                Log.d(TAG, "doInBackground: average "+i+" " +average);
            }
            Log.d(TAG, "doInBackground: result "+result);
            Log.d(TAG, "doInBackground: final average "+average);
            return result;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            Log.d(TAG, "onProgressUpdate: "+values[0]+""+values[1]);
            progressBar.setProgress(values[1].intValue());
            txtprogresshappened.setText(i +"/" +selectedProgress);
            txtaverage.setText(getResources().getString(R.string.average)+" " +values[0]);
            resultList.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<Double> s) {
            threadButton.setEnabled(true);
            btn_asynchtask.setEnabled(true);
        }

    }
}
