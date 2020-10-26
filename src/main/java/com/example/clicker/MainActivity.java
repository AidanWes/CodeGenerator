package com.example.clicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.Chronometer;

import com.example.clicker.dialog.AboutDialog;
import com.example.clicker.dialog.ResetSuccess;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Plays the game of Code Generator
 * This activity features a bottom navigation bar that is used to navigate the app
 * by switching out displayed fragments
 *
 * @author Aidan Wesloskie
 *
 * User can tap on the main button to add to their score
 *
 * @version 1
 * -Implemented clicking button, with functionality and responsive shrinking and growing
 *
 * @version 2
 * -Implemented task bar at bottom of screen and added some save data functionality
 *
 * @version 3
 * -Added image icons to menu bar and started to create other activity screens
 *
 * @version 4
 * -Added background sound to the app
 *
 * @version 5
 * -Created an animation that fades from screen to screen when moving to a new fragment
 *
 * @version 6
 * -Added main logo to the click screen
 * -Added a linear layout with buttons for both upgrade purchase lists
 *
 * @version 7
 * -Added functionality to all upgrade buttons, currently they only add onto their total
 *  amount they do not remove costs yet
 *
 * @version 8
 * -Created an event handler to implement the rate functionality and automatically
 * increase the total amount
 *
 * @version 9
 * -Moved all Fragment variables to the MainActivity and implemented SharedPreferences to
 *  save all user data
 * -Created a number of setters and getters to move data
 * -Created reset method and button in the settings Fragment
 *
 * @version 10
 * -Both shops now have a decor theme to make the appearance more appealing
 * -Shops now feature randomly generated designs for their upgrade buttons
 *
 * @version 11
 * -Fixed bug where shops were displaying the incorrect amount for an upgrade
 * -Implemented NumberFormat to make reading larger numbers easier
 *
 * @version 12
 * -Made the visual appearance of the clicker fragment more appealing by
 *  updating the clicker icon and the sign logo
 * -Started work on the StatsFragment, which uses a GridLayout filled with LinearLayouts
 * -App now features an icon for the app
 *
 * @version 13
 * -Both shops now implement SpannableString so as to create better feedback visuals
 *  for users
 * -Basic layout for settings created
 * -Statistics fragment has been fully configured with nested layouts for a
 *  visually pleasing layout
 *
 * @version 14
 * -Implemented a GifView for the main computer icon for better visuals
 * -Settings is now capable of changing the background music as well as muting it.
 *
 * @Bugs
 * -If a user switches from the main screen to another, and then back to the main
 * screen very fast the app will crash
 */

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    //Instances of every fragment for easier management
    Clicker clickerFragment = new Clicker();
    ClickUpgrades clickUpgradesFragment = new ClickUpgrades();
    RateUpgrade rateUpgradeFragment = new RateUpgrade();
    Settings settingsFragment = new Settings();
    Stats statsFragment = new Stats();

    //Variables for the main clicker screen
    private double rate = 0.0;
    private double currentAmount = 0;
    private double tapValue = 1;
    private int tapCount = 0;
    private int totalLines = 0;

    //Shop values for the Rate Shop
    private int[] rateUpgradeAmounts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] DEFAULT_RATE_AMOUNTS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] rateUpgradeCosts =
            {15, 100, 500, 3_000, 10_000, 40_000, 200_000,
                    1_500_000, 5_000_000, 100_000_000, 1_000_000_000};
    private final double[] RATE_DEFAULT =
            {15, 100, 500, 3_000, 10_000, 40_000, 200_000,
                    1_500_000, 5_000_000, 100_000_000, 1_000_000_000};

    //Shop values for the Click Shop
    private int[] clickUpgradeAmounts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] DEFAULT_CLICK_AMOUNTS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] clickUpgradeCosts =
            {10, 100, 2_500, 5_000, 10_000, 150_000, 750_000,
                    2_500_000, 20_000_000, 1_000_000_000};
    private final double[] CLICK_DEFAULT =
            {10, 100, 2_500, 5_000, 10_000, 150_000, 750_000,
                    2_500_000, 20_000_000, 1_000_000_000};

    //Name tags
    private final String PREFS = "prefs", RATE = "rate", TAP_VALUE = "tapValue",
            TAP_COUNT = "tapCount", CURRENT_AMOUNT = "currentAmount",
            TOTAL_LINES = "totalLines", TIME = "time", COMP_ID = "computerId",
            SONG = "track";

    private MediaPlayer backgroundMusic;
    private int track = R.raw.calm_music; //Which song is playing
    private long pauseOffset;
    private Chronometer gameTime;
    private boolean running;

    private int computerId = R.drawable.retro_icon;
    private Handler handler = new Handler();
    private boolean handlerFlag = true;
    private Runnable tick;
    private final int DELAY = 1000;
    private boolean firstTimeOpened = true;
    private int positionComp = 0;
    private int positionMusic = 0;
    private float volume = 1;
    private boolean muteState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.clicker);

        tick = new Runnable() {
            @Override
            public void run() {
                currentAmount += rate;
                totalLines += rate;
                //Handler flag prevent multiple handlers from being created
                if(handlerFlag)
                    handler.postDelayed(this, DELAY);
            }
        };

        backgroundMusic = MediaPlayer.create(this, track);
        backgroundMusic.setLooping(true);
        gameTime = findViewById(R.id.gameTime);
        startTimer();

        if(firstTimeOpened) {
            about();
            firstTimeOpened = false;
        }

    }

    /**
     * Creates the Dialog window on the first launch of the app
     */
    private void about() {
        AboutDialog about = new AboutDialog();
        about.show(getSupportFragmentManager(), "AboutDialog");
    }

    /**
     * Switches the current fragment viewed by the user
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.clicker:
                setFragment(clickerFragment);
                return true;
            case R.id.clicker_upgrades:
                setFragment(clickUpgradesFragment);
                return true;
            case R.id.rate_upgrades:
                setFragment(rateUpgradeFragment);
                return true;
            case R.id.stats:
                setFragment(statsFragment);
                return true;
            case R.id.settings:
                setFragment(settingsFragment);
                return true;
        }
        return false;
    }

    /**
     * Method to create the fragments for the Bottom Navigation Bar
     * @param fragment The fragment that is being made
     */
    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                replace(R.id.container, fragment).commit();
    }

    /**
     * Saves all the current variables into a SharedPreferences
     */
    private void save(){
        SharedPreferences shared = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putLong(CURRENT_AMOUNT, Double.doubleToRawLongBits(currentAmount));
        editor.putLong(TAP_VALUE, Double.doubleToRawLongBits(tapValue));
        editor.putLong(RATE, Double.doubleToRawLongBits(rate));
        editor.putInt(TAP_COUNT, tapCount);
        editor.putInt(TOTAL_LINES, totalLines);
        editor.putInt(COMP_ID, computerId);
        editor.putInt(SONG, track);
        editor.putLong(TIME, SystemClock.elapsedRealtime() - gameTime.getBase());
        editor.putBoolean("GAME_PLAYED", firstTimeOpened);
        editor.putInt("MUSIC_POS", positionMusic);
        editor.putInt("COMP_POS", positionComp);
        editor.putFloat("VOLUME", volume);
        editor.putBoolean("MUTE", muteState);

        for (int i = 0; i < rateUpgradeAmounts.length; i++) {
            editor.putInt("rAmount" + i, rateUpgradeAmounts[i]);
            editor.putLong("rCost" + i, Double.doubleToRawLongBits(rateUpgradeCosts[i]));
        }
        for (int i = 0; i < clickUpgradeAmounts.length; i++) {
            editor.putInt("cAmount" + i, clickUpgradeAmounts[i]);
            editor.putLong("cCost" + i, Double.doubleToRawLongBits
                    (clickUpgradeCosts[i]));
        }
        editor.apply();
    }

    /**
     * Pulls all data from SharedPreferences to load them into the app
     */
    private void load(){
        SharedPreferences shared = getSharedPreferences(PREFS, MODE_PRIVATE);
        currentAmount = Double.longBitsToDouble(
                shared.getLong(CURRENT_AMOUNT, Double.doubleToLongBits(0)));
        rate = Double.
                longBitsToDouble(shared.getLong(RATE, Double.doubleToLongBits(0)));
        tapValue = Double.
                longBitsToDouble(shared.getLong(TAP_VALUE, Double.doubleToLongBits(1)));
        tapCount = shared.getInt(TAP_COUNT, 0);
        totalLines = shared.getInt(TOTAL_LINES, 0);
        pauseOffset = shared.getLong(TIME, 0);
        computerId = shared.getInt(COMP_ID, R.drawable.retro_icon);
        track = shared.getInt(SONG, R.raw.calm_music);
        firstTimeOpened = shared.getBoolean("GAME_PLAYED", true);
        positionMusic = shared.getInt("MUSIC_POS", 0);
        positionComp = shared.getInt("COMP_POS", 0);
        volume = shared.getFloat("VOLUME", 1);
        muteState = shared.getBoolean("MUTE", false);

        for (int i = 0; i < rateUpgradeAmounts.length; i++) {
            rateUpgradeAmounts[i] = shared.getInt("rAmount" + i, 0);
            rateUpgradeCosts[i] = Double.longBitsToDouble(shared.getLong("rCost" +
                    i, Double.doubleToLongBits(RATE_DEFAULT[i])));
        }
        for (int i = 0; i < clickUpgradeAmounts.length; i++) {
            clickUpgradeAmounts[i] = shared.getInt("cAmount" + i, 0);
            clickUpgradeCosts[i] = Double.longBitsToDouble(shared.getLong("cCost" + i,
                    Double.doubleToLongBits(CLICK_DEFAULT[i])));
        }
    }

    /**
     * Resets all values back to their default state
     */
    public void reset() {
        ResetSuccess confirm = new ResetSuccess();
        confirm.show(getSupportFragmentManager(), "CONFIRM");
        rate = 0;
        currentAmount = 0;
        tapValue = 1;
        tapCount = 0;
        totalLines = 0;
        rateUpgradeAmounts = DEFAULT_RATE_AMOUNTS;
        rateUpgradeCosts = RATE_DEFAULT;
        clickUpgradeAmounts = DEFAULT_CLICK_AMOUNTS;
        clickUpgradeCosts = CLICK_DEFAULT;
        gameTime.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    /**
     * Starts the chronometer used to track how long the user has played the game
     */
    public void startTimer(){
        if(!running) {
            gameTime.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            gameTime.start();
            running = true;
        }
    }

    /**
     * Causes the chronometer created in the background to stop running
     */
    public void pauseTimer(){
        if(running) {
            gameTime.stop();
            pauseOffset = SystemClock.elapsedRealtime() - gameTime.getBase();
            running = false;
        }
    }

    /**
     * Stops the current track and reinitialize the MediaPlayer to the new one
     * and then starts the song
     *
     * @param music New song being played
     */
    public void changeMusic(int music){
        backgroundMusic.stop();
        track = music;
        backgroundMusic = MediaPlayer.create(this, track);
        backgroundMusic.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        backgroundMusic.pause();
        tick = null;
        pauseTimer();
        save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusic.start();
        handlerFlag = true;
        handler.postDelayed(tick, DELAY);
        backgroundMusic.setVolume(volume, volume);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }

    /**
     * @return the current rate
     */
    public double getRate(){return rate;}

    /**
     * Sets the new rate for the game
     * @param newRate new value being assigned to rate
     */
    public void setRate(double newRate){rate = newRate;}

    /**
     * Sets the amount of each upgrade that is purchased
     * @param newAmount new array of values which holds
     * the amount of each rate upgrade bought
     */
    public void setRateAmount(int[] newAmount){ rateUpgradeAmounts = newAmount;}

    /**
     * Sets the cost of each upgrade in the rate store
     * @param newCosts new array of costs for every upgrade in the rate store
     */
    public void setRateCost(double[] newCosts){rateUpgradeCosts = newCosts;}

    /**
     * @return array of values that contain how many of
     * each rate upgrade that is purchased
     */
    public int[] getRateAmount(){return rateUpgradeAmounts;}

    /**
     * @return an array of values that contain the costs of every rate upgrade
     */
    public double[] getRateCost(){return rateUpgradeCosts;}

    /**
     * @return the current amount of lines of code generated
     */
    public double getCurrentAmount(){return currentAmount;}

    /**
     * @param newCurrentAmount new value being assigned
     */
    public void setCurrentAmount(double newCurrentAmount)
    {currentAmount = newCurrentAmount;}

    /**
     * Sets the amount of each upgrade that is purchased
     * @param newAmount new array of values which holds
     * the amount of each click upgrade bought
     */
    public void setClickAmount(int[] newAmount){ clickUpgradeAmounts = newAmount;}

    /**
     * Sets the cost of each upgrade in the click store
     * @param newCosts new array of costs for every upgrade in the click store
     */
    public void setClickCost(double[] newCosts){clickUpgradeCosts = newCosts;}

    /**
     * @return array of values that contain how many of
     * each click upgrade that is purchased
     */
    public int[] getClickAmount(){return clickUpgradeAmounts;}

    /**
     * @return an array of values that contain the costs of every click upgrade
     */
    public double[] getClickCost(){return clickUpgradeCosts;}

    /**
     * @return value of the current tap power
     */
    public double getTapValue(){return tapValue;}

    /**
     * Sets the new tap value
     * @param newTap new value being assigned to the tap value
     */
    public void setTapValue(double newTap){tapValue = newTap;}

    /**
     * @return number of times the tap icon was clicked
     */
    public int getTapCount(){return tapCount;}

    /**
     * Sets the value of the tap count
     * @param newCount new value being assigned to tap count
     */
    public void setTapCount(int newCount){tapCount = newCount;}

    /**
     * @return the total number of lines the user has created
     */
    public int getTotalLines(){return totalLines;}

    /**
     * Sets the new total lines amount when loading data
     * @param newTotal new value being assigned to total lines
     */
    public void setTotalLines(int newTotal){totalLines = newTotal;}

    /**
     * @return the total amount of time the game has been played
     */
    public Chronometer getGameTime(){return gameTime;}

    /**
     * Sets the current image for the tap icon
     * @param id new icon that has been selected
     */
    public void setComputerId(int id){computerId = id;}

    /**
     * @return the id of the current computer icon
     */
    public int getComputerId(){return computerId;}

    /**
     * Sets position for the current computer inside the computer spinner
     * @param newPosition new icon being assigned to the spinner
     */
    public void setComputerPos(int newPosition){positionComp = newPosition;}

    /**
     * @return the position of the current icon in the spinner
     */
    public int getComputerPos(){return positionComp;}

    /**
     * @return the current position of the music in the spinner being played
     */
    public int getMusicPos(){return positionMusic;}

    /**
     * sets the position of the music being played in the music spinner
     * @param newPos position in the spinner of the music being played
     */
    public void setMusicPos(int newPos){ positionMusic = newPos;}

    /**
     * @return the user selected volume for the background music
     */
    public float getVol(){return volume;}

    /**
     * sets the new user selected volume for the background music
     * @param newVol new volume value for music
     */
    public void setVol(float newVol){volume = newVol;}

    /**
     * @return the current song being played
     */
    public int getTrack(){return track;}

    /**
     * Sets the new background music
     * @param newTrack new song being played in the background
     */
    public void setTrack(int newTrack){track = newTrack;}

    /**
     * @return the current MediaPlayer that handles the background music
     */
    public MediaPlayer getBackgroundMusic(){return backgroundMusic;}

    /**
     * Randomly generates background wood for each shop button in the list
     *
     * @return A random plank background
     */
    public Drawable randomPlank() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int num = rand.nextInt(1, 4);
        switch (num) {
            case 1: return this.getResources().getDrawable(R.drawable.wood_plank);
            case 2: return this.getResources().getDrawable(R.drawable.wood_plank_two);
            case 3: return this.getResources().getDrawable(R.drawable.wood_plank_three);
            case 4: return this.getResources().getDrawable(R.drawable.wood_plank_four);
        }
        return this.getResources().getDrawable(R.drawable.wood_plank);
    }
}