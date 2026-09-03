package exa.free.linuxbox;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;
import java.util.Date;

public class Settings extends Fragment {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RadioGroup radioGroup;
    RadioButton themeSystem;
    RadioButton themeLight;
    RadioButton themeDark;
    RelativeLayout.LayoutParams relativeLayoutParam;
    ScrollView scrollView;
    String theme;
    int leftMargin;
    int rightMargin;
    int topMargin;
    int bottomMargin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        getActivity().setTitle(R.string.settings);

        View view = inflater.inflate(R.layout.settings, container, false);

        context = getActivity().getApplicationContext();

        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);
        editor = sharedPreferences.edit();

        scrollView = view.findViewById(R.id.scrollView);
        relativeLayoutParam = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
        leftMargin = relativeLayoutParam.leftMargin;
        rightMargin = relativeLayoutParam.rightMargin;
        topMargin = relativeLayoutParam.topMargin;
        bottomMargin = 0;
        if(donationInstalled() || isVideoAdsWatched()){
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        }

        radioGroup = view.findViewById(R.id.themeRadioGroup);

        themeSystem = view.findViewById(R.id.themeSystem);
        themeLight = view.findViewById(R.id.themeLight);
        themeDark = view.findViewById(R.id.themeDark);

        theme = sharedPreferences.getString("Theme", "Follow");

        switch(theme){

            case "Follow":
                themeSystem.setChecked(true);
                break;

            case "Light":
                themeLight.setChecked(true);
                break;

            case "Dark":
                themeDark.setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.themeSystem){
                editor.putString("Theme", "Follow").apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }else if(checkedId == R.id.themeLight){
                editor.putString("Theme", "Light").apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else if(checkedId == R.id.themeDark){
                editor.putString("Theme", "Dark").apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        return view;
    }
    private boolean donationInstalled() {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo("exa.free.linuxbox.d", 0);
            return true;
        }catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private boolean isVideoAdsWatched(){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        cal.setTime(date);
        int a =  cal.get(Calendar.DAY_OF_MONTH);
        int b = sharedPreferences.getInt("VideoAds", 0);
        return a == b;
    }
    public void removeAdView() {
        if (donationInstalled() || isVideoAdsWatched()) {
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            scrollView.setLayoutParams(relativeLayoutParam);
            scrollView.requestLayout();
        }
    }
}
