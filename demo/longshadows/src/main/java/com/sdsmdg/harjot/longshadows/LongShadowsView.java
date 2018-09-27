package com.sdsmdg.harjot.longshadows;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.longshadows.models.ShadowPath;
import com.sdsmdg.harjot.longshadows.shadowutils.Utils;

import java.util.ArrayList;

/**
 * Created by Harjot on 2/26/2018.
 */

public class LongShadowsView extends View {

    private Context context;

    private ArrayList<ShadowPath> shadowPaths;
    private Paint shadowPaint;

    private boolean isShadowDirty = true;

    private String shadowAngle = Constants.DEFAULT_SHADOW_ANGLE;
    private int shadowStartColor = Constants.DEFAULT_SHADOW_START_COLOR;
    private int shadowEndColor = Constants.DEFAULT_SHADOW_END_COLOR;
    private String shadowLength = Constants.DEFAULT_SHADOW_LENGTH;
    private boolean shadowBlurEnabled = Constants.DEFAULT_SHADOW_BLUR_ENABLED;
    private float shadowBlurRadius = Constants.DEFAULT_SHADOW_BLUR_RADIUS;
    private int shadowAlpha = Constants.DEFAULT_SHADOW_ALPHA;
    private boolean backgroundTransparent = Constants.DEFAULT_BACKGROUND_TRANSPARENT;
    private int backgroundColorToIgnore = Constants.DEFAULT_BACKGROUND_COLOR_TO_IGNORE;

    private ViewGroup parentLongShadowWrapper;

    public LongShadowsView(Context context) {
        super(context);
        this.context = context;
    }

    public LongShadowsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.initBaseXMLAttrs(context, attrs);
        this.initBasePaintsAndArrays();
    }

    public LongShadowsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.initBaseXMLAttrs(context, attrs);
        this.initBasePaintsAndArrays();
    }

    final void initBaseXMLAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LongShadowsView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.LongShadowsView_shadow_angle) {
                shadowAngle = a.getString(attr);
            } else if (attr == R.styleable.LongShadowsView_shadow_startColor) {
                shadowStartColor = a.getColor(attr, Constants.DEFAULT_SHADOW_START_COLOR);
            } else if (attr == R.styleable.LongShadowsView_shadow_endColor) {
                shadowEndColor = a.getColor(attr, Constants.DEFAULT_SHADOW_END_COLOR);
            } else if (attr == R.styleable.LongShadowsView_shadow_length) {
                shadowLength = a.getString(attr);
            } else if (attr == R.styleable.LongShadowsView_shadow_blur_enabled) {
                shadowBlurEnabled = a.getBoolean(attr, Constants.DEFAULT_SHADOW_BLUR_ENABLED);
            } else if (attr == R.styleable.LongShadowsView_shadow_blur_radius) {
                shadowBlurRadius = a.getFloat(attr, Constants.DEFAULT_SHADOW_BLUR_RADIUS);
            } else if (attr == R.styleable.LongShadowsView_shadow_alpha) {
                shadowAlpha = a.getInt(attr, Constants.DEFAULT_SHADOW_ALPHA);
            } else if (attr == R.styleable.LongShadowsView_background_transparent) {
                backgroundTransparent = a.getBoolean(attr, Constants.DEFAULT_BACKGROUND_TRANSPARENT);
            } else if (attr == R.styleable.LongShadowsView_background_color_to_ignore) {
                backgroundColorToIgnore = a.getColor(attr, Constants.DEFAULT_BACKGROUND_COLOR_TO_IGNORE);
            }
        }
        a.recycle();
    }

    final void initBasePaintsAndArrays() {
        shadowPaths = new ArrayList<>();

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setAlpha(shadowAlpha);
        if (shadowBlurEnabled) {
            shadowPaint.setMaskFilter(new BlurMaskFilter(shadowBlurRadius, BlurMaskFilter.Blur.NORMAL));
        }
    }

    @Override
    public void draw(Canvas canvas) {

        if (backgroundTransparent) {
            if (shadowPaths != null && shadowPaths.size() > 0) {
                for (ShadowPath shadowPath : shadowPaths) {
                    shadowPaint.setShader(Utils.generateLinearGradient(shadowPath, shadowStartColor, shadowEndColor));
                    canvas.drawPath(shadowPath.getPath(), shadowPaint);
                }
            }
        }

        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!backgroundTransparent) {
            if (shadowPaths != null && shadowPaths.size() > 0) {
                for (ShadowPath shadowPath : shadowPaths) {
                    shadowPaint.setShader(Utils.generateLinearGradient(shadowPath, shadowStartColor, shadowEndColor));
                    canvas.drawPath(shadowPath.getPath(), shadowPaint);
                }
            }
        }

        super.onDraw(canvas);

    }

    public void setParentLongShadowWrapper(ViewGroup v) {
        this.parentLongShadowWrapper = v;
    }

    public void setShadowPaths(ArrayList<ShadowPath> shadowPaths) {
        this.shadowPaths = shadowPaths;
        isShadowDirty = false;
    }

    public void updateWithShadowAlpha(int shadowAlpha) {
        if (shadowAlpha != -1) {
            shadowPaint.setAlpha(shadowAlpha);
        }
        invalidate();
    }

    public void update() {
        invalidate();
        if (isShadowDirty) {
            if (parentLongShadowWrapper != null) {
                parentLongShadowWrapper.requestLayout();
            }
        }
    }

    public boolean isShadowDirty() {
        return isShadowDirty;
    }

    public String getShadowAngle() {
        return shadowAngle;
    }

    public void setShadowAngle(String shadowAngle) {
        this.shadowAngle = shadowAngle;
        isShadowDirty = true;
        shadowPaths = null;
    }

    public int getShadowStartColor() {
        return shadowStartColor;
    }

    public void setShadowStartColor(int shadowStartColor) {
        this.shadowStartColor = shadowStartColor;
    }

    public int getShadowEndColor() {
        return shadowEndColor;
    }

    public void setShadowEndColor(int shadowEndColor) {
        this.shadowEndColor = shadowEndColor;
    }

    public String getShadowLength() {
        return shadowLength;
    }

    public void setShadowLength(String shadowLength) {
        this.shadowLength = shadowLength;
        isShadowDirty = true;
        shadowPaths = null;
    }

    public boolean isShadowBlurEnabled() {
        return shadowBlurEnabled;
    }

    public void setShadowBlurEnabled(boolean shadowBlurEnabled) {
        this.shadowBlurEnabled = shadowBlurEnabled;
        if (shadowBlurEnabled) {
            shadowPaint.setMaskFilter(new BlurMaskFilter(shadowBlurRadius, BlurMaskFilter.Blur.NORMAL));
        } else {
            shadowPaint.setMaskFilter(null);
        }
    }

    public float getShadowBlurRadius() {
        return shadowBlurRadius;
    }

    public void setShadowBlurRadius(float shadowBlurRadius) {
        this.shadowBlurRadius = shadowBlurRadius;
        if (shadowBlurEnabled) {
            shadowPaint.setMaskFilter(new BlurMaskFilter(shadowBlurRadius, BlurMaskFilter.Blur.NORMAL));
        }
    }

    public int getShadowAlpha() {
        return shadowAlpha;
    }

    public void setShadowAlpha(int shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
        shadowPaint.setAlpha(shadowAlpha);
    }

    public boolean isBackgroundTransparent() {
        return backgroundTransparent;
    }

    public void setBackgroundTransparent(boolean backgroundTransparent) {
        this.backgroundTransparent = backgroundTransparent;
        isShadowDirty = true;
        shadowPaths = null;
    }

    public int getBackgroundColorToIgnore() {
        return backgroundColorToIgnore;
    }

    public void setBackgroundColorToIgnore(int backgroundColorToIgnore) {
        this.backgroundColorToIgnore = backgroundColorToIgnore;
        isShadowDirty = true;
        shadowPaths = null;
    }

}
