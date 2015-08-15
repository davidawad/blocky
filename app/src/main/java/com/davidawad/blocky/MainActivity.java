package com.davidawad.blocky;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iv = (ImageView) findViewById(R.id.qr_code);
        new LoadQrCodeTask().execute(iv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class LoadQrCodeTask extends AsyncTask<ImageView, Void, Boolean> {

        private ImageView mImageView;
        private Bitmap mQrCodeBmp;

        @Override
        protected Boolean doInBackground(ImageView... params) {
            mImageView = params[0];
            try {
                URL thumb_u = new URL("https://blockchain.info/qr?data=1Agb153xWsbqS9vt8gP4vBFKHkAchLMdSX");
                mQrCodeBmp = BitmapFactory.decodeStream(thumb_u.openStream());
            } catch (IOException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.remote_view);

            contentView.setImageViewBitmap(R.id.qr_code, mQrCodeBmp);

            // Create intent for the onclick button
            Intent cyngnIntent = new Intent(Intent.ACTION_VIEW)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(Uri.parse("http://www.cyngn.com"));

            // contentView.setOnClickPendingIntent(R.id.qr_code, PendingIntent.getActivity(MainActivity.this, cyngnIntent));

            // Create the new RemoteExpandedStyle
            CustomTile.RemoteExpandedStyle remoteExpandedStyle =
                    new CustomTile.RemoteExpandedStyle();
            remoteExpandedStyle.setRemoteViews(contentView);


            // Build the custom tile
            CustomTile customTile = new CustomTile.Builder(MainActivity.this)
                    .setLabel("Bitcoin Wallet")
                    .setIcon(R.drawable.testimage)
                    .setExpandedStyle(remoteExpandedStyle)
                    .setContentDescription("QR code for your Bitcoin wallet")
                    .build();

            // Publish the custom tile
            CMStatusBarManager.getInstance(MainActivity.this)
                    .publishTile(10, customTile);

        }
    }
}