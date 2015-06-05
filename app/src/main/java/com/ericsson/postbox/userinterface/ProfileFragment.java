package com.ericsson.postbox.userinterface;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.shared.Constants;
import com.ericsson.postbox.shared.SecurityUtil;
import com.ericsson.postbox.userinterface.user.ChangePasswordDialog;
import com.ericsson.postbox.entity.User;
import com.ericsson.postbox.library.UserFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements ChangePasswordDialog.ChangePasswordDialogListener, AsyncResponse
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProdfileFragment";
    private static final String FILE_UPLOAD_URL = "http://192.168.0.104/AndroidFileUpload/fileUpload.php";
    private static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private static int RESULT_LOAD_IMAGE = 1;

    private String mParam1;
    private String mParam2;
    private View myProfileView;
    private EditText myName;
    private EditText myEmail;
    private DBTools myDb;
    private Button myCancelButton;
    private Button myUpdateButton;
    private TextView myChangePicture;
    private TextView myChangePassword;
    private String myNewPassword;
    private View myProgressView;

    public static ProfileFragment newInstance(String param1, String param2)
    {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myProfileView = inflater.inflate(R.layout.fragment_profile, container, false);
        myDb = new DBTools(getActivity().getApplicationContext());
        myNewPassword = myDb.getUserInfo().getPassword();

        myChangePicture = (TextView) myProfileView.findViewById(R.id.change_picture);
        myName = (EditText) myProfileView.findViewById(R.id.name);
        myEmail = (EditText) myProfileView.findViewById(R.id.email);
        myCancelButton = (Button) myProfileView.findViewById(R.id.cancel_button);
        myUpdateButton = (Button) myProfileView.findViewById(R.id.update_button);
        myChangePassword = (TextView) myProfileView.findViewById(R.id.change_password);
        myProgressView = myProfileView.findViewById(R.id.progress_bar);

        User user = myDb.getUserInfo();
        myName.setText(user.getName());
        myEmail.setText(user.getEmail());

        myChangePicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        myChangePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
                changePasswordDialog.show(getFragmentManager(), "ChangePassword");
            }
        });

        myUpdateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateInfo();
            }
        });

        myCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity activity = (MainActivity) getActivity();
            }
        });

        return myProfileView;
    }

    private void updateInfo()
    {
        try
        {
            String newName = myName.getText().toString().trim();
            String newEmail = myEmail.getText().toString().trim();

            if (TextUtils.isEmpty(newName))
            {
                myName.setError(getString(R.string.error_field_required));
                myName.requestFocus();
            }
            else if (TextUtils.isEmpty(newEmail))
            {
                myEmail.setError(getString(R.string.error_field_required));
                myEmail.requestFocus();
            }
            UserFunctions userFunctions = new UserFunctions(this);
            userFunctions.updateInfo(newEmail, newName, myNewPassword, myDb);
            showProgress(true);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        myDb.closeDb();
        super.onDestroy();
    }

    @Override
    public void onDialogPositiveClick(String newPassword)
    {
        myNewPassword = SecurityUtil.md5(newPassword);
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

            myProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
            myProfileView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    myProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            myProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) myProfileView.findViewById(R.id.user_image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @Override
    public void processFinish(JSONArray results)
    {
        try
        {
            if (results == null)
            {
                showProgress(false);
            }
            else
            {
                showProgress(false);
                JSONObject result = results.getJSONObject(0);
                int returnCode = result.getInt(Constants.POSTBOX_CODE);
                switch (returnCode)
                {
                    case 4:
                        updateSqliteUserInfo(result);
                        String message = result.getString(Constants.POSTBOX_MESSAGE);
                        showAlertWithMessage(message);
                        MainActivity activity = (MainActivity) getActivity();
                        break;
                    default:
                        showAlertWithMessage(result.getString(Constants.POSTBOX_MESSAGE));
                        break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            showProgress(false);
        }
    }

    private void showAlertWithMessage(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateSqliteUserInfo(JSONObject result)
    {
        try
        {
            String newName = result.getString(Constants.FULL_NAME_COULMN);
            String newEmail = result.getString(Constants.EMAIL_COULMN);
            myDb.updateUserInfo(newName, newEmail, myNewPassword);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            return uploadFile("FILE_PATH");
        }

        private String uploadFile(String sourceFileUri)
        {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;
            String pathToOurFile = "/data/file_to_send.mp3";
            String urlServer = "http://192.168.1.1/handle_upload.php";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            try
            {
                FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

                URL url = new URL(urlServer);
                connection = (HttpURLConnection) url.openConnection();

                // Allow Inputs &amp; Outputs.
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST.
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            }
            catch (Exception ex)
            {
                //Exception handling
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
        }
    }




}
