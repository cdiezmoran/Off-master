package com.offapps.off.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.offapps.off.R;

/**
 * Created by Carlos Diez on 12/06/2015.
 */
public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_title))
                .setMessage("Error")
                .setPositiveButton(context.getString(R.string.error_ok_button_text), null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
