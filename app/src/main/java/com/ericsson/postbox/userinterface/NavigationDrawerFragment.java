package com.ericsson.postbox.userinterface;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ericsson.postbox.userinterface.util.MainNavigationItem;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
{

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks myCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle myDrawerToggle;

    private DrawerLayout myDrawerLayout;
    private ListView myDrawerListView;
    private View myFragmentContainerView;

    private int myCurrentSelectedPosition = 0;
    private boolean myFromSavedInstanceState;
    private boolean myUserLearnedDrawer;
    private ArrayAdapter<String> myAdapter;
    private MainNavigationItem myCurrentNavItem;
    private Fragment myCurrentNavFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        myUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null)
        {
            myCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            myFromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        // Select either the default item (0) or the last selected item.
        selectItem(myCurrentSelectedPosition);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myDrawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        myDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectItem(position);
            }
        });
        myAdapter = new ArrayAdapter<String>(getActionBar().getThemedContext(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, new String[]{getString(R.string.title_message_list), getString(R.string.title_map)});
        myDrawerListView.setAdapter(myAdapter);
        myDrawerListView.setItemChecked(myCurrentSelectedPosition, true);
        return myDrawerListView;
    }

    public boolean isDrawerOpen()
    {
        return myDrawerLayout != null && myDrawerLayout.isDrawerOpen(myFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout)
    {
        myFragmentContainerView = getActivity().findViewById(fragmentId);
        myDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        myDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        myDrawerToggle = new ActionBarDrawerToggle(getActivity(),                    /* host Activity */
                myDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                if (!isAdded())
                {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                if (!isAdded())
                {
                    return;
                }

                if (!myUserLearnedDrawer)
                {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    myUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!myUserLearnedDrawer && !myFromSavedInstanceState)
        {
            myDrawerLayout.openDrawer(myFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        myDrawerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                myDrawerToggle.syncState();
            }
        });

        myDrawerLayout.setDrawerListener(myDrawerToggle);
    }

    private void selectItem(int position)
    {
        myCurrentSelectedPosition = position;
        if (myDrawerListView != null)
        {
            myDrawerListView.setItemChecked(position, true);
        }
        if (myDrawerLayout != null)
        {
            myDrawerLayout.closeDrawer(myFragmentContainerView);
        }
        if (myCallbacks != null)
        {
            myCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            myCallbacks = (NavigationDrawerCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        myCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, myCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        myDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (myDrawerLayout != null && isDrawerOpen())
        {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (myDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

         return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar()
    {
        return getActivity().getActionBar();
    }

    private void setTitle(CharSequence title)
    {
        getActionBar().setTitle(title);
    }

    public Fragment displayNavFragment(MainNavigationItem navItem, boolean addToBackStack)
    {
        if(navItem == myCurrentNavItem)
        {
            return myCurrentNavFragment;
        }

        Fragment fragment = Fragment.instantiate(getActivity().getApplicationContext(), navItem.getFragClass().getName());
        if(fragment != null)
        {
            if (addToBackStack)
            {
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
            }
            else
            {
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
            setCurrentNavItem(navItem);
        }

        myCurrentNavFragment = fragment;
        return myCurrentNavFragment;
    }
    private void setCurrentNavItem(MainNavigationItem navItem)
    {
        int position = navItem.ordinal();
        // If navItem is in DrawerAdapter
        if(position >= 0 && position < myAdapter.getCount()){
            myDrawerListView.setItemChecked(position, true);
        }
        else{
            // navItem not in DrawerAdapter, de-select current item
            if(myCurrentNavItem != null){
                myDrawerListView.setItemChecked(myCurrentNavItem.ordinal(), false);
            }
        }
        myDrawerLayout.closeDrawer(myDrawerListView);
        setTitle(getString(navItem.getTitleResId()));
        myCurrentNavItem = navItem;
    }

    public void invalidateCurrentNavigationItem()
    {
        myCurrentNavItem = null;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks
    {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
