package com.example.inclass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button threadButton;
    ExecutorService threadPool;
    Handler handler;
    ProgressBar progressBar;
    String TAG = "demo";
    SeekBar seekBar;
    TextView txtprogress;
    int seekBarProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadPool = Executors.newFixedThreadPool(2);
        threadButton = findViewById(R.id.btnThread);
        progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);
        txtprogress = findViewById(R.id.textView_complexity);

        threadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadPool.execute(new DoWork());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtprogress.setText(progress+" "+getResources().getString(R.string.seekbar_count));
                seekBarProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
            Log.d(TAG, "run: "+seekBarProgress);
            for(int i=0;i<seekBarProgress;i++) {
                double randomNumbers = HeavyWork.getNumber();
                Log.d(TAG, "run: ");
            }
            Log.d(TAG, "run: end");
        }
    }

    }
