package cn.edu.pku.kingarcher.wefund.util;

import android.graphics.Bitmap;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NoCache;

/**
 * Created by xtrao on 2016/4/4.
 */
public class SingletonRequestQueue {

    private static SingletonRequestQueue ourInstance = new SingletonRequestQueue();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static SingletonRequestQueue getInstance() {
        return ourInstance;
    }

    private SingletonRequestQueue() {
        getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(new NoCache(), network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
