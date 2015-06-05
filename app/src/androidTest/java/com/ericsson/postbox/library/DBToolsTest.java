package com.ericsson.postbox.library;

import static com.ericsson.postbox.shared.Constants.*;
import android.test.AndroidTestCase;

import com.ericsson.postbox.entity.Messages;
import com.ericsson.postbox.entity.User;
import com.ericsson.postbox.entity.Users;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.shared.SecurityUtil;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Noman on 12/25/2014.
 */
public class DBToolsTest extends AndroidTestCase
{
    private static final long USER_ID = 123;
    private static final String USER_NAME = "Test User";
    private static final String EMAIL = "testUser@postbox.com";
    private static final String PASSWORD = "123456789";

    private static final long MESSAGE_ID = 1;

    private DBTools myDb;

    private void setup() throws JSONException
    {
        myDb = new DBTools(getContext());
        myDb.resetTables();
        insertUser();
    }

    private void insertUser() throws JSONException
    {
        JSONObject params = new JSONObject();
        params.put(USER_ID_COULMN, USER_ID);
        params.put(FULL_NAME_COULMN, USER_NAME);
        params.put(EMAIL_COULMN, EMAIL);
        params.put(LONGITUDE_COULMN, 0.0);
        params.put(LATITUDE_COULMN, 0.0);

        myDb.insertUser(params, PASSWORD);
    }

    public void testCanInsertUser() throws JSONException
    {
        setup();

        User user = myDb.getUserInfo();
        Assert.assertEquals(USER_ID, user.getId());
        Assert.assertEquals(SecurityUtil.md5(PASSWORD), user.getPassword());
        Assert.assertEquals(USER_NAME, user.getName());
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(0.0, user.getLongitude());
        Assert.assertEquals(0.0, user.getLatitude());
    }

    public void testCanUpdateGcmRegistration() throws JSONException
    {
        setup();

        String gcmRegistrationId = "123456789";
        int rowsAffected = myDb.updateGcmRegistration(gcmRegistrationId);
        Assert.assertEquals(1, rowsAffected);

        User user = myDb.getUserInfo();
        Assert.assertEquals(gcmRegistrationId, user.getGcmRegistationId());
    }

    public void testCanAddReceivePostMessage() throws JSONException
    {
        setup();
        myDb.insertMessage();
        Messages messages = myDb.getMessages();

        Assert.assertEquals(1, messages.size());
    }

    private JSONObject createJsonFor(long userId, long friendId, String name, String email) throws JSONException
    {
        JSONObject friend1Json = new JSONObject();
        friend1Json.put(USER_ID_COULMN, userId);
        friend1Json.put(FRIEND_ID_COULMN, friendId);
        friend1Json.put(FULL_NAME_COULMN, name);
        friend1Json.put(EMAIL_COULMN, email);
        friend1Json.put(LONGITUDE_COULMN, 0);
        friend1Json.put(LATITUDE_COULMN, 0);
        return friend1Json;
    }
}
