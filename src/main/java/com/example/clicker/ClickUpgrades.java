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
 * This fragment handles the purchasing of upgrades which affect how many "Lines of Code"
 * are generated when the user taps the computer icon.
 *
 * Each button is updated constantly to provide up to date information, including price,
 * amount, and a color for whether or not the user has enough to buy the upgrade
 *
 * @author Aidan Wesloskie
 */

public class ClickUpgrades extends Fragment {
    private int[] clickUpgradeAmounts; //How many of each upgrade is bought
    private double[] clickUpgradeCosts;
    private double currentAmount;
    //How much each upgrade effects rate
    private double[] valueIncreases = {1, 2, 5, 15, 30, 60, 160, 450, 500, 1000};
    private Button[] clickUpgradeTypes = new Button[10]; //Shop buttons
    private String[] clickUpgradeNames =
            {"Faster Hands:", "Stronger Index Finger:", "Clicking Gloves:",
                    "Motivational Coaching:", "Medical Enhancements:", "Caffeine " +
                    "Tremors:", "Carpel Tunnel Medication:", "Genetic Enhancements:",
                    "Cybernetic Enhancements:",  "Fingerless Gloves:"};

    private Animation buttonResponse;
    private Animation wrongAmount;
    private MainActivity activity;
    private LinearLayout.LayoutParams params;
    private LinearLayout list;
    private TextView currentView;
    private NumberFormat nf = NumberFormat.getInstance();

    private Handler handler = new Handler();
    private Runnable tick;
    private final int DELAY = 1000;


    public ClickUpgrades(){} //Keep Empty

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_click_upgrades, container,false);
        loadValues(v);

        //Modular button initialization
        for (int i = 0; i < clickUpgradeTypes.length; i++)
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
        //Every variable used by this fragment
        buttonResponse = AnimationUtils.loadAnimation(getContext(),
                R.anim.button_response);
        wrongAmount = AnimationUtils.loadAnimation(getContext(), R.anim.not_enough);
        currentView = v.findViewById(R.id.cSpendAmount);
        activity = (MainActivity) getActivity();
        assert activity != null;
        currentAmount = activity.getCurrentAmount();
        //Load in arrays
        clickUpgradeAmounts = activity.getClickAmount();
        clickUpgradeCosts = activity.getClickCost();
        list = v.findViewById(R.id.clickUpgradeList);
        int height = (int)getResources().getDimension(R.dimen.upgrade_button_size);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height);
        params.setMargins(0, 0, 0, 35);
    }

    /**
     * Creates a button for every index of the Button list array, and then customizes
     * each one with their individual properties, as well as the functionality to buy
     * upgrades, then adds that button to the LinearLayout
     */
    @SuppressLint("DefaultLocale")
    private void buildButton(int index){
        clickUpgradeTypes[index] = new Button(getContext());
        clickUpgradeTypes[index].setLayoutParams(params);
        clickUpgradeTypes[index].setId(index+5000);
        clickUpgradeTypes[index].setText
                (String.format("%s  %s\n%d", clickUpgradeNames[index],
                        nf.format((int) clickUpgradeCosts[index]),
                        clickUpgradeAmounts[index]));
        clickUpgradeTypes[index].setBackground(activity.randomPlank());
        clickUpgradeTypes[index].setOnClickListener(this::buy);
        list.addView(clickUpgradeTypes[index]);
    }

    /**
     * Figures out which button has been selected and then calls the task method with
     * the specified index
     *
     * @param v The button that has been selected by the user
     */
    private void buy(View v) {
        switch (v.getId())
        {
            case 5000: task(0); break;
            case 5001: task(1); break;
            case 5002: task(2); break;
            case 5003: task(3); break;
            case 5004: task(4); break;
            case 5005: task(5); break;
            case 5006: task(6); break;
            case 5007: task(7); break;
            case 5008: task(8); break;
            case 5009: task(9); break;
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
        //Checks if user has enough
        if((int) activity.getCurrentAmount() >= (int) clickUpgradeCosts[index]) {
            //If they do buy the upgrade and set a new cost
            purchaseUpgrade(index);
            clickUpgradeCosts[index] = clickUpgradeCosts[index] + (int)
                    (clickUpgradeCosts[index] / 7);
            clickUpgradeTypes[index].startAnimation(buttonResponse);
            updatePrice();
        }else
            clickUpgradeTypes[index].startAnimation(wrongAmount);
    }

    /**
     * After a purchase is made the sign is then updated so that the correct price
     * and color of the button is displayed
     */
    @SuppressLint("DefaultLocale")
    private void updatePrice() {
        for (int index = 0; index < clickUpgradeTypes.length; index++) {
            String text = String.format("%s   %s\n%d", clickUpgradeNames[index],
                    nf.format((int) clickUpgradeCosts[index]),
                    clickUpgradeAmounts[index]);
            SpannableString ss = new SpannableString(text);
            ss.setSpan(setPriceColor(index), text.indexOf(':') + 1,
                    text.indexOf('\n'), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            clickUpgradeTypes[index].setText(ss);
        }
    }

    /**
     * Chooses the color that is displayed on the button
     *
     * @param index Which button is being effected
     * @return A Color based on whether the user can buy the item, red if they
     * can't buy it, and green if they can buy the item
     */
    private ForegroundColorSpan setPriceColor(int index) {
        if((int) activity.getCurrentAmount() >= (int) clickUpgradeCosts[index])
            return new ForegroundColorSpan(Color.parseColor(getString(R.string.green_shop_color)));
        else
            return new ForegroundColorSpan(Color.RED);
    }

    /**
     * Buys the specified upgrade, then updates the cost to a newly assigned higher one,
     * as well as incrementing the total amount bought
     *
     * @param index Position of the updated button
     */
    private void purchaseUpgrade(int index){
        activity.setCurrentAmount(currentAmount - (int) clickUpgradeCosts[index]);
        clickUpgradeAmounts[index]++;
        activity.setTapValue(activity.getTapValue() + valueIncreases[index]);
        currentAmount = currentAmount - clickUpgradeCosts[index];
        String amount = "Current Lines:" + nf.format((int) currentAmount);
        currentView.setText(amount);
    }

    @Override
    public void onResume() {
        super.onResume();
        String amount = "Current Lines:" + nf.format((int) currentAmount);
        currentView.setText(amount);
        handler.postDelayed(tick, DELAY);
    }

    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    /**
     * Sends the arrays for the costs and amounts to the MainActivity to be
     * stored in SharedPreferences
     */
    private void save() {
        activity.setClickAmount(clickUpgradeAmounts);
        activity.setClickCost(clickUpgradeCosts);
    }
}
