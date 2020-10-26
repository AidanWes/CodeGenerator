package com.example.clicker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.NumberFormat;

/**
 * This fragment handles the purchasing of upgrades which affect how fast "Lines of Code"
 * are generated automatically.
 *
 * Each button is updated constantly to provide up to date information, including price,
 * amount, and a color for whether or not the user has enough to buy the upgrade
 *
 * @author Aidan Wesloskie
 */

public class RateUpgrade extends Fragment {
    private int[] rateUpgradeAmounts; //How many of each upgrade is bought
    private double[] rateUpgradeCosts;
    private double currentAmount;
    //How much each upgrade effects tap value
    private double[] valueIncreases ={1, 2, 5, 15, 25, 100, 250, 300, 500, 1000, 3000};
    private Button[] rateUpgradeTypes = new Button[11]; //Shop buttons
    private String[] rateUpgradeNames = {"For Loops:", "While Loops:", "Interns:",
                                         "Tapping Algorithms:", "Bitcoin Miners:",
                                         "Server Room:", "Tapping API:",
                                         "Cloud Networks:", "Virtual Machines:",
                                         "Robotic Assistants:", "AI Systems:"};
    private Animation buttonResponse;
    private Animation wrongAmount;
    private LinearLayout list;
    private LinearLayout.LayoutParams params;
    private MainActivity activity;
    private TextView currentView;
    private NumberFormat nf = NumberFormat.getInstance();

    private Handler handler = new Handler();
    private Runnable tick;
    private final int DELAY = 1000;

    public RateUpgrade() { } //Keep Empty

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rate_upgrade, container, false);
        loadValues(v);

        //Modular code for Button creation
        for (int i = 0; i < rateUpgradeTypes.length; i++)
            buildButton(i);
        updatePrice();

        tick = new Runnable() {
            @Override
            public void run() {
                currentAmount = activity.getCurrentAmount();
                String amount = "Current Lines:" + nf.format((int) currentAmount);
                currentView.setText(amount);
                if(getContext() != null)
                    updatePrice();
                handler.postDelayed(this, DELAY);
            }
        };

        return v;
    }

    private void loadValues(View v){
        buttonResponse = AnimationUtils.loadAnimation(
                getContext(), R.anim.button_response);
        wrongAmount = AnimationUtils.loadAnimation(getContext(), R.anim.not_enough);
        currentView = v.findViewById(R.id.rSpendAmount);
        activity = (MainActivity) getActivity();
        assert activity != null;
        currentAmount = activity.getCurrentAmount();
        //Load values in
        rateUpgradeAmounts = activity.getRateAmount();
        rateUpgradeCosts = activity.getRateCost();
        list = v.findViewById(R.id.rateUpgradeList);
        int height = (int) getResources().getDimension(R.dimen.upgrade_button_size);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(0, 0, 0, 35);
    }

    /**
     * Creates a button for every index of the Button list array, and then customizes
     * each one with their individual properties, as well as the functionality to buy
     * upgrades, then adds that button to the LinearLayout
     */
    @SuppressLint("DefaultLocale")
    private void buildButton(int index){
        rateUpgradeTypes[index] = new Button(getContext());
        rateUpgradeTypes[index].setLayoutParams(params);
        rateUpgradeTypes[index].setId(index+6000);
        rateUpgradeTypes[index].setText
                (String.format("%s  %s\n%d", rateUpgradeNames[index],
                        nf.format((int) rateUpgradeCosts[index]),
                        rateUpgradeAmounts[index]));
        rateUpgradeTypes[index].setBackground(activity.randomPlank());
        rateUpgradeTypes[index].setOnClickListener(this::buy);
        list.addView(rateUpgradeTypes[index]);
    }

    /**
     * Chooses the color that is displayed on the button
     *
     * @param index Which button is being effected
     * @return A Color based on whether the user can buy the item, red if they
     * can't buy it, and green if they can buy the item
     */
    private ForegroundColorSpan setPriceColor(int index) {
        if((int) activity.getCurrentAmount() >= (int) rateUpgradeCosts[index])
            return new ForegroundColorSpan(Color.parseColor(getString(R.string.green_shop_color)));
        else
            return new ForegroundColorSpan(Color.RED);
    }

    /**
     * After a purchase is made the sign is then updated so that the correct price
     * and color of the button is displayed
     */
    @SuppressLint("DefaultLocale")
    private void updatePrice(){
        for (int index = 0; index < rateUpgradeTypes.length; index++) {
             String text = String.format("%s   %s\n%d", rateUpgradeNames[index],
                    nf.format((int) rateUpgradeCosts[index]), rateUpgradeAmounts[index]);
            SpannableString ss = new SpannableString(text);
            ss.setSpan(setPriceColor(index), text.indexOf(':') + 1,
                    text.indexOf('\n'), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            rateUpgradeTypes[index].setText(ss);
        }
    }

    /**
     * Figures out which button has been selected and then calls the task method
     * with the specified index
     *
     * @param v The button that has been selected by the user
     */
    private void buy(View v) {
        switch (v.getId() ) {
            case 6000: task(0); break;
            case 6001: task(1); break;
            case 6002: task(2); break;
            case 6003: task(3); break;
            case 6004: task(4); break;
            case 6005: task(5); break;
            case 6006: task(6); break;
            case 6007: task(7); break;
            case 6008: task(8); break;
            case 6009: task(9); break;
            case 6010: task(10); break;
        }
    }

    /**
     * Checks to see if the user has enough lines of code to buy the selected upgrade,
     * if they do a small animation will play and will call the purchaseUpgrade method.
     *
     * @param index Where the button that is having changes made can be found
     */
    @SuppressLint("DefaultLocale")
    private void task(int index) {
        if((int) activity.getCurrentAmount() >= (int) rateUpgradeCosts[index]) {
            purchaseUpgrade(index);
            rateUpgradeCosts[index] = rateUpgradeCosts[index] +
                    (int)(rateUpgradeCosts[index] / 7);
            rateUpgradeTypes[index].startAnimation(buttonResponse);
            updatePrice();
        }else
            rateUpgradeTypes[index].startAnimation(wrongAmount);
    }

    /**
     * Buys the specified upgrade, then updates the cost to a newly assigned higher one,
     * as well as incrementing the total amount bought
     *
     * @param index Position of the updated button
     */
    private void purchaseUpgrade(int index) {
        activity.setCurrentAmount(activity.getCurrentAmount() -
                (int)rateUpgradeCosts[index]);
        rateUpgradeAmounts[index]++;
        activity.setRate(activity.getRate() + valueIncreases[index]);
        currentAmount = currentAmount - rateUpgradeCosts[index];
        String amount = "Current Lines:" + nf.format((int) currentAmount);
        currentView.setText(amount);
    }

    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    @Override
    public void onResume() {
        super.onResume();
        String amount = "Current Lines:" + nf.format((int) currentAmount);
        currentView.setText(amount);
        handler.postDelayed(tick, DELAY);
    }

    /**
     * Sends the arrays for the costs and amounts to the MainActivity to be
     * stored in SharedPreferences
     */
    private void save(){
        activity.setRateAmount(rateUpgradeAmounts);
        activity.setRateCost(rateUpgradeCosts);
    }
}