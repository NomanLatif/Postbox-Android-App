package com.ericsson.postbox.entity;

import static com.ericsson.postbox.shared.Constants.*;
import android.test.AndroidTestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Noman on 1/4/2015.
 */
public class UsersTest extends AndroidTestCase
{

    public static final int USER1_ID = 1;
    public static final String USER1_NAME = "user1";
    public static final String USER1_EMAIL = "user1@user1.com";
    public static final int USER2_ID = 2;
    public static final String USER2_NAME = "user2";
    public static final String USER2_EMAIL = "user2@user2.com";

    public void testCanCreateUsersFromJsonArray() throws JSONException
    {
        JSONObject user1Json = createUser(USER1_ID, USER1_NAME, USER1_EMAIL);
        JSONObject user2Json = createUser(USER2_ID, USER2_NAME, USER2_EMAIL);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(user1Json);
        jsonArray.put(user2Json);

        Users users = new Users(jsonArray);

        assertEquals(2, users.size());
        verify(users, USER1_ID, USER1_NAME, USER1_EMAIL);
        verify(users, USER2_ID, USER2_NAME, USER2_EMAIL);
    }

    private JSONObject createUser(int id, String name, String email) throws JSONException
    {
        JSONObject user = new JSONObject();
        user.put(USER_ID_COULMN, id);
        user.put(FULL_NAME_COULMN, name);
        user.put(EMAIL_COULMN, email);
        user.put(LONGITUDE_COULMN, 0.0);
        user.put(LATITUDE_COULMN, 0.0);

        return user;
    }

    private void verify(Users users, int id, String name, String email)
    {
        User user = users.getUserById(id);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
    }
}
