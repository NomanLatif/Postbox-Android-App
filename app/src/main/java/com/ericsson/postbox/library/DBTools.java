package com.ericsson.postbox.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.util.Log;

import com.ericsson.postbox.entity.Messages;
import com.ericsson.postbox.entity.PostMessage;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.shared.SecurityUtil;
import com.ericsson.postbox.entity.User;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DBTools extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "postbox";
    private static final String NO_WHERE_CLAUSE = null;
    private static final String[] NO_WHERE_CLAUSE_ARGUMENTS = null;
    private static final String TAG = "DBTools";
    private final Context myContext;

    public DBTools(Context applicationContext)
    {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = applicationContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + Constants.USER_TABLE + "(" + Constants.USER_ID_COULMN + " TEXT PRIMARY KEY," + Constants.PASSWORD_COULMN + " TEXT," + Constants.FULL_NAME_COULMN
                + " TEXT," + Constants.EMAIL_COULMN + " TEXT UNIQUE," + Constants.GCM_REGISTRATION_ID_COULMN + " TEXT DEFAULT ''," + Constants.CREATED_AT_COULMN + " TIMESTAMP" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + Constants.MESSAGES_TABLE + "(" + Constants.MESSAGE_ID_COULMN + " TEXT PRIMARY KEY," + Constants.RECEIVING_DATE_COULMN + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.MESSAGES_TABLE);
        onCreate(db);
    }

    public void insertUser(JSONObject json, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String insertQuery = " INSERT INTO USERS(" + Constants.USER_ID_COULMN + ", " + Constants.PASSWORD_COULMN
                    + ", " + Constants.FULL_NAME_COULMN + ", " + Constants.EMAIL_COULMN + ") "
                    + "VALUES('" + json.getString(Constants.USER_ID_COULMN) + "', '" + SecurityUtil.md5(password) + "', '"
                    + json.getString(Constants.FULL_NAME_COULMN) + "', '" + json.getString(Constants.EMAIL_COULMN) + "')";

            db.execSQL(insertQuery);
        }catch (Exception e)
        {
            Log.e("DbTools", e.getMessage());
        }
    }

    public void insertMessage()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String maxQuery = "SELECT MAX(" + Constants.MESSAGE_ID_COULMN +") AS " + Constants.MESSAGE_ID_COULMN +" FROM "+ Constants.MESSAGES_TABLE;
            Cursor cursor = db.rawQuery(maxQuery, null);

            long max = 1;
            if (cursor.moveToFirst())
            {
                max = cursor.getLong(0) + 1;
            }

            String insertQuery = " INSERT INTO " + Constants.MESSAGES_TABLE +"(" + Constants.MESSAGE_ID_COULMN + ") "
                    + "VALUES(" + max +")";
            db.execSQL(insertQuery);
        }catch (Exception e)
        {
            Log.e("DbTools", e.getMessage());
        }
    }

    public Messages getMessages()
    {
        Messages messages = new Messages();

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String selectQuery = "SELECT * FROM " + Constants.MESSAGES_TABLE + " ORDER BY " + Constants.RECEIVING_DATE_COULMN;
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                PostMessage message = createMessage(cursor);
                messages.add(message);
            }
            cursor.close();
        }
        catch (Exception e)
        {
            Log.e("DBTools", e.getMessage());
        }
        return messages;
    }


    public void deleteMessage(long messageId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.MESSAGES_TABLE, Constants.MESSAGE_ID_COULMN + "=" + messageId, null);
    }

     public int updateGcmRegistration(String gcmRegistrationId)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.GCM_REGISTRATION_ID_COULMN, gcmRegistrationId);

            return db.update(Constants.USER_TABLE, values, NO_WHERE_CLAUSE, NO_WHERE_CLAUSE_ARGUMENTS);
        }
        catch (Exception e)
        {
            Log.e("DbTools", e.getMessage());
            return 0;
        }
    }

    private User createUser(Cursor cursor)
    {
        User user = new User.UserBuilder()
                .withId(cursor.getLong(0))
                .withPassword(cursor.getString(1))
                .withFullName(cursor.getString(2))
                .withEmail(cursor.getString(3))
                .withGcmRegisterationId(cursor.getString(4))
                .build();

        return user;
    }

    private PostMessage createMessage(Cursor cursor)
    {
        PostMessage message = new PostMessage.MesssageBuilder()
                .withId(cursor.getLong(0))
                .withDateTime(formatDateTime(myContext, cursor.getString(1)))
                .build();

        return message;
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (timeToFormat != null) {
            Date date;
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    public User getUserInfo()
    {
        User user = null;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String selectQuery = "SELECT * FROM " + Constants.USER_TABLE + " ORDER BY " + Constants.FULL_NAME_COULMN;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                user = createUser(cursor);
            }
            cursor.close();
        }
        catch (Exception e)
        {
            Log.e("DBTools", e.getMessage());
        }
        return user;
    }

    public int getRowCount()
    {
        int rowCount = 0;
        try
        {
            String countQuery = "SELECT  * FROM " + Constants.USER_TABLE;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            rowCount = cursor.getCount();
            cursor.close();
        }
        catch (Exception e)
        {
            Log.e("DBTools", e.getMessage());
        }
        return rowCount;
    }

    public void resetTables()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.USER_TABLE, null, null);
        db.delete(Constants.MESSAGES_TABLE, null, null);
    }

    public boolean isUserLoggedIn(Context context)
    {
        int count = getRowCount();
        return count > 0;
    }

    public void logoutUser()
    {
        resetTables();
    }

    public void closeDb()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen())
        {
            db.close();
        }
    }

    public int updateUserInfo(String newName,String newEmail, String newPassword)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.FULL_NAME_COULMN, newName);
            values.put(Constants.EMAIL_COULMN, newEmail);
            values.put(Constants.PASSWORD_COULMN, newPassword);

            return db.update(Constants.USER_TABLE, values, NO_WHERE_CLAUSE, NO_WHERE_CLAUSE_ARGUMENTS);
        }
        catch (Exception e)
        {
            Log.e("DbTools", e.getMessage());
            return 0;
        }
    }
}
