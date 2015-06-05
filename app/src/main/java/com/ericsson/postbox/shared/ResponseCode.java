package com.ericsson.postbox.shared;

public enum ResponseCode
{
    INVALID_USER(1),
    INVALID_PASSWORD(2),
    INACTIVE_USER(3),
    SUCCESSFUL_LOGIN(4),
    SUCCESSFUL_PROCESSED(5),
    FAILED_PROCESSED(6);

    private int myCode;

    ResponseCode(int code)
    {
        myCode = code;
    }

    public int getCode()
    {
        return myCode;
    }
}