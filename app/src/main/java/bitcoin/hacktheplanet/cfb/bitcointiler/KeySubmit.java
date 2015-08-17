package bitcoin.hacktheplanet.cfb.bitcointiler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class KeySubmit extends AppCompatActivity {

    Button submitButton;
    EditText publicKey;
    Context context;
    Toast toast;
    String contents;
    SharedPreferences BlockyPrefs;
    SharedPreferences.Editor BlockyPrefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_submit);
        BlockyPrefs = getSharedPreferences("BlockyPrefs", 0);
        //Set EditText and Button
        submitButton = (Button) findViewById(R.id.key_submit_button);

        context = getApplicationContext();
        toast = Toast.makeText(context, "Invalid Public Key", Toast.LENGTH_SHORT);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_key_submit, menu);
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

    public boolean buildQR(View view) {
        publicKey = (EditText) findViewById(R.id.key_enter);
        String pkey = publicKey.getText().toString();
        if (pkey.matches("^[13][a-km-zA-HJ-NP-Z0-9]{26,33}$")) {
            Intent pi = new Intent(this, MainActivity.class);
            BlockyPrefsEdit = BlockyPrefs.edit();
            BlockyPrefsEdit.putString("publicKey", pkey).commit();
            //pi.putExtra(EXTRA_MESSAGE, pkey);
            publicKey.setText("");
            startActivity(pi);
            return true;
        } else {
            toast.show();
            return false;
        }
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
                publicKey = (EditText) findViewById(R.id.key_enter);
                publicKey.setText(contents);
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }
}
