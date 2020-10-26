package com.example.clicker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AboutDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("About")
               .setMessage("Hello, and welcome to Code Generator\n\n" +
                       "To play all you need to do is generate code.\n" +
                       "How do you do that? Well there are a \n" +
                       "multitude of ways for you to go about it.\n" +
                       "The primary way is to tap on the computer \n" +
                       "icon, the second way to generate code\n" +
                       "is by doing it automatically by buying\n" +
                       "upgrades.\n\n" +

                       "You can view your progression in the Stat\n" +
                       "menu. You can also customize your\n" +
                       "experience by going to the Settings\n" +
                       "menu and choosing what kind of computer\n" +
                       "you would like to use or what kind of\n" +
                       "music you want to list to.\n\n" +

                       "The purpose of this game is for it to be a\n" +
                       "background game that you play when you\n" +
                       "are doing some other task. So when you\n" +
                       "want to take a break from writing your\n" +
                       "real code you can come here and relax.\n\n" +

                       "Thanks so much for playing,\n" +
                       "AW")
               .setPositiveButton("OK", (dialog, which) -> {}) ;
        return builder.create();
    }
}
