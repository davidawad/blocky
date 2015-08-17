package bitcoin.hacktheplanet.cfb.bitcointiler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PasswordActivity extends AppCompatActivity {

    Intent intent;
    String message;
    String pw1;
    String pw2;
    EditText amount;
    String amounts;
    Toast toast;
    Context context;
    public String baseUrl;
    String messfrom;
    SharedPreferences BlockyPrefs;
    SharedPreferences.Editor BlockyPrefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        BlockyPrefs = getSharedPreferences("BlockyPrefs", 0);
        intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        context = getApplicationContext();
        toast = Toast.makeText(context, "Invalid request", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password, menu);
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

    public void submitHttp(View view) {
        pw1 = BlockyPrefs.getString("Pw1", "");
        pw2 = BlockyPrefs.getString("Pw2", "");
        amount = (EditText) findViewById(R.id.input_amount);
        amounts = amount.getText().toString();

        if (pw2.equals("")) {
            baseUrl = "https://blockchain.info/merchant/" + BlockyPrefs.getString("guid", "") + "/payment?password="+ pw1 + "&to=" + message + "&amount=" + amounts;
        } else {
            baseUrl = "https://blockchain.info/merchant/" + BlockyPrefs.getString("guid", "") + "/payment?password="+ pw1 + "&second_password="+pw2 +"&to=" + message + "&amount=" + amounts;
        }
        sendString simple = new sendString();
        Object hold = simple.execute();
        if (hold.equals(null)){
            toast.show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    class sendString extends AsyncTask<Void, Void, Object> {

        protected Object doInBackground(Void... params) {
            //initialize http client
            HttpClient client = new DefaultHttpClient();

            //can change this to make any url you want
            String endpoint = baseUrl;

            //initialize a GET request with whatever url you want, im using active as an example
            HttpGet get = new HttpGet(endpoint);

            try {
                //execute the get request and store response
                HttpResponse response = client.execute(get);

                //get status code of http request
                StatusLine statusLine = response.getStatusLine();

                //initialize byte outputstream to store data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                //store data from http response
                response.getEntity().writeTo(bos);

                //close the stream
                bos.close();

                //if the status code is OK
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                    //return data in a string
                    return bos.toString();

                } else {

                    //something got messed up
                    return null;
                }

            } catch (IOException e) {

                //lets see what the error was
                Log.e("ERROR", e.getMessage());

                return null;
            }

        }
    }
}
