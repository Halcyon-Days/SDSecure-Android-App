package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText idText;
    EditText latText;
    EditText lngText;
    EditText encryptText;
    TextView serverResponseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idText = (EditText) findViewById(R.id.idText);
        latText = (EditText) findViewById(R.id.latText);
        lngText = (EditText) findViewById(R.id.lngText);
        encryptText = (EditText) findViewById(R.id.encryptText);
        serverResponseText = (TextView) findViewById(R.id.serverResponseText);

        Button postBtn = findViewById(R.id.postBut);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                new asyncServerPost().execute(getApplicationContext() );
            }
        });

        Button getBtn = findViewById(R.id.getBut);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                new asyncServerGet().execute(getApplicationContext() );
            }
        });

        Button deleteBtn = findViewById(R.id.deleteBut);
        deleteBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                new asyncServerDelete().execute(getApplicationContext() );
            }
        });
    }

    //asynchronous task to send request
    private class asyncServerPost extends AsyncTask<Context, Void, String> {

        protected void onPreExecute() {
            serverResponseText.setText("Waiting for response");
        }

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("lat",latText.getText().toString());
            params.put("lng",lngText.getText().toString());
            params.put("encryption",encryptText.getText().toString());

            returnText = ServerComm.getRequest(ServerComm.POST, params);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            latText.setText("Latitude");
            lngText.setText("Longitude");
            encryptText.setText("Encryption");
            serverResponseText.setText("Sending a POST request: \n Created new entry with ID: " + returnText);
        }
    }

    //asynchronous task to send request
    private class asyncServerGet extends AsyncTask<Context, Void, String> {

        protected void onPreExecute() {
            serverResponseText.setText("Waiting for response");
        }

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id",idText.getText().toString());
            returnText = ServerComm.getRequest(ServerComm.GET, params);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            idText.setText("ID");
            if(returnText.equals("")) {
                serverResponseText.setText("Sent a GET request: \n No entry found with this ID!");
            } else {
                serverResponseText.setText("Sent a GET request: \n " + returnText);
            }


        }
    }

    //asynchronous task to send request
    private class asyncServerDelete extends AsyncTask<Context, Void, String> {

        protected void onPreExecute() {
            serverResponseText.setText("Waiting for response");
        }

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id",idText.getText().toString());
            returnText = ServerComm.getRequest(ServerComm.DELETE, params);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            //updates text boxes based on result of searching for stop
            idText.setText("ID");
            serverResponseText.setText("Sending a DELETE request: \n " + returnText);

        }
    }

    /*
        Referenced to https://stackoverflow.com/questions/35941051/on-button-click-hide-keyboard
     */
    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
