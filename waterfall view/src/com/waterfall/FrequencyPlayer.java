package com.waterfall;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

public class FrequencyPlayer {

	private final float duration = 0.2f; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = 400;//duration * sampleRate
    private final double sample[] = new double[numSamples];
    private double freqOfTone = 15000; // hz
    private byte generatedSnd[] = new byte[2 * numSamples];
    private Handler handler = new Handler();
    public boolean running = true,playing = false;
    private static AudioTrack audioTrack;
    private final Runnable runnable;
    private final Thread thread;
	
	public FrequencyPlayer(){
		runnable = new Runnable() {
            public void run() {
                playSound();
            }
        };
		thread = new Thread(new Runnable() {
            public void run() {
            	while(running){
            		if(playing){
            			synchronized(FrequencyPlayer.this){
            				genTone();
            			}
		                handler.post(runnable);
            		}
	                try {
	                	int i = (int)(duration*500);
						Thread.sleep(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            	}
            }
        });
        thread.start();
	}
	
	public void play(){
		playing = true;
	}
	
	private void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    private void playSound(){
    	if(audioTrack == null){
    		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, numSamples*5,
                    AudioTrack.MODE_STREAM);
    		audioTrack.write(generatedSnd, 0, generatedSnd.length);
            if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED)
            	audioTrack.play();
    	}else{
    		audioTrack.write(generatedSnd, 0, generatedSnd.length);
    	}
        playing = false;
    }
	
	public void setFrequency(double frequency){
		freqOfTone = frequency; 
	}
	
	public void changeFrequency(double delta){
		freqOfTone += delta;
		System.out.println("FREQ: " + freqOfTone);
	}
	
	public double getFrequency(){
		return freqOfTone;
	}
	
	public void onDestroy(){
		running = false;
		audioTrack.pause();
		audioTrack.flush();
		audioTrack.release();
	}
}
