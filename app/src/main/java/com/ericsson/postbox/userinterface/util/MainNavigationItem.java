package com.ericsson.postbox.userinterface.util;

import android.app.Fragment;

import com.ericsson.postbox.userinterface.MapsFragment;
import com.ericsson.postbox.userinterface.MessageListFragment;
import com.ericsson.postbox.userinterface.ProfileFragment;
import com.ericsson.postbox.userinterface.R;

/**
 * Created by Noman on 2/21/2015.
 */
public enum MainNavigationItem
{
    // Displayed in NavDrawerListAdapter
    MESSAGE_LIST(R.string.app_name, R.layout.fragment_message, MessageListFragment.class),
    MAP(R.string.title_map, R.layout.activity_maps, MapsFragment.class),
    PROFILE(R.string.app_name, R.layout.fragment_profile, ProfileFragment.class);

    // not the drawer options
//    SETTINGS(R.string.title_settings, R.layout.fragment_settings,  SettingsFragment.class),

    private static MainNavigationItem LAST_NAV_ITEM = MESSAGE_LIST;

    private int mTitleResId;
    private int mLayoutResId;
    private Class<? extends Fragment> mFragClass;

    MainNavigationItem(int titleResId, int layoutResId, Class<? extends Fragment> fragClass)
    {
        mTitleResId  =  titleResId;
        mLayoutResId = layoutResId;
        mFragClass   = fragClass;
    }

    public int getLayoutResId()
    {
        return mLayoutResId;
    }

    public int getTitleResId()
    {
        return mTitleResId;
    }

    public Class<? extends Fragment> getFragClass()
    {
        return mFragClass;
    }

    public static MainNavigationItem[] getNavAdapterItems()
    {
        int count = LAST_NAV_ITEM.ordinal();

        MainNavigationItem[] adapterItems = new MainNavigationItem[count];
        for(int i = 0; i < count; i++){
            adapterItems[i] = values()[i];
        }
        return adapterItems;
    }
}
