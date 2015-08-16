package bitcoin.hacktheplanet.cfb.bitcointiler;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "bitcoin.hacktheplanet.cfb.bitcointiler.MESSAGE";
    public final static String EXTRA_MESSAGE2 = "bitcoin.hacktheplanet.cfb.bitcointiler.MESSAGE2";
    ImageView qrImage;
    Intent intent;
    TextView walletid;
    WebView qrWebview;
    public String message;
    String contents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        message = intent.getStringExtra(KeySubmit.EXTRA_MESSAGE);

        //example wallet addr
        //message = "1BmTrNt7EBcE5Wj3vBLHjvtP7L8TcXyEPS";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configure new image view
        ImageView iv = (ImageView) findViewById(R.id.QRimage);
        new LoadQrCodeTask().execute(iv);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



    }

    // async task to load the QR code in the android remote view URI
    // we run this in the background as network calls are blocking.
    class LoadQrCodeTask extends AsyncTask<ImageView, Void, Boolean> {

        private ImageView mImageView;
        private Bitmap mQrCodeBmp;

        @Override
        protected Boolean doInBackground(ImageView... params) {
            mImageView = params[0];
            try {
                System.out.println("MESSAGE IS" + message);
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

            // Create intent for the onclick button
            Intent cyngnIntent = new Intent(Intent.ACTION_VIEW)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(Uri.parse("http://www.cyngn.com"));

            // contentView.setOnClickPendingIntent(R.id.qr_code, PendingIntent.getActivity(MainActivity.this, cyngnIntent));
            // contentView.setOnClickPendingIntent(R.id.qr_code, PendingIntent.getActivity(MainActivity.this, cyngnIntent));

            // Create the new RemoteExpandedStyle
            CustomTile.RemoteExpandedStyle remoteExpandedStyle =
                    new CustomTile.RemoteExpandedStyle();
            remoteExpandedStyle.setRemoteViews(contentView);


            // Build the custom tile
            CustomTile customTile = new CustomTile.Builder(MainActivity.this)
                    .setLabel(R.string.app_name)
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

    public void buildNew (View view) {
        CMStatusBarManager.getInstance(MainActivity.this).removeTile(10);
        message = "";
        startActivity(new Intent(this, KeySubmit.class));
        finish();
    }

    public void findQR (View view) {
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
                pi.putExtra(EXTRA_MESSAGE2, message);
                startActivity(pi);
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

}
