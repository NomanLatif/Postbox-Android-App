package com.ericsson.postbox;

import android.content.BroadcastReceiver;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.ericsson.postbox.entity.MarkerMap;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.userinterface.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
{

    private static final String TAG = "Map";
    private GoogleMap myMap;
    private SupportMapFragment myFragment;
    private FragmentActivity myFragmentActivity;
    private static View view;
    private BroadcastReceiver myReceiver;
    private DBTools myDb;
    private MarkerMap myMarkerMap = new MarkerMap();
    private Marker myEricssonMarker;
    private Marker myHyperMarker;
    private long myEricssonMarkerId = 1;
    private long myHyperMarkerId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #myMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (myMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (myMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #myMap} is not null.
     */
    private void setUpMap() {
        myMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
