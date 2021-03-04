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
    TextView p;
    ListView listView;
    ArrayList<Double> threadArray = new ArrayList<>(20);
    ArrayAdapter threadAdapter;

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

        btn_asynchtask = findViewById(R.id.btn_asynckTask);
        btn_asynchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoWorkAsynch().execute(selectedProgress);
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
//        ListView listviewresult = findViewById(R.id.Listview_complexity);
//        ArrayAdapter<Integer> resultadapter = new ArrayAdapter<>();
        //listviewresult.setAdapter();

        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPool.execute(new DoWorkThread());
                progressBar.setVisibility(View.VISIBLE);
                p.setText(""+getResources().getString(R.string.slash)+selectedProgress);
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


