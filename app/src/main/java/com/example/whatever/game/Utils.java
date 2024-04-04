package com.example.whatever.game;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class Utils {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123;
    private static final String CHANNEL_ID = "dummy_channel";
    private final Activity activity;
    private final View view;
    private ToastUtils toastUtils;
    private SnackBarUtils snackBarUtils;
    private NotificationUtils notificationUtils;
    private DialogUtils dialogUtils;
    private IntentUtils intentUtils;
    private SFXUtils sfxUtils;

    public Utils(Activity a, View v, Context c){
        this.activity = a;
        this.view = v;

        UserPreferences.init(a);

        utilsInit(a, v, c);
    }

    public void utilsInit(Activity a , View v, Context c){
        toastUtils = new ToastUtils();
        snackBarUtils = new SnackBarUtils(v);
        notificationUtils = new NotificationUtils(a, v);
        dialogUtils = new DialogUtils(c);
        intentUtils = new IntentUtils(c);
        sfxUtils = new SFXUtils(c);
    }

    // Functions
    public void showToastMessage(String message){
        toastUtils.showMessage(activity, message);
    }

    public void showSnackBarMessage(String message){
        snackBarUtils.setSnackBarMessage(message);
    }

    public void showActionSnackBar(String message, String buttonText){
        snackBarUtils.setActionSnackbar(message, buttonText);
    }

    public void showNotifications(String message){
        notificationUtils.showNotif(activity, "This is a notification", view);
    }

    public void showSimpleDialog(String message, int titleResourceID){
        dialogUtils.setSimpleDialog(message, titleResourceID);
    }

    public void showYesNoDialog(String message, int titleResourceID){
        dialogUtils.setYesNoDialog(message, titleResourceID );
    }

    public void startUrlIntent(String url){
        intentUtils.urlIntent(url);
    }


    public void playSFX(int soundResourceId){
        sfxUtils.playSound(activity, soundResourceId);
    }
    public void playWrongSFX(){ sfxUtils.playSound(activity, R.raw.wrong_sfx); }
    public void playEnterLevelSFX(){ sfxUtils.playSound(activity, R.raw.enter_level_sfx);}
    public void playCorrectSFX(){ sfxUtils.playCorrectSFXSound(activity);}

    //Classes to handel the functions
    public class ToastUtils {
        public void showMessage(Context context, String message){
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }
    }

    public class SnackBarUtils {

        private final View view;

        public SnackBarUtils(View view){
            this.view = view;
        }

        public void setSnackBarMessage(String message) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        public void setActionSnackbar(String message, String buttonText) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction(buttonText, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showToastMessage("Clicked");
                        }
                    });
            snackbar.show();
        }
    }

    public class NotificationUtils {

        private Activity activity;
        private View view;
        private NotificationManager notificationManager;
        private SnackBarUtils snackBarUtils;

        public NotificationUtils(Activity a, View v){
            this.activity = a;
            this.view = v;
            this.notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            snackBarUtils = new SnackBarUtils(view);
            createNotificationChannel();

        }

        private void createNotificationChannel() {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Important Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("This notification contains important announcement, etc.");
            notificationManager.createNotificationChannel(channel);
        }

        public void showNotif(Context context, String message, View view) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(1, builder.build());
            } else {
                showSnackBarMessage("Notification permission is denied, as posting notification in or above Android 13 requires permission");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

    }

    public class DialogUtils{

        private Context context;

        public DialogUtils(Context c){
            this.context = c;
        }

        public void setSimpleDialog(String message, int titleResourceId) {
            AlertDialog.Builder dialogBuilder;

            dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setMessage(message).setTitle(titleResourceId);
            showDialog(dialogBuilder);
        }

        public void setYesNoDialog(String message, int titleResourceId) {
            // Legacy (Material 2)
            //AlertDialog.Builder dialogBuilder;

            //dialogBuilder = new AlertDialog.Builder(context);
            //dialogBuilder.setMessage(message)
            //        .setTitle(titleResourceId)
            //        .setCancelable(false)
            //        .setPositiveButton("Yes", null)  // Pass null for no action
            //        .setNegativeButton("No", null);  // Pass null for no action

            new MaterialAlertDialogBuilder(context).setTitle(titleResourceId).setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", null).show();

            //showDialog(dialogBuilder);
        }

        public void showDialog(AlertDialog.Builder alertBlueprint){
            AlertDialog alert = alertBlueprint.create();
            alert.show();
        }

    }

    private class IntentUtils{

        private Context context;

        public IntentUtils(Context c){
            context = c;
        }

        public void urlIntent(String url){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            context.startActivity(intent);
        }

    }

    public class SFXUtils {
        private MediaPlayer sfxPlayer;
        private Context context;

        public SFXUtils(Context c){
            context = c;
        }

        public void playSound(Context context, int soundResourceId) {
            // Release any resources from the previous MediaPlayer
            if (sfxPlayer != null) {
                sfxPlayer.release();
            }

            // Create a new MediaPlayer instance and start it
            sfxPlayer = MediaPlayer.create(context, soundResourceId);
            if (sfxPlayer != null) {
                if (UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, false)) {
                    sfxPlayer.setOnCompletionListener(MediaPlayer::release);
                    sfxPlayer.start();
                }
            }
        }

        public void playCorrectSFXSound(Context c){
            sfxPlayer = MediaPlayer.create(c, R.raw.correct_sfx);
            sfxPlayer.setOnCompletionListener(mediaPlayer -> MediaPlayer.create(c, R.raw.clapping_sfx).start());
            sfxPlayer.start();
        }

    }

}
