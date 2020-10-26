package com.example.clicker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ResetSuccess extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
        confirm.setTitle("Reset Successful")
                .setPositiveButton("OK", (dialog, which) -> {});
        return confirm.create();
    }
}
