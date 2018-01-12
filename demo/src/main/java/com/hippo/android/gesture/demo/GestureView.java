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

package com.hippo.android.gesture.demo;

/*
 * Created by Hippo on 2018/1/12.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.hippo.android.gesture.GestureRecognizer;

public class GestureView extends View implements GestureRecognizer.OnGestureListener {

  private GestureRecognizer detector;

  public GestureView(Context context) {
    super(context);
    detector = new GestureRecognizer(context, this);
  }

  public GestureView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    detector = new GestureRecognizer(context, this);
    detector.setLongPressEnabled(true);
    detector.setDoubleTapEnabled(true);
    detector.setScaleEnabled(true);
    detector.setRotateEnabled(true);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    detector.onTouchEvent(event);
    return true;
  }

  @Override
  public void onDown(float x, float y) {
    Log.d("TAG", "onDown{" + x + ", " + y + "}");
  }

  @Override
  public void onUp(float x, float y) {
    Log.d("TAG", "onUp{" + x + ", " + y + "}");
  }

  @Override
  public void onCancel() {
    Log.d("TAG", "onCancel{}");
  }

  @Override
  public void onSingleTap(float x, float y) {
    Log.d("TAG", "onSingleTap{" + x + ", " + y + "}");
  }

  @Override
  public void onDoubleTap(float x, float y) {
    Log.d("TAG", "onDoubleTap{" + x + ", " + y + "}");
  }

  @Override
  public void onLongPress(float x, float y) {
    Log.d("TAG", "onLongPress{" + x + ", " + y + "}");
  }

  @Override
  public void onScroll(float dx, float dy, float totalX, float totalY, float x, float y) {
    Log.d("TAG", "onScroll{" + dx + ", " + dy + ", " + totalX + ", " + totalY + ", " + x + ", " + y + "}");
  }

  @Override
  public void onFling(float velocityX, float velocityY) {
    Log.d("TAG", "onFling{" + velocityX + ", " + velocityY + "}");
  }

  @Override
  public void onScale(float focusX, float focusY, float scale) {
    Log.d("TAG", "onScale{" + focusX + ", " + focusY + ", " + scale + "}");
  }

  @Override
  public void onRotate(float angle, float x, float y) {
    Log.d("TAG", "onRotate{" + angle + ", " + x + ", " + y + "}");
  }
}
