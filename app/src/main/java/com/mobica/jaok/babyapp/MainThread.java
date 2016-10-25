package com.mobica.jaok.babyapp;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by jaok on 2016-03-07.
 */
public class MainThread extends Thread {

    private static  final String CLASS_NAME = MainThread.class.getSimpleName();
    public static final int MAX_FPS = 30;
    public static final int MAX_FRAME_SKIPPED = 4;
    public static final int MILISECONDS_IN_SECOND = 1000;
    public static final int FRAME_PERIOD = MILISECONDS_IN_SECOND / MAX_FPS;

    private boolean running;
    private SurfaceHolder surfaceHolder;
    private MainGamePanel gamePanel;

    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(CLASS_NAME, "Just before loop start!");

        long beginTime;
        long timeDiff;
        int timeLeft;
        int framesSkipped = 0;

        while (isRunning()) {
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();

                    this.gamePanel.update();
                    this.gamePanel.render(canvas);

                    timeDiff = System.currentTimeMillis() - beginTime;

                    timeLeft = (int) (FRAME_PERIOD - timeDiff);

                    if (timeLeft > 0) {
                        try {
                            Thread.sleep(timeLeft);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        while (timeLeft < 0 && framesSkipped < MAX_FRAME_SKIPPED) {
                            this.gamePanel.update();
                            timeLeft += FRAME_PERIOD;
                            framesSkipped ++;
                            Log.d(CLASS_NAME, "Skipped: " + framesSkipped);
                        }
                    }
                }

            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
