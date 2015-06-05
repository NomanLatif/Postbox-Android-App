package com.ericsson.postbox.library;

import org.json.JSONArray;

public interface AsyncResponse
{
	void processFinish(JSONArray results);
}
