package com.firebase.ui.auth.data.remote;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FlowParameters;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.ui.HelperActivityBase;
import com.firebase.ui.auth.viewmodel.ProviderSignInBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class AnonymousSignInHandler extends ProviderSignInBase<FlowParameters> {

    @VisibleForTesting
    public FirebaseAuth mAuth;

    public AnonymousSignInHandler(Application application) {
        super(application);
    }

    @Override
    protected void onCreate() {
        mAuth = getAuth();
    }

    @Override
    public void startSignIn(@NonNull HelperActivityBase activity) {
        showTopProgressBar();

        // Calling signInAnonymously() will always return the same anonymous user if already
        // available. This is enforced by the client SDK.
        mAuth.signInAnonymously()
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        setResult(Resource.<IdpResponse>forSuccess(getResponse(
                                result.getAdditionalUserInfo().isNewUser())));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setResult(Resource.<IdpResponse>forFailure(e));
                    }
                });

    }

    public IdpResponse getResponse(boolean isNewUser) {
        return new IdpResponse.Builder(
                new User.Builder(AuthUI.AnonymousAuthProvider.PROVIDER_ID, null)
                        .build())
                .setNewUser(isNewUser)
                .build();
    }

    private void showTopProgressBar() {
        setResult(Resource.<IdpResponse>forLoading());
    }

    private FirebaseAuth getAuth() {
        FirebaseApp app = FirebaseApp.getInstance(getArguments().appName);
        return FirebaseAuth.getInstance(app);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {}
}
