package bitcoin.hacktheplanet.cfb.bitcointiler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.IOException;

import java.net.URL;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "bitcoin.hacktheplanet.cfb.bitcointiler.MESSAGE";

    public String message;
    String contents;
    SharedPreferences BlockyPrefs;
    SharedPreferences.Editor BlockyPrefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BlockyPrefs = getSharedPreferences("BlockyPrefs", 0);
        message = BlockyPrefs.getString("publicKey", null);



        ImageView iv = (ImageView) findViewById(R.id.QRimage);
        new LoadQrCodeTask().execute(iv);
    }

    class LoadQrCodeTask extends AsyncTask<ImageView, Void, Boolean> {

        private Bitmap mQrCodeBmp;

        @Override
        protected Boolean doInBackground(ImageView... params) {
            try {
                URL thumb_u = new URL("https://blockchain.info/qr?data=" + message);
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

            contentView.setImageViewBitmap(R.id.QRimage, mQrCodeBmp);

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

            ImageView mainImage = (ImageView) findViewById(R.id.mainQR);
            mainImage.setImageBitmap(mQrCodeBmp);

            TextView mainTxt = (TextView) findViewById(R.id.walletID);
            mainTxt.setText(message);
        }
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

    public void resetAll (View view) {
        CMStatusBarManager.getInstance(MainActivity.this).removeTile(10);
        BlockyPrefsEdit = BlockyPrefs.edit();
        BlockyPrefsEdit.clear().commit();
        startActivity(new Intent(this, KeySubmit.class));
        finish();
    }

    public void buildNew(View view) {
        Intent pi = new Intent(this, SettingsActivity.class);
        startActivity(pi);
    }

    public void findQR (View view) {
        if (BlockyPrefs.getString("guid", "").equals("") || BlockyPrefs.getString("Pw1", "").equals("")){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("SCAN_RESULT");
                Intent pi = new Intent(this, PasswordActivity.class);
                pi.putExtra(EXTRA_MESSAGE, contents);
                startActivity(pi);
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

}
