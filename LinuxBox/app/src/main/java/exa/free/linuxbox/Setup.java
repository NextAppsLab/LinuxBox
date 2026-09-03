package exa.free.linuxbox;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;

public class Setup extends Fragment {

    Context context;
    SharedPreferences sharedPreferences;
    LinearLayout details;
    ImageView arrow;
    LinearLayout header;
    AlertDialog.Builder alertDialog;
    AlertDialog alert;
    MaterialButton materialButton;
    TextView textView;
    TextView installState;
    TextView termuxVersion;
    TextView termuxSource;
    TextView vncChooseText;
    TextView addressDetails;
    Button button;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    RelativeLayout.LayoutParams relativeLayoutParam;
    ScrollView scrollView;
    boolean isDialogShowing;
    boolean buttonClicked;
    String clickedType;
    int leftMargin;
    int rightMargin;
    int topMargin;
    int bottomMargin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.linuxbox_setup);

        View view = inflater.inflate(R.layout.setup, container, false);

        context = getActivity().getApplicationContext();
        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);

        scrollView = view.findViewById(R.id.scrollView);
        relativeLayoutParam = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
        leftMargin = relativeLayoutParam.leftMargin;
        rightMargin = relativeLayoutParam.rightMargin;
        topMargin = relativeLayoutParam.topMargin;
        bottomMargin = 0;
        if (donationInstalled() || isVideoAdsWatched()) {
            relativeLayoutParam.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        }

        isDialogShowing = false;

        details = view.findViewById(R.id.details);
        arrow = view.findViewById(R.id.expandIcon);
        header = view.findViewById(R.id.header);
        materialButton = view.findViewById(R.id.buttonChooseVnc);
        textView = view.findViewById(R.id.textView);
        installState = view.findViewById(R.id.installState);
        termuxVersion = view.findViewById(R.id.termuxVersion);
        termuxSource = view.findViewById(R.id.termuxSource);
        vncChooseText = view.findViewById(R.id.vnc_chooseText);
        addressDetails = view.findViewById(R.id.addressDetails);

        final String address = "127.0.0.1:5901";
        String fullText = getString(R.string.address_details, address);
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf(address);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#78989C")), start, start + address.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        addressDetails.setText(spannableString);

        if (isPackageInstalled("com.termux", context.getPackageManager())) {
            if (VNCServerInstalled(context).equals("VNC not installed")) {
                textView.setText(getString(R.string.setup_half));
                textView.setTextColor(Color.parseColor("#FFB74D"));
            } else {
                textView.setText(getString(R.string.setup_ready));
                textView.setTextColor(Color.parseColor("#66BB6A"));
            }
            installState.setText(R.string.termux_installed);
            installState.setTextColor(Color.parseColor("#43A047"));
            termuxVersion.setText(getString(R.string.version, getTermuxVersionName(context)));
            termuxSource.setText(getString(R.string.installed_from, getTermuxInstallerVendor(context)));
        } else {
            if (VNCServerInstalled(context).equals("VNC not installed")) {
                textView.setText(getString(R.string.setup_none));
                textView.setTextColor(Color.parseColor("#90A4AE"));
            } else {
                textView.setText(getString(R.string.setup_half));
                textView.setTextColor(Color.parseColor("#FFB74D"));
            }
            installState.setText(R.string.termux_notinstalled);
            installState.setTextColor(Color.parseColor("#FB9C00"));
        }
        if (VNCServerInstalled(context).equals("VNC not installed")) {
            vncChooseText.setText(getString(R.string.vnc_choose));
            vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
        } else {
            vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
            vncChooseText.setTextColor(Color.parseColor("#43A047"));
        }

        header.setOnClickListener(v -> {
            if (isPackageInstalled("com.termux", context.getPackageManager())) {
                if (details.getVisibility() == View.GONE) {
                    details.setVisibility(View.VISIBLE);
                    arrow.animate().rotation(180).start();
                } else {
                    details.setVisibility(View.GONE);
                    arrow.animate().rotation(0).start();
                }
                installState.setText(R.string.termux_installed);
                installState.setTextColor(Color.parseColor("#43A047"));
                termuxVersion.setText(getString(R.string.version, getTermuxVersionName(context)));
                termuxSource.setText(getString(R.string.installed_from, getTermuxInstallerVendor(context)));
                if (VNCServerInstalled(context).equals("VNC not installed")) {
                    textView.setText(getString(R.string.setup_half));
                    textView.setTextColor(Color.parseColor("#FFB74D"));
                } else {
                    textView.setText(getString(R.string.setup_ready));
                    textView.setTextColor(Color.parseColor("#66BB6A"));
                }
            } else {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.termux");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (Build.VERSION.SDK_INT >= 21) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/en/packages/com.termux/")));
                }
            }
        });
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("com.termux", context.getPackageManager())) {
                    if (VNCServerInstalled(context).equals("VNC not installed")) {
                        textView.setText(getString(R.string.setup_half));
                        textView.setTextColor(Color.parseColor("#FFB74D"));
                        vncChooseText.setText(getString(R.string.vnc_choose));
                        vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
                    } else {
                        textView.setText(getString(R.string.setup_ready));
                        textView.setTextColor(Color.parseColor("#66BB6A"));
                        vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                        vncChooseText.setTextColor(Color.parseColor("#43A047"));
                    }
                    installState.setText(R.string.termux_installed);
                    installState.setTextColor(Color.parseColor("#43A047"));
                    termuxVersion.setText(getString(R.string.version, getTermuxVersionName(context)));
                    termuxSource.setText(getString(R.string.installed_from, getTermuxInstallerVendor(context)));
                } else {
                    if (details.getVisibility() != View.GONE) {
                        details.setVisibility(View.GONE);
                        arrow.animate().rotation(0).start();
                    }
                    if (VNCServerInstalled(context).equals("VNC not installed")) {
                        textView.setText(getString(R.string.setup_none));
                        textView.setTextColor(Color.parseColor("#90A4AE"));
                        vncChooseText.setText(getString(R.string.vnc_choose));
                        vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
                    } else {
                        textView.setText(getString(R.string.setup_half));
                        textView.setTextColor(Color.parseColor("#FFB74D"));
                        vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                        vncChooseText.setTextColor(Color.parseColor("#43A047"));
                    }
                    installState.setText(R.string.termux_notinstalled);
                    installState.setTextColor(Color.parseColor("#FB9C00"));
                }
                showVNCDialog();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPackageInstalled("com.termux", context.getPackageManager())) {
            if (VNCServerInstalled(context).equals("") || VNCServerInstalled(context).equals("VNC not installed")) {
                textView.setText(getString(R.string.setup_half));
                textView.setTextColor(Color.parseColor("#FFB74D"));
            } else {
                textView.setText(getString(R.string.setup_ready));
                textView.setTextColor(Color.parseColor("#66BB6A"));
            }
            termuxVersion.setText(getString(R.string.version, getTermuxVersionName(context)));
            termuxSource.setText(getString(R.string.installed_from, getTermuxInstallerVendor(context)));
            installState.setText(R.string.termux_installed);
            installState.setTextColor(Color.parseColor("#43A047"));
        } else {
            if (details.getVisibility() != View.GONE) {
                details.setVisibility(View.GONE);
                arrow.animate().rotation(0).start();
            }
            if (VNCServerInstalled(context).equals("") || VNCServerInstalled(context).equals("VNC not installed")) {
                textView.setText(getString(R.string.setup_none));
                textView.setTextColor(Color.parseColor("#90A4AE"));
            } else {
                textView.setText(getString(R.string.setup_half));
                textView.setTextColor(Color.parseColor("#FFB74D"));
            }
            details.setVisibility(View.GONE);
            arrow.animate().rotation(0).start();
            termuxVersion.setText(getString(R.string.termux_isntinstalled));
            termuxSource.setText(getString(R.string.termux_isntinstalled));
            installState.setText(R.string.termux_notinstalled);
            installState.setTextColor(Color.parseColor("#FB9C00"));
        }
        if (VNCServerInstalled(context).equals("VNC not installed")) {
            vncChooseText.setText(getString(R.string.vnc_choose));
            vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
        } else {
            vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
            vncChooseText.setTextColor(Color.parseColor("#43A047"));
        }
        if (isDialogShowing) {
            if (isPackageInstalled("com.realvnc.viewer.android", context.getPackageManager())) {
                button.setText(getString(R.string.installed));
                button.setClickable(false);
                button.setEnabled(false);
                if (buttonClicked) {
                    if (clickedType.equals("Button1")) {
                        buttonClicked = false;
                        clickedType = "";
                        alert.dismiss();
                    }
                }
            } else {
                button.setText(getString(R.string.download));
                button.setClickable(true);
                button.setEnabled(true);
            }
            if (isPackageInstalled("com.iiordanov.freebVNC", context.getPackageManager())) {
                button2.setText(getString(R.string.installed));
                button2.setClickable(false);
                button2.setEnabled(false);
                if (buttonClicked) {
                    if (clickedType.equals("Button2")) {
                        buttonClicked = false;
                        clickedType = "";
                        alert.dismiss();
                    }
                }
            } else {
                button2.setText(getString(R.string.download));
                button2.setClickable(true);
                button2.setEnabled(true);
            }
            if (isPackageInstalled("com.gaurav.avnc", context.getPackageManager())) {
                button3.setText(getString(R.string.installed));
                button3.setClickable(false);
                button3.setEnabled(false);
                if (buttonClicked) {
                    if (clickedType.equals("Button3")) {
                        buttonClicked = false;
                        clickedType = "";
                        alert.dismiss();
                    }
                }
            } else {
                button3.setText(getString(R.string.download));
                button3.setClickable(true);
                button3.setEnabled(true);
            }
            if (isPackageInstalled("net.christianbeier.droidvnc_ng", context.getPackageManager())) {
                button4.setText(getString(R.string.installed));
                button4.setClickable(false);
                button4.setEnabled(false);
                if (buttonClicked) {
                    if (clickedType.equals("Button4")) {
                        buttonClicked = false;
                        clickedType = "";
                        alert.dismiss();
                    }
                }
            } else {
                button4.setText(getString(R.string.download));
                button4.setClickable(true);
                button4.setEnabled(true);
            }
            if (isPackageInstalled("com.coboltforge.dontmind.multivnc", context.getPackageManager())) {
                button5.setText(getString(R.string.installed));
                button5.setClickable(false);
                button5.setEnabled(false);
                if (buttonClicked) {
                    if (clickedType.equals("Button5")) {
                        buttonClicked = false;
                        clickedType = "";
                        alert.dismiss();
                    }
                }
            } else {
                button5.setText(getString(R.string.download));
                button5.setClickable(true);
                button5.setEnabled(true);
            }
            if (VNCServerInstalled(context).equals("") || VNCServerInstalled(context).equals("VNC not installed")) {
                vncChooseText.setText(getString(R.string.vnc_choose));
                vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
            }
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public String getTermuxVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.termux", 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Not Installed";
        }
    }

    public String getTermuxInstallerVendor(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String installerVendor = packageManager.getInstallerPackageName("com.termux");

        if (installerVendor.equals("com.android.vending")) {
            return "Google Play";
        } else if (installerVendor.equals("org.fdroid.fdroid")) {
            return "F-Droid";
        } else {
            return "Unknown";
        }
    }

    public void showVNCDialog() {
        final ViewGroup nullParent = null;
        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isDialogShowing = false;
                if (isPackageInstalled("com.termux", context.getPackageManager())) {
                    if (VNCServerInstalled(context).equals("VNC not installed")) {
                        textView.setText(getString(R.string.setup_half));
                        textView.setTextColor(Color.parseColor("#FFB74D"));
                        vncChooseText.setText(getString(R.string.vnc_choose));
                        vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
                    } else {
                        textView.setText(getString(R.string.setup_ready));
                        textView.setTextColor(Color.parseColor("#66BB6A"));
                        vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                        vncChooseText.setTextColor(Color.parseColor("#43A047"));
                    }
                    installState.setText(R.string.termux_installed);
                    installState.setTextColor(Color.parseColor("#43A047"));
                    termuxVersion.setText(getString(R.string.version, getTermuxVersionName(context)));
                    termuxSource.setText(getString(R.string.installed_from, getTermuxInstallerVendor(context)));
                } else {
                    if (details.getVisibility() != View.GONE) {
                        details.setVisibility(View.GONE);
                        arrow.animate().rotation(0).start();
                    }
                    if (VNCServerInstalled(context).equals("VNC not installed")) {
                        textView.setText(getString(R.string.setup_none));
                        textView.setTextColor(Color.parseColor("#90A4AE"));
                        vncChooseText.setText(getString(R.string.vnc_choose));
                        vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
                    } else {
                        textView.setText(getString(R.string.setup_half));
                        textView.setTextColor(Color.parseColor("#FFB74D"));
                        vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                        vncChooseText.setTextColor(Color.parseColor("#43A047"));
                    }
                    installState.setText(R.string.termux_notinstalled);
                    installState.setTextColor(Color.parseColor("#FB9C00"));
                }
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isDialogShowing = false;
                if (isPackageInstalled("com.termux", context.getPackageManager())) {
                    if (VNCServerInstalled(context).equals("VNC not installed")) {
                        textView.setText(getString(R.string.setup_half));
                        textView.setTextColor(Color.parseColor("#FFB74D"));
                        vncChooseText.setText(getString(R.string.vnc_choose));
                        vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
                    } else {
                        textView.setText(getString(R.string.setup_ready));
                        textView.setTextColor(Color.parseColor("#66BB6A"));
                        vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                        vncChooseText.setTextColor(Color.parseColor("#43A047"));
                    }
                    installState.setText(R.string.termux_installed);
                    installState.setTextColor(Color.parseColor("#43A047"));
                    termuxVersion.setText(getString(R.string.version, getTermuxVersionName(context)));
                    termuxSource.setText(getString(R.string.installed_from, getTermuxInstallerVendor(context)));
                } else {
                    if (details.getVisibility() != View.GONE) {
                        details.setVisibility(View.GONE);
                        arrow.animate().rotation(0).start();
                    }
                    if (VNCServerInstalled(context).equals("VNC not installed")) {
                        textView.setText(getString(R.string.setup_none));
                        textView.setTextColor(Color.parseColor("#90A4AE"));
                        vncChooseText.setText(getString(R.string.vnc_choose));
                        vncChooseText.setTextColor(Color.parseColor("#42A5F5"));
                    } else {
                        textView.setText(getString(R.string.setup_half));
                        textView.setTextColor(Color.parseColor("#FFB74D"));
                        vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                        vncChooseText.setTextColor(Color.parseColor("#43A047"));
                    }
                    installState.setText(R.string.termux_notinstalled);
                    installState.setTextColor(Color.parseColor("#FB9C00"));
                }
            }
        });
        alert = alertDialog.create();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.vnc_list, nullParent);
        button = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);
        button5 = view.findViewById(R.id.button5);
        if (isPackageInstalled("com.realvnc.viewer.android", context.getPackageManager())) {
            button.setText(getString(R.string.installed));
            button.setClickable(false);
            button.setEnabled(false);
        }
        if (isPackageInstalled("com.iiordanov.freebVNC", context.getPackageManager())) {
            button2.setText(getString(R.string.installed));
            button2.setClickable(false);
            button2.setEnabled(false);
        }
        if (isPackageInstalled("com.gaurav.avnc", context.getPackageManager())) {
            button3.setText(getString(R.string.installed));
            button3.setClickable(false);
            button3.setEnabled(false);
        }
        if (isPackageInstalled("net.christianbeier.droidvnc_ng", context.getPackageManager())) {
            button4.setText(getString(R.string.installed));
            button4.setClickable(false);
            button4.setEnabled(false);
        }
        if (isPackageInstalled("com.coboltforge.dontmind.multivnc", context.getPackageManager())) {
            button5.setText(getString(R.string.installed));
            button5.setClickable(false);
            button5.setEnabled(false);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("com.realvnc.viewer.android", context.getPackageManager())) {
                    button.setText(getString(R.string.installed));
                    button.setClickable(false);
                    button.setEnabled(false);
                    vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                    vncChooseText.setTextColor(Color.parseColor("#43A047"));
                } else {
                    buttonClicked = true;
                    clickedType = "Button1";
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.realvnc.viewer.android");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (Build.VERSION.SDK_INT >= 21) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intentSearch = new Intent(Intent.ACTION_WEB_SEARCH);
                        intentSearch.putExtra(SearchManager.QUERY, "RealVNC Viewer: Remote Desktop");
                        startActivity(intentSearch);
                        Toast.makeText(context, getString(R.string.googleplay_notinstalled), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("com.iiordanov.freebVNC", context.getPackageManager())) {
                    button2.setText(getString(R.string.installed));
                    button2.setClickable(false);
                    button2.setEnabled(false);
                    vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                    vncChooseText.setTextColor(Color.parseColor("#43A047"));
                } else {
                    buttonClicked = true;
                    clickedType = "Button2";
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.iiordanov.freebVNC");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (Build.VERSION.SDK_INT >= 21) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intentSearch = new Intent(Intent.ACTION_WEB_SEARCH);
                        intentSearch.putExtra(SearchManager.QUERY, "bVNC");
                        startActivity(intentSearch);
                        Toast.makeText(context, getString(R.string.googleplay_notinstalled), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("com.gaurav.avnc", context.getPackageManager())) {
                    button3.setText(getString(R.string.installed));
                    button3.setClickable(false);
                    button3.setEnabled(false);
                    vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                    vncChooseText.setTextColor(Color.parseColor("#43A047"));
                } else {
                    buttonClicked = true;
                    clickedType = "Button3";
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.gaurav.avnc");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (Build.VERSION.SDK_INT >= 21) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/en/packages/com.gaurav.avnc/")));
                    }
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("net.christianbeier.droidvnc_ng", context.getPackageManager())) {
                    button4.setText(getString(R.string.installed));
                    button4.setClickable(false);
                    button4.setEnabled(false);
                    vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                    vncChooseText.setTextColor(Color.parseColor("#43A047"));
                } else {
                    buttonClicked = true;
                    clickedType = "Button4";
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=net.christianbeier.droidvnc_ng");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (Build.VERSION.SDK_INT >= 21) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/en/packages/net.christianbeier.droidvnc_n/")));
                    }
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled("com.coboltforge.dontmind.multivnc", context.getPackageManager())) {
                    button5.setText(getString(R.string.installed));
                    button5.setClickable(false);
                    button5.setEnabled(false);
                    vncChooseText.setText(getString(R.string.installed_vnc, VNCServerInstalled(context)));
                    vncChooseText.setTextColor(Color.parseColor("#43A047"));
                } else {
                    buttonClicked = true;
                    clickedType = "Button5";
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.coboltforge.dontmind.multivnc");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (Build.VERSION.SDK_INT >= 21) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/en/packages/com.coboltforge.dontmind.multivnc/")));
                    }
                }
            }
        });
        alert.setView(view);
        alert.show();
        isDialogShowing = true;
    }

    public String VNCServerInstalled(Context context) {
        String s = "";
        int i = 0;
        boolean isVNCInstalled = false;
        if (isPackageInstalled("com.realvnc.viewer.android", context.getPackageManager())) {
            s = s + "RealVNC Viewer";
            i = 1;
            isVNCInstalled = true;
        } else {
            s.replace("RealVNC Viewer, ", "");
            s.replace("RealVNC Viewer", "");
        }
        if (isPackageInstalled("com.iiordanov.freebVNC", context.getPackageManager())) {
            if (i == 1) {
                s = s + ", bVNC";
            } else {
                s = s + "bVNC";
                i = 1;
            }
            isVNCInstalled = true;
        } else {
            s.replace("bVNC, ", "");
            s.replace("bVNC", "");
        }
        if (isPackageInstalled("com.gaurav.avnc", context.getPackageManager())) {
            if (i == 1) {
                s = s + ", AVNC";
            } else {
                s = s + "AVNC";
                i = 1;
            }
            isVNCInstalled = true;
        } else {
            s.replace("AVNC, ", "");
            s.replace("AVNC", "");
        }
        if (isPackageInstalled("net.christianbeier.droidvnc_ng", context.getPackageManager())) {
            if (i == 1) {
                s = s + ", droidVNC-NG";
            } else {
                s = s + "droidVNC-NG";
                i = 1;
            }
            isVNCInstalled = true;
        } else {
            s.replace("droidVNC-NG, ", "");
            s.replace("droidVNC-NG", "");
        }

        if (isPackageInstalled("com.coboltforge.dontmind.multivnc", context.getPackageManager())) {
            if (i == 1) {
                s = s + ", MultiVNC";
            } else {
                s = s + "MultiVNC";
            }
            isVNCInstalled = true;
        } else {
            s.replace(", MultiVNC", "");
            s.replace("MultiVNC", "");
        }
        if (isVNCInstalled) {
            return s;
        } else {
            return "VNC not installed";
        }
    }

    private boolean donationInstalled() {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo("exa.free.linuxbox.d", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isVideoAdsWatched() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        cal.setTime(date);
        int a = cal.get(Calendar.DAY_OF_MONTH);
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
