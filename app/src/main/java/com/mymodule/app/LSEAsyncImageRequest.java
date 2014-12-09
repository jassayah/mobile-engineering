package com.mymodule.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by jassayah on 12/8/14.
 * AsyncImageRequest is a Singleton used to get an image into a Volley NetworkImageView
 * While the image is loading a placeholder is shown if the NetworkImageView had a default
 * image set via networkImageView.setDefaultImageResId(R.drawable.image_placeholder) before making
 * a call to setImage(..)
 */

public class LSEAsyncImageRequest {

    private static LSEAsyncImageRequest asyncImageRequestInstance;

    private RequestQueue requestQueue;

    private LSEAsyncImageRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context, new HttpClientStack(new DefaultHttpClient()));
    }

    /**
     * Because this method has its own Request Queue, AsyncImageRequest is
     * a singleton.
     * <p/>
     * After calling this method, define a NetworkImageView, set the default image
     * to be displayed and then call setImage(NetworkImageView imageView, String url).
     *
     * @param context Activity Context of the caller.
     * @return a LSEAsyncImageRequest singleton instance is returned.
     */
    public static LSEAsyncImageRequest getInstance(Context context) {
        if (asyncImageRequestInstance == null) {
            asyncImageRequestInstance = new LSEAsyncImageRequest(context);
        }
        return asyncImageRequestInstance;
    }

    /**
     * Fetch the image at the URL. While the image is loading
     * any defaultImage loaded into the NetworkImage will be displayed.
     *
     * @param networkImageView a NetworkImageView
     * @param url              a full uri where the image (jpg) resides.
     */
    public void setImage(NetworkImageView networkImageView, String url) {
        networkImageView.setImageUrl(url, new ImageLoader(requestQueue, new LSEVolleyLruCache()));
    }


    /**
     * This Utility method asynchronously requests the image
     * at the provided imageURL. While that is loading, the
     * default image at R.drawable.image_placeholder will be displayed.
     * <p/>
     * A NetworkImageView, which is an extension of ImageView, is created and returned.
     *
     * @param context                 The Activity's context
     * @param imageUrl                a full uri to the image (.jpg) to be displayed
     * @param defaultDrawableResource R.drawable.xxxxx to use as a default while the image loads
     * @return The NetworkImageView, an extension of ImageView created by this method
     */
    public static  NetworkImageView fetchImageAsync(Context context, String imageUrl, int defaultDrawableResource) {
        NetworkImageView networkImageView = new NetworkImageView((context));
        return fetchImageAsync(context, imageUrl, networkImageView, defaultDrawableResource);
    }


    /**
     * This Utility method asynchronously requests the image
     * at the provided imageURL. While that is loading, the
     * default image at R.drawable.xxxxx will be displayed.
     * <p/>
     * The input NetworkImageView, which is an extension of ImageView, is returned.
     *
     * @param context                 The Activity's context
     * @param imageUrl                a full uri to the image (.jpg) to be displayed
     * @param networkImageView        an extension of ImageView that displays a temp image before the network image is received.
     * @param defaultDrawableResource R.drawable.xxxxx to use as a default while the image loads
     * @return The same NetworkImageView input into this method but now with the default image set.
     */
    public static NetworkImageView fetchImageAsync(Context context, String imageUrl, NetworkImageView networkImageView, int defaultDrawableResource) {
        LSEAsyncImageRequest imageLoader = LSEAsyncImageRequest.getInstance(context);
        networkImageView.setDefaultImageResId(defaultDrawableResource);
        imageLoader.setImage(networkImageView, imageUrl);
        return networkImageView;
    }


    public class LSEVolleyLruCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> memoryCache;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        public LSEVolleyLruCache() {
            memoryCache = new LruCache<String, Bitmap>(cacheSize);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return memoryCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            memoryCache.put(url, bitmap);
        }
    }
}
