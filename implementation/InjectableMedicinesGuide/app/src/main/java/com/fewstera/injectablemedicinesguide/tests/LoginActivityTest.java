package com.fewstera.injectablemedicinesguide.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.fewstera.injectablemedicinesguide.LoginActivity;
import com.fewstera.injectablemedicinesguide.R;

/**
 * Created by fewstera on 16/04/2014.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private LoginActivity _loginActivity;
    private EditText _usernameEditText, _passwordEditText;
    private Button _loginButton;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _loginActivity = getActivity();
        _usernameEditText = (EditText) _loginActivity.findViewById(R.id.username_input);
        _passwordEditText = (EditText) _loginActivity.findViewById(R.id.password_input);
        _loginButton = (Button) _loginActivity.findViewById(R.id.login_button);

    }

    @SmallTest
    public void testPreconditions() {
        assertNotNull("_loginActivity is null", _loginActivity);
        assertNotNull("_usernameTextView is null", _usernameEditText);
        assertNotNull("_passwordTextView is null", _passwordEditText);
        assertNotNull("_loginButton is null", _loginButton);
    }

    @MediumTest
    public void testUsernameTextView(){
        final View decorView = _loginActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _usernameEditText);

        final ViewGroup.LayoutParams layoutParams =
                _usernameEditText.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check username hint */
        String usernameHint = _loginActivity.getResources().getString(R.string.username_hint);
        assertEquals(_usernameEditText.getHint(), usernameHint);
    }

    @MediumTest
    public void testPasswordTextView(){
        final View decorView = _loginActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, _passwordEditText);

        final ViewGroup.LayoutParams layoutParams =
                _passwordEditText.getLayoutParams();

        /* Check layout */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check password hint */
        String passwordHint = _loginActivity.getResources().getString(R.string.password_hint);
        assertEquals(_passwordEditText.getHint(), passwordHint);
    }

    @MediumTest
    public void testLoginButton(){
        final View decorView = _loginActivity.getWindow().getDecorView();
        /* Check button is displayed */
        ViewAsserts.assertOnScreen(decorView, _loginButton);


        final ViewGroup.LayoutParams layoutParams =
                _loginButton.getLayoutParams();

        /* Check layout of button */
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        /* Check button text */
        String buttonText = _loginActivity.getResources().getString(R.string.login_button_text);
        assertEquals(buttonText, _loginButton.getText());
    }
}
