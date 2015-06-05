package com.ericsson.postbox.userinterface;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.userinterface.util.Utils;
import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.library.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>, AsyncResponse
{
    private UserFunctions myUserFunctions = null;
    DBTools mySqlite = new DBTools(this);

    private AutoCompleteTextView myEmailView;
    private EditText myPasswordView;
    private View myProgressView;
    private View myLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (mySqlite.isUserLoggedIn(getApplicationContext()))
        {
            startMainActivityAndTrackerService();
        }

        myEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        myLoginFormView = findViewById(R.id.login_form);
        myProgressView = findViewById(R.id.login_progress);

        myPasswordView = (EditText) findViewById(R.id.password);
        myPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    login();
                    return true;
                }
                return false;
            }
        });

        TextView registerScreen = (TextView) findViewById(R.id.textSignUp);
        TextView linkForgotPassword = (TextView) findViewById(R.id.textForgotPassword);

        registerScreen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent signUp = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUp);
            }
        });

        linkForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent resetPasswordActivity = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(resetPasswordActivity);
            }
        });

        Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                login();
            }
        });

        SettingsFragment.setDefaultValues(getApplicationContext());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mySqlite.closeDb();
    }

    private void startMainActivityAndTrackerService()
    {
        if (Utils.checkPlayServices(this))
        {
            Utils.registerGcmIfNot(this);
            startMainActivity();
        }
    }

    private void populateAutoComplete()
    {
        getLoaderManager().initLoader(0, null, this);
    }

    public void login()
    {
        myEmailView.setError(null);
        myPasswordView.setError(null);

        String email = myEmailView.getText().toString();
        String password = myPasswordView.getText().toString();

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            invalidPassword(getString(R.string.error_invalid_password));
        }

        if (TextUtils.isEmpty(email))
        {
            invalidEmail(getString(R.string.error_field_required));
        }
        else if (!isEmailValid(email))
        {
           invalidEmail(getString(R.string.error_invalid_email));
        }
        else
        {
            showProgress(true);
            myUserFunctions = new UserFunctions(this);
            myUserFunctions.loginUser(email, password);
        }
    }

    private boolean isEmailValid(String email)
    {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password)
{
    return password.length() > 3;
}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            myLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            myLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    myLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            myProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            myProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    myProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            myProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            myLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }

    @Override
    public void processFinish(JSONArray results)
    {
        try
        {
            if (results == null)
            {
                myUserFunctions = null;
                showProgress(false);
            }
            else
            {
                JSONObject result = results.getJSONObject(0);
                switch (result.getInt(Constants.POSTBOX_CODE))
                {
                    case 1:
                        invalidEmail(getString(R.string.error_invalid_email));
                        break;
                    case 2:
                        invalidPassword(getString(R.string.error_wrong_password));
                        break;
                    case 3:
                        inactiveUser(result);
                        break;
                    case 4:
                        successfullLogin(result);
                        break;
                    default:

                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void invalidEmail(String message)
    {
        myEmailView.setError(message);
        myEmailView.requestFocus();
        showProgress(false);
    }

    private void invalidPassword(String message)
    {
        myPasswordView.setError(message);
        myPasswordView.requestFocus();
        showProgress(false);
    }

    private void inactiveUser(JSONObject output)
    {
        try
        {
            myEmailView.setError(output.getString(Constants.POSTBOX_MESSAGE));
            myEmailView.requestFocus();
            showProgress(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void successfullLogin(JSONObject json) throws JSONException
    {
        String password = myPasswordView.getText().toString();
        mySqlite.logoutUser();
        mySqlite.insertUser(json, password);

        finish();
        showProgress(false);
        startMainActivityAndTrackerService();
    }

    private void startMainActivity()
    {
        Intent activity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(activity);
        finish();
    }

    private interface ProfileQuery
    {
        String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection)
    {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        myEmailView.setAdapter(adapter);
    }
}