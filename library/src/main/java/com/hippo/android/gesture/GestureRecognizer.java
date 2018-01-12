/*
 * Copyright 2018 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.android.gesture;

/*
 * Created by Hippo on 2018/1/12.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Detects various gestures and events using the supplied {@link MotionEvent}s.
 * The {@link OnGestureListener} callback will notify users when a particular
 * motion event has occurred.
 *
 * This class should only be used with {@link MotionEvent}s reported via touch.
 *
 * To use this class:
 * <ul>
 *  <li>Create an instance of the {@code GestureRecognizer} for your
 *      {@link android.view.View View}
 *  <li>In the {@link android.view.View#onTouchEvent(MotionEvent) View#onTouchEvent(MotionEvent)}
 *      method ensure you call {@link #onTouchEvent(MotionEvent)}. The methods defined in your
 *      callback will be executed when the events occur.
 * </ul>
 */
public class GestureRecognizer {

  /**
   * The listener that is used to notify when gestures occur.
   * If you want to listen for all the different gestures then implement
   * this interface. If you only want to listen for a subset it might
   * be easier to extend {@link SimpleOnGestureListener}.
   */
  public interface OnGestureListener {

    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event.
     */
    void onDown(float x, float y);

    /**
     * Notified when a tap occurs with the up {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every up event.
     */
    void onUp(float x, float y);

    /**
     * Notified when a tap occurs with the cancel {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every cancel event.
     */
    void onCancel();

    /**
     * Notified when a single tap confirmed.
     *
     * If double-tap gesture is enabled, it will only be called
     * after the detector is confident that the user's first tap is not followed
     * by a second tap leading to a double-tap gesture.
     * If double-tap gesture is disabled, it is called
     * as soon as a tap occurs with the up event.
     *
     * @see GestureRecognizer#setDoubleTapEnabled(boolean)
     * @see GestureRecognizer#isDoubleTapEnabled()
     */
    void onSingleTap(float x, float y);

    /**
     * Notified when a double tap occurs.
     * Call {@link GestureRecognizer#setDoubleTapEnabled(boolean)} to enable it.
     * By default double-tap gesture is disabled.
     *
     * @see GestureRecognizer#setDoubleTapEnabled(boolean)
     * @see GestureRecognizer#isDoubleTapEnabled()
     */
    void onDoubleTap(float x, float y);

    /**
     * Notified when a long press occurs.
     * Call {@link GestureRecognizer#setLongPressEnabled(boolean)} to enable it.
     * By default long-press gesture is disabled.
     *
     * @see GestureRecognizer#setLongPressEnabled(boolean)
     * @see GestureRecognizer#isLongPressEnabled()
     */
    void onLongPress(float x, float y);

    /**
     * Notified when a scroll occurs.
     */
    void onScroll(float dx, float dy, float totalX, float totalY, float x, float y);

    /**
     * Notified when a fling occurs.
     */
    void onFling(float velocityX, float velocityY);

    /**
     * Notified when a scale occurs.
     * Call {@link GestureRecognizer#setScaleEnabled(boolean)} to enable it.
     * By default scale gesture is disabled.
     *
     * @see GestureRecognizer#setScaleEnabled(boolean)
     * @see GestureRecognizer#isScaleEnabled()
     */
    void onScale(float focusX, float focusY, float scale);

    /**
     * Notified when a rotate occurs.
     * Call {@link GestureRecognizer#setRotateEnabled(boolean)} to enable it.
     * By default rotate gesture is disabled.
     *
     * @see GestureRecognizer#setRotateEnabled(boolean)
     * @see GestureRecognizer#isRotateEnabled()
     */
    void onRotate(float angle, float x, float y);
  }

