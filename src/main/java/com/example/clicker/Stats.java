package com.example.clicker;


import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import java.text.NumberFormat;

/**
 * This fragment is used to display the players current statistics
 * These stats include: How long they've played, how many lines of code total they made,
 * how fast they are generating code, and so on.
 *
 * @author Aidan Wesloskie
 */

public class Stats extends Fragment {
    private String[] statName =
            {"Time Played:", "Tap Value:", "Tap Count:", "Current Rate:",
                    "Total Lines Made:", "Tap Upgrades Bought:",
                    "Rate Upgrades Bought:"};
    private GridLayout list;
    private LinearLayout timeSpot;
    private MainActivity activity;
    private NumberFormat nf = NumberFormat.getInstance();
    private Chronometer gameTime;

    public Stats() {} //Keep Empty

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        activity = (MainActivity) getActivity();
        assert activity != null;
        gameTime = activity.getGameTime();
        list = v.findViewById(R.id.statList);
        timeSpot = v.findViewById(R.id.timePos);
        list.setColumnCount(2);
        list.setRowCount(6);
        setView();

        return v;
    }

    /**
     * Creates stats for the fragment and adds them to the View
     */
    private void setView(){
        for (int index = 0; index < statName.length; index++) {
            TextView text = statName(index);
            TextView stat = statVar(index);
            LinearLayout item = createStatContainer(text, stat);
            Space pad = new Space(getContext());

            if(index != 0)
                createStat(item, pad);
            else
                createTime(item, pad);
        }
    }

    /**
     * Creates a layout specific to every stat except time
     * @param item Which stat is being configured
     * @param pad Spacing to place after the statistic
     */
    private void createStat(LinearLayout item, Space pad) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            300, LinearLayout.LayoutParams.WRAP_CONTENT);
        pad.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        params.setMargins(25, 0, 25, 0);
        item.setLayoutParams(params);
        list.addView(item);
        list.addView(pad);
    }

    /**
     * Creates the specific layout for the time played statistic
     * @param item The time played stat layout
     * @param pad Space played after the statistic
     */
    private void createTime(LinearLayout item, Space pad) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        pad.setLayoutParams(new LinearLayout.LayoutParams(60, 30));
        item.setLayoutParams(params);
        timeSpot.addView(item);
        timeSpot.addView(pad);
    }

    /**
     * Creates the layout for the statistic being provided to t
     * @param text Name of the statistic being inserted into the layout
     * @param stat Statistic that is being inserted into the layout
     * @return A LinearLayout that contains the Statistic label and value, with
     * it configured
     */
    private LinearLayout createStatContainer(TextView text, TextView stat) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.VERTICAL);

        item.setPadding(20, 20, 20, 20);
        item.setBackground(this.getResources().getDrawable(R.drawable.stat_style));
        item.addView(text);
        item.addView(stat);
        return item;
    }

    /**
     * Creates the text view that will be placed into each category
     * @param index Which stat is being affected
     * @return The value of the stat
     */
    private TextView statVar(int index) {
        TextView stat = new TextView(getContext());
        stat.setText(getStat(index));
        stat.setTextSize(15f);
        return stat;
    }

    /**
     * Finds the name of the stat that is associated with the index
     *
     * @param index Position of the text that is being placed into the text view
     * @return A text view that contains the name of the stat
     */
    private TextView statName(int index) {
        TextView text = new TextView(getContext());
        text.setText(statName[index]);
        text.setTextSize(18f);
        return text;
    }

    /**
     * Pulls all the data from the MainActivity to view on screen
     *
     * @param index which stat is being pulled from the MainActivity
     * @return the selected stat
     */
    private String getStat(int index) {
        switch(index) {
            case 0: return getTime();
            case 1: return nf.format((int) activity.getTapValue()) + "\n";
            case 2: return nf.format(activity.getTapCount()) + "\n";
            case 3: return nf.format((int) activity.getRate()) + "\n";
            case 4: return nf.format(activity.getTotalLines());
            case 5:
                int[] upgrades = activity.getClickAmount();
                int count = 0;
                for (int upgrade : upgrades) count += upgrade;
                return nf.format(count);
            case 6:
                int[] upgradesTwo = activity.getRateAmount();
                int counterTwo = 0;
                for (int upgradeTwo : upgradesTwo) counterTwo += upgradeTwo;
                return nf.format(counterTwo);

        }
        return "0";
    }

    /**
     * Calculates and formats the total time that the user has been playing the game
     *
     * @return A formatted string which represents total time played
     */
    private String getTime() {
        long time = SystemClock.elapsedRealtime() - gameTime.getBase();
        int days, hours, minutes, seconds;
        seconds = (int) time / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        days = hours / 24;
        return days + " day(s)     " + hours % 24 + " hour(s)     " + minutes % 60 +
                " minute(s)     " + seconds % 60 + " second(s)";
    }
}
