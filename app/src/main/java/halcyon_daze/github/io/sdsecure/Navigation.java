package halcyon_daze.github.io.sdsecure;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<SDCard> cardList;
    private FragmentManager fm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString("username");
        } else {
            username = "";
        }

        // This puts the fragment into the blank frame space specified in content_navigation.xml
        fm = getFragmentManager();

        //prevents keyboard from automatically opening
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //changes navigation bar text to username
        TextView usernameText = navigationView.getHeaderView(0).findViewById(R.id.usernameText);
        usernameText.setText(username);

        //handler for periodically refreshing the stops in the list according to the refreshtimer value
        final Handler refreshHandler = new Handler();
        Runnable refreshCode = new Runnable() {
            @Override
            public void run() {
                new asyncServerList().execute();
                refreshHandler.postDelayed(this, 1000*60*10);
            }
        };

        //starts refresh handler
        refreshHandler.post(refreshCode);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            fm.beginTransaction().replace(R.id.content_frame, ListFragment.newInstance("",cardList)).commit();
        } else if (id == R.id.nav_map) {

            // Implements the google maps fragment
            MapFragment mapFragment = MapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    //CREATES MARKER FOR LOCATIONS OF SD CARD
                    LatLng latLng = new LatLng( 49.2606, -123.2460);

                    //focuses the map on ubc
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                    for(SDCard s: cardList) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(s.getLatitude()), Double.valueOf(s.getLongitude())))
                                .title(s.getID()));
                    }
                }
            });

            fm.beginTransaction().replace(R.id.content_frame, mapFragment).commit();
        } else if (id == R.id.nav_debug) {
            fm.beginTransaction().replace(R.id.content_frame, DebugFragment.newInstance("","")).commit();
        } else if (id == R.id.nav_logout) {
            Intent startIntent = new Intent(getApplicationContext(), login.class);
            startActivity(startIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //asynchronous task to send list request
    private class asyncServerList extends AsyncTask<Context, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Context... input) {
            String returnText = "";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name",username);
            returnText = ServerComm.getRequest(ServerComm.GET, params, ServerComm.URL_HISTORY_LIST);

            return returnText;
        }

        protected void onPostExecute(String returnText) {
            if(cardList == null) {
                updateList(returnText);
                fm.beginTransaction().replace(R.id.content_frame, ListFragment.newInstance("",cardList)).commit();
            }

            updateList(returnText);
        }
    }

    private void updateList(String returnText) {
        try {
            JSONArray testArray = new JSONArray(returnText);
            cardList = SDCard.parseSDJSON(testArray);
        } catch(JSONException e) {
            System.out.println("Json parse failed");
        }
    }
}
