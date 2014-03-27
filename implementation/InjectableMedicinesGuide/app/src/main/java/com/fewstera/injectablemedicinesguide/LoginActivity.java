package com.fewstera.injectablemedicinesguide;

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
	
	private DataRetrieval _dataRetrival;
	private Button _loginButton;
	private String _username;
	private String _password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		_loginButton = (Button)findViewById(R.id.login_button);

        _dataRetrival = new DataRetrieval();
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

		RetrieveIndexTask retrieveIndexTask = new RetrieveIndexTask();
		retrieveIndexTask.execute(new String[] { _username, _password });
		this.setProgressBarIndeterminateVisibility(true); 
	
	}
	
	public void loginFailed(){
		_loginButton.setEnabled(true);
		Toast toast = Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void indexError(){
		_loginButton.setEnabled(true);
		Toast toast = Toast.makeText(getApplicationContext(), "Error downloading index", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void loginComplete(int drugCount){
		Preferences.setString(this, Preferences.USERNAME_KEY, _username);
    	Preferences.setString(this, Preferences.PASSWORD_KEY, _password);
		
		Toast toast = Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT);
		toast.show();
		Intent intent = new Intent(this, DownloadDataActivity.class);
        intent.putExtra("num_of_drugs", drugCount);
        intent.putExtra("username", _username);
        intent.putExtra("password", _password);
		startActivity(intent);
		finish();
	}
	
	private class RetrieveIndexTask extends AsyncTask<String, Void, ArrayList<Drug>> {
		private boolean loginFailed = false;
		
		@Override
		protected ArrayList<Drug> doInBackground(String... params) {
			
			String username = params[0];
			String password = params[1];


            _dataRetrival.setCredentials(username, password);
			
			try{
				return _dataRetrival.fetchIndex();
			}catch(AuthException e){
				loginFailed = true;
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Drug> drugsIndex) {			
			LoginActivity.this.setProgressBarIndeterminateVisibility(false); 
			if(loginFailed){
				LoginActivity.this.loginFailed();
			}else if(drugsIndex == null){
				LoginActivity.this.indexError();
			}else{
                int drugCount = _dataRetrival.getUniqueIdsFromIndex(drugsIndex).length;
				LoginActivity.this.loginComplete(drugCount);
			}
		}
	}

}
