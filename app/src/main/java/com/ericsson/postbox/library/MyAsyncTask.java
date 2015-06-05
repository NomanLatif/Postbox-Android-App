package com.ericsson.postbox.library;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyAsyncTask extends AsyncTask<JSONObject, String, JSONArray>
{
	public AsyncResponse responseListener;
	private String myUrl;

	static InputStream myInputStream = null;
	static JSONArray myJsonArray = null;

	public MyAsyncTask(AsyncResponse listener, String url)
	{
		responseListener = listener;
		myUrl = url;
	}

	@Override
	protected JSONArray doInBackground(JSONObject... allParams)
	{
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(myUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            JSONObject params = allParams[0];
            StringEntity encodedEntity = new StringEntity(params.toString(), "UTF-8");
			httpPost.setEntity(encodedEntity);

			HttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            myInputStream = httpEntity.getContent();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

 		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(myInputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "n");
			}
			myInputStream.close();
            myJsonArray = new JSONArray(sb.toString());
			Log.e("JSON", myJsonArray.toString());
		}
		catch (Exception e)
		{
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		return myJsonArray;
	}

	@Override
	protected void onPostExecute(JSONArray result)
	{
		responseListener.processFinish(result);
	}

    @Override
    protected void onCancelled()
    {
        super.onCancelled();
        responseListener.processFinish(null);
    }
}
