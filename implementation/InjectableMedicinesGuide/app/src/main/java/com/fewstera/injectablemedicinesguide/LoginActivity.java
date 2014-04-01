package com.fewstera.injectablemedicinesguide;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private Auth _auth;
	private Button _loginButton;
	private String _username;
	private String _password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		_loginButton = (Button)findViewById(R.id.login_button);
        _auth = new Auth();

        // Reset the download complete boolean so that if a data download has failed
        // the user isn't displayed with a partial database.
        Preferences.setDownloadComplete(this, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void loginClick(View view){
		EditText usernameText = (EditText) findViewById(R.id.username_input);
		_username = usernameText.getText().toString();
		
		EditText passwordText = (EditText) findViewById(R.id.password_input);
		_password = passwordText.getText().toString();
		
		_loginButton.setEnabled(false);

        ValidateLoginTask validateLoginTask = new ValidateLoginTask();
        validateLoginTask.execute();
		this.setProgressBarIndeterminateVisibility(true); 
	
	}
	
	public void loginFailed(){
		_loginButton.setEnabled(true);
		Toast toast = Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT);
		toast.show();
	}

	public void connectionError(){
		_loginButton.setEnabled(true);
		Toast toast = Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void loginComplete(){
        _auth.saveCredentials(this);
		Toast toast = Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT);
		toast.show();
		Intent intent = new Intent(this, DownloadDataActivity.class);
		startActivity(intent);
		finish();
	}
	
	private class ValidateLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... p) {

            _auth.setCredentials(_username, _password);
            try{
			    return new Boolean(_auth.isValid());
            }catch(Exception e){
                return null;
            }
		}

		@Override
		protected void onPostExecute(Boolean isValid) {
			LoginActivity.this.setProgressBarIndeterminateVisibility(false); 
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
