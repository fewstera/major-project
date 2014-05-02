package com.fewstera.injectablemedicinesguide;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Activity for logging in
 *
 * This activity is used to allow the user to enter their NHS credentials, which logs them into
 * the system.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends CommonActivity {

    private Auth _auth;
	private Button _loginButton;
	private String _username;
	private String _password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);

        /* Init varaibles */
		_loginButton = (Button)findViewById(R.id.login_button);
        _auth = new Auth();
	}

    /**
     * Called when the login button is press
     *
     * @param view of the login button
     */
	public void loginClick(View view){
        /* Get the entered username */
		EditText usernameText = (EditText) findViewById(R.id.username_input);
		_username = usernameText.getText().toString();
		/* Get the entered password */
		EditText passwordText = (EditText) findViewById(R.id.password_input);
		_password = passwordText.getText().toString();

        /* Disable the login button whilst processing. */
		_loginButton.setEnabled(false);

        /* Start the login task */
        ValidateLoginTask validateLoginTask = new ValidateLoginTask();
        validateLoginTask.execute();
        /* Set the progress spinner as loading */
		this.setProgressBarIndeterminateVisibility(true); 
	
	}

    /**
     *  Called when the ValidateLoginTask fails (invalid password)
     */
	public void loginFailed(){
		_loginButton.setEnabled(true);
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.login_error_password), Toast.LENGTH_SHORT);
		toast.show();
	}

    /**
     *  Called when the ValidateLoginTask returns connection error
     */
    public void connectionError(){
		_loginButton.setEnabled(true);
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.login_error_connection), Toast.LENGTH_SHORT);
		toast.show();
	}

    /**
     * Called when the ValidateLoginTask returns connection error
     */
    public void loginComplete(){
        _auth.saveCredentials(this);
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.login_success_toast), Toast.LENGTH_SHORT);
		toast.show();
		Intent intent = new Intent(this, DownloadDataActivity.class);
		startActivity(intent);
		finish();
	}

    /**
     * InnerClass for validating the users login credentials, called in an AsyncTask to keep the
     * process off the UI thread.
     */
	private class ValidateLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... p) {

            _auth.setCredentials(_username, _password);
            try{
			    return _auth.isValid(getApplicationContext());
            }catch(Exception e){
                return null;
            }
		}

        /* When the task is complete */
		@Override
		protected void onPostExecute(Boolean isValid) {
            /* Stop the loading spinner */
			LoginActivity.this.setProgressBarIndeterminateVisibility(false);

            /* Call appropriate method depending on tasks output */
			if(isValid==null){
                LoginActivity.this.connectionError();
            }else if(!isValid.booleanValue()){
				LoginActivity.this.loginFailed();
			}else{
				LoginActivity.this.loginComplete();
			}
		}
	}


}
