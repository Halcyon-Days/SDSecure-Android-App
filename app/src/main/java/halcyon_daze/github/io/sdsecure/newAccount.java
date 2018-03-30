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

import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class newAccount extends AppCompatActivity {

    private File mPhoto; // TODO: gaaaah!

    EditText username;
    EditText password;
    TextView responseText;
    ImageView mImageView;
    Button mGalleryBtn;

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
        //mGalleryBtn = findViewById(R.id.galleryBtn);
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
                // Source: https://android-arsenal.com/details/1/5241
                Intent intent = new Intent(newAccount.this, GalleryActivity.class);
                Params params = new Params();
                params.setCaptureLimit(10);
                params.setPickerLimit(10);
                params.setToolbarColor(100);
                params.setActionButtonColor(100);
                params.setButtonTextColor(100);
                intent.putExtra(Constants.KEY_PARAMS, params);
                startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);

                /*if (numPhotos > 1) {
                    responseText.setText(numPhotos + " photos chosen!");
                } else if (numPhotos == 1){
                    responseText.setText(numPhotos + " photo chosen!");
                } else {
                    responseText.setText("Error choosing photos!");
                }*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE: {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                break;
            }
            case Constants.TYPE_MULTI_PICKER: {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                // TODO: clean this up if time allows
                for (Image i : images) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), i.uri);
                        Date currentTime = Calendar.getInstance().getTime();
                        File file = new File(this.getCacheDir(), currentTime.toString() + ".jpg");
                        try {
                            file.createNewFile();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bitmapdata = baos.toByteArray();

                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            mPhoto = file;

                            // TODO: there should be another button to actually upload AFTER confirming, for now upload here directly
                            if (mPhoto != null) {
                                AsyncTask<Context, Void, String> task = new asyncServerUpload();
                                try {
                                    task.execute(getApplicationContext()).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                            bitmap.recycle();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
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
        Referenced to https://stackoverflow.com/questions/35941051/on-button-click-hide-keyboard
     */
        private void closeKeyboard() {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
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

    private class asyncServerUpload extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            ServerComm.uploadImage(mPhoto, "TEMP");
            return null;
        }
    }
}
