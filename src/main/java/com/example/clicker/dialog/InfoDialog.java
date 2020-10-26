package com.example.clicker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class InfoDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Upgrade Information")
                .setMessage("Here you can see all upgrade info\n\n" +

                            "Click\nFaster Hands: 1\nStronger Finger: 2\n" +
                            "Clicking Gloves: 5\nMotivational Coaching: 15\n" +
                            "Medical Enhancements: 30\nCaffeine Tremors: 60\n" +
                            "Carpel Tunnel Medication: 160\nGenetic Enhancement: 450\n"
                            +"Cybernetic Enhancements: 500\n" +
                            "Fingerless Gloves: 1,000\n\n" +

                            "Rate:\nFor Loop: 1\nWhile Loop: 2\nInterns: 5\n" +
                            "Tapping Algorithm: 15\nBitcoin Miners: 25\n" +
                            "Server Room: 100\nTapping API: 250\nCloud Network: 300\n" +
                            "Virtual Machines; 500\nRobotic Assistants: 1,000\n" +
                            "AI Systems: 3,000")
                .setPositiveButton("OK", (dialog, which) -> {}) ;
        return builder.create();
    }
}