  /**
   * A convenience class to extend when you only want to listen for a subset
   * of all the gestures.
   */
  public static class SimpleOnGestureListener implements OnGestureListener {
    @Override
    public void onDown(float x, float y) {}
    @Override
    public void onUp(float x, float y) {}
    @Override
    public void onCancel() {}
    @Override
    public void onSingleTap(float x, float y) {}
    @Override
    public void onDoubleTap(float x, float y) {}
    @Override
    public void onLongPress(float x, float y) {}
    @Override
    public void onScroll(float dx, float dy, float totalX, float totalY, float x, float y) {}
    @Override
    public void onFling(float velocityX, float velocityY) {}
    @Override
    public void onScale(float focusX, float focusY, float scale) {}
    @Override
    public void onRotate(float angle, float x, float y) {}
  }

  private static final float SCALE_SLOP = 0.015f;
  private static final float ROTATE_SLOP = 0.5f;

  private final Context context;
  private final OnGestureListener listener;

  private final GestureDetectorCompat gestureDetector;
  @Nullable
  private ScaleGestureDetector scaleDetector;
  @Nullable
  private RotationGestureDetector rotationDetector;

  @Nullable
  private DoubleTapListener doubleTapListener;

  private boolean isLongPressEnabled = false;
  private boolean isDoubleTapEnabled = false;
  private boolean isScaleEnabled = false;
  private boolean isRotateEnabled = false;

  private boolean isScaling = false;
  private boolean isRotating = false;

  private float scaling;
  private float rotating;

  public GestureRecognizer(Context context, OnGestureListener listener) {
    this.context = context;
    this.listener = listener;
    this.gestureDetector = new GestureDetectorCompat(context, new GestureListener());
    this.gestureDetector.setIsLongpressEnabled(false);
  }

  /**
   * Set whether long-press gesture is enabled.
   * If this is enabled when a user presses and holds down
   * you get a long-press event and nothing further.
   * If it's disabled the user can press and hold down and then later
   * moved their finger and you will get scroll events.
   *
   * By default long-press gesture is disabled.
   *
   * @see #isLongPressEnabled()
   */
  public void setLongPressEnabled(boolean enabled) {
    if (isLongPressEnabled != enabled) {
      isLongPressEnabled = enabled;
      gestureDetector.setIsLongpressEnabled(enabled);
    }
  }

  /**
   * Returns {@code true} if long-press gesture is enabled.
   *
   * @see #setLongPressEnabled(boolean)
   */
  public boolean isLongPressEnabled() {
    return isLongPressEnabled;
  }

  /**
   * Sets whether double-tap gesture is enabled.
   * If this is enabled, {@link OnGestureListener#onSingleTap(float, float)} will only be called
   * after the detector is confident that the user's first tap is not followed
   * by a second tap leading to a double-tap gesture.
   * If this is disabled, {@link OnGestureListener#onSingleTap(float, float)} is called
   * as soon as a tap occurs with the up event.
   *
   * By default double-tap gesture is disabled.
   *
   * @see #isDoubleTapEnabled()
   */
  public void setDoubleTapEnabled(boolean enabled) {
    if (isDoubleTapEnabled != enabled) {
      isDoubleTapEnabled = enabled;
      if (isDoubleTapEnabled) {
        if (doubleTapListener == null) {
          doubleTapListener = new DoubleTapListener();
        }
        gestureDetector.setOnDoubleTapListener(doubleTapListener);
      } else {
        gestureDetector.setOnDoubleTapListener(null);
      }
    }
  }

  /**
   * Returns {@code true} if double-tap gesture is enabled.
   *
   * @see #setDoubleTapEnabled(boolean)
   */
  public boolean isDoubleTapEnabled() {
    return isDoubleTapEnabled;
  }

