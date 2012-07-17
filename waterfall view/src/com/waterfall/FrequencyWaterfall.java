package com.waterfall;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FrequencyWaterfall extends View {
	
	private LinkedList<Bitmap> lines;
	private Paint paint;
	private Paint fPaint;
	private Paint wPaint;
	private int height = 200;
	private int width = 400;
	private static int currentX = -1;
	private int bandwidth = 2000;

	public FrequencyWaterfall(Context context) {
		super(context);
	    init(context);
	}
	
	public FrequencyWaterfall(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	
	public FrequencyWaterfall(Context context, AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
		init(context);
	}
	
	private void init(Context c){
		paint = new Paint();
		fPaint = new Paint();
		fPaint.setColor(Color.RED);
		wPaint = new Paint();
		wPaint.setColor(Color.WHITE);
		lines = new LinkedList<Bitmap>();
	}
	
	public void addLine(Bitmap temp){
		Bitmap b = temp.copy(temp.getConfig(), false);
		lines.addFirst(b);
		if(lines.size()==height+1)
			lines.removeLast();
	}
	
	public void onDraw(Canvas c){
		int y = 0;
		for(Bitmap b : lines){
			c.drawBitmap(b, 0, y++,paint);
		}
		
		if(currentX == -1)
			return;
		
		c.drawLine(currentX, 0, currentX, height, fPaint);
		double freq = ((double)currentX/width)*bandwidth;
		if(currentX < 20){
			c.drawText(String.valueOf(freq) + " Hz", currentX, height - 10, wPaint);
		}else if(currentX > (width - 20)){
			c.drawText(String.valueOf(freq) + " Hz", currentX - 50, height - 10, wPaint);
		}else
			c.drawText(String.valueOf(freq) + " Hz", currentX - 20, height - 10, wPaint);
	}
	
	@Override
	public void onMeasure(int width,int height){
		super.setMeasuredDimension(this.width, this.height);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_UP:
			currentX = (int) event.getX();
			return true;
		}
		
		return false;
	}
	
	public void incrementFreq(float value){
		currentX += value;
	}

}
