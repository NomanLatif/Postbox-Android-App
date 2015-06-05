package com.ericsson.postbox.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 5/21/15.
 */
public class Messages implements Iterable<PostMessage>
{
    private List<PostMessage> myList = new ArrayList<>();

    public void add(PostMessage message)
    {
        myList.add(message);
    }

    @Override
    public Iterator<PostMessage> iterator()
    {
        return myList.iterator();
    }

    public int size()
    {
        return myList.size();
    }

    public PostMessage get(int position)
    {
        return myList.get(position);
    }
}
