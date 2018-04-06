package halcyon_daze.github.io.sdsecure;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlueToothFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlueToothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlueToothFragment extends android.app.Fragment {

    private static final String BLUETOOTH_MAC = "0006666CA782";
    private static final int REQUEST_PHOTO = 1;
    private static String result = "";

    private String lat;
    private String lng;
    private int encrypt;

    private File mPhoto;
    asyncBluetooth pollBluetooth;

    // a bluetooth “socket” to a bluetooth device
    private BluetoothSocket mmSocket = null;

    // input/output “streams” with which we can read and write to device
    // Use of “static” important, it means variables can be accessed
    // without an object, this is useful as other activities can use
    // these streams to communicate after they have been opened.
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;

    private TextView updateText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String username;

    private OnFragmentInteractionListener mListener;
    private boolean connected = false;
    private boolean activityDone = true;
    private boolean polling = false;

    String returnText;

    BluetoothAdapter mBluetoothAdapter;

    public BlueToothFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlueToothFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlueToothFragment newInstance(String param1, String param2) {
        BlueToothFragment fragment = new BlueToothFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            username = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blue_tooth, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //activity code
        super.onActivityCreated(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        updateText = getView().findViewById(R.id.updateText);
        // check to see if your android device even has a bluetooth device !!!!,
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(getView().getContext(), "No Bluetooth !!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            // Paul: need this for Android 6.0+, dynamic permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH},
                    1);
        }

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            // Paul: need this for Android 6.0+, dynamic permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                    1);
        }

        tryServerConnect();

        ToggleButton bluetoothSwitch = getView().findViewById(R.id.bluetoothSwitch);
        bluetoothSwitch.setChecked(false);
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                polling = isChecked;
                if(isChecked) {
                    updateText.setText("Awaiting response from device...");
                } else {
                    updateText.setText("Start polling?");
                }
            }
        });

        Button skipBtn = getView().findViewById(R.id.SkipBtn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteToBTDevice("Success");
            }
        });

        FloatingActionButton refreshBtn = getView().findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mmInStream != null) {
                    closeConnection();
                }
                tryServerConnect();
            }
        });

        if(connected == false||mmInStream == null) {
            Toast toast = Toast.makeText(getView().getContext(), "Connection failure, try reloading the page", Toast.LENGTH_LONG);
            toast.show();
        } else {
            pollBluetooth = new asyncBluetooth();
            pollBluetooth.execute();
        }
    }


    void closeConnection() {
        try {
            mmInStream.close();
            mmInStream = null;
        } catch (IOException e) {
        }
        try {
            mmOutStream.close();
            mmOutStream = null;
        } catch (IOException e) {
        }
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {
        }
        connected = false;
    }

    public void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        mmSocket = null;
        // universal UUID for a serial profile RFCOMM blue tooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        // Get a Bluetooth Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            System.out.println(e);
            Toast.makeText(getView().getContext(), "Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void ConnectToSerialBlueToothDevice() {
        try {
            // Attempt connection to the device through the socket.
            mmSocket.connect();
            Toast.makeText(getView().getContext(), "Connection Made", Toast.LENGTH_LONG).show();
        } catch (IOException connectException) {
            System.out.println(connectException);
            Toast.makeText(getView().getContext(), "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }
        //create the input/output stream and record fact we have made a connection
        GetInputOutputStreamsForSocket(); // see page 26
        connected = true;
    }

    // gets the input/output stream associated with the current socket
    public void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
            System.out.println("Streams created successfully!");
        } catch (IOException e) {
            System.out.println("Error in creating bluetooth streams!");
        }
    }

    //
// This function write a line of text (in the form of an array of bytes)
// to the Bluetooth device and then sends the string “\r\n”
// (required by the bluetooth dongle)
//
    public void WriteToBTDevice(String message) {
        String s = new String("\r\n");
        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();
        try {
            mmOutStream.write(msgBuffer);
            mmOutStream.write(newline);
        } catch (IOException e) {
        }
    }

    // This function reads a line of text from the Bluetooth device
    public String ReadFromBTDevice() {
        byte c;
        String s = new String("");
        try { // Read from the InputStream using polling and timeout
            for (int i = 0; i < 200; i++) { // try to read for 2 seconds max
                SystemClock.sleep(10);
                if(mmInStream == null) return "";

                if (mmInStream.available() > 0) {
                    if ((c = (byte) mmInStream.read()) != '\r') // '\r' terminator
                        s += (char) c; // build up string 1 byte by byte
                    else {
                        System.out.println(s);
                        return s;
                    }
                }
            }
        } catch (IOException e) {
            return new String("-- No Response --");
        }
        return s;
    }

    private boolean tryServerConnect() {

        if((connected == false||mmInStream == null)) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("SDSecure")) {
                    CreateSerialBluetoothDeviceSocket(device);
                    ConnectToSerialBlueToothDevice();
                    return true;
                }
            }
        }

        return false;
    }

    //asynchronous task to send request
    private class asyncBluetooth extends AsyncTask<Context, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(Context... input) {
            String s;
            String latitude = "";
            String longitude = "";

            while(true) {
                if (polling) {
                    if (!connected || mmInStream == null) {
                        tryServerConnect();
                        SystemClock.sleep(10);
                    } else {

                        s = ReadFromBTDevice();
                        System.out.println(s);
                        if (s.contains("Testing")) {
                            WriteToBTDevice("Success");
                            System.out.println("Connection established!");
                        }

                        if (s.contains("lat") && s.contains("lng")) {
                            //removes spaces, words latitude and longitude, leaving begin values separated by comma
                            latitude = s.substring(s.indexOf("lat: ") + 5, s.indexOf(","));
                            longitude = s.substring(s.indexOf("lng: ") + 5, s.lastIndexOf("."));
                            WriteToBTDevice("Changed current location!");
                        }

                        if (s.contains("Encryption Start")) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run () {
                                    updateText.setText("Starting Encryption!");
                                }
                            });
                            System.out.println("Encryption process, starting verify");
                            WriteToBTDevice("Starting encrypt verify");
                            returnText = VerifyEncrypt(latitude, longitude);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run () {
                                    updateText.setText(returnText);
                                }
                            });
                            latitude = "";
                            longitude = "";

                        } else if (s.contains("Decryption Start")) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run () {
                                    updateText.setText("Starting Decryption!");
                                }
                            });
                            System.out.println("Decryption process, starting verify");
                            WriteToBTDevice("Starting decrypt verify");
                            returnText = VerifyDecrypt(latitude, longitude);
                            if(returnText.contains("Success")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        updateText.setText("Facial Recognition Success!");
                                    }
                                });
                            }
                            latitude = "";
                            longitude = "";
                        }
                    }
                }
            }
        }
    }

    //probably create new activity
    String VerifyEncrypt(String latitude, String longitude) {

        lat = latitude;
        lng = longitude;
        encrypt = 1;
        System.out.println("Creating new encryption with latitude: " + latitude + " and longitude: " + longitude);

        AsyncTask<Context, Void, String> task = new asyncServerPost();
        try {
            System.out.println("Starting new task to create encryption");
            result = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity().getApplicationContext()).get();
            System.out.println("Ending new task to create encryption");
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (ExecutionException e) {
        System.out.println(e);
    }

    //BEFORE PARSED RESULT INTO INTEGER, DID NOT WORK CHECK
        String ok = !result.equals("0") ? "Encryption Success" : "Encryption Failed";
        WriteToBTDevice(ok);

        return ok;
    }

    //probably create new activity
    String VerifyDecrypt(String latitude, String longitude) {
        lat = latitude;
        lng = longitude;
        encrypt = 0;
        String ok;

        System.out.println("Creating decryption with latitude " + latitude + "and longitude " + longitude);
        uploadPhotos();
        while(!activityDone);
        getActivity().runOnUiThread(new Runnable() {
            public void run () {
                updateText.setText("Sending photo!");
            }
        });
        if(result.contains("Failed")) {
            ok = "Decryption Failed";
            getActivity().runOnUiThread(new Runnable() {
                public void run () {
                    updateText.setText("Error in Decryption!\nTry Again!");
                }
            });
        } else {
            AsyncTask<Context, Void, String> task = new asyncServerUpload();
            try {
                System.out.println("Starting new task to send photos");
                result = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity().getApplicationContext()).get();
                System.out.println("Ending new task to send photos");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            ok = result.contains("success") ? "Decryption Success" : "Decryption Failed";
            if (result.contains("success")) {
                AsyncTask<Context, Void, String> task2 = new asyncServerPost();
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity().getApplicationContext());
            } else {
                ok = "Decryption Failed";
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        updateText.setText("Error in Decryption!\nTry Again!");
                    }
                });
            }
            WriteToBTDevice(ok);
        }
            return ok;

    }

    /*
    returns number of photos uploaded
     */
    private int uploadPhotos() {
        activityDone = false;
        dispatchTakePictureIntent();
        return 1; // for now just 1
    }

    private void dispatchTakePictureIntent() {
        System.out.println("taking picture");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_PHOTO);
        }
    }

    private class asyncServerUpload extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            return ServerComm.uploadImage(mPhoto, username, true);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == (RESULT_CANCELED)) {
            activityDone = true;
            result = "Failed, Camera app interrupted";
        }
        if (resultCode != RESULT_OK) {
            return;
        }
            switch (requestCode) {
            case REQUEST_PHOTO: {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                Date currentTime = Calendar.getInstance().getTime();
                File file = new File(getActivity().getCacheDir(), currentTime.toString() + ".jpg");
                try {
                    file.createNewFile();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] bitmapdata = baos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    mPhoto = file;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        activityDone = true;
        result = "Success, picture taken";
    }

    private class asyncServerPost extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... input) {
            System.out.println("Sending request");
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            if(!lat.equals("") && !lng.equals("")) {
                params.put("lat", lat);
                params.put("lng", lng);
            }
            params.put("encryption", String.valueOf(encrypt));
            params.put("name", username);
            returnText = ServerComm.getRequest(ServerComm.POST, params, ServerComm.URL_HISTORY);
            System.out.println("got request " + returnText);
            return returnText;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if(pollBluetooth != null) {
            pollBluetooth.cancel(true);
        }

        if(mmInStream != null) {
            closeConnection();
        }

    }

}
