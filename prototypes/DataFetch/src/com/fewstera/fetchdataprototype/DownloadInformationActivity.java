package com.fewstera.fetchdataprototype;

import java.util.ArrayList;
import java.util.Arrays;

import com.fewstera.NHSPackage.AuthException;
import com.fewstera.NHSPackage.DataRetrieval;
import com.fewstera.NHSPackage.Drug;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class DownloadInformationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_information);
		Intent intent = getIntent();
		ArrayList<Drug> drugList = intent.getParcelableArrayListExtra(DataFetchActivity.EXTRA_DRUG_INDEX);
		RetrieveDrugInfoTask retrieveDrugInfoTask = new RetrieveDrugInfoTask();
		Drug[] drugArray = drugList.toArray(new Drug[drugList.size()]);
		retrieveDrugInfoTask.execute(drugArray);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download_information, menu);
		return true;
	}
	

	private class RetrieveDrugInfoTask extends AsyncTask<Drug, Void, ArrayList<Drug>> {
		
		@Override
		protected ArrayList<Drug> doInBackground(Drug... drugs) {
			
			ArrayList<Drug> drugList = new ArrayList<Drug>(Arrays.asList(drugs));
			
			DataRetrieval dataRetrival = DataRetrieval.getInstance();
			try{
				char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
				//Begin downloading drugs beginning with each letter
				for(char letter: letters){
					dataRetrival.populateLetter(letter, drugList);
				}
				return drugList;
			}catch(AuthException e){
				System.out.println("Authentication failed.");
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Drug> drugsIndex) {
			
		}
	}

}
