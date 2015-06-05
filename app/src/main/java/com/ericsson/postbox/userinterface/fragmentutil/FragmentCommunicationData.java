package com.ericsson.postbox.userinterface.fragmentutil;

import java.util.Map;

/**
 * Created by Noman on 1/7/2015.
 */
public class FragmentCommunicationData
{
    private final String myFragmentId;
    private final int myAction;
    private final Map<Integer, Object> myParams;

    public FragmentCommunicationData(String fragmentName, int action, Map<Integer, Object> params)
    {
        myFragmentId = fragmentName;
        myAction = action;
        myParams = params;
    }

    public Map<Integer, Object> getParams()
    {
        return myParams;
    }

    public String getFragmentName()
    {
        return myFragmentId;
    }

    public int getAction()
    {
        return myAction;
    }
}
