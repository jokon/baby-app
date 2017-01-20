package com.mobica.jaok.babyapp.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by jaok on 2016-03-08.
 */
public class Symbol implements Comparable{
    private int MAX_HEIGHT = 250;
    private int minHeight;
    private int minWidth;
    private static final int LIVETIME = 100;
    private static final int DISAPEARING_TIME = 8;

    private Bitmap scaledBitmap;
    private Bitmap bitmap;
    private Paint paint;
    private int x;
    private int y;
    private int age = LIVETIME;
    private boolean touched;
    private int number;

    //private int rotation = 0;
    private int currentAlpha = 255;
    private int height = 400;
    private int width = 400;

    static int numberCount = 0;

    public Symbol(Bitmap bitmap, int x, int y, boolean touched) {
        this.x = x;
        this.y = y;
        this.touched = true;
        this.number = numberCount ++;

        Double proportion = bitmap.getHeight() / new Double(bitmap.getWidth());

        height = Math.min(bitmap.getHeight(), MAX_HEIGHT);
        width =  ((Double) (height/proportion)).intValue();

        minHeight = height /2;
        minWidth = width /2;

        this.bitmap = bitmap; //rotateBitmapRandomly(bitmap);
        update();
    }

    private Bitmap rotateBitmapRandomly(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int rotation = (int) (Math.random() * 360.0);
        matrix.postRotate(rotation);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return rotatedBitmap;
    }

    public void update() {
        scaledBitmap = resizeBitmap(bitmap);
        paint = updatePaint();

        age--;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(scaledBitmap, getLeftEdge(), getTopEdge(), paint);
    }

    private Paint updatePaint() {
        Paint paint = null;
        if (age < DISAPEARING_TIME) {
            paint = new Paint();
            currentAlpha = currentAlpha - 255/DISAPEARING_TIME;
            paint.setAlpha(currentAlpha);
        }

        return paint;
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        double sizingTime = LIVETIME - DISAPEARING_TIME;
        int currentSizeProgress = age - DISAPEARING_TIME >= 0 ? age - DISAPEARING_TIME : 0;
        int height = minHeight + (int)(((sizingTime - currentSizeProgress)/sizingTime) * (double) (this.height - minHeight));
        int width = minWidth + (int)(((sizingTime - currentSizeProgress)/sizingTime) * (double) (this.width - minWidth));

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        return scaledBitmap;
    }

    public void move(int x, int y) {
        setX(x);
        setY(y);
    }

    public boolean isTouched(int x, int y) {
        return (x <= getRightEdge() && x >= getLeftEdge()) && (y >= getTopEdge() && y <= getBottomEdge());
    }

    public int getLeftEdge() {
        return getX() - getScaledBitmap().getWidth() /2;
    }

    public int getRightEdge() {
        return getX() + getScaledBitmap().getWidth() /2;
    }

    public int getTopEdge() {
        return getY() - getScaledBitmap().getHeight() /2;
    }

    public int getBottomEdge() {
        return getY() + getScaledBitmap().getHeight() /2;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public int getAge() {
        return age;
    }

    public Bitmap getScaledBitmap() {
        return scaledBitmap;
    }

    public void setScaledBitmap(Bitmap scaledBitmap) {
        this.scaledBitmap = scaledBitmap;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(Object another) {

        if (another instanceof Symbol) {
            return this.getAge() > ((Symbol) another).getAge() ? -1 : 1;

        }

        return 0;
    }

    @Override
    public String toString() {
        return String.format("SBL [%d] (%d, %d, %d)", number, age, x,y);
    }

    public static SymbolSpecific getExample() {
        SymbolSpecific[] symbols = SymbolSpecific.values();
        int randomSymbolNumber = (int) (Math.random() * (double) symbols.length);

        return symbols[randomSymbolNumber];
    }


    public enum SymbolSpecific {

        BEE, BIRD, BULLDOG("dog"), DUCK, ELEPHANT, LION, MONKEY, OWL, SHEEP,SNAKE, COW, PIG,
        BEAR, DADDY, EMILY, FERNANDO, GORGE, MASZA, MUMMY, PEPPA
        //BULL, FISH, FROG, MOUSE, WOLF
        ;

        private String name;
        private String sound;

        SymbolSpecific() {
            this.name = name().toLowerCase();
            sound = this.name;
        }

        SymbolSpecific(String sound) {
            this();
            this.sound = sound;
        }

        public String getSound() {
            return sound;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
