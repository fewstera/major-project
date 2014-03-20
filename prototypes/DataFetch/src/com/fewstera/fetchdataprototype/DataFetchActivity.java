package com.fewstera.fetchdataprototype;

import java.util.ArrayList;

import com.fewstera.NHSPackage.AuthException;
import com.fewstera.NHSPackage.DataRetrieval;
import com.fewstera.NHSPackage.Drug;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class DataFetchActivity extends Activity {

    public final static String EXTRA_DRUG_INDEX = "com.fewstera.DRUG_INDEX";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_data_fetch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.data_fetch, menu);
		return true;
	}
	
	public void loginClick(View view){
		EditText usernameText = (EditText) findViewById(R.id.username_input);
		String username = usernameText.getText().toString();
		
		EditText passwordText = (EditText) findViewById(R.id.password_input);
		String password = passwordText.getText().toString();

		RetrieveIndexTask retrieveIndexTask = new RetrieveIndexTask();
		retrieveIndexTask.execute(new String[] {username, password});
		this.setProgressBarIndeterminateVisibility(true); 
	}
	
	public void loginFailed(){
		System.out.println("Login failed.");
	}
	
	public void indexError(){
		System.out.println("Error downloading index");
	}
	
	public void downloadDrugInformation(ArrayList<Drug> drugsIndex){
		Intent intent = new Intent(this, DownloadInformationActivity.class);
		intent.putParcelableArrayListExtra (EXTRA_DRUG_INDEX, drugsIndex);
		startActivity(intent);
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
			DataFetchActivity.this.setProgressBarIndeterminateVisibility(false); 
			if(loginFailed){
				DataFetchActivity.this.loginFailed();
			}else if(drugsIndex == null){
				DataFetchActivity.this.indexError();
			}else{
				DataFetchActivity.this.downloadDrugInformation(drugsIndex);
			}
		}
	}
}
