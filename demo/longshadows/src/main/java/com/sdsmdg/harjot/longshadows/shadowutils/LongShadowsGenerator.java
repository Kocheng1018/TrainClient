package com.sdsmdg.harjot.longshadows.shadowutils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.longshadows.Constants;
import com.sdsmdg.harjot.longshadows.LongShadowsFrameLayoutWrapper;
import com.sdsmdg.harjot.longshadows.LongShadowsImageView;
import com.sdsmdg.harjot.longshadows.LongShadowsTextView;
import com.sdsmdg.harjot.longshadows.LongShadowsView;
import com.sdsmdg.harjot.longshadows.LongShadowsWrapper;
import com.sdsmdg.harjot.longshadows.models.ShadowPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.sdsmdg.harjot.longshadows.Constants.POS_UPDATE_ALL;

/**
 * Created by Harjot on 27-Jan-18.
 */

public class LongShadowsGenerator {

    private final Object TASKS_LOCK = new Object();

    private List<Future<?>> tasksInProgress = new ArrayList<>();
    private ExecutorService workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    private SparseArray<ShadowPath[]> viewShadowPaths;

    private ViewGroup viewGroup;
    private ArrayList<View> shadowChildren;

    private boolean shouldShowWhenAllReady = Constants.DEFAULT_SHOW_WHEN_ALL_READY;
    private boolean shouldCalculateAsync = Constants.DEFAULT_CALCULATE_ASYNC;
    private boolean shouldAnimateShadow = Constants.DEFAULT_ANIMATE_SHADOW;
    private int animationDuration = Constants.DEFAULT_ANIMATION_TIME;

    private int childrenWithShadow;

    static {
        System.loadLibrary("native-lib");
    }

    public LongShadowsGenerator(ViewGroup viewGroup, boolean shouldShowWhenAllReady, boolean shouldCalculateAsync, boolean shouldAnimateShadow, int animationDuration) {
        this.viewGroup = viewGroup;
        this.shouldShowWhenAllReady = shouldShowWhenAllReady;
        this.shouldCalculateAsync = shouldCalculateAsync;
        this.shouldAnimateShadow = shouldAnimateShadow;
        this.animationDuration = animationDuration;
    }

    public boolean isShouldShowWhenAllReady() {
        return shouldShowWhenAllReady;
    }

    public void setShouldShowWhenAllReady(boolean shouldShowWhenAllReady) {
        this.shouldShowWhenAllReady = shouldShowWhenAllReady;
    }

    public boolean isShouldCalculateAsync() {
        return shouldCalculateAsync;
    }

    public void setShouldCalculateAsync(boolean shouldCalculateAsync) {
        this.shouldCalculateAsync = shouldCalculateAsync;
    }

    public boolean isShouldAnimateShadow() {
        return shouldAnimateShadow;
    }

    public void setShouldAnimateShadow(boolean shouldAnimateShadow) {
        this.shouldAnimateShadow = shouldAnimateShadow;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void generate() {
        clearShadowCache(); //Maybe some children changed their size
        childrenWithShadow = 0;

        shadowChildren = getAllChildren(viewGroup);
        for (int i = 0; i < shadowChildren.size(); i++) {
            View view = shadowChildren.get(i);
            childrenWithShadow++;
            if (shouldCalculateAsync) {
                calculateAndRenderShadowAsync(view, i);
            } else {
                calculateAndRenderShadow(view, i);
            }
        }
    }

    private ArrayList<View> getAllChildren(ViewGroup v) {

        ArrayList<View> result = new ArrayList<>();

        for (int i = 0; i < v.getChildCount(); i++) {

            View child = v.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<>();

            if (child instanceof LongShadowsWrapper) {
                continue;
            } else if (child instanceof LongShadowsFrameLayoutWrapper) {
                continue;
            } else if (child instanceof ViewGroup) {
                if (viewGroup instanceof LongShadowsWrapper) {
                    ((ViewGroup) child).setClipChildren(((LongShadowsWrapper) viewGroup).isShouldClipChildren());
                    ((ViewGroup) child).setClipToPadding(((LongShadowsWrapper) viewGroup).isShouldClipToPadding());
                } else if (viewGroup instanceof LongShadowsFrameLayoutWrapper) {
                    ((ViewGroup) child).setClipChildren(((LongShadowsFrameLayoutWrapper) viewGroup).isShouldClipChildren());
                    ((ViewGroup) child).setClipToPadding(((LongShadowsFrameLayoutWrapper) viewGroup).isShouldClipToPadding());
                }
                viewArrayList.addAll(getAllChildren((ViewGroup) child));
            } else {
                if (child instanceof LongShadowsImageView) {
                    ((LongShadowsImageView) child).setParentLongShadowWrapper(v);
                } else if (child instanceof LongShadowsTextView) {
                    ((LongShadowsTextView) child).setParentLongShadowWrapper(v);
                } else if (child instanceof LongShadowsView) {
                    ((LongShadowsView) child).setParentLongShadowWrapper(v);
                }
                viewArrayList.add(child);
            }

            result.addAll(viewArrayList);
        }
        return result;
    }

    public void releaseResources() {
        workerPool.shutdown();
        uiThreadHandler.removeCallbacksAndMessages(null);
    }

    private void clearShadowCache() {
        cancelTasksInProgress();
        uiThreadHandler.removeCallbacksAndMessages(null);
        viewShadowPaths = new SparseArray<>();
    }

    private void updateShadows(int pos) {
        if (pos == POS_UPDATE_ALL) {
            for (int i = 0; i < shadowChildren.size(); i++) {
                setLongShadowAtPosition(i);
            }
        } else {
            setLongShadowAtPosition(pos);
        }
    }

    private void setLongShadowAtPosition(int childIndex) {
        ShadowPath[] shadowPaths = viewShadowPaths.get(childIndex);
        if (shadowPaths == null) {
            //Path calculation is still in progress
            return;
        }
        final View child = shadowChildren.get(childIndex);

        if (child instanceof LongShadowsImageView) {
            ((LongShadowsImageView) child).setShadowPaths(new ArrayList<>(Arrays.asList(shadowPaths)));
            if (shouldAnimateShadow) {
                animateShadow(child);
            } else {
                ((LongShadowsImageView) child).updateWithShadowAlpha(-1);
            }
        } else if (child instanceof LongShadowsTextView) {
            ((LongShadowsTextView) child).setShadowPaths(new ArrayList<>(Arrays.asList(shadowPaths)));
            if (shouldAnimateShadow) {
                animateShadow(child);
            } else {
                ((LongShadowsTextView) child).updateWithShadowAlpha(-1);
            }
        } else if (child instanceof LongShadowsView) {
            ((LongShadowsView) child).setShadowPaths(new ArrayList<>(Arrays.asList(shadowPaths)));
            if (shouldAnimateShadow) {
                animateShadow(child);
            } else {
                ((LongShadowsView) child).updateWithShadowAlpha(-1);
            }
        }
    }

    private void animateShadow(final View child) {
        ValueAnimator animator = null;
        if (child instanceof LongShadowsImageView) {
            animator = ObjectAnimator.ofInt(0, ((LongShadowsImageView) child).getShadowAlpha());
        } else if (child instanceof LongShadowsTextView) {
            animator = ObjectAnimator.ofInt(0, ((LongShadowsTextView) child).getShadowAlpha());
        } else if (child instanceof LongShadowsView) {
            animator = ObjectAnimator.ofInt(0, ((LongShadowsView) child).getShadowAlpha());
        } else {
            return;
        }
        animator.setDuration(animationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (child instanceof LongShadowsImageView) {
                    ((LongShadowsImageView) child).updateWithShadowAlpha((int) animation.getAnimatedValue());
                } else if (child instanceof LongShadowsTextView) {
                    ((LongShadowsTextView) child).updateWithShadowAlpha((int) animation.getAnimatedValue());
                } else if (child instanceof LongShadowsView) {
                    ((LongShadowsView) child).updateWithShadowAlpha((int) animation.getAnimatedValue());
                }
            }
        });
        animator.start();
    }

    private void calculateAndRenderShadowAsync(final View view, final int pos) {
        final Future[] future = new Future[1];
        future[0] = workerPool.submit(new Runnable() {
            @Override
            public void run() {
                calculateAndRenderShadow(view, pos);
                synchronized (TASKS_LOCK) {
                    tasksInProgress.remove(future[0]);
                }
            }
        });
        tasksInProgress.add(future[0]);
    }

    private void cancelTasksInProgress() {
        synchronized (TASKS_LOCK) {
            for (int i = tasksInProgress.size() - 1; i >= 0; i--) {
                Future<?> task = tasksInProgress.get(i);
                task.cancel(true);
                tasksInProgress.remove(i);
            }
        }
    }

    private void calculateAndRenderShadow(final View view, int pos) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        ShadowPath[] shadowPaths = getShadowPathsForView(view);

        synchronized (TASKS_LOCK) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            uiThreadHandler.postAtFrontOfQueue(new SetLongShadowTask(pos, shadowPaths));
        }
    }

    private ShadowPath[] getShadowPathsForView(View view) {
        if (view instanceof LongShadowsImageView) {
            LongShadowsImageView longShadowsImageView = (LongShadowsImageView) view;

            if (!longShadowsImageView.isShadowDirty()) {
                return null;
            }

            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] intArray = new int[width * height];
            bitmap.getPixels(intArray, 0, width, 0, 0, width, height);

            String[] angleArray = longShadowsImageView.getShadowAngle().split(",");
            int shadowAngleArraySize = angleArray.length;
            float[] angles_array = new float[shadowAngleArraySize];

            String[] shadowLengthArray = longShadowsImageView.getShadowLength().split(",");
            int shadowLengthArraySize = shadowLengthArray.length;
            int[] shadow_lengths = new int[shadowAngleArraySize]; // No of angles dictate the number of shadow lengths

            for (int i = 0; i < shadowAngleArraySize; i++) {
                angles_array[i] = Float.parseFloat(angleArray[i].trim());
            }

            for (int i = 0; i < shadowAngleArraySize; i++) {
                if (i < shadowLengthArraySize) {
                    shadow_lengths[i] = Integer.parseInt(shadowLengthArray[i].trim());
                } else {
                    shadow_lengths[i] = Integer.parseInt(Constants.DEFAULT_SHADOW_LENGTH);
                }
            }

            ShadowPath[] paths = getShadowPaths(intArray,
                    width,
                    height,
                    angles_array,
                    shadowAngleArraySize,
                    shadow_lengths,
                    (longShadowsImageView.isBackgroundTransparent()) ? 0 : longShadowsImageView.getBackgroundColorToIgnore());

            for (ShadowPath path : paths) {
                path.constructPath();
            }

            return paths;

        } else if (view instanceof LongShadowsTextView) {

            LongShadowsTextView longShadowsTextView = (LongShadowsTextView) view;

            if (!longShadowsTextView.isShadowDirty()) {
                return null;
            }

            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] intArray = new int[width * height];
            bitmap.getPixels(intArray, 0, width, 0, 0, width, height);

            String[] angleArray = longShadowsTextView.getShadowAngle().split(",");
            int shadowAngleArraySize = angleArray.length;
            float[] angles_array = new float[shadowAngleArraySize];

            String[] shadowLengthArray = longShadowsTextView.getShadowLength().split(",");
            int shadowLengthArraySize = shadowLengthArray.length;
            int[] shadow_lengths = new int[shadowAngleArraySize]; // No of angles dictate the number of shadow lengths

            for (int i = 0; i < shadowAngleArraySize; i++) {
                angles_array[i] = Float.parseFloat(angleArray[i].trim());
            }

            for (int i = 0; i < shadowAngleArraySize; i++) {
                if (i < shadowLengthArraySize) {
                    shadow_lengths[i] = Integer.parseInt(shadowLengthArray[i].trim());
                } else {
                    shadow_lengths[i] = Integer.parseInt(Constants.DEFAULT_SHADOW_LENGTH);
                }
            }

            ShadowPath[] paths = getShadowPaths(intArray,
                    width,
                    height,
                    angles_array,
                    shadowAngleArraySize,
                    shadow_lengths,
                    (longShadowsTextView.isBackgroundTransparent()) ? 0 : longShadowsTextView.getBackgroundColorToIgnore());

            for (ShadowPath path : paths) {
                path.constructPath();
            }

            return paths;

        } else if (view instanceof LongShadowsView) {

            LongShadowsView longShadowsView = (LongShadowsView) view;

            if (!longShadowsView.isShadowDirty()) {
                return null;
            }

            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] intArray = new int[width * height];
            bitmap.getPixels(intArray, 0, width, 0, 0, width, height);

            String[] angleArray = longShadowsView.getShadowAngle().split(",");
            int shadowAngleArraySize = angleArray.length;
            float[] angles_array = new float[shadowAngleArraySize];

            String[] shadowLengthArray = longShadowsView.getShadowLength().split(",");
            int shadowLengthArraySize = shadowLengthArray.length;
            int[] shadow_lengths = new int[shadowAngleArraySize]; // No of angles dictate the number of shadow lengths

            for (int i = 0; i < shadowAngleArraySize; i++) {
                angles_array[i] = Float.parseFloat(angleArray[i].trim());
            }

            for (int i = 0; i < shadowAngleArraySize; i++) {
                if (i < shadowLengthArraySize) {
                    shadow_lengths[i] = Integer.parseInt(shadowLengthArray[i].trim());
                } else {
                    shadow_lengths[i] = Integer.parseInt(Constants.DEFAULT_SHADOW_LENGTH);
                }
            }

            ShadowPath[] paths = getShadowPaths(intArray,
                    width,
                    height,
                    angles_array,
                    shadowAngleArraySize,
                    shadow_lengths,
                    (longShadowsView.isBackgroundTransparent()) ? 0 : longShadowsView.getBackgroundColorToIgnore());

            for (ShadowPath path : paths) {
                path.constructPath();
            }

            return paths;

        } else {
            return null;
        }
    }

    private class SetLongShadowTask implements Runnable {

        private int viewPos;
        private ShadowPath[] shadowPaths;

        private SetLongShadowTask(int pos, ShadowPath[] shadowPaths) {
            this.viewPos = pos;
            this.shadowPaths = shadowPaths;
        }

        @Override
        public void run() {
            if (viewGroup instanceof LongShadowsWrapper) {
                if (((LongShadowsWrapper) viewGroup).isAttached()) {
                    viewShadowPaths.put(viewPos, shadowPaths);
                    if (shouldShowWhenAllReady) {
                        if (viewShadowPaths.size() == childrenWithShadow) {
                            updateShadows(POS_UPDATE_ALL);
                        }
                        return;
                    }
                    updateShadows(viewPos);
                }
            } else if (viewGroup instanceof LongShadowsFrameLayoutWrapper) {
                if (((LongShadowsFrameLayoutWrapper) viewGroup).isAttached()) {
                    viewShadowPaths.put(viewPos, shadowPaths);
                    if (shouldShowWhenAllReady) {
                        if (viewShadowPaths.size() == childrenWithShadow) {
                            updateShadows(POS_UPDATE_ALL);
                        }
                        return;
                    }
                    updateShadows(viewPos);
                }
            }
        }
    }

    public native ShadowPath[] getShadowPaths(int arr[], int width, int height, float[] angles_array, int numAngles, int[] shadowLengths, int backgroundColor);

}
