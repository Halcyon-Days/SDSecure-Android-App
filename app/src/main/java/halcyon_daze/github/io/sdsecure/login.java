package halcyon_daze.github.io.sdsecure;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class login extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //prevents keyboard from automatically opening
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Paul: need this for Android 6.0+, dynamic permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        responseText = findViewById(R.id.responseText);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button newAccountBtn = findViewById(R.id.createNewAccountBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                if(authenticateLogin()) {
                    Intent startIntent = new Intent(getApplicationContext(), Navigation.class);
                    startIntent.putExtra("username", username.getText().toString());
                    startActivity(startIntent);
                } else {
                    //responseText.setText("Invalid username or password!");
                }
            }
        });

        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), newAccount.class);
                startActivity(startIntent);
            }
        });
    }

    private boolean authenticateLogin() {
        AsyncTask<Context, Void, String> task = new asyncServerLogin();
        String result = "";
        try {
            result = task.execute(getApplicationContext()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result.contains("success");
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

    private class asyncServerLogin extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", username.getText().toString());
            params.put("password", password.getText().toString());

            returnText = ServerComm.getRequest(ServerComm.GET, params, ServerComm.URL_LOGIN);

            return returnText;
        }
    }
}
