package com.destan.epiandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by bury_a.
 */

public class ImageClass extends AsyncTask<String, Void, Bitmap> {
    private ImageView m_image;

    ImageClass(ImageView image){
        m_image = image;
    }

    protected Bitmap doInBackground(String... urls)
    {
        Bitmap image = null;

        try{
            InputStream in = new URL(urls[0]).openStream();
            image = BitmapFactory.decodeStream(in);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return (image);
    }

    protected void onPostExecute(Bitmap image)
    {
        m_image.setImageBitmap(image);
    }
}
