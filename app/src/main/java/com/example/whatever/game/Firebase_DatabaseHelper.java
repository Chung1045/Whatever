package com.example.whatever.game;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase_DatabaseHelper {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public Firebase_DatabaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void logout() {
        mAuth.signOut();
    }

    public void userSetUp(String userName, String email, UserSetupListener listener) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = mDatabase.child("users").child(user.getUid());
            userRef.child("username").setValue(userName);
            userRef.child("email").setValue(email)
                    .addOnSuccessListener(e -> {
                        listener.onSetupSuccess(true);
                    })
                    .addOnFailureListener(e -> {
                        listener.onSetupSuccess(false);
                    });
        }
    }

    public void checkUsernameUsed(String userName, UsernameCheckListener listener) {
        boolean isOccupied = false;
        mDatabase.child("users").orderByChild("username").equalTo(userName)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            listener.onUsernameOccupied(true);
                        } else {
                            listener.onUsernameOccupied(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onUsernameCheckFailed(true);
                    }
                });

    }

    // implement this into the class for returning the status of operation
    // like public class SignUp extends AppCompatActivity implements Firebase_DatabaseHelper.UserSetupListener {
    public interface UserSetupListener {
        void onSetupSuccess(boolean isSuccess);
    }

    public interface UsernameCheckListener {
        void onUsernameOccupied(boolean isOccupied);
        void onUsernameCheckFailed(boolean isFailed);

    }
}
