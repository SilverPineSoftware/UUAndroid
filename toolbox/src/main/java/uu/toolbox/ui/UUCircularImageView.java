package uu.toolbox.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import uu.toolbox.R;

public class UUCircularImageView extends AppCompatImageView
{
    private final Path clippingPath = new Path();
    private int clipX;
    private int clipY;
    private int clipRadius;
    private Paint borderPaint;

    public UUCircularImageView(Context context)
    {
        super(context);
        init(null);
    }

    public UUCircularImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public UUCircularImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@Nullable final AttributeSet attrs)
    {
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UUCircularImageView);

            int bordeWidth = a.getDimensionPixelSize(R.styleable.UUCircularImageView_borderWidth, - 1);
            int borderColor = a.getColor(R.styleable.UUCircularImageView_borderColor, -1);

            if (borderColor != -1 && bordeWidth != -1)
            {
                borderPaint = new Paint();
                borderPaint.setDither(true);
                borderPaint.setAntiAlias(true);
                borderPaint.setColor(borderColor);
                borderPaint.setStrokeWidth(bordeWidth);
                borderPaint.setStrokeCap(Paint.Cap.ROUND);
                borderPaint.setStyle(Paint.Style.STROKE);
            }

            a.recycle();
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        if (changed)
        {
            clippingPath.reset();

            int w = (right - left);
            int h = (bottom - top);
            clipX = w / 2;
            clipY = h / 2;
            clipRadius = Math.min(w, h) / 2;
            clippingPath.addCircle(clipX, clipY, clipRadius, Path.Direction.CW);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.clipPath(clippingPath);
        super.onDraw(canvas);

        if (borderPaint != null)
        {
            canvas.drawCircle(clipX, clipY, clipRadius, borderPaint);
        }
    }
}