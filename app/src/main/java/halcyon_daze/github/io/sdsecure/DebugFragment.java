package halcyon_daze.github.io.sdsecure;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DebugFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DebugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebugFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    EditText idText;
    EditText latText;
    EditText lngText;
    EditText encryptText;
    TextView serverResponseText;
    DrawerLayout mDrawerLayout;

    public DebugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DebugFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebugFragment newInstance(String param1, String param2) {
        DebugFragment fragment = new DebugFragment();
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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //activity code
        super.onActivityCreated(savedInstanceState);
        idText = (EditText) getView().findViewById(R.id.idText);
        latText = (EditText) getView().findViewById(R.id.latText);
        lngText = (EditText) getView().findViewById(R.id.usernameText);
        encryptText = (EditText) getView().findViewById(R.id.encryptText);
        serverResponseText = (TextView) getView().findViewById(R.id.serverResponseText);

        Button postBtn = getView().findViewById(R.id.postBut);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                new asyncServerPost().execute(getActivity() );
            }
        });

        Button getBtn = getView().findViewById(R.id.getBut);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                new asyncServerGet().execute(getActivity() );
            }
        });

        Button deleteBtn = getView().findViewById(R.id.deleteBut);
        deleteBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                new asyncServerDelete().execute(getActivity() );
            }
        });

        // Paul: need this for Android 6.0+, dynamic permission
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
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
            latText.setText("");
            lngText.setText("");
            encryptText.setText("");
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
            idText.setText("");
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
            idText.setText("");
            serverResponseText.setText("Sending a DELETE request: \n " + returnText);

        }
    }

    /*
        Referenced to https://stackoverflow.com/questions/35941051/on-button-click-hide-keyboard
     */
    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
