package com.example.whatever.game;

import static com.example.whatever.game.UserPreferences.editor;
import static com.example.whatever.game.UserPreferences.sharedPref;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.util.Base64;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    private VibrateUtils vibrateUtils;
    private ImageUtils imageUtils;


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
        imageUtils = new ImageUtils();
        vibrateUtils = new VibrateUtils(c);
    }

    // Functions
    public void showToastMessage(String message){
        toastUtils.showMessage(activity, message);
    }

    public void showSnackBarMessage(String message){
        snackBarUtils.setSnackBarMessage(message);
    }

    public void getBitmapFromByte(Consumer<Bitmap> consumer){
        consumer.accept(imageUtils.getAvatarFromSharedPreferences());
    }

    public void saveAvatar(Bitmap bitmap){
        imageUtils.saveAvatarToSharedPreferences(bitmap);
    }

    public void removeAvatar(){
        UserPreferences.editor.putString(UserPreferences.USER_AVATAR, null).commit();
    }

    public void vibrateTick(){
        vibrateUtils.vibrateTick();
    }
    public void vibrateHeavyTick(){
        vibrateUtils.vibrateHeavyTick();
    }
    public void hideKeyboard(){
        hideKeyboardFunction(view, activity);
    }
    public void playSFX(int soundResourceId){
        sfxUtils.playSound(activity, soundResourceId);
    }
    public void playWrongSFX(){
        sfxUtils.playSound(activity, R.raw.wrong_sfx);
        vibrateUtils.vibrateWrongAttempt();
    }
    public void playEnterLevelSFX(){ sfxUtils.playSound(activity, R.raw.enter_level_sfx);}
    public void playCorrectSFX(){ sfxUtils.playCorrectSFXSound(activity);}

    private String calculateTimeFromLong(long time){
        if (time == 0){
            return "-- : -- : --";
        }else {
            long seconds = time / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (time % 1000);

            return String.format("%02d:%02d:%03d", minutes, seconds, (long) milliseconds);
        }
    }

    public String getBestTotaltime(){
        long bestTimeLevel1 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0);
        long bestTimeLevel2 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0);
        long bestTimeLevel3 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0);
        long bestTimeLevel4 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0);
        long bestTimeLevel5 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0);
        long bestTimeLevel6 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0);
        long bestTimeLevel7 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0);
        long bestTimeLevel8 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0);
        long bestTimeLevel9 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0);
        long bestTimeLevel10 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0);

        if (bestTimeLevel1 == 0 || bestTimeLevel2 == 0 || bestTimeLevel3 == 0 || bestTimeLevel4 == 0
                || bestTimeLevel5 == 0 || bestTimeLevel6 == 0 || bestTimeLevel7 == 0 || bestTimeLevel8 == 0 || bestTimeLevel9 == 0 || bestTimeLevel10 == 0){
            return "Finish all level to see your best total time.";
        } else {
            long totalTime = bestTimeLevel1 + bestTimeLevel2 + bestTimeLevel3 + bestTimeLevel4
                    + bestTimeLevel5 + bestTimeLevel6 + bestTimeLevel7 + bestTimeLevel8 + bestTimeLevel9 + bestTimeLevel10;
            long seconds = totalTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (totalTime % 1000);
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_USED_TOTAL, totalTime).commit();
            return "Best total time : " + String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
        }

    }

    public void localGetAllRecords(Consumer<Map<String, Object>> consumer){

        Map<String, Object> recordHolder = new HashMap<>();

        long bestTimeLevel1 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0);
        long bestTimeLevel2 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0);
        long bestTimeLevel3 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0);
        long bestTimeLevel4 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0);
        long bestTimeLevel5 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0);
        long bestTimeLevel6 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0);
        long bestTimeLevel7 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0);
        long bestTimeLevel8 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0);
        long bestTimeLevel9 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0);
        long bestTimeLevel10 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0);
        int dinoCount = sharedPref.getInt(UserPreferences.DINO_COUNT, 0);

        recordHolder.put("Best time Level01", calculateTimeFromLong(bestTimeLevel1));
        recordHolder.put("Best time Level02", calculateTimeFromLong(bestTimeLevel2));
        recordHolder.put("Best time Level03", calculateTimeFromLong(bestTimeLevel3));
        recordHolder.put("Best time Level04", calculateTimeFromLong(bestTimeLevel4));
        recordHolder.put("Best time Level05", calculateTimeFromLong(bestTimeLevel5));
        recordHolder.put("Best time Level06", calculateTimeFromLong(bestTimeLevel6));
        recordHolder.put("Best time Level07", calculateTimeFromLong(bestTimeLevel7));
        recordHolder.put("Best time Level08", calculateTimeFromLong(bestTimeLevel8));
        recordHolder.put("Best time Level09", calculateTimeFromLong(bestTimeLevel9));
        recordHolder.put("Best time Level10", calculateTimeFromLong(bestTimeLevel10));

        if (isAllLevelPassed()) {
            recordHolder.put("Average Time", getAverageTime());
        }
        recordHolder.put("Dino Count", dinoCount);

        consumer.accept(recordHolder);
    }

    public boolean isAllLevelPassed(){
        long bestTimeLevel1 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0);
        long bestTimeLevel2 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0);
        long bestTimeLevel3 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0);
        long bestTimeLevel4 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0);
        long bestTimeLevel5 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0);
        long bestTimeLevel6 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0);
        long bestTimeLevel7 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0);
        long bestTimeLevel8 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0);
        long bestTimeLevel9 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0);
        long bestTimeLevel10 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0);

        return bestTimeLevel1 != 0 && bestTimeLevel2 != 0 && bestTimeLevel3 != 0 && bestTimeLevel4 != 0
                && bestTimeLevel5 != 0 && bestTimeLevel6 != 0 && bestTimeLevel7 != 0 && bestTimeLevel8 != 0 && bestTimeLevel9!= 0 && bestTimeLevel10!= 0;
    }

    public String getAverageTime(){
        long bestTimeLevel1 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0);
        long bestTimeLevel2 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0);
        long bestTimeLevel3 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0);
        long bestTimeLevel4 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0);
        long bestTimeLevel5 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0);
        long bestTimeLevel6 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0);
        long bestTimeLevel7 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0);
        long bestTimeLevel8 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0);
        long bestTimeLevel9 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0);
        long bestTimeLevel10 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0);

        return calculateTimeFromLong((bestTimeLevel1 + bestTimeLevel2 + bestTimeLevel3 + bestTimeLevel4
                + bestTimeLevel5 + bestTimeLevel6 + bestTimeLevel7 + bestTimeLevel8 + bestTimeLevel9 + bestTimeLevel10) / 10);
    }

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

        private final Activity activity;
        private final View view;
        private final NotificationManager notificationManager;
        private final SnackBarUtils snackBarUtils;

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

        private final Context context;

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

            new MaterialAlertDialogBuilder(context).setTitle(titleResourceId).setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", null).show();

        }

        public void showDialog(AlertDialog.Builder alertBlueprint){
            AlertDialog alert = alertBlueprint.create();
            alert.show();
        }

    }

    private class IntentUtils{

        private final Context context;

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
        private final Context context;

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

    public class VibrateUtils{
        private Context context;
        private Vibrator vibrator;

        public VibrateUtils(Context c){
            context = c;
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        public void vibrateTick() {
            if (vibrator != null) {
                if (UserPreferences.sharedPref.getBoolean(UserPreferences.VIBRATION_ENABLED, false)) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.EFFECT_TICK));
                }
            }
        }

        public void vibrateHeavyTick() {
            if (vibrator!= null) {
                if (UserPreferences.sharedPref.getBoolean(UserPreferences.VIBRATION_ENABLED, false)) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.EFFECT_HEAVY_CLICK));
                }
            }
        }

        public void vibrateWrongAttempt() {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                long[] pattern = {0, 150, 100, 150};

                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
                if (UserPreferences.sharedPref.getBoolean(UserPreferences.VIBRATION_ENABLED, false)) {
                    vibrator.vibrate(vibrationEffect);
                }
            }
        }
    }

    public class ImageUtils {

        public void saveAvatarToSharedPreferences(Bitmap avatarBitmap) {

            // Convert bitmap to Base64 string
            String avatarBase64 = bitmapToBase64(avatarBitmap);

            // Save Base64 string to SharedPreferences
            UserPreferences.editor.putString(UserPreferences.USER_AVATAR, avatarBase64).commit();
        }

        // Retrieve cropped user avatar image from SharedPreferences
        public Bitmap getAvatarFromSharedPreferences() {

            // Retrieve Base64 string from SharedPreferences
            String avatarBase64 = UserPreferences.sharedPref.getString(UserPreferences.USER_AVATAR,null);

            // Convert Base64 string to bitmap
            return base64ToBitmap(avatarBase64);
        }

        // Convert Bitmap to Base64 string
        private String bitmapToBase64(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        // Convert Base64 string to Bitmap
        private Bitmap base64ToBitmap(String base64) {
            if (base64 != null && !base64.isEmpty()) {
                byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } else {
                // Handle the case when the Base64 string is null or empty
                return null; // Or you can return a default Bitmap if needed
            }
        }

    }

    private void hideKeyboardFunction(View v, Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void logoutRemoveRecord(){
        UserPreferences.editor.clear().commit();
    }

}
