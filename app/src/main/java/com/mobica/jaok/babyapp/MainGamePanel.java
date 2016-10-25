package com.mobica.jaok.babyapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mobica.jaok.babyapp.model.Symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaok on 2016-03-07.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final String CLASS_NAME = MainGamePanel.class.getSimpleName();
    public static final int BOTTOM_HEIGHT_TO_EXIT = 100;

    private MainThread thread;

    private List<Symbol> symbols = Collections.synchronizedList(new ArrayList<Symbol>());
    private Map<Symbol.SymbolSpecific, MediaPlayer> symbolSounds = new HashMap<>();
    private Symbol touchedSymbol = null;

    public void update() {
        synchronized (symbols) {
            Collection<Symbol> symbolsToRemove = new ArrayList<>();
            for (Symbol symbol : getSymbols()) {
                symbol.update();
                if (symbol.getAge() <= 0) {
                    symbolsToRemove.add(symbol);
                }
            }
            getSymbols().removeAll(symbolsToRemove);
        }
    }

    public void render(Canvas canvas) {
        //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ant), 20, 20, null);

        canvas.drawColor(Color.BLACK);
        drawBottomLine(canvas);
        drawSymbols(canvas);
    }

    private void drawBottomLine(Canvas canvas) {
//        Log.d(CLASS_NAME, "Line drawing...");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        //p.setColor(Color.RED);
        canvas.drawLine(0, getHeight() - BOTTOM_HEIGHT_TO_EXIT, getWidth(), getHeight() - BOTTOM_HEIGHT_TO_EXIT + 1, paint);
    }

    private void drawSymbols(Canvas canvas) {

        synchronized (symbols) {
            Collections.sort(getSymbols());
            for (Symbol symbol : getSymbols()) {
                symbol.draw(canvas);
            }

        }
    }

    public MainGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    public int getResourceId(String name, String defType) {
        int resId = getContext().getResources().getIdentifier(name, defType, getContext().getPackageName());
        if (resId == 0) {
            Log.e(CLASS_NAME, "Resource was not found for name " + name);
        }
        return resId;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        while(retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d(CLASS_NAME, "ACTION: " + event.toString());

        switch (event.getAction()) {
            case(MotionEvent.ACTION_DOWN):
                handleActionDown(event) ;
                break;
            case (MotionEvent.ACTION_MOVE):
                handleActionMove(event);
                break;
            case (MotionEvent.ACTION_UP):
                handleActionUp(event);
                break;
        }

        return true;
    }

    private void handleActionDown(MotionEvent event) {
        Log.d(CLASS_NAME, String.format("Touched coordinates are: %f %f; (%f)", event.getX(), event.getY(), getHeight() - event.getY()));

        if (event.getY() > getHeight() - BOTTOM_HEIGHT_TO_EXIT) {
            thread.setRunning(false);
            ((Activity)getContext()).finish();
        } else {
            Symbol symbol = findTouchedSymbol((int) event.getX(), (int) event.getY());
            if (symbol != null) {
                Log.d(CLASS_NAME, "You touched existing symbol (" +symbol+ ")");
                setTouchedSymbol(symbol);
            } else {
                Log.d(CLASS_NAME, "Creating new symbol...");

                Symbol.SymbolSpecific exampleSymbol = Symbol.getExample();

                int resource = getResourceId(exampleSymbol.getName(), "mipmap");

                if (resource != 0) {
                    symbol = new Symbol(BitmapFactory.decodeResource(getResources(), resource), (int) event.getX(), (int) event.getY(), true);


                    MediaPlayer mp = getSoundForSymbol(exampleSymbol);
                    if (mp != null)
                        mp.start();

                    synchronized (symbols) {
                        getSymbols().add(symbol);
                    }
                    setTouchedSymbol(symbol);
                }
            }
        }
    }

    private MediaPlayer getSoundForSymbol(Symbol.SymbolSpecific exampleSymbol) {
        MediaPlayer mp = symbolSounds.get(exampleSymbol);

        if (mp == null) {
            int soundResource = getResourceId(exampleSymbol.getSound(), "raw");
            if (soundResource != 0)
                mp = MediaPlayer.create(getContext().getApplicationContext(), soundResource);

            symbolSounds.put(exampleSymbol, mp);
        }

        return mp;
    }


    private Symbol findTouchedSymbol(int x, int y) {
        setTouchedSymbol(null);

        synchronized (symbols) {
            for (Symbol symbol : getSymbols()) {
                if (symbol.isTouched(x, y)) {
                    setTouchedSymbol(symbol);
                }
            }
        }

        return getTouchedSymbol();
    }


    private void handleActionMove(MotionEvent event) {
        Log.d(CLASS_NAME, String.format("Moved coordinates are: %f %f", event.getX(), event.getY()));
        if (getTouchedSymbol() != null) {
            getTouchedSymbol().move((int) event.getX(), (int) event.getY());
        }
    }

    private void handleActionUp(MotionEvent event) {
        Log.d(CLASS_NAME, String.format("Up coordinates are: %f %f", event.getX(), event.getY()));
    }

    public List<Symbol> getSymbols() {
        synchronized (symbols) {
            return symbols;
        }
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public Symbol getTouchedSymbol() {
        return touchedSymbol;
    }

    public void setTouchedSymbol(Symbol touchedSymbol) {
        this.touchedSymbol = touchedSymbol;
    }
}
