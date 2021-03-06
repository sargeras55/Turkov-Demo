package com.example.santa.anative.ui.auth;

import android.support.annotation.StringRes;

import com.example.santa.anative.ui.common.View;

/**
 * Created by santa on 15.03.17.
 */

public interface AuthView extends View {
    void showMessage(@StringRes int id);
    void showDialog();
    void hideDialog();
    void onStartMain();
    void showAuthorizationForm();
}
