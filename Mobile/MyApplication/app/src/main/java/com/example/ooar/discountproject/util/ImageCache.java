package com.example.ooar.discountproject.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


/**
 * Created by Onur Kuru on 2.4.2016.
 */
public class ImageCache {
    private LruCache<String, Bitmap> mMemoryCache;
    private Context context;
    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final int cacheSize = maxMemory / 8;

    public ImageCache(Context context) {
        this.context = context;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
