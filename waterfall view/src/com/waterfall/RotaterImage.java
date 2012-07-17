package com.waterfall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class RotaterImage extends ImageView {

	private static Bitmap imageOriginal, imageScaled;
	private static Matrix matrix;
	private FrequencyPlayer freq;
	private boolean[] quadrantTouched;
	private boolean allowRotating;
	private double startAngle;
	private int dialerHeight = 100, dialerWidth = 100;
	
	public RotaterImage(Context context) {
		super(context);
		init(context);
	}
	
	public RotaterImage(Context context,AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	
	public RotaterImage(Context context,AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
		init(context);
	}
	
	public void init(Context context){
		invalidate();
		
		freq = new FrequencyPlayer();
        imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.dial);
        matrix = new Matrix();
        
        // there is no 0th quadrant, to keep it simple the first value gets ignored
        quadrantTouched = new boolean[] { false, false, false, false, false };
        allowRotating = true;
        
        if (dialerHeight == 0 || dialerWidth == 0) {
			dialerHeight = getHeight();
			dialerWidth = getWidth();
			
			// resize
			Matrix resize = new Matrix();
			resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getHeight());
			imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
			
			// translate to the image view's center
			float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
			float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
			matrix.postTranslate(translateX, translateY);
			
			setImageBitmap(imageScaled);
			setImageMatrix(matrix);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			
			// reset the touched quadrants
			for (int i = 0; i < quadrantTouched.length; i++) {
				quadrantTouched[i] = false;
			}
			
			allowRotating = false;
			
			startAngle = getAngle(event.getX(), event.getY());
			break;
			
		case MotionEvent.ACTION_MOVE:
			double currentAngle = getAngle(event.getX(), event.getY());
			if(Math.abs(startAngle - currentAngle) > 100)
				rotateDialer((float)Math.abs((float)(startAngle - currentAngle)) - 360);
			else
				rotateDialer((float) (startAngle - currentAngle));
			startAngle = currentAngle;
			break;
			
		case MotionEvent.ACTION_UP:
			allowRotating = true;
			break;
		}
	
		// set the touched quadrant to true
		quadrantTouched[getQuadrant(event.getX() - (dialerWidth / 2), dialerHeight - event.getY() - (dialerHeight / 2))] = true;
	
		return true;
	}
	
	/**
	 * @return The selected quadrant.
	 */
	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}
	
	/**
	 * @return The angle of the unit circle with the image view's center
	 */
	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (dialerWidth / 2d);
		double y = dialerHeight - yTouch - (dialerHeight / 2d);

		switch (getQuadrant(x, y)) {
			case 1:
				return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
			
			case 2:
			case 3:
				return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
			
			case 4:
				return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
			
			default:
				// ignore, does not happen
				return 0;
		}
	}
	
	public void onDestroy(){
		freq.onDestroy();
	}
	
	/**
	 * Rotate the dialer.
	 * 
	 * @param degrees The degrees, the dialer should get rotated.
	 */
	private void rotateDialer(float degrees) {
		matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
		freq.changeFrequency(degrees);
		freq.play();
		setImageMatrix(matrix);
		//onRotateDial(degrees);
		
	}
	
	
	
	protected void onMeasure(int width, int height){
		super.onMeasure(dialerWidth, dialerHeight);
		setMeasuredDimension(dialerWidth, dialerHeight);
	}

}
