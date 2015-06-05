package com.ericsson.postbox.userinterface;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ericsson.postbox.entity.MarkerMap;
import com.ericsson.postbox.entity.User;
import com.ericsson.postbox.library.DBTools;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.ericsson.postbox.shared.Constants.*;

public class MapsFragment extends Fragment
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        myDb = new DBTools(getActivity().getApplicationContext());
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        myFragmentActivity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        createLocationBroadcastListener();
        if (view != null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
            {
                parent.removeView(view);
            }
        }
        try
        {
            view = inflater.inflate(R.layout.activity_maps, container, false);
        }
        catch (InflateException e)
        {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((myReceiver), new IntentFilter(LOCATION_UPDATE_BROADCASTER));
        refresh();
    }

    @Override
    public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        myDb.closeDb();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        myFragmentActivity = null;
    }

    private void createLocationBroadcastListener()
    {
        myReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.i("Broadcast receiver", "Refresh due to location update broadcaster");
                if(intent.getExtras() == null || intent.getExtras().isEmpty())
                {
                    refresh();
                }
                else
                {
                    String boxId = intent.getStringExtra(BOX_ID_COULMN);
                    BitmapDescriptor bmpDescripter = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                    if ("Box_1".equals(boxId))
                    {
                        myEricssonMarker.remove();
                        addEricssonMarker(bmpDescripter);
                    }else
                    {
                        myHyperMarker.remove();
                        addHyperIslandMarker(bmpDescripter);
                    }
                }
            }
        };
    }

    public void refresh()
    {
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
    private void setUpMapIfNeeded()
    {
        if (myMap == null)
        {
            createMap();
        }

        if (myMap != null)
        {
            setUpMap();
        }
    }

    private void createMap()
    {
        myFragment = ((SupportMapFragment) myFragmentActivity.getSupportFragmentManager().findFragmentById(R.id.map));
        //myFragment = getMapFragment().getMap();
        myMap = myFragment.getMap();
        //myMap = getMapFragment().getMap();
        LatLng position = new LatLng(56.160001, 15.596899);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            public Marker lastOpenedMarker;

            @Override
            public boolean onMarkerClick(Marker marker)
            {
                if (lastOpenedMarker != null)
                {
                    lastOpenedMarker.hideInfoWindow();

                    if (lastOpenedMarker.equals(marker))
                    {
                        lastOpenedMarker = null;
                        return true;
                    }
                }

                marker.showInfoWindow();
                lastOpenedMarker = marker;
                return true;
            }
        });
    }


    private MapFragment getMapFragment() {
        FragmentManager fm = null;

        Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "release: " + Build.VERSION.RELEASE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using getFragmentManager");
            fm = getFragmentManager();
        } else {
            Log.d(TAG, "using getChildFragmentManager");
            fm = getChildFragmentManager();
        }

        return (MapFragment) fm.findFragmentById(R.id.map);
    }

    private void setUpMap()
    {
        myMap.clear();
        User user = myDb.getUserInfo();

        // Ericsson
        BitmapDescriptor bmpDescripter = BitmapDescriptorFactory.defaultMarker();
        addEricssonMarker(bmpDescripter);
        addHyperIslandMarker(bmpDescripter);
    }

    private void addHyperIslandMarker(BitmapDescriptor bmpDescripter) {
        myHyperMarker = addMarker(56.160001, 15.596899, "Hyper Island Box", myHyperMarkerId, bmpDescripter);
    }

    private void addEricssonMarker(BitmapDescriptor bmpDescripter)
    {
        myEricssonMarker = addMarker(56.164661, 15.593358, "Ericsson Box", myEricssonMarkerId, bmpDescripter);
    }

    private Marker addMarker(double latitude, double longitude, String name, long id, BitmapDescriptor bmpDescriptor)
    {
        LatLng position = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions()
                .position(position)
                .title(name)
                .icon(bmpDescriptor);
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
        Marker locationMarker = myMap.addMarker(marker);
        myMarkerMap.put(id, locationMarker);
        return locationMarker;
    }
}
