package com.ericsson.postbox.entity;

import android.database.Cursor;

import com.ericsson.postbox.shared.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Noman on 12/31/2014.
 */
public class Users implements Iterable<User>
{
    private static final User USER_NOT_FOUND = null;
    List<User> myList = new ArrayList<>();

    public Users()
    {
    }

    public Users(JSONArray results) throws JSONException
    {
        for(int i= 0; i< results.length(); i++)
        {
            JSONObject userJson = results.getJSONObject(i);
            long userId = userJson.getLong(Constants.USER_ID_COULMN);
            User user = createUserWithId(userId, userJson);
            add(user);
        }
    }

    private User createUserWithId(long userId, JSONObject userJson) throws JSONException
    {
        User user = new User.UserBuilder()
                .withId(userId)
                .withFullName(userJson.getString(Constants.FULL_NAME_COULMN))
                .withEmail(userJson.getString(Constants.EMAIL_COULMN))
                .withLongitude(userJson.getDouble(Constants.LONGITUDE_COULMN))
                .withLatitude(userJson.getDouble(Constants.LATITUDE_COULMN))
                .build();

        return user;
    }

    private void add(User user)
    {
        myList.add(user);
    }

    public int size()
    {
        return myList.size();
    }

    public User get(int position)
    {
        return myList.get(position);
    }

    public User getUserById(long id)
    {
        for(User user: myList)
        {
            if (id == user.getId())
            {
                return user;
            }
        }
        return USER_NOT_FOUND;
    }

    @Override
    public Iterator<User> iterator()
    {
        return myList.iterator();
    }
}
