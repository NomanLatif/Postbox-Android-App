package com.ericsson.postbox.userinterface;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;


public class SettingsFragment extends PreferenceFragment
{
    private static final boolean ALWAYS_SIMPLE_PREFS = true;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Activity myActivity;

    public static SettingsFragment newInstance(String param1, String param2)
    {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        myActivity = getActivity();
        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen()
    {
        if (!isSimplePreferences(getActivity().getApplicationContext()))
        {
            return;
        }
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory notificationHeader = new PreferenceCategory(getActivity().getApplicationContext());
        notificationHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(notificationHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_min_distance)));
        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
    }

    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context)
    {
        return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();

            if (preference instanceof ListPreference)
            {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                String key = listPreference.getContext().getString(R.string.pref_key_min_distance);
            }
            else if (preference instanceof RingtonePreference)
            {
                if (TextUtils.isEmpty(stringValue))
                {
                    preference.setSummary(R.string.pref_ringtone_silent);
                }
                else
                {
                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null)
                    {
                        preference.setSummary(null);
                    }
                    else
                    {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            }
            else
            {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    public static void setDefaultValues(Context context)
    {
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(context, R.xml.pref_notification, false);
    }
}
