package com.example.clicker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clicker.MainActivity;

public class ResetDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        MainActivity activity = (MainActivity) getActivity();
        builder.setTitle("Reset")
                .setMessage("Are you sure you want to reset your game? This will erase" +
                        "all progress you've made in the game")
                .setPositiveButton("Yes", (dialog, which) -> {
                    assert activity != null;
                    activity.reset();})
                .setNegativeButton("No", (dialog, which) -> {
                });
        return builder.create();
    }
}
