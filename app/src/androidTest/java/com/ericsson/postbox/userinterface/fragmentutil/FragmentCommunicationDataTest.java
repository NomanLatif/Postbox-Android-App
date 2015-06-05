package com.ericsson.postbox.userinterface.fragmentutil;

import android.test.AndroidTestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Noman on 1/7/2015.
 */
public class FragmentCommunicationDataTest extends AndroidTestCase
{
    public void testCanCreate()
    {
        Map<Integer, Object> params = new HashMap<>();
        String fragmentName = "anyFragment";
        int action = 1;
        FragmentCommunicationData fcd = new FragmentCommunicationData(fragmentName, action, params);

        assertEquals(fragmentName, fcd.getFragmentName());
        assertEquals(action, fcd.getAction());
        assertEquals(params, fcd.getParams());
    }
}
