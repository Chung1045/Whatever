package com.example.whatever.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FirebaseHelper {
    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;
    private FirebaseUser user;
    private final FirebaseStorage mStorage;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        user = mAuth.getCurrentUser();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public String getUserName() {
        if (isLoggedIn()) {
            user = mAuth.getCurrentUser();
            if (!(user.getDisplayName() == null)) {
                return getCurrentUser().getDisplayName();
            } else {
                return "Anonymous?";
            }
        } else {
            return "Guest";
        }
    }

    // May reuse later on
    public void getEmailFromUsername(String input, Consumer<String> onSuccess) {

        DatabaseReference userRef = mDatabase.child("UserProfile").child(input);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    onSuccess.accept(email);
                } else {
                    // in case if that the input is actually an email address
                    onSuccess.accept(input);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onSuccess.accept(input);
            }
        });

    }

    public String getUID(){
        return user.getUid();
    }

    public void logout() {
        mAuth.signOut();
    }

    public void isUserNameOccupied(String input, Consumer<Boolean> onSuccess) {
        mDatabase.child("UserProfile")
                .orderByChild("username")
                .equalTo(input)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean usernameExists = snapshot.exists();
                        onSuccess.accept(usernameExists);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onSuccess.accept(false);
                    }
                });
    }



    public void updateProfileImage(Bitmap input, Consumer<Boolean> onSuccess) {
        if (isLoggedIn()) {
            StorageReference storageRef = mStorage.getReference()
                    .child("UserProfilePics").child(user.getUid() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            input.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Map<String, Object> userProfilePic = new HashMap<>();
                userProfilePic.put("profilePicURL", uri.toString());
                // Now you can save this URL to Firebase Realtime Database or Firestore
                mDatabase.child("UserProfile")
                        .child(user.getUid()).updateChildren(userProfilePic).addOnSuccessListener(e -> {
                            // or use it wherever you need.
                            onSuccess.accept(true);
                        });
            })).addOnFailureListener(e -> onSuccess.accept(false));
        }
    }

    public void downloadProfileImage(Consumer<Bitmap> onSuccess) {
        if (isLoggedIn()) {
            user = mAuth.getCurrentUser();
            StorageReference storageRef = mStorage.getReference()
                    .child("UserProfilePics").child(user.getUid() + ".jpg");

            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                onSuccess.accept(bitmap);

            }).addOnFailureListener(e -> onSuccess.accept(null));
        }
    }

    public void updateUserName(String input, Consumer<Boolean> onSuccess) {
        if (isLoggedIn()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(input)
                    .build();

            getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    onSuccess.accept(true);
                } else {
                    onSuccess.accept(false);
                }
            });
        }

    }

    public void updateBestTime(Activity activity, Consumer<Boolean> onSuccess) {
        if (isLoggedIn()) {
            if (isAllLevelPassed(activity)) {
                Map<String, Object> bestTimeMap = firebaseGetBestTime(activity);
                mDatabase.child("UserProfile")
                        .child(user.getUid()).updateChildren(bestTimeMap)
                        .addOnSuccessListener(e -> onSuccess.accept(true))
                        .addOnFailureListener(e -> onSuccess.accept(false));
            } else onSuccess.accept(false);
        }
    }

    public boolean isAllLevelPassed(Activity activity){
        UserPreferences.init(activity);
        long bestTimeLevel1 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0);
        long bestTimeLevel2 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0);
        long bestTimeLevel3 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0);
        long bestTimeLevel4 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0);
        long bestTimeLevel5 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0);
        long bestTimeLevel6 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0);
        long bestTimeLevel7 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0);
        long bestTimeLevel8 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0);
        long bestTimeLevel9 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0);

        return bestTimeLevel1 != 0 && bestTimeLevel2 != 0 && bestTimeLevel3 != 0 && bestTimeLevel4 != 0
                && bestTimeLevel5 != 0 && bestTimeLevel6 != 0 && bestTimeLevel7 != 0 && bestTimeLevel8 != 0 && bestTimeLevel9!= 0;
    }

    public Map<String, Object> firebaseGetBestTime(Activity activity){
        UserPreferences.init(activity);
        Long bestTimeLevel1 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0);
        Long bestTimeLevel2 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0);
        Long bestTimeLevel3 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0);
        Long bestTimeLevel4 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0);
        Long bestTimeLevel5 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0);
        Long bestTimeLevel6 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0);
        Long bestTimeLevel7 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0);
        Long bestTimeLevel8 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0);
        Long bestTimeLevel9 = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0);
        int dinoCount = UserPreferences.sharedPref.getInt(UserPreferences.DINO_COUNT, 0);

        Map<String, Object> result = new HashMap<>();
        result.put("bestTimeLevel1", String.valueOf(bestTimeLevel1));
        result.put("bestTimeLevel2", String.valueOf(bestTimeLevel2));
        result.put("bestTimeLevel3", String.valueOf(bestTimeLevel3));
        result.put("bestTimeLevel4", String.valueOf(bestTimeLevel4));
        result.put("bestTimeLevel5", String.valueOf(bestTimeLevel5));
        result.put("bestTimeLevel6", String.valueOf(bestTimeLevel6));
        result.put("bestTimeLevel7", String.valueOf(bestTimeLevel7));
        result.put("bestTimeLevel8", String.valueOf(bestTimeLevel8));
        result.put("bestTimeLevel9", String.valueOf(bestTimeLevel9));
        result.put("bestTotalTime", String.valueOf(bestTimeLevel1 + bestTimeLevel2 + bestTimeLevel3 +
                bestTimeLevel4 + bestTimeLevel5 + bestTimeLevel6 + bestTimeLevel7 + bestTimeLevel8 + bestTimeLevel9));
        result.put("dinoCount", dinoCount);
        return result;

    }

    public void getLeaderboard(Consumer<List<HashMap<String, Object>>> callback) {
        List<HashMap<String, Object>> leaderboardList = new ArrayList<>();

        mDatabase.child("UserProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()){
                    String uid = user.getKey();
                    String userName = user.child("username").getValue(String.class);
                    String bestTimeString = user.child("bestTotalTime").getValue(String.class);
                    String profilePicURL = user.child("profilePicURL").getValue(String.class);

                    Log.d("Record", "Uid = " + uid + ", UserName = " + userName + ", BestTime = " + bestTimeString + ", ProfilePicURL = " + profilePicURL);

                    // Check if bestTime is not zero
                    if (bestTimeString!= null &&!bestTimeString.equals("0")){
                        Log.d("Record", "Accept data");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("uid", uid);
                        userMap.put("username", userName);
                        userMap.put("bestTime", Long.parseLong(bestTimeString));
                        userMap.put("profilePicURL", profilePicURL);

                        // Add the user data to the leaderboard list
                        leaderboardList.add(userMap);
                    } else {
                        Log.d("Record", "Discard data");
                    }
                }
                Log.d("Record", "Leaderboard size = " + leaderboardList.size() + "\n Data = " + leaderboardList + "\n Now going to sort");
                // Sort the leaderboard list by bestTime in ascending order
                sortByBestTimeAscending(leaderboardList);
                Log.d("Record", "Sorted: " + leaderboardList);

                // Call the callback with the leaderboard data
                callback.accept(leaderboardList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }

            private void sortByBestTimeAscending(List<HashMap<String, Object>> leaderboardList) {
                // Define a custom Comparator
                Comparator<HashMap<String, Object>> comparator = new Comparator<HashMap<String, Object>>() {
                    @Override
                    public int compare(HashMap<String, Object> item1, HashMap<String, Object> item2) {
                        // Retrieve the bestTime values from the HashMaps
                        float bestTime1 = ((Number) item1.get("bestTime")).floatValue();
                        float bestTime2 = ((Number) item2.get("bestTime")).floatValue();

                        // Compare the bestTime values
                        return Float.compare(bestTime1, bestTime2);
                    }
                };

                // Sort the list using the custom Comparator
                Collections.sort(leaderboardList, comparator);
                for (int i = 0; i < leaderboardList.size(); i++) {
                    leaderboardList.get(i).put("position", i + 1);
                }
            }

        });

    }

    public void retrieveProgress(){
        mDatabase.child("UserProfile").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Level1 = snapshot.child("bestTimeLevel1").getValue(String.class);
                String Level2 = snapshot.child("bestTimeLevel2").getValue(String.class);
                String Level3 = snapshot.child("bestTimeLevel3").getValue(String.class);
                String Level4 = snapshot.child("bestTimeLevel4").getValue(String.class);
                String Level5 = snapshot.child("bestTimeLevel5").getValue(String.class);
                String Level6 = snapshot.child("bestTimeLevel6").getValue(String.class);
                String Level7 = snapshot.child("bestTimeLevel7").getValue(String.class);
                String Level8 = snapshot.child("bestTimeLevel8").getValue(String.class);
                String bestTotalTime = snapshot.child("bestTotalTime").getValue(String.class);
                int dinoCount = snapshot.child("dinoCount").getValue(Integer.class);

                if (Level1!= null &&!Level1.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL1, Long.parseLong(Level1));
                }
                if (Level2!= null &&!Level2.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL2, Long.parseLong(Level2));
                }
                if (Level3!= null &&!Level3.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL3, Long.parseLong(Level3));
                }
                if (Level4!= null &&!Level4.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL4, Long.parseLong(Level4));
                }
                if (Level5!= null &&!Level5.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL5, Long.parseLong(Level5));
                }
                if (Level6!= null &&!Level6.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL6, Long.parseLong(Level6));
                }
                if (Level7!= null &&!Level7.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL7, Long.parseLong(Level7));
                }
                if (Level8!= null &&!Level8.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL8, Long.parseLong(Level8));
                }
                if (bestTotalTime!= null &&!bestTotalTime.equals("0")) {
                    UserPreferences.editor.putLong(UserPreferences.BEST_TIME_USED_TOTAL, Long.parseLong(bestTotalTime));
                }
                if (!(dinoCount == 0)) {
                    UserPreferences.editor.putInt(UserPreferences.DINO_COUNT, dinoCount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
