package com.ericsson.postbox.shared;

public class Constants
{
    public static final String POSTBOX_CODE = "Postbox_Code";
    public static final String HTTP_CODE = "Http_Code";
    public static final String POSTBOX_MESSAGE = "Postbox_Message";

    public static final int HTTP_SUCCESS = 200;
    public static final int HTTP_FAILURE = 500;

    // users tables constants
    public static final String USER_TABLE = "users";
    public static final String USER_ID_COULMN = "User_Id";
    public static final String BOX_ID_COULMN = "Box_id";
    public static final String FULL_NAME_COULMN = "Full_Name";
    public static final String EMAIL_COULMN = "Email";
    public static final String PASSWORD_COULMN = "Pass";
    public static final String NEW_PASSWORD_COULMN = "New_Pass";
    public static final String STATUS_COULMN = "Status";
    public static final String STATUS_REASON_COULMN = "Status_Reason";
    public static final String LONGITUDE_COULMN = "Longitude";
    public static final String LATITUDE_COULMN = "Latitude";
    public static final String CREATED_AT_COULMN = "Created_At";

    public static final String FRIEND_ID_COULMN = "Friend_Id";

    // Gcm registration table constants
    public static final String GCM_REGISTRATION_TABLE = "gcm_registration";
    public static final String GCM_REGISTRATION_ID_COULMN = "Registration_Id";

    // Messages table constants
    public static final String MESSAGES_TABLE = "messages";
    public static final String MESSAGE_ID_COULMN = "Message_Id";
    public static final String RECEIVING_DATE_COULMN = "receiving_date_time";

    public static final String ASKED_USER_ID = "Asked_User_Id";
    public static final String ASKED_USER_EMAIL = "Asked_User_Email";

    // Notification data keys
    public static final String NOTIFICATION_TYPE = "Notification_Type";
    public static final String TICKER_TEXT = "Ticker_Text";
    public static final String CONTENT_TITLE = "Content_Title";
    public static final String CONTENT_TEXT = "Content_Text";

    public static final String FRAGMENT_TO_LOAD = "Fragment_To_Load";
    public static final int MESSAGE_LIST_FRAGMENT = 1;

    // Notification Types
    public static final String FRIEND_REQUEST = "Friend_Request";
    public static final String BOX_FULL = "Box_Full";

    public static final String MESSAGE_RECEIVED_BROADCASTER = "Message_Received";
    public static final String LOCATION_UPDATE_BROADCASTER = "com.meetbook.noman.location.update.broadcaster";
}
