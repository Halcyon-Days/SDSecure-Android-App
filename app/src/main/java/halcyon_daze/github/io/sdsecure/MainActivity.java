package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final EditText idText = (EditText) findViewById(R.id.idText);
    final EditText latText = (EditText) findViewById(R.id.latText);
    final EditText lngText = (EditText) findViewById(R.id.lngText);
    final EditText encryptText = (EditText) findViewById(R.id.encryptText);
    final TextView serverResponseText = (TextView) findViewById(R.id.serverResponseText);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button postBtn = findViewById(R.id.postBut);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new asyncServerPost().execute(getApplicationContext() );
            }
        });

        Button getBtn = findViewById(R.id.getBut);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new asyncServerGet().execute(getApplicationContext() );
            }
        });

        Button deleteBtn = findViewById(R.id.deleteBut);
        deleteBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new asyncServerDelete().execute(getApplicationContext() );
            }
        });
    }

    //asynchronous task to send request
    private class asyncServerPost extends AsyncTask<Context, Void, String> {

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
            //updates text boxes based on result of searching for stop
            serverResponseText.setText("Sending a POST request: \n " + returnText);

        }
    }

    //asynchronous task to send request
    private class asyncServerGet extends AsyncTask<Context, Void, String> {

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id",idText.getText().toString());
            returnText = ServerComm.getRequest(ServerComm.GET, params);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            //updates text boxes based on result of searching for stop
            serverResponseText.setText("Sending a GET request: \n " + returnText);

        }
    }

    //asynchronous task to send request
    private class asyncServerDelete extends AsyncTask<Context, Void, String> {

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id",idText.getText().toString());
            returnText = ServerComm.getRequest(ServerComm.DELETE, params);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            //updates text boxes based on result of searching for stop
            serverResponseText.setText("Sending a DELETE request: \n " + returnText);

        }
    }
}
