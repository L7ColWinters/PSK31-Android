package com.waterfall;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WaterfallviewActivity extends Activity {
	
	private FrequencyWaterfall waterfall;
	private RotaterImage dial;
	private Timer timer;
	private TimerTask task;
	private Handler handler;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        waterfall = (FrequencyWaterfall)findViewById(R.id.waterfall);
        
        dial = new RotaterImage(this);
        
        timer = new Timer();
        handler = new Handler(){
        	@Override
        	public void handleMessage(Message m){
        		createTestWaterfall();
        	}
        };
        task = new TimerTask(){
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
        };
        timer.schedule(task, 0,70);
    }
    
    private void createTestWaterfall(){
    	int [] colors = new int[400];
    	for(int i=0;i<colors.length;i++){
    		if(Math.random()<0.3)
    			colors[i] = Color.GREEN;
    		else
    			colors[i] = Color.BLACK;	
    	}
    	Bitmap b = Bitmap.createBitmap(colors, 400, 1, Bitmap.Config.RGB_565);
    	waterfall.addLine(b);
    	b.recycle();
    	waterfall.invalidate();
    }
}