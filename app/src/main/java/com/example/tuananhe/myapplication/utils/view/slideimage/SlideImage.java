package com.example.tuananhe.myapplication.utils.view.slideimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SlideImage extends View {

    private Paint mPaint;
    private int index;
    private List<Bitmap> mListBitmap;
    private int mHeight, mWidth, mBitmapWidth;

    public SlideImage(Context context) {
        this(context, null);
    }

    public SlideImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mHeight = getHeight();
        mWidth = getWidth();
        mBitmapWidth = (int) (mWidth * 1.0f / 10);
    }

    private void init() {
        mPaint = new Paint();
        index = 0;
        if (mListBitmap == null) mListBitmap = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mListBitmap.size() > 0 && index >= 0 && index <= 9)
            drawListBitmap(canvas, index, mListBitmap, mPaint);
    }

    void drawListBitmap(Canvas canvas, int index, List<Bitmap> bm, Paint paint) {
        if (mListBitmap != null && mListBitmap.size() > 0)
            for (int i = 0; i < mListBitmap.size(); i++) {
                Bitmap bitmap = bm.get(i);
                bitmap = Bitmap.createScaledBitmap(bitmap, mBitmapWidth, mHeight, false);
                canvas.drawBitmap(bitmap, i * mBitmapWidth, 0, paint);
            }
    }

    public void addListBitmap(List<Bitmap> bm) {
        mListBitmap = new ArrayList<>();
        mListBitmap.clear();
        mListBitmap.addAll(bm);
        invalidate();
    }

    public void addBitmap(Bitmap bitmap) {
        if (mListBitmap == null) mListBitmap = new ArrayList<>();
        this.mListBitmap.add(bitmap);
        invalidate();
    }
}
