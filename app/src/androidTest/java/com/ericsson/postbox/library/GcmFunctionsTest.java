package com.ericsson.postbox.library;

import android.test.AndroidTestCase;

import com.ericsson.postbox.shared.ResponseCode;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.ericsson.postbox.library.util.UserTestUtils.*;
import static com.ericsson.postbox.shared.Constants.*;

/**
 * Created by Noman on 1/15/2015.
 */
public class GcmFunctionsTest extends AndroidTestCase implements AsyncResponse
{
    private static final String PASSWORD = "123456";
    private static final String EMAIL = "testUser@postbox.com"; // should not be changed
    private static final String NAME = "test";

    public static final String GCM_REG_ID = "123456789";
    DBTools myDb;
    GcmFunctions myGcmFunctions = new GcmFunctions(this);

    private void setup() throws InterruptedException, ExecutionException, JSONException
    {
        myDb = new DBTools(getContext());
        deleteTestUser(EMAIL, this);
        createTestUser(NAME, EMAIL, PASSWORD, this);
        loginTestUser(EMAIL, PASSWORD, this, myDb);
    }

    public void testUserCanAddGcmRegistrationId() throws JSONException, ExecutionException, InterruptedException
    {
        setup();
        myGcmFunctions.registerGcmRegisterationId(GCM_REG_ID, myDb);

        JSONArray results = myGcmFunctions.getAsyncTaskResult();
        JSONObject result = results.getJSONObject(0);
        Assert.assertEquals("" + ResponseCode.SUCCESSFUL_PROCESSED.getCode(), result.getString(POSTBOX_CODE));
    }

    public void testCanDeleteGcmRegistrationId() throws JSONException, ExecutionException, InterruptedException
    {
        setup();
        myGcmFunctions.registerGcmRegisterationId(GCM_REG_ID, myDb);
        myGcmFunctions.deleteGcmRegisterationId(myDb);

        JSONArray results = myGcmFunctions.getAsyncTaskResult();
        JSONObject result = results.getJSONObject(0);
        Assert.assertEquals("" + ResponseCode.SUCCESSFUL_PROCESSED.getCode(), result.getString(POSTBOX_CODE));
    }

    @Override
    public void processFinish(JSONArray results)
    {
    }
}
