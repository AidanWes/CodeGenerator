package com.example.clicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.clicker.dialog.AboutDialog;
import com.example.clicker.dialog.InfoDialog;
import com.example.clicker.dialog.ResetDialog;

/**
 * This fragment allows the user to provides some elements of customization such as
 * background music to play or what kind of computer is displayed in the main screen.
 *
 * This fragment also implements a button to reset the game, and two other buttons
 * that show information about the game.
 *
 * Also included in this fragment is a volume control slider and a switch to mute
 * the current song.
 */

public class Settings extends Fragment {

    public Settings() {} //Keep Empty

    private MainActivity activity;
    private Switch mute;
    private float volumeAmount = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        Button reset = v.findViewById(R.id.reset);
        Button about = v.findViewById(R.id.about);
        Button info = v.findViewById(R.id.info);
        activity = (MainActivity) getActivity();
        assert activity != null;
        reset.setOnClickListener(v1 -> resetDialog());
        about.setOnClickListener(v1 -> aboutDialog());
        info.setOnClickListener(v1 -> infoDialog());

        //Music Menu
        Spinner music = v.findViewById(R.id.music_spinner);
        setUpMusic(music);
        music.setSelection(activity.getMusicPos());
        volumeAmount = activity.getVol();

        //Computer Menu
        Spinner computer = v.findViewById(R.id.computer_spinner);
        setUpComputer(computer);
        computer.setSelection(activity.getComputerPos());

        //Mute Switch
        mute = v.findViewById(R.id.mute_switch);
        mute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                activity.getBackgroundMusic().setVolume(0, 0);
                activity.getBackgroundMusic().pause();
            }
            else {
                activity.getBackgroundMusic().setVolume(1, 1);
                activity.getBackgroundMusic().start();
            }
        });

        //Volume Bar
        SeekBar volume = v.findViewById(R.id.volume_bar);
        volume.setMax(10);
        volume.setProgress(Math.round(volumeAmount * 10));
        activity.getBackgroundMusic().setVolume(volumeAmount, volumeAmount);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                volumeAmount = progress / 10f; //Divide by 10 to create decimal value
                activity.getBackgroundMusic().setVolume(volumeAmount, volumeAmount);
                activity.setVol(volumeAmount);
                mute.setChecked(false);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return v;
    }

    private void resetDialog() {
        ResetDialog reset = new ResetDialog();
        reset.show(getFragmentManager(), "ResetDialog");
    }

    private void infoDialog() {
        InfoDialog info = new InfoDialog();
        info.show(getFragmentManager(), "InfoDialog");
    }

    private void aboutDialog(){
        AboutDialog about = new AboutDialog();
        about.show(getFragmentManager(), "AboutDialog");
    }

    /**
     * Configures the spinner to act as a music menu
     *
     * @param music The spinner being configured
     */
    private void setUpMusic(Spinner music) {
        ArrayAdapter<CharSequence> musicAdapter =
                ArrayAdapter.createFromResource(getContext(),
                R.array.music, android.R.layout.simple_spinner_dropdown_item);
        musicAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        music.setAdapter(musicAdapter);
        music.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long arg3) {
                String text = parent.getItemAtPosition(position).toString();
                changeTune(text, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    /**
     * Configures the spinner to act as a computer selection menu
     *
     * @param computer Spinner being made
     */
    private void setUpComputer(Spinner computer){
        ArrayAdapter<CharSequence> computerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                R.array.computer, android.R.layout.simple_spinner_dropdown_item);
        computerAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        computer.setAdapter(computerAdapter);

        computer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long arg3) {
                String text = parent.getItemAtPosition(position).toString();
                activity.setComputerPos(position);
                changeComputer(text);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    /**
     * Changes the music based the the selection made
     *
     * @param text Which song was chosen
     */
    private void changeTune(String text, int position) {
        switch (text){
            case "Calm": findMusic(R.raw.calm_music, position); break;
            case "Heavy": findMusic(R.raw.heavy_music, position); break;
            case "Piano": findMusic(R.raw.piano_music, position); break;
            case "Jazz": findMusic(R.raw.jazz_music, position); break;
        }
    }

    /**
     * Checks to see if the selected track is already playing, and if it isn't then
     * the track begins
     *
     * @param id R.raw ID of the song that was chosen
     */
    private void findMusic(int id, int position){
        if(activity.getTrack() != id) {
            activity.changeMusic(id);
            activity.setTrack(id);
            mute.setChecked(false);
            activity.getBackgroundMusic().setVolume(volumeAmount, volumeAmount);
            activity.setMusicPos(position);
        }
    }

    /**
     * Changes the icon of the clicker on the main screen
     *
     * @param text Which computer has been selected
     */
    private void changeComputer(String text){
        switch (text){
            case "Retro": activity.setComputerId(R.drawable.retro_icon); break;
            case "Orange": activity.setComputerId(R.drawable.orange_icon); break;
            case "Modern": activity.setComputerId(R.drawable.modern_icon); break;
        }
    }
}
