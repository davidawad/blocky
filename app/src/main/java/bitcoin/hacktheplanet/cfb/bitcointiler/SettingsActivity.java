package bitcoin.hacktheplanet.cfb.bitcointiler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences BlockyPrefs;
    SharedPreferences.Editor BlockyPrefsEdit;
    String guid;
    String pw1;
    String pw2;
    EditText guidET;
    EditText Pw1;
    EditText Pw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BlockyPrefs = getSharedPreferences("BlockyPrefs", 0);
        guidET = (EditText) findViewById(R.id.sparkle);
        Pw1 = (EditText) findViewById(R.id.pw1);
        Pw2 = (EditText) findViewById(R.id.pw2);
        guidET.setText(BlockyPrefs.getString("guid", ""));
        Pw1.setText(BlockyPrefs.getString("Pw1", ""));
        Pw2.setText(BlockyPrefs.getString("Pw2", ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void passBack(View view){
        guid = guidET.getText().toString();
        pw1 = Pw1.getText().toString();
        pw2 = Pw2.getText().toString();
        BlockyPrefsEdit = BlockyPrefs.edit();
        BlockyPrefsEdit = BlockyPrefsEdit.putString("guid", guid);
        BlockyPrefsEdit = BlockyPrefsEdit.putString("Pw1", pw1);
        BlockyPrefsEdit = BlockyPrefsEdit.putString("Pw2", pw2);
        BlockyPrefsEdit.commit();
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
