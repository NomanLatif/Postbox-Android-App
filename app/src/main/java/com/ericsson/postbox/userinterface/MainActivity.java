package com.ericsson.postbox.userinterface;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ericsson.postbox.MapsActivity;
import com.ericsson.postbox.userinterface.fragmentutil.FragmentCommunicator;
import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.userinterface.fragmentutil.FragmentCommunicationData;
import com.ericsson.postbox.userinterface.util.MainNavigationItem;

import org.json.JSONArray;

public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentCommunicator, AsyncResponse
{
    private static final boolean DO_NOT_ADD_TO_BACK_STACK = false;
    private static final boolean ADD_TO_BACK_STACK = true;
    private NavigationDrawerFragment myDrawerFragment;
    private CharSequence myTitle;
    private ProfileFragment myProfileFragment;
    private SettingsFragment mySettingsFragment;
    private MessageListFragment myMessagesList;
    private MapsFragment myMapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        myDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        myTitle = getTitle();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        switch (position)
        {
            case 0:
                myMessagesList= (MessageListFragment) myDrawerFragment.displayNavFragment(MainNavigationItem.MESSAGE_LIST, ADD_TO_BACK_STACK);
                break;
            case 1:
                //startMapActivity();
                myMapsFragment = (MapsFragment) myDrawerFragment.displayNavFragment(MainNavigationItem.MAP, DO_NOT_ADD_TO_BACK_STACK);
                break;
            case 2:
                break;
        }
    }

    private void startMapActivity()
    {
        Intent activity = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(activity);
        finish();
    }
    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0)
        {
            fm.popBackStack();
            myDrawerFragment.invalidateCurrentNavigationItem();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!myDrawerFragment.isDrawerOpen())
        {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i("onOptionsItemSelected", "onOptionsItemSelected");

        switch (item.getItemId())
        {
            case R.id.action_profile:
                myProfileFragment= (ProfileFragment) myDrawerFragment.displayNavFragment(MainNavigationItem.PROFILE, ADD_TO_BACK_STACK);
                return true;

            case R.id.action_logout:
                unregisterGcmId();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(myTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void unregisterGcmId()
    {
        GcmRegisteration gcmRegisteration = new GcmRegisteration(this);
        gcmRegisteration.unregisterGcmAndLogout();
    }

    @Override
    public void onFragmentInteraction(FragmentCommunicationData data)
    {

    }

    @Override
    public void processFinish(JSONArray results)
    {

    }

}
