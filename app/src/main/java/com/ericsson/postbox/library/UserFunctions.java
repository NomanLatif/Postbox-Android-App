package com.ericsson.postbox.library;

import com.ericsson.postbox.entity.User;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.shared.SecurityUtil;
import com.ericsson.postbox.shared.UserStatus;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserFunctions
{
    private final AsyncResponse myResponseListener;
    private MyAsyncTask myAsyncTask;

	// Testing in localhost using wamp or xampp
	// use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String LOGIN_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/login";
    private static String SIGN_UP_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/signup";
    private static String UPDATE_INFO_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/updateinfo";
    private static String GET_USERS_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/allusers";
    private static String GET_USER_BY_ID_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/userbyid";
    private static String GET_USER_BY_EMAIL_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/userbyemail";
    private static String DELETE_URL = HostSettings.HOST + "/PostboxServer/api/v1/user/delete"; // only test user can be deleted

	public UserFunctions(AsyncResponse responseListener)
	{
        myResponseListener = responseListener;
	}

	public void loginUser(String email, String password)
	{
        try
        {
            myAsyncTask = new MyAsyncTask(myResponseListener, LOGIN_URL);
            JSONObject params = new JSONObject();
            params.put(Constants.EMAIL_COULMN, email);
            params.put(Constants.PASSWORD_COULMN, SecurityUtil.md5(password));

            executeAsyncTask(params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
	}

	private void executeAsyncTask(JSONObject params)
	{
		myAsyncTask.execute(params);
	}

	public void signUp(String name, String email, String password) throws JSONException
	{
            myAsyncTask = new MyAsyncTask(myResponseListener, SIGN_UP_URL);
            JSONObject params = new JSONObject();
            params.put(Constants.FULL_NAME_COULMN, name);
            params.put(Constants.EMAIL_COULMN, email);
            params.put(Constants.PASSWORD_COULMN, SecurityUtil.md5(password));

            executeAsyncTask(params);
	}

    public void updateInfo(String email, String newName, String newPassword, DBTools db)  throws JSONException
    {
        User user = db.getUserInfo();
        if (user != null)
        {
            JSONObject params = new JSONObject();
            params.put(Constants.USER_ID_COULMN, user.getId());
            params.put(Constants.PASSWORD_COULMN, user.getPassword());
            params.put(Constants.EMAIL_COULMN, email);
            params.put(Constants.FULL_NAME_COULMN, newName);
            params.put(Constants.NEW_PASSWORD_COULMN, newPassword);


            myAsyncTask = new MyAsyncTask(myResponseListener, UPDATE_INFO_URL);
            executeAsyncTask(params);
        }
    }

    public void getUsersWhereEmailLike(String email, DBTools db) throws JSONException
    {
        User user = db.getUserInfo();

        JSONObject params = new JSONObject();
        params.put(Constants.USER_ID_COULMN, user.getId());
        params.put(Constants.PASSWORD_COULMN, user.getPassword());
        params.put(Constants.EMAIL_COULMN, email);

        myAsyncTask = new MyAsyncTask(myResponseListener, GET_USERS_URL);
        executeAsyncTask(params);
    }

    public void getUserDetailById(long userId, DBTools db) throws JSONException
    {
        User user = db.getUserInfo();

        JSONObject params = new JSONObject();
        params.put(Constants.USER_ID_COULMN, user.getId());
        params.put(Constants.PASSWORD_COULMN, user.getPassword());
        params.put(Constants.ASKED_USER_ID, userId);

        myAsyncTask = new MyAsyncTask(myResponseListener, GET_USER_BY_ID_URL);
        executeAsyncTask(params);
    }

    public void getUserDetailByEmail(String email, DBTools db) throws JSONException
    {
        User user = db.getUserInfo();

        JSONObject params = new JSONObject();
        params.put(Constants.USER_ID_COULMN, user.getId());
        params.put(Constants.PASSWORD_COULMN, user.getPassword());
        params.put(Constants.ASKED_USER_EMAIL, email);

        myAsyncTask = new MyAsyncTask(myResponseListener, GET_USER_BY_EMAIL_URL);
        executeAsyncTask(params);
    }

	public void deleteUser(String email) throws JSONException
	{
        myAsyncTask = new MyAsyncTask(myResponseListener, DELETE_URL);
        JSONObject params = new JSONObject();
        params.put(Constants.EMAIL_COULMN, email);

		executeAsyncTask(params);
	}

	public void resetPassword(String email)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("email", email));

		//executeAsyncTask(params);
	}

	public JSONArray getAsyncTaskResult() throws InterruptedException, ExecutionException
	{
		return myAsyncTask.get();
	}
}
