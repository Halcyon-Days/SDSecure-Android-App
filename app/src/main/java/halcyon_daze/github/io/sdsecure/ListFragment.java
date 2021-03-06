package halcyon_daze.github.io.sdsecure;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends android.app.Fragment {

    private ArrayList<SDCard> cardList;
    private ListView cardListView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String username;
    private ArrayList<SDCard> mParam2;

    private OnFragmentInteractionListener mListener;
    private asyncServerList task;
    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, ArrayList<SDCard> param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            cardList = (ArrayList<SDCard>) getArguments().getSerializable(ARG_PARAM2);
            getArguments().remove(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_list_encryptions_phone, container, false);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //activity code
        super.onActivityCreated(savedInstanceState);

        cardListView = getView().findViewById(R.id.cardList);

        //Creates adapter which shows the details of a bus when it is clicked in the listview
        if(cardList != null) {
            ListEncryptionAdapter cardListAdapter = new ListEncryptionAdapter(getActivity(), cardList);
            cardListView.setAdapter(cardListAdapter);
        }

        task = new asyncServerList();
        task.execute();
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

    //asynchronous task to send list request
    private class asyncServerList extends AsyncTask<Context, Void, String> {

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name",username);
            returnText = ServerComm.getRequest(ServerComm.GET, params, ServerComm.URL_HISTORY_LIST);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            updateList(returnText);
        }
    }

    private void updateList(String returnText) {
        try {
            JSONArray testArray = new JSONArray(returnText);
            cardList = SDCard.parseSDJSON(testArray);

            //Creates adapter which shows the details of a bus when it is clicked in the listview
            ListEncryptionAdapter cardListAdapter = new ListEncryptionAdapter(getActivity(), cardList);
            cardListView.setAdapter(cardListAdapter);
        } catch(JSONException e) {
            System.out.println("Json parse failed");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        task.cancel(true);

    }
}
