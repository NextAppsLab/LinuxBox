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

public class Uninstall extends Fragment {

    Context context;
    SharedPreferences sharedPreferences;
    Button button;
    MaterialButton materialButton;
    MaterialButton materialButton2;
    MaterialButton materialButton3;
    MaterialButton materialButton4;
    RelativeLayout.LayoutParams relativeLayoutParam;
    ScrollView scrollView;
    int leftMargin;
    int rightMargin;
    int topMargin;
    int bottomMargin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        getActivity().setTitle(R.string.uninstall);

        View view = inflater.inflate(R.layout.uninstall, container, false);

        context = getActivity().getApplicationContext();
        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);

        scrollView = view.findViewById(R.id.scrollView);
        relativeLayoutParam = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
        leftMargin = relativeLayoutParam.leftMargin;
        rightMargin = relativeLayoutParam.rightMargin;
        topMargin = relativeLayoutParam.topMargin;
        bottomMargin = 0;
        if(donationInstalled() || isVideoAdsWatched()){
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
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
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && rm -f ubuntu-xfce-uninstaller.sh* && wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/main/Scripts/Uninstaller/Ubuntu/Xfce/ubuntu-xfce-uninstaller.sh && bash ubuntu-xfce-uninstaller.sh");
                clipboard.setPrimaryClip(clip);
            }
        });
        materialButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && rm -f ubuntu-kde-uninstaller.sh* && wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/main/Scripts/Uninstaller/Ubuntu/Kde/ubuntu-kde-uninstaller.sh && bash ubuntu-kde-uninstaller.sh");
                clipboard.setPrimaryClip(clip);
            }
        });
        materialButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && rm -f debian-xfce-uninstaller.sh* && wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/main/Scripts/Uninstaller/Debian/Xfce/debian-xfce-uninstaller.sh && bash debian-xfce-uninstaller.sh");
                clipboard.setPrimaryClip(clip);
            }
        });
        materialButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("Command", "pkg install wget openssl-tool proot xz-utils -y && hash -r && rm -f kali-kde-uninstaller.sh* && wget https://raw.githubusercontent.com/EXALab/LinuxBoxResources/main/Scripts/Uninstaller/Kali/Kde/kali-kde-uninstaller.sh && bash kali-kde-uninstaller.sh");
                clipboard.setPrimaryClip(clip);
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
    public void removeAdView() {
        if (donationInstalled() || isVideoAdsWatched()) {
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            scrollView.setLayoutParams(relativeLayoutParam);
            scrollView.requestLayout();
        }
    }
}
