package com.ericsson.postbox.shared;

public enum UserStatus
{
    Active(1), Inactive(0);

    private int myValue;

    UserStatus(int value)
    {
        myValue = value;
    }

    public int getValue()
    {
        return myValue;
    }
}
