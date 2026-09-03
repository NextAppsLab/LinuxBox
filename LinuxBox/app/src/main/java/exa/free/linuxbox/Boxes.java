package exa.free.linuxbox;

import android.app.AlertDialog;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;

public class Boxes extends Fragment {

    MainUI mainUI;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button button;
    MaterialButton materialButton;
    MaterialButton materialButton2;
    MaterialButton materialButton3;
    MaterialButton materialButton4;
    TextView textView;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    RelativeLayout.LayoutParams relativeLayoutParam;
    ScrollView scrollView;
    boolean shouldShowAds;
    int leftMargin;
    int rightMargin;
    int topMargin;
    int bottomMargin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        getActivity().setTitle(R.string.boxes);

        View view = inflater.inflate(R.layout.boxes, container, false);

        context = getActivity().getApplicationContext();

        mainUI = (MainUI)getActivity();

        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);
        editor = sharedPreferences.edit();
        shouldShowAds = sharedPreferences.getBoolean("ShouldShowAds", false);

        if(shouldShowAds){
            if(!donationInstalled() && !isVideoAdsWatched()){
                if(mainUI.mInterstitialAd == null){
                    mainUI.loadAd();
                }
            }
        }

        textView = view.findViewById(R.id.clickCopyCommand);
        textView2 = view.findViewById(R.id.clickCopyCommand2);
        textView3 = view.findViewById(R.id.clickCopyCommand3);
        textView4 = view.findViewById(R.id.clickCopyCommand4);

        scrollView = view.findViewById(R.id.scrollView);
        relativeLayoutParam = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
        leftMargin = relativeLayoutParam.leftMargin;
        rightMargin = relativeLayoutParam.rightMargin;
        topMargin = relativeLayoutParam.topMargin;
        bottomMargin = 0;
        if(donationInstalled() || isVideoAdsWatched()){
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            textView.setText(R.string.clickcopycommand_adfree);
            textView2.setText(R.string.clickcopycommand_adfree);
            textView3.setText(R.string.clickcopycommand_adfree);
            textView4.setText(R.string.clickcopycommand_adfree);
        }else{
            textView.setText(R.string.clickcopycommand);
            textView2.setText(R.string.clickcopycommand);
            textView3.setText(R.string.clickcopycommand);
            textView4.setText(R.string.clickcopycommand);
        }


        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);

        button = view.findViewById(R.id.button);
        materialButton = view.findViewById(R.id.copy);
        materialButton2 = view.findViewById(R.id.copy2);
        materialButton3 = view.findViewById(R.id.copy3);
        materialButton4 = view.findViewById(R.id.copy4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPackageInstalled("com.termux", context.getPackageManager())){
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.termux");
                    startActivity(intent);
                }else{
                    notifyUserToInstallTermux();
                }
            }
        });
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mainUI.temporaryRemoveBoxesAd){
                    if(mainUI.mInterstitialAd != null && shouldShowAds && !donationInstalled() && !isVideoAdsWatched()){
                        mainUI.mInterstitialAd.show(mainUI);
                        mainUI.onInterstitialAdDismissed = true;
                        mainUI.temporaryRemoveBoxesAd = true;
                        editor.putString("BoxesButton", "MaterialButton1").apply();
                    }else{
                        ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f ubuntu-xfce-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Ubuntu/Xfce/ubuntu-xfce-installer.sh) && ([ -f start-ubuntu-xfce.sh ] || bash ubuntu-xfce-installer.sh) && bash start-ubuntu-xfce.sh linuxbox-start");
                        clipboard.setPrimaryClip(clip);
                        notifyCommandCopied();
                    }
                }else{
                    ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f ubuntu-xfce-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Ubuntu/Xfce/ubuntu-xfce-installer.sh) && ([ -f start-ubuntu-xfce.sh ] || bash ubuntu-xfce-installer.sh) && bash start-ubuntu-xfce.sh linuxbox-start");
                    clipboard.setPrimaryClip(clip);
                    notifyCommandCopied();
                }
            }
        });
        materialButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mainUI.temporaryRemoveBoxesAd){
                    if(mainUI.mInterstitialAd != null && shouldShowAds && !donationInstalled() && !isVideoAdsWatched()){
                        mainUI.mInterstitialAd.show(mainUI);
                        mainUI.onInterstitialAdDismissed = true;
                        mainUI.temporaryRemoveBoxesAd = true;
                        editor.putString("BoxesButton", "MaterialButton2").apply();
                    }else{
                        ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f ubuntu-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Ubuntu/KDE/ubuntu-kde-installer.sh) && ([ -f start-ubuntu-kde.sh ] || bash ubuntu-kde-installer.sh) && bash start-ubuntu-kde.sh linuxbox-start");
                        clipboard.setPrimaryClip(clip);
                        notifyCommandCopied();
                    }
                }else{
                    ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f ubuntu-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Ubuntu/KDE/ubuntu-kde-installer.sh) && ([ -f start-ubuntu-kde.sh ] || bash ubuntu-kde-installer.sh) && bash start-ubuntu-kde.sh linuxbox-start");
                    clipboard.setPrimaryClip(clip);
                    notifyCommandCopied();
                }
            }
        });
        materialButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainUI.mInterstitialAd != null && shouldShowAds && !donationInstalled() && !isVideoAdsWatched()){
                    if(mainUI.mInterstitialAd != null){
                        mainUI.mInterstitialAd.show(mainUI);
                        mainUI.onInterstitialAdDismissed = true;
                        mainUI.temporaryRemoveBoxesAd = true;
                        editor.putString("BoxesButton", "MaterialButton3").apply();
                    }else{
                        ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f debian-xfce-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Debian/Xfce/debian-xfce-installer.sh) && ([ -f start-debian-xfce.sh ] || bash debian-xfce-installer.sh) && bash start-debian-xfce.sh linuxbox-start");
                        clipboard.setPrimaryClip(clip);
                        notifyCommandCopied();
                    }
                }else{
                    ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f debian-xfce-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Debian/Xfce/debian-xfce-installer.sh) && ([ -f start-debian-xfce.sh ] || bash debian-xfce-installer.sh) && bash start-debian-xfce.sh linuxbox-start");
                    clipboard.setPrimaryClip(clip);
                    notifyCommandCopied();
                }
            }
        });
        materialButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainUI.mInterstitialAd != null && shouldShowAds && !donationInstalled() && !isVideoAdsWatched()){
                    if(mainUI.mInterstitialAd != null){
                        mainUI.mInterstitialAd.show(mainUI);
                        mainUI.onInterstitialAdDismissed = true;
                        mainUI.temporaryRemoveBoxesAd = true;
                        editor.putString("BoxesButton", "MaterialButton4").apply();
                    }else{
                        ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f kali-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Kali/KDE/kali-kde-installer.sh) && ([ -f start-kali-kde.sh ] || bash kali-kde-installer.sh) && bash start-kali-kde.sh linuxbox-start");
                        clipboard.setPrimaryClip(clip);
                        notifyCommandCopied();
                    }
                }else{
                    ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && ([ -f kali-kde-installer.sh ] || wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/refs/heads/main/Scripts/Installer/Kali/KDE/kali-kde-installer.sh) && ([ -f start-kali-kde.sh ] || bash kali-kde-installer.sh) && bash start-kali-kde.sh linuxbox-start");
                    clipboard.setPrimaryClip(clip);
                    notifyCommandCopied();
                }
            }
        });

        return view;
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void notifyUserToInstallTermux(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.termux");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if(Build.VERSION.SDK_INT >= 21){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try{
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/en/packages/com.termux/")));
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.please_install_termux);
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
    public void notifyCommandCopied(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainUI);
        LayoutInflater layoutInflater = LayoutInflater.from(mainUI);
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
    public void removeAdView() {
        if (donationInstalled() || isVideoAdsWatched()) {
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            scrollView.setLayoutParams(relativeLayoutParam);
            scrollView.requestLayout();
        }
    }
    public void removeAdNotice(){
        if (donationInstalled() || isVideoAdsWatched()) {
            textView.setText(R.string.clickcopycommand_adfree);
            textView2.setText(R.string.clickcopycommand_adfree);
            textView3.setText(R.string.clickcopycommand_adfree);
            textView4.setText(R.string.clickcopycommand_adfree);
        }
    }
}
