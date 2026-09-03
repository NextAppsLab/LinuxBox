package exa.free.linuxbox;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.rewarded.OnUserEarnedRewardListener;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardItem;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import java.util.Calendar;
import java.util.Date;

public class MainUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;
    ConsentRequestParameters params;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;
    private long lastPressedTime;
    private static final int PERIOD = 3000;
    private RewardedAd rewardedAd;
    AppOpenAdManager appOpenAdManager;
    InterstitialAd mInterstitialAd;
    AdView mAdView;
    FrameLayout frameLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int i = 0;
    boolean shouldShowAds;
    boolean firstDonationInstalled;
    boolean lockOpenAds = false;
    boolean showOpenAdsNow = false;
    boolean isDialogShowing = false;
    boolean shouldRemoveVideoAds = false;
    public boolean temporaryRemoveBoxesAd = false;
    boolean onInterstitialAdDismissed = false;
    String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);

        context = getApplicationContext();

        new Thread(
                () -> {
                    // Initialize GMA Next-Gen SDK on a background thread.
                    MobileAds.initialize(
                            this,
                            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
                            new InitializationConfig.Builder("ca-app-pub-5748356089815497~6187310378")
                                    .build(),
                            initializationStatus -> {
                                // Adapter initialization is complete.
                            });
                    // SDK initialization is complete. If you don't want to wait for bidding adapters to
                    // finish initializing, start loading ads now.
                })
                .start();

        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);
        editor = sharedPreferences.edit();

        theme = sharedPreferences.getString("Theme", "Follow");

        switch(theme){

            case "Follow":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;

            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;

            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        shouldShowAds = sharedPreferences.getBoolean("ShouldShowAds", false);
        firstDonationInstalled = sharedPreferences.getBoolean("FirstDonationInstalled", false);
        shouldRemoveVideoAds = false;
        temporaryRemoveBoxesAd = false;
        onInterstitialAdDismissed = false;

        final long splashDelay = 3500;
        final long startTime = System.currentTimeMillis();
        splashScreen.setKeepOnScreenCondition(
                () -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    return elapsed < splashDelay;
                });
        splashScreen.setOnExitAnimationListener(splashScreenview ->{
            appOpenAdManager.loadAd(MainUI.this);
            splashScreenview.remove();
        });

        frameLayout = findViewById(R.id.ad_view_container);

        mAdView = new AdView(this);
        frameLayout.addView(mAdView);

        appOpenAdManager = new AppOpenAdManager();

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        if(!drawer.isDrawerOpen(GravityCompat.START)){
            drawer.openDrawer(GravityCompat.START);
        }

        params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);

        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED || consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED){
                            if(!donationInstalled() && !isVideoAdsWatched()){
                                AdSize adSize = AdSize.getLargeAnchoredAdaptiveBannerAdSize(MainUI.this, 360);
                                BannerAdRequest adRequest = new BannerAdRequest.Builder("ca-app-pub-5748356089815497/1345107880", adSize).build();
                                mAdView.loadAd(
                                        adRequest,
                                        new AdLoadCallback<BannerAd>() {
                                            @Override
                                            public void onAdLoaded(@NonNull BannerAd ad) {
                                                ad.setAdEventCallback(
                                                        new BannerAdEventCallback() {
                                                            @Override
                                                            public void onAdImpression() {
                                                            }

                                                            @Override
                                                            public void onAdClicked() {
                                                            }
                                                        });
                                                Log.i("admob info", ad.toString());
                                            }
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                                Log.i("admob info", adError.toString());
                                            }
                                        });
                                loadAd();
                                appOpenAdManager.loadAd(MainUI.this);
                                editor.putBoolean("ShouldShowAds", true).apply();
                                shouldShowAds = sharedPreferences.getBoolean("ShouldShowAds", false);
                            }else{
                                mAdView.destroy();
                                mAdView.setVisibility(View.GONE);
                                frameLayout.removeView(mAdView);
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentHolder);
                                if(fragment instanceof Setup){
                                    ((Setup)fragment).removeAdView();
                                }else if(fragment instanceof Boxes){
                                    ((Boxes)fragment).removeAdView();
                                }else if(fragment instanceof Uninstall){
                                    ((Uninstall)fragment).removeAdView();
                                }else if(fragment instanceof Settings){
                                    ((Settings)fragment).removeAdView();
                                }else if(fragment instanceof About){
                                    ((About)fragment).removeAdView();
                                }
                                editor.putBoolean("ShouldShowAds", false).apply();
                                shouldShowAds = sharedPreferences.getBoolean("ShouldShowAds", false);
                            }
                        }else if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED){
                            loadForm();
                        }
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        // Handle the error.
                    }
                });
        if(shouldShowAds && !donationInstalled() && !isVideoAdsWatched()){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    appOpenAdManager.showAdIfAvailable(MainUI.this, new AppOpenAdManager.OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                            // Empty because the user will go back to the activity that shows the ad.
                            showOpenAdsNow = false;
                            i = -1;
                        }
                    });
                }
            }, 8500);
        }
        newFragment(0);
        //toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.icon_setup));
    }
    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
        if (mInterstitialAd == null) {
            loadAd();
        }
        shouldShowAds = sharedPreferences.getBoolean("ShouldShowAds", false);
        if(shouldShowAds && !donationInstalled() && !isVideoAdsWatched()){
            if(showOpenAdsNow){
                appOpenAdManager.showAdIfAvailable(MainUI.this, new AppOpenAdManager.OnShowAdCompleteListener() {
                    @Override
                    public void onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                        lockOpenAds = false;
                        showOpenAdsNow = false;
                        i = -1;
                    }
                });
            }
            if(lockOpenAds){
                showOpenAdsNow = true;
            }else{
                if(!isDialogShowing){
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            appOpenAdManager.showAdIfAvailable(MainUI.this, new AppOpenAdManager.OnShowAdCompleteListener() {
                                @Override
                                public void onShowAdComplete() {
                                    // Empty because the user will go back to the activity that shows the ad.
                                    showOpenAdsNow = false;
                                    i = -1;
                                }
                            });
                        }
                    }, 4500);
                }
            }
        }
        if(rewardedAd == null){
            loadRewardedAd();
        }
        if(shouldRemoveVideoAds){
            if(donationInstalled()){
                Toast.makeText(context, R.string.thanks_for_support, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, R.string.ads_removed_temp, Toast.LENGTH_LONG).show();
            }
            shouldRemoveVideoAds = false;
        }
        if(!firstDonationInstalled){
            if(donationInstalled()){
                thanksUserForSupport();
            }
        }
        if(donationInstalled() || isVideoAdsWatched()){
            mAdView.destroy();
            mAdView.setVisibility(View.GONE);
            frameLayout.removeView(mAdView);
            Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentHolder);
            if(fragment instanceof Setup){
                ((Setup)fragment).removeAdView();
            }else if(fragment instanceof Boxes){
                ((Boxes)fragment).removeAdView();
                ((Boxes)fragment).removeAdNotice();
            }else if(fragment instanceof Uninstall){
                ((Uninstall)fragment).removeAdView();
            }else if(fragment instanceof Settings){
                ((Settings)fragment).removeAdView();
            }else if(fragment instanceof About){
                ((About)fragment).removeAdView();
            }
        }
        if(onInterstitialAdDismissed){
            onInterstitialAdDismissed = false;
            ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            String boxesButton = sharedPreferences.getString("BoxesButton", "empty");
            if(boxesButton.equals("MaterialButton1")){
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f ubuntu-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Ubuntu/KDE/ubuntu-kde-installer.sh) && ([ -f start-ubuntu-kde.sh ] || bash ubuntu-kde-installer.sh) && bash start-ubuntu-kde.sh linuxbox-start");
                clipboard.setPrimaryClip(clip);
                notifyCommandCopied();
            }else if(boxesButton.equals("MaterialButton2")) {
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f ubuntu-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Ubuntu/KDE/ubuntu-kde-installer.sh) && ([ -f start-ubuntu-kde.sh ] || bash ubuntu-kde-installer.sh) && bash start-ubuntu-kde.sh linuxbox-start");
                clipboard.setPrimaryClip(clip);
                notifyCommandCopied();
            }else if(boxesButton.equals("MaterialButton3")) {
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f debian-xfce-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Debian/Xfce/debian-xfce-installer.sh) && ([ -f start-debian-xfce.sh ] || bash debian-xfce-installer.sh) && bash start-debian-xfce.sh linuxbox-start");
                clipboard.setPrimaryClip(clip);
                notifyCommandCopied();
            }else if(boxesButton.equals("MaterialButton4")) {
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f kali-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Kali/KDE/kali-kde-installer.sh) && ([ -f start-kali-kde.sh ] || bash kali-kde-installer.sh) && bash start-kali-kde.sh linuxbox-start");
                clipboard.setPrimaryClip(clip);
                notifyCommandCopied();
            }
            editor.putString("BoxesButton", "empty").apply();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if(drawer.isDrawerOpen(GravityCompat.START)){
                switch(event.getAction()){
                    case KeyEvent.ACTION_DOWN:
                        if(event.getDownTime() - lastPressedTime < PERIOD){
                            finish();
                        }else{
                            Toast.makeText(context, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
                            lastPressedTime = event.getEventTime();
                        }
                        return true;
                }
            }else if(!drawer.isDrawerOpen(GravityCompat.START)){
                drawer.openDrawer(GravityCompat.START);
            }
        }
        return false;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = this.getFragmentManager().findFragmentById(R.id.fragmentHolder);
        shouldShowAds = sharedPreferences.getBoolean("ShouldShowAds", false);

        if (id == R.id.setup) {
            MenuItem selected = navigationView.getMenu().findItem(R.id.setup);
            selected.setCheckable(true);
            selected.setChecked(true);
            if(!(fragment instanceof Setup)){
                newFragment(0);
                //toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.icon_setup));
            }
        }else if(id == R.id.box){
            MenuItem selected = navigationView.getMenu().findItem(R.id.box);
            selected.setCheckable(true);
            selected.setChecked(true);
            if(!(fragment instanceof Boxes)){
                newFragment(1);
                //toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.icon_boxes));
            }
        }else if(id == R.id.uninstall){
            MenuItem selected = navigationView.getMenu().findItem(R.id.uninstall);
            selected.setCheckable(true);
            selected.setChecked(true);
            if(!(fragment instanceof Uninstall)){
                newFragment(2);
                //toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.icon_uninstall));
            }
        }else if(id == R.id.settings){
            MenuItem selected = navigationView.getMenu().findItem(R.id.settings);
            selected.setCheckable(true);
            selected.setChecked(true);
            if(!(fragment instanceof Settings)){
                newFragment(3);
                //toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.icon_settings));
            }
        }else if(id == R.id.support){
            if(!donationInstalled()){
                notifyUserForSupport();
            }else{
                notifyUserForSupportAfterDonation();
            }
        }else if(id == R.id.report){
            notifyUserToReportError();
        }else if(id == R.id.about){
            MenuItem selected = navigationView.getMenu().findItem(R.id.about);
            selected.setCheckable(true);
            selected.setChecked(true);
            if(!(fragment instanceof About)){
                newFragment(4);
                //toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.icon_about));
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    protected void newFragment(int position){

        Fragment fragment;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch(position){

            case 0:
                fragment = new Setup();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case 1:
                fragment = new Boxes();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case 2:
                fragment = new Uninstall();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case 3:
                fragment = new Settings();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case 4:
                fragment = new About();
                fragmentTransaction.replace(R.id.fragmentHolder, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }
    public void notifyUserForSupport(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://details?id=exa.lnx.d");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if(Build.VERSION.SDK_INT >= 21){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try{
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=exa.free.linuxbox.d")));
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(R.string.watch_ads, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (rewardedAd != null) {
                    rewardedAd.show(
                            MainUI.this,
                            new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    // User earned the reward.
                                    Calendar cal = Calendar.getInstance();
                                    Date date = cal.getTime();
                                    cal.setTime(date);
                                    int a =  cal.get(Calendar.DAY_OF_MONTH);
                                    int b = sharedPreferences.getInt("VideoAds", 0);
                                    if(a != b){
                                        editor.putInt("VideoAds", a);
                                        editor.apply();
                                    }
                                    shouldRemoveVideoAds = true;
                                }
                            });
                }else{
                    Toast.makeText(context, R.string.no_video_ads, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNeutralButton(R.string.not_now, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.ask_for_donation);
    }
    public void notifyUserForSupportAfterDonation(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.watch_ads, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (rewardedAd != null) {
                    rewardedAd.show(MainUI.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            if(donationInstalled()){
                                Calendar cal = Calendar.getInstance();
                                Date date = cal.getTime();
                                cal.setTime(date);
                                int a =  cal.get(Calendar.DAY_OF_MONTH);
                                int b = sharedPreferences.getInt("VideoAds", 0);
                                if(a != b){
                                    editor.putInt("VideoAds", a);
                                    editor.apply();
                                }
                            }else{
                                Calendar cal = Calendar.getInstance();
                                Date date = cal.getTime();
                                cal.setTime(date);
                                int a =  cal.get(Calendar.DAY_OF_MONTH);
                                int b = sharedPreferences.getInt("VideoAds", 0);
                                if(a != b){
                                    if(!isVideoAdsWatched()){
                                        mAdView.destroy();
                                        mAdView.setVisibility(View.GONE);
                                        frameLayout.removeView(mAdView);
                                        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentHolder);
                                        if(fragment instanceof Setup){
                                            ((Setup)fragment).removeAdView();
                                        }else if(fragment instanceof Boxes){
                                            ((Boxes)fragment).removeAdView();
                                        }else if(fragment instanceof Uninstall){
                                            ((Uninstall)fragment).removeAdView();
                                        }else if(fragment instanceof Settings){
                                            ((Settings)fragment).removeAdView();
                                        }else if(fragment instanceof About){
                                            ((About)fragment).removeAdView();
                                        }
                                    }
                                    editor.putInt("VideoAds", a);
                                    editor.apply();
                                }
                            }
                            shouldRemoveVideoAds = true;
                        }
                    });
                }else{
                    Toast.makeText(context, R.string.no_video_ads, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.support_after_donation);
    }
    public void notifyUserToReportError(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"exalabdevelopers@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.bug_report1);
                startActivity(Intent.createChooser(emailIntent, getString(R.string.bug_report2)));
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Github", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EXALab/LinuxBox/issues"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.bug_encounter);
    }
    public void thanksUserForSupport(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string._continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                editor.putBoolean("FirstDonationInstalled", true).apply();
                firstDonationInstalled = sharedPreferences.getBoolean("FirstDonationInstalled", false);
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.thanks_user_for_support);
    }
    private boolean donationInstalled() {
        PackageManager packageManager = getPackageManager();
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
    public void loadAd(){

        InterstitialAd.load(
                new AdRequest.Builder("ca-app-pub-5748356089815497/9493849195").build(),
                new AdLoadCallback<InterstitialAd>() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        // Called when an ad has loaded.
                        ad.setAdEventCallback(new InterstitialAdEventCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                            }
                        });
                        mInterstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Called when ad fails to load.
                    }
                });
    }
    public void loadRewardedAd(){
        RewardedAd.load(
                new AdRequest.Builder("ca-app-pub-5748356089815497/5002221211").build(),
                new AdLoadCallback<RewardedAd>() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        // Called when an ad has loaded.
                        ad.setAdEventCallback(new RewardedAdEventCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Code to be invoked when the ad showed full screen content.
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                rewardedAd = null;
                                // Code to be invoked when the ad dismissed full screen content.
                            }
                        });
                        rewardedAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Called when ad fails to load.
                    }
                });
    }
    public void loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(
                this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        MainUI.this.consentForm = consentForm;
                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    MainUI.this,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                                // App can start requesting ads.
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);
                                            }
                                            // Handle dismissal by reloading form.
                                            loadForm();
                                        }
                                    });
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        // Handle Error.
                    }
                }
        );
    }
    public void notifyCommandCopied(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.command_copied);
    }
}