  /**
   * Sets whether scale gesture is enabled.
   *
   * By default scale gesture is disabled.
   *
   * @see #isScaleEnabled()
   */
  public void setScaleEnabled(boolean enabled) {
    if (isScaleEnabled != enabled) {
      isScaleEnabled = enabled;
      if (enabled && scaleDetector == null) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
      }
    }
  }

  /**
   * Returns {@code true} if scale gesture is enabled.
   *
   * @see #setScaleEnabled(boolean)
   */
  public boolean isScaleEnabled() {
    return isScaleEnabled;
  }

  /**
   * Sets whether rotate gesture is enabled.
   *
   * By default rotate gesture is disabled.
   *
   * @see #isRotateEnabled()
   */
  public void setRotateEnabled(boolean enabled) {
    isRotateEnabled = enabled;
  }

  /**
   * Returns {@code true} if rotate gesture is enabled.
   *
   * @see #setRotateEnabled(boolean)
   */
  public boolean isRotateEnabled() {
    return isRotateEnabled;
  }

  public void onTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        listener.onDown(event.getX(), event.getY());
        break;
      case MotionEvent.ACTION_UP:
        listener.onUp(event.getX(), event.getY());
        break;
      case MotionEvent.ACTION_CANCEL:
        listener.onCancel();
        break;
    }

    if (isRotateEnabled) {
      if (rotationDetector == null) {
        rotationDetector = new RotationGestureDetector(new RotateGestureListener());
      }
      rotationDetector.onTouchEvent(event);
    }

    if (isScaleEnabled) {
      if (scaleDetector == null) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
      }
      scaleDetector.onTouchEvent(event);
    }

    gestureDetector.onTouchEvent(event);
  }

  private class GestureListener implements GestureDetector.OnGestureListener {

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      if (!isDoubleTapEnabled) {
        listener.onSingleTap(e.getX(), e.getY());
      }
      return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
      if (isLongPressEnabled) {
        listener.onLongPress(e.getX(), e.getY());
      }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      // Scroll action is easier caught than Scale action
      // Only catch scroll action when no scaling and point count is one
      if (!isScaling && !isRotating && e1.getPointerCount() == 1 && e2.getPointerCount() == 1) {
        listener.onScroll(-distanceX, -distanceY,
            e2.getX() - e1.getX(), e2.getY() - e1.getY(),
            e2.getX(), e2.getY());
      }
      return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      listener.onFling(velocityX, velocityY);
      return true;
    }
  }

  private class DoubleTapListener implements GestureDetector.OnDoubleTapListener {

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
      if (isDoubleTapEnabled) {
        listener.onSingleTap(e.getX(), e.getY());
      }
      return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      if (isDoubleTapEnabled) {
        listener.onDoubleTap(e.getX(), e.getY());
      }
      return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
      return false;
    }
  }

  private class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      if (isScaleEnabled) {
        if (!isRotating) {
          isScaling = true;
        }
        return true;
      } else {
        return false;
      }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      if (!isScaleEnabled) {
        isScaling = false;
        return true;
      }

      scaling = detector.getScaleFactor();
      if (scaling < 1.0f) {
        scaling = 1.0f / scaling;
      }
      scaling -= 1.0f;

      if (isRotating) {
        if (rotating < ROTATE_SLOP && scaling > SCALE_SLOP) {
          // Switch from rotating to scaling
          isRotating = false;
          isScaling = true;
        }
      } else if (!isScaling) {
        isScaling = true;
      }

      if (isScaling) {
        listener.onScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
      }

      return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
      isScaling = false;
    }
  }

  private class RotateGestureListener implements RotationGestureDetector.OnRotateGestureListener {

    @Override
    public boolean onRotateBegin() {
      if (isRotateEnabled) {
        if (!isScaling) {
          isRotating = true;
        }
        return true;
      } else {
        return false;
      }
    }


    @Override
    public void onRotate(float angle, float x, float y) {
      if (!isRotateEnabled) {
        isRotating = false;
        return;
      }

      rotating = Math.abs(angle);

      if (isScaling) {
        if (scaling < SCALE_SLOP && rotating > ROTATE_SLOP) {
          // Switch from scaling to rotating
          isScaling = false;
          isRotating = true;
        }
      } else if (!isRotating) {
        isRotating = true;
      }

      if (isRotating) {
        listener.onRotate(angle, x, y);
      }
    }

    @Override
    public void onRotateEnd() {
      isRotating = false;
    }
  }
}
