package com.example.tuananhe.myapplication.screen.edit.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.tuananhe.myapplication.R;

public class CropView extends View {
    public static final int NON_SELECT = -1;
    public static final int SELECT_LEFT = 0;
    public static final int SELECT_RIGHT = 1;
    public static final int SELECT_TOP = 2;
    public static final int SELECT_BOTTOM = 3;
    private static final int SELECT_TOP_LEFT = 4;
    private static final int SELECT_TOP_RIGHT = 5;
    private static final int SELECT_BOTTOM_LEFT = 6;
    private static final int SELECT_BOTTOM_RIGHT = 7;
    private static final int SELECT_CENTER = 8;
    private Paint paint;
    private int selectPos = NON_SELECT;
    private float top, left, bottom, right;
    private float x2;
    private float y2;
    private int padding = getResources().getDimensionPixelSize(R.dimen.default_padding_select);
    public int padding_line = 2;
    public int line_size = 20;
    private int minimumSize = 2 * padding + getResources().getDimensionPixelSize(R.dimen.crop_minimum_size);
    private float lineThickness = getResources().getDimensionPixelSize(R.dimen.line_thick_ness);
    private float borderThickness = getResources().getDimensionPixelSize(R.dimen.border_thick_ness);
    private boolean isFixSize = false;
    private float cropRate = -1f;

    public CropView(Context context) {
        super(context);
        init();
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        this.top = 0;
        this.bottom = top;
        this.left = 0;
        this.right = left;
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
            height = heightSpecSize;
        } else if (widthSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
            height = heightMeasureSpec;
        } else if (heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = widthMeasureSpec;
        } else {
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
//            canvas.restore();
            paint.setColor(getResources().getColor(R.color.colorPrimary));
            paint.setStrokeWidth(borderThickness);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRect(rect, paint);

            canvas.drawLine(left + padding_line, top + padding_line, left + line_size, top + padding_line, paint);
            canvas.drawLine(left + padding_line, top + padding_line, left + padding_line, top + line_size, paint);

            canvas.drawLine(left + padding_line, bottom - padding_line, left + line_size, bottom - padding_line, paint);
            canvas.drawLine(left + padding_line, bottom - padding_line, left + padding_line, bottom - line_size, paint);

            canvas.drawLine(right - padding_line, top + padding_line, right - line_size, top + padding_line, paint);
            canvas.drawLine(right - padding_line, top + padding_line, right - padding_line, top + line_size, paint);

            canvas.drawLine(right - padding_line, bottom - padding_line, right - line_size, bottom - padding_line, paint);
            canvas.drawLine(right - padding_line, bottom - padding_line, right - padding_line, bottom - line_size, paint);
            paint.reset();
            paint.setColor(getResources().getColor(R.color.colorPrimary));
            paint.setStrokeWidth(lineThickness);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);

            canvas.drawLine(left + (right - left) / 3, top, left + (right - left) / 3, bottom, paint);
            canvas.drawLine(left + 2 * (right - left) / 3, top, left + 2 * (right - left) / 3, bottom, paint);

            canvas.drawLine(left, top + (bottom - top) / 2, right, top + (bottom - top) / 2, paint);

