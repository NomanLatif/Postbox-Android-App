package com.ericsson.postbox.userinterface.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ericsson.postbox.userinterface.R;

/**
 * Created by Noman on 2/23/2015.
 */
public class ChangePasswordDialog extends DialogFragment
{
    private TextView myPasswordView;
    private TextView myConfirmPasswordView;
    private ChangePasswordDialogListener myListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View changePasswordView = inflater.inflate(R.layout.change_password, null);

        myPasswordView = (TextView) changePasswordView.findViewById(R.id.new_password);
        myConfirmPasswordView = (TextView) changePasswordView.findViewById(R.id.confirm_password);

        builder.setView(changePasswordView)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel,null);

        final AlertDialog changePasswordDialog = builder.create();

        changePasswordDialog.setOnShowListener(new DialogInterface.OnShowListener(){

            @Override
            public void onShow(DialogInterface dialog)
            {
                Button ok = changePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (verifyInput())
                        {
                            changePasswordDialog.dismiss();
                        }
                    }
                });
            }
        });
        myPasswordView.requestFocus();
        return changePasswordDialog;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            activity.getFragmentManager().findFragmentById(R.id.container);
            myListener = (ChangePasswordDialogListener) activity.getFragmentManager().findFragmentById(R.id.container);
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement ChangePasswordDialogListener");
        }
    }

    private boolean verifyInput()
    {
        myPasswordView.setError(null);
        myConfirmPasswordView.setError(null);

        String password = myPasswordView.getText().toString();
        String confirmPassword = myConfirmPasswordView.getText().toString();

        if (TextUtils.isEmpty(password))
        {
            myPasswordView.setError(getString(R.string.error_field_required));
            myPasswordView.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword))
        {
            myPasswordView.setError(getString(R.string.error_not_matching_required));
            myPasswordView.requestFocus();
            return false;
        }
        myListener.onDialogPositiveClick(password);
        return true;
    }

    public interface ChangePasswordDialogListener
    {
        void onDialogPositiveClick(String newPassword);
    }
}
