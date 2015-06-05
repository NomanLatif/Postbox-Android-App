package com.ericsson.postbox.userinterface;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.library.UserFunctions;
import com.ericsson.postbox.userinterface.util.Utils;
import com.ericsson.postbox.shared.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, AsyncResponse
{
    private UserFunctions myUserFunctions = null;

    private AutoCompleteTextView myEmailView;
    private EditText myPasswordView;
    private View myProgressView;
    private EditText myConfirmPasswordView;
    private View mySignUpFormView;
    private DBTools myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        myDb = new DBTools(getApplicationContext());
        myEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        myPasswordView = (EditText) findViewById(R.id.password);
        myConfirmPasswordView = (EditText) findViewById(R.id.confirm_password);
        myConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    signUp();
                    return true;
                }
                return false;
            }
        });

        Button signUpButton = (Button) findViewById(R.id.email_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                signUp();
            }
        });

        mySignUpFormView = findViewById(R.id.email_sign_up_form);
        myProgressView = findViewById(R.id.progress);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        myDb.closeDb();
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
                int returnCode = result.getInt(Constants.POSTBOX_CODE);
                switch (returnCode)
                {
                    case 4:
                        addToSQLite(result);
                        startMainActivity();
                        if (Utils.checkPlayServices(this))
                        {
                            Utils.registerGcmIfNot(this);
                        }
                        showProgress(false);
                        finish();
                        break;
                    default:
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            showProgress(false);
        }
    }

    private void addToSQLite(JSONObject json)
    {
        String password = myPasswordView.getText().toString();
        myDb.logoutUser();
        myDb.insertUser(json, password);
        finish();
    }

    private void startMainActivity()
    {
        Intent recyclingInformation = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(recyclingInformation);
        finish();
    }

    private void populateAutoComplete()
    {
        getLoaderManager().initLoader(0, null, this);
    }

    public void signUp()
    {
        if (myUserFunctions != null)
        {
            return;
        }

        myEmailView.setError(null);
        myPasswordView.setError(null);
        myConfirmPasswordView.setError(null);

        String email = myEmailView.getText().toString();
        String password = myPasswordView.getText().toString();
        String confirmPassword = myConfirmPasswordView.getText().toString();

        validateAndSignUp(email, password, confirmPassword);
    }

    private void validateAndSignUp(String email, String password, String confirmPassword)
    {
        try
        {
            if (TextUtils.isEmpty(email))
            {
                invalidEmail(getString(R.string.error_field_required));
            }
            else if (TextUtils.isEmpty(password))
            {
                invalidPassword(getString(R.string.error_field_required));
            }
            else if (!isPasswordValid(password, confirmPassword))
            {
                invalidPassword(getString(R.string.error_not_matching_required));
            }
            else
            {
                showProgress(true);
                myUserFunctions = new UserFunctions(this);
                String name = email.substring(0, email.indexOf("@"));
                myUserFunctions.signUp(name, email, password);
            }
        }
        catch (JSONException e)
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

    private boolean isPasswordValid(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mySignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mySignUpFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mySignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mySignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        List<String> emails = new ArrayList<>();
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

    private interface ProfileQuery
    {
        String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection)
    {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        myEmailView.setAdapter(adapter);
    }
}
