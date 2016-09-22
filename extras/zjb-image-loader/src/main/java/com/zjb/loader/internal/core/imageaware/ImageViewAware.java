/*******************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.zjb.loader.internal.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Field;

import com.zjb.loader.internal.core.assist.ViewScaleType;
import com.zjb.loader.internal.utils.L;

/**
 * Wrapper for Android {@link android.widget.ImageView ImageView}. Keeps weak reference of ImageView to prevent memory
 * leaks.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.0
 */
public class ImageViewAware extends ViewAware {

    /**
     * Constructor. <br />
     * References {@link #ImageViewAware(android.widget.ImageView, boolean) ImageViewAware(imageView, true)}.
     *
     * @param imageView {@link android.widget.ImageView ImageView} to work with
     */
    public ImageViewAware(ImageView imageView) {
        super(imageView);
    }

    /**
     * Constructor
     *
     * @param imageView           {@link android.widget.ImageView ImageView} to work with
     * @param checkActualViewSize <b>true</b> - then {@link #getWidth()} and {@link #getHeight()} will check actual
     *                            size of ImageView. It can cause known issues like
     *                            <a href="https://github.com/nostra13/Android-Universal-Image-Loader/issues/376">this</a>.
     *                            But it helps to save memory because memory cache keeps bitmaps of actual (less in
     *                            general) size.
     *                            <p>
     *                            <b>false</b> - then {@link #getWidth()} and {@link #getHeight()} will <b>NOT</b>
     *                            consider actual size of ImageView, just layout parameters. <br /> If you set 'false'
     *                            it's recommended 'android:layout_width' and 'android:layout_height' (or
     *                            'android:maxWidth' and 'android:maxHeight') are set with concrete values. It helps to
     *                            save memory.
     *                            <p>
     */
    public ImageViewAware(ImageView imageView, boolean checkActualViewSize) {
        super(imageView, checkActualViewSize);
    }

    /**
     * {@inheritDoc}
     * <br />
     * 3) Get <b>maxWidth</b>.
     */
    @Override
    public int getWidth() {
        int width = 0;
        ImageView imageView = (ImageView) viewRef.get();
        if (imageView != null) {
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check maxWidth parameter
        }
        if (width <= 0) {
            width = super.getWidth();
        }
        return width;
    }

    /**
     * {@inheritDoc}
     * <br />
     * 3) Get <b>maxHeight</b>
     */
    @Override
    public int getHeight() {
        int height = 0;
        ImageView imageView = (ImageView) viewRef.get();
        if (imageView != null) {
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check maxHeight parameter
        }
        if (height <= 0) {
            height = super.getHeight();
        }
        return height;
    }

    @Override
    public ViewScaleType getScaleType() {
        ImageView imageView = (ImageView) viewRef.get();
        if (imageView != null) {
            return ViewScaleType.fromImageView(imageView);
        }
        return super.getScaleType();
    }

    @Override
    public ImageView getWrappedView() {
        return (ImageView) super.getWrappedView();
    }

    @Override
    protected void setImageDrawableInto(Drawable drawable, View view) {
        ((ImageView) view).setImageDrawable(drawable);
        if (drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
    }

    @Override
    protected void setImageBitmapInto(Bitmap bitmap, View view) {
        ((ImageView) view).setImageBitmap(bitmap);
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            L.e(e);
        }
        return value;
    }
}
