package com.ericsson.postbox.entity;

import android.content.Context;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class PostMessage
{
    private final long myId;
    private final String myDateTime;


    public PostMessage(MesssageBuilder builder)
    {
        myId = builder.myId;
        myDateTime = builder.myDateTime;
    }

    public long getId()
    {
        return myId;
    }

    public String getDateTime()
    {
        return myDateTime;
    }

    public static class MesssageBuilder
    {
        private long myId;
        private String myDateTime;

        public MesssageBuilder withId(long id)
        {
            this.myId = id;
            return this;
        }

        public MesssageBuilder withDateTime(String dateTime)
        {
            this.myDateTime = dateTime;
            return this;
        }

        public PostMessage build()
        {
            return new PostMessage(this);
        }
    }
}
