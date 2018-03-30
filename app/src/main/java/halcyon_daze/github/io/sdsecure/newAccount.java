package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class newAccount extends AppCompatActivity {

    private static int REQUEST_PHOTO = 1;
    private File mPhoto; // TODO: gaaaah!

    EditText username;
    EditText password;
    TextView responseText;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        //prevents keyboard from automatically opening
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Button newAccountBtn = findViewById(R.id.createNewAccountBtn);
        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        responseText = findViewById(R.id.responseText);
        mImageView = findViewById(R.id.choosePhotosBtn);
        mPhoto = null;

        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                if (createAccount()) {
                    Intent startIntent = new Intent(getApplicationContext(), login.class);
                    startActivity(startIntent);
                } else {
                    responseText.setText("Invalid username or password!");
                }
            }
        });

        FloatingActionButton choosePhotosBtn =  findViewById(R.id.choosePhotosBtn);

        choosePhotosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                int numPhotos = uploadPhotos();

                if (numPhotos > 1) {
                    responseText.setText(numPhotos + " photos chosen!");
                } else if (numPhotos == 1){
                    responseText.setText(numPhotos + " photo chosen!");
                } else {
                    responseText.setText("Error choosing photos!");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);

            Date currentTime = Calendar.getInstance().getTime();
            File file = new File(this.getCacheDir(), currentTime.toString() + ".png");
            try {
                file.createNewFile();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                byte[] bitmapdata = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                mPhoto = file;

                // TODO: there should be another button to actually upload AFTER confirming, for now upload here directly
                if (mPhoto != null) {
                    new asyncServerUpload().execute(getApplicationContext());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean createAccount() {
        AsyncTask<Context, Void, String> task = new asyncServerRegister();
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
    returns number of photos uploaded
     */
    private int uploadPhotos() {
        dispatchTakePictureIntent();
        return 1; // for now just 1
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_PHOTO);
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

    private class asyncServerUpload extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            ServerComm.uploadImage(mPhoto, "TEMP");
            return null;
        }
    }

    private class asyncServerRegister extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", username.getText().toString());
            params.put("password", password.getText().toString());

            returnText = ServerComm.getRequest(ServerComm.POST, params, ServerComm.URL_LOGIN);

            return returnText;
        }
    }
}
