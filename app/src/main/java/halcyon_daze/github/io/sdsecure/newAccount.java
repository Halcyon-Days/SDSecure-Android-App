package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class newAccount extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        Button newAccountBtn = findViewById(R.id.newAccountBtn);
        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        responseText = findViewById(R.id.responseText);

        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                if(createAccount(username.getText().toString(), password.getText().toString())) {
                    Intent startIntent = new Intent(getApplicationContext(), login.class);
                    startActivity(startIntent);
                } else {
                    responseText.setText("Invalid username or password!");
                }
            }
    }

    private boolean createAccount(String username, Strong)
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
