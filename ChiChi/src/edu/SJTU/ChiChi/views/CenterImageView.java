package edu.SJTU.ChiChi.views;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: JeffreyZhang
 * Date: 13-8-13
 * Time: 下午4:58
 */
public class CenterImageView extends ImageView {

    public CenterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
    }

    public CenterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }

    public CenterImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (getDrawable() == null) {
            return super.setFrame(l, t, r, b);
        }
        Matrix matrix = getImageMatrix();
        float scaleFactor = getHeight() / (float) getDrawable().getIntrinsicHeight();
        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
        setImageMatrix(matrix);
        return super.setFrame(l, t, r, b);
    }

}