            paint.reset();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.bg_play_bar));
            rect = new RectF(0, 0, left, getHeight());
            canvas.drawRect(rect, paint);
            rect = new RectF(left, 0, right, top);
            canvas.drawRect(rect, paint);
            rect = new RectF(right, 0, getWidth(), getHeight());
            canvas.drawRect(rect, paint);
            rect = new RectF(left, bottom, right, getHeight());
            canvas.drawRect(rect, paint);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setCropRate(0, 0);
        caculateDimen();
    }

    private void caculateDimen() {
        DisplayMetrics dm = getResources().getSystem().getDisplayMetrics();
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);
        padding_line = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm);
        line_size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, dm);
        minimumSize = 2 * padding + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm);
        lineThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        borderThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float xPos = (int) event.getX();
                float yPos = (int) event.getY();
                float x1 = x2 = xPos;
                float y1 = y2 = yPos;
                if (xPos > left - padding && xPos < left + padding && yPos > top + padding && yPos < bottom - padding) {
                    selectPos = SELECT_LEFT;
                } else if (xPos > right - padding && xPos < right + padding && yPos > top + padding && yPos < bottom - padding) {
                    selectPos = SELECT_RIGHT;
                } else if (xPos > left + padding && xPos < right - padding && yPos > top - padding && yPos < top + padding) {
                    selectPos = SELECT_TOP;
                } else if (xPos > left + padding && xPos < right - padding && yPos > bottom - padding && yPos < bottom + padding) {
                    selectPos = SELECT_BOTTOM;
                } else if (xPos > left - padding && xPos < left + padding && yPos > top - padding && yPos < top + padding) {
                    selectPos = SELECT_TOP_LEFT;
                } else if (xPos > right - padding && xPos < right + padding && yPos > top - padding && yPos < top + padding) {
                    selectPos = SELECT_TOP_RIGHT;
                } else if (xPos > left - padding && xPos < left + padding && yPos > bottom - padding && yPos < bottom + padding) {
                    selectPos = SELECT_BOTTOM_LEFT;
                } else if (xPos > right - padding && xPos < right + padding && yPos > bottom - padding && yPos < bottom + padding) {
                    selectPos = SELECT_BOTTOM_RIGHT;
                } else if (xPos > left + padding && xPos < right - padding && yPos > top + padding && yPos < bottom - padding) {
                    selectPos = SELECT_CENTER;
                } else {
                    selectPos = NON_SELECT;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                int xtmp = (int) event.getX();
                int ytmp = (int) event.getY();

//                if (!isDragging && Math.abs(xtmp - x1) > mTouchSlop && Math.abs(ytmp - y1) > mTouchSlop) {
//                    isDragging = true;
//                }
//                if (isDragging) {
                float moveX = xtmp - x2;
                float moveY = ytmp - y2;

                switch (selectPos) {
                    case SELECT_LEFT:
                        if (isValidMoveLeft(moveX))
                            if (isFixSize) {
                                float move = (moveX / cropRate / 2);
                                if (isValidMoveTop(move) && isValidMoveBottom(-move) && Math.abs(move) > 0) {
                                    left += moveX;
                                    top += move;
                                    bottom -= move;
                                }
                            } else left += moveX;
                        break;
                    case SELECT_RIGHT:
                        if (isValidMoveRight(moveX))
                            if (isFixSize) {
                                float move = (moveX / cropRate / 2);
                                if (isValidMoveTop(-move) && isValidMoveBottom(move) && Math.abs(move) > 0) {
                                    right += moveX;
                                    top -= move;
                                    bottom += move;
                                }
                            } else
                                right += moveX;
                        break;
                    case SELECT_TOP:
                        if (isValidMoveTop(moveY))
                            if (isFixSize) {
                                float move = (moveY * cropRate / 2);
                                if (isValidMoveLeft(move) && isValidMoveRight(-move) && Math.abs(move) > 0) {
                                    top += moveY;
                                    left += move;
                                    right -= move;
                                }
                            } else
                                top += moveY;
                        break;
                    case SELECT_BOTTOM:
                        if (isValidMoveBottom(moveY))
                            if (isFixSize) {
                                float move = (moveY * cropRate / 2);
                                if (isValidMoveLeft(-move) && isValidMoveRight(move) && Math.abs(move) > 0) {
                                    bottom += moveY;
                                    left -= move;
                                    right += move;
                                }
                            } else
                                bottom += moveY;
                        break;
                    case SELECT_CENTER:
                        if (top + moveY > 0 && top + moveY < getHeight())
                            if (bottom + moveY > 0 && bottom + moveY < getHeight()) {
                                top += moveY;
                                bottom += moveY;
                            }
                        if (left + moveX > 0 && left + moveX < getWidth())
                            if (right + moveX > 0 && right + moveX < getWidth()) {
                                left += moveX;
                                right += moveX;
                            }
                        break;
                    case SELECT_TOP_LEFT:
                        if (isFixSize) {
                            if (moveX * moveY > 0) {
                                float move = (Math.abs(moveX) < Math.abs(moveY)) ? moveX : moveY;
                                float mox = move / cropRate;
                                if (isValidMoveLeft(move) && isValidMoveTop(mox)) {
                                    left += move;
                                    top += mox;
                                }
                            }
                        } else {
                            if (isValidMoveTop(moveY))
                                top += moveY;
                            if (isValidMoveLeft(moveX)) {
                                left += moveX;
                            }
                        }
                        break;
                    case SELECT_TOP_RIGHT:
                        if (isFixSize) {
                            if (moveX * moveY < 0) {
                                float move = (Math.abs(moveX) < Math.abs(moveY)) ? moveX : moveY;
                                move = Math.abs(move);
                                float signRight = Math.signum(moveX);
                                float signTop = Math.signum(moveY);
                                float mox = Math.abs(move / cropRate);
                                if (isValidMoveRight(signRight * move) && isValidMoveTop(signTop * mox) && Math.abs(mox) > 0 && Math.abs(move) > 0) {
                                    right += signRight * move;
                                    top += signTop * mox;
                                }
                            }
                        } else {
                            if (isValidMoveTop(moveY))
                                top += moveY;
                            if (isValidMoveRight(moveX))
                                right += moveX;
                        }
                        break;
                    case SELECT_BOTTOM_LEFT:
                        if (isFixSize) {
                            if (moveX * moveY < 0) {
                                float move = (Math.abs(moveX) < Math.abs(moveY)) ? moveX : moveY;
                                move = Math.abs(move);
                                float signLeft = Math.signum(moveX);
                                float signBottom = Math.signum(moveY);
                                Log.e("signL, SignB", signLeft + "===" + signBottom);
                                float mox = Math.abs(move / cropRate);
                                if (isValidMoveLeft(signLeft * move) && isValidMoveBottom(signBottom * mox) && Math.abs(mox) > 0 && Math.abs(move) > 0) {
                                    left += signLeft * move;
                                    bottom += signBottom * mox;
                                }
                            }
                        } else {
                            if (isValidMoveBottom(moveY))
                                bottom += moveY;
                            if (isValidMoveLeft(moveX))
                                left += moveX;
                        }
                        break;
                    case SELECT_BOTTOM_RIGHT:
                        if (isFixSize) {
                            if (moveX * moveY > 0) {
                                float move = (Math.abs(moveX) < Math.abs(moveY)) ? moveX : moveY;
                                float mox = move / cropRate;
                                if (isValidMoveRight(move) && isValidMoveBottom(mox)) {
                                    right += move;
                                    bottom += mox;
                                }
                            }
                        } else {
                            if (isValidMoveBottom(moveY))
                                bottom += moveY;
                            if (isValidMoveRight(moveX))
                                right += moveX;
                        }
                        break;
                }
                x2 = xtmp;
                y2 = ytmp;
                invalidate();
                return true;
//                }

            case MotionEvent.ACTION_UP:
                x1 = x2 = 0;
                y1 = y2 = 0;
//                selectPos = NON_SELECT;
                boolean isDragging = false;
                return true;
            default:
                return false;
        }

//        return false;
    }

    boolean isValidMoveLeft(float move) {
        return left + move > 0 && left + move < getWidth() && right - left - move > minimumSize;
    }

    boolean isValidMoveRight(float move) {
        return right + move > 0 && right + move < getWidth() && right - left + move > minimumSize;
    }

    boolean isValidMoveTop(float move) {
        return top + move > 0 && top + move < getHeight() && bottom - top - move > minimumSize;
    }

    boolean isValidMoveBottom(float move) {
        return bottom + move > 0 && bottom + move < getHeight() && bottom - top + move > minimumSize;
    }


    public float getWidthRate() {
        return (right - left) * 1.0f / getWidth();
    }

    public float getHeightRate() {
        return (bottom - top) * 1.0f / getHeight();
    }

    public float getXRate() {
        return left * 1.0f / getWidth();
    }

    public float getYRate() {
        return top * 1.0f / getHeight();
    }

    public void setCropRate(int w, int h) {
        if (w == 0 || h == 0) {
            cropRate = -1;
            isFixSize = false;
            top = getHeight() / 8;
            left = getWidth() / 8;
            right = getWidth() - left;
            bottom = getHeight() - top;
        } else {
            isFixSize = true;
            cropRate = w * 1.0f / h;
            if (getWidth() * 1.0f / getHeight() > cropRate) {
                top = 0;
                bottom = getHeight();
                left = (int) ((getWidth() - getHeight() * cropRate) / 2);
                right = (int) ((getWidth() + getHeight() * cropRate) / 2);
            } else {
                left = 0;
                right = getWidth();
                top = (int) ((getHeight() - getWidth() / cropRate) / 2);
                bottom = (int) ((getHeight() + getWidth() / cropRate) / 2);
            }
        }
        invalidate();
    }
}
