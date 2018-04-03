package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class newAccount extends AppCompatActivity {

    private List<File> mPhotos; // TODO: gaaaah!
    private int picked = 0;

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
        mPhotos = new ArrayList<>();

        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                while (mPhotos.size() != picked) {
                    // just poll for now
                }

                for (File f : mPhotos) {
                    AsyncTask<File, Void, String> task = new asyncServerUpload();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, f);
                }

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
                picked = images.size();
                mPhotos.clear();

                for (Image i : images) {
                    AsyncTask<Image, Void, Boolean> convertTask = new asyncConvertImage();
                    convertTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
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

    private class asyncServerUpload extends AsyncTask<File, Void, String> {

        @Override
        protected String doInBackground(File... files) {
            File file = files[0];
            return ServerComm.uploadImage(file, username.getText().toString(), false);
        }
    }

    private class asyncConvertImage extends AsyncTask<Image, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Image... images) {
            Image image = images[0];
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image.uri);
            Bitmap bitmap = null;
            try {
                bitmap = getThumbnail(image.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Date currentTime = Calendar.getInstance().getTime();
            Random rand = new Random();
            File file = new File(getCacheDir(), currentTime.toString() + String.valueOf(rand.nextInt(50000) + 1) + ".jpg");
            try {
                file.createNewFile();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapdata = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                mPhotos.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    // Taken from:
    // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 1024) ? (originalSize / 1024) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
}
