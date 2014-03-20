package com.fewstera.injectablemedicinesguide;

import java.util.ArrayList;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	private ArrayList<Drug> _drugsIndex;
	private Button _loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		_loginButton = (Button)findViewById(R.id.login_button);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void loginClick(View view){
		EditText usernameText = (EditText) findViewById(R.id.username_input);
		String username = usernameText.getText().toString();
		
		EditText passwordText = (EditText) findViewById(R.id.password_input);
		String password = passwordText.getText().toString();
		
		_loginButton.setEnabled(false);

		System.out.println("STARTING TASK");
		
		RetrieveIndexTask retrieveIndexTask = new RetrieveIndexTask();
		retrieveIndexTask.execute(new String[] { username, password });
		this.setProgressBarIndeterminateVisibility(true); 
	
	}
	
	public void loginFailed(){
		_loginButton.setEnabled(true);
		System.out.println("Login failed.");
	}
	
	public void indexError(){
		_loginButton.setEnabled(true);
		System.out.println("Error downloading index");
	}
	
	public void loginComplete(){
		Intent intent;
		if(true){
			intent = new Intent(this, MainActivity.class);
			//intent.putParcelableArrayListExtra (EXTRA_DRUG_INDEX, _drugsIndex);
		}else{
			intent = new Intent(this, MainActivity.class);
		}
		startActivity(intent);
		finish();
	}
	
	private class RetrieveIndexTask extends AsyncTask<String, Void, ArrayList<Drug>> {
		private boolean loginFailed = false;
		
		@Override
		protected ArrayList<Drug> doInBackground(String... params) {
			
			String username = params[0];
			String password = params[1];
			
			DataRetrieval dataRetrival = DataRetrieval.getInstance();
			dataRetrival.setCredentials(username, password);
			
			try{
				return dataRetrival.fetchIndex();
			}catch(AuthException e){
				loginFailed = true;
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Drug> drugsIndex) {
			System.out.println("ASYNC TASK COMPLETE");
			
			LoginActivity.this.setProgressBarIndeterminateVisibility(false); 
			if(loginFailed){
				LoginActivity.this.loginFailed();
			}else if(drugsIndex == null){
				LoginActivity.this.indexError();
			}else{
				_drugsIndex = drugsIndex;
				LoginActivity.this.loginComplete();
			}
		}
	}

}
