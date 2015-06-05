package com.ericsson.postbox.entity;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Noman on 3/3/2015.
 */
public class MarkerMap
{
    private Map<Long, Marker> myMap = new HashMap<>();

    public void put(long id, Marker locationMarker)
    {
        myMap.put(id, locationMarker);
    }

    public Marker get(long userId)
    {
        return myMap.get(userId);
    }
}
