package com.fewstera.injectablemedicinesguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fewstera.injectablemedicinesguide.dataDownload.*;
import com.fewstera.injectablemedicinesguide.database.DatabaseHelper;
import com.fewstera.injectablemedicinesguide.models.Drug;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for handling and displaying the progress of the download of data from the application.
 *
 * This class is used to show the user progress information whilst the database data is being
 * downloaded. This class also enables error handling when data download fails, allow the user
 * to cancel or retry.
 *
 * @author Aidan Wynne Fewster
 * @version 1.0
 * @since 1.0
 */
public class DownloadDataActivity extends LoggedInActivity {

    private SpiceManager _spiceManager = new SpiceManager(DownloadService.class);
    int _drugCount = 0;
    DataProgress _dataProgress;
    ProgressBar _progressBar;
    TextView _progressText;
    HashMap<String,String> _drugDataLinks = new HashMap<String,String>();
    Toast _toast;

    private String _encodedUsername, _encodedPassword;

    /* The failure types for a request */
    private final int INDEX_FAIL = 0;
    private final int CALCS_FAIL = 1;
    private final int DRUG_FAIL = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_download_data);

        beforeDownloadStart();
        startIndexDownload();
	}

    /**
     * This method resets the database ready for the new data. Inits variables and
     * encodes the users username and password
     */
    private void beforeDownloadStart() {
        _progressText = (TextView) findViewById(R.id.progressText);
        _progressBar = (ProgressBar) findViewById(R.id.downloadProgress);
        _progressBar.setMax(100);

        _toast = Toast.makeText(this , "", Toast.LENGTH_SHORT);

        /* Fetch the DataProgress object */
        _dataProgress = DataProgress.getInstance();
        _dataProgress.reset();

        /* Reset the download complete boolean so that if a data download has failed
         * the user isn't displayed with a partial database. */
        Preferences.setDownloadComplete(this, false);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.truncateAll();

        /* Encode the users username and password ready be used for a web request.  */
        try {
            _encodedUsername = URLEncoder.encode(Auth.getSavedUsername(this), "UTF-8");
            _encodedPassword = URLEncoder.encode(Auth.getSavedPassword(this), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            _encodedUsername = Auth.getSavedUsername(this);
            _encodedPassword = Auth.getSavedPassword(this);
            e.printStackTrace();
        }
        populateDrugDataLinks();
    }

    /**
     * Populates the drugDataLinks attribute to contain the list of API urls for downloading
     * drug information's
     */
    private void populateDrugDataLinks() {
        String[] drugDataTags = getResources().getStringArray(R.array.drug_data_download_tags);
        String[] drugDataLinks = getResources().getStringArray(R.array.drug_data_download_links);

        /* Ensure drug link and drug tag lists are of equal length */
        if(drugDataTags.length!=drugDataLinks.length) {
            throw new Error("Drug tag and link list are different sizes");
        }

        for(int count = 0; count<drugDataTags.length; count++){
            _drugDataLinks.put(drugDataTags[count], formatApiUrl(drugDataLinks[count]));
        }
    }

    /**
     * Adds the encoded username and password to the provided URL
     *
     * @param url the url to format
     * @return the formatted url
     */
    String formatApiUrl(String url){
        url = url.replace("%USERNAME%", _encodedUsername);
        url = url.replace("%PASSWORD%", _encodedPassword);
        return url;
    }

    /**
     *  Starts the requests to download the drug indexes
     */
    private void startIndexDownload(){
        this.setProgressBarIndeterminateVisibility(true);
        _progressText.setText("Downloading drug index information");
        DownloadIndexRequest downloadRequest = new DownloadIndexRequest(this, _encodedUsername, _encodedPassword);
        _spiceManager.execute(downloadRequest, "index_download", -1, new indexDownloadRequestListener());
    }

    /**
     * Starts the request to download the calculator informations
     */
    private void startCalcInfoDownload(){
        _progressText.setText("Downloading drug calculation information");
        _progressBar.setProgress(5);
        DownloadCalculationsRequest downloadRequest = new DownloadCalculationsRequest(this, _encodedUsername, _encodedPassword);
        _spiceManager.execute(downloadRequest, "calcs_download", -1, new calcsDownloadRequestListener());
    }

    /**
     * Starts the download for all drugs and drug informations
     */
    private void startDrugDataDownloads(){
        _dataProgress.drugsDownloadHasStarted();
        updateDownloadDrugProgress();
        for (Map.Entry<String, String> linkEntry: _drugDataLinks.entrySet()) {
            startDrugDataDownload(linkEntry.getKey(), linkEntry.getValue());
        }
    }

    /**
     * Starts the request to download drug information's from the API link provided
     *
     * @param tag the tag of the link, user for tracking progress
     * @param link the api link to donwload
     */
    private void startDrugDataDownload(String tag, String link) {
        DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest(this, tag, link);
        _spiceManager.execute(downloadRequest, "drug_download_" + tag, -1, new drugsDownloadRequestListener(tag));
    }

    /**
     * Updates the progress whilst drugs download
     */
    private void updateDownloadDrugProgress(){
        _progressText.setText("Downloading (" + _dataProgress.getDrugList().size() + " of " + _drugCount + ")");
        _progressBar.setProgress(10 + Math.round((float) _dataProgress.getDrugList().size() / (float) _drugCount * 90));

    }

    /**
     * Checks if all downloads have finished, if they have open the MainActivity
     */
    private void checkIfFinished() {
        /* If all requests have finished or drug list is full */
        if((_dataProgress.getFinishedCount()>=(_drugDataLinks.size()+2)) || (_dataProgress.getDrugList().size()>=_drugCount)){
            /* Cancel loading spinner  */
            this.setProgressBarIndeterminateVisibility(false);
            if(_dataProgress.getSucceededTags().size()<_drugDataLinks.size()){
                /* Decrease the amount of finished services to allow the user to try again. */
                _dataProgress.decreaseFinishedCountBy(_drugDataLinks.size()-_dataProgress.getSucceededTags().size());
                onDownloadFail(DRUG_FAIL);
            }else{
                updateCompletePrefs();

                /* Start main activity */
                Intent intent = new Intent();
                intent.setClass(DownloadDataActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    /**
     * Sets the date of the last update to todays date.
     */
    private void updateCompletePrefs() {
        /* Set the download complete to complete. */
        Preferences.setDownloadComplete(this, true);
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        String dateString = dataFormat.format(dateNow);
        Preferences.setString(this, Preferences.UPDATE_DATE_KEY, dateString);
    }

    /**
     * This method is called when a request has failed. It will let the user know what has failed
     * and give them the option to try download them sections again
     *
     * @param type which request type has failed
     */
    private void onDownloadFail(int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download failed, try again?");
        String message = "";
        if(type==INDEX_FAIL){
            message = "Failed to download drug index.\n\nWould you like to try again?";
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startIndexDownload();
                }
            });
        }else if(type==CALCS_FAIL){
            message = "Failed to download drug calculation info.\n\nWould you like to try again?";
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startCalcInfoDownload();
                }
            });
        }else if(type==DRUG_FAIL){
            final ArrayList<String> failedTags = new ArrayList<String>();
            /* Determine the links which have failed, from the tags that succeeded */
            for(String tag : _drugDataLinks.keySet()){
                if(!_dataProgress.getSucceededTags().contains(tag)){
                    failedTags.add(tag);
                }
            }

            /* Sort failed tags alphabetically and build failed string */
            String failedTagString = "";
            Collections.sort(failedTags);
            for(String tag: failedTags){
                failedTagString = failedTagString + tag + ", ";
            }
            /* Remove extra  ', ' */
            failedTagString = failedTagString.substring(0 , failedTagString.length() - 2);

            message = "Failed to download drugs starting with the letters " +
                    failedTagString + "\n\nWould you like to try again?";

            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for(String failedTag : failedTags){
                        startDrugDataDownload(failedTag, _drugDataLinks.get(failedTag));
                    }
                }
            });
        }
        builder.setMessage(message);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logout();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Displays the provided message in a Toast
     * @param message the message to display
     * @see android.widget.Toast
     */
    private void showMessage(String message){
        _toast.setText(message);
        _toast.show();
    }

    /**
     * Alerts the user that their credentials are incorrect and logs them out.
     */
    private void invalidLogin(){
        showMessage("Invalid credentials");
        logout();
    }

    /**
     * Logs the users out and returns them to login screen
     */
    private void logout(){
        _spiceManager.cancelAllRequests();
        Intent intent = new Intent();
        Auth.logout(this);
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Called when the activity resumes (E.g. when the user comes back into the application)
     *
     * This method resumes all request listeners and updates progress, so that the progress
     * displays progress made whilst in the background.
     */
    @Override
    protected void onStart() {
        /* Check that a login error hasn't occurred */
        if(_dataProgress.isLoginError()){ invalidLogin(); }
        _spiceManager.start(this);

        /* Add the Listeners for the index_download and calcs_download */
        _spiceManager.addListenerIfPending(Integer.class, "index_download", new indexDownloadRequestListener());
        _spiceManager.addListenerIfPending(Void.class, "calcs_download", new calcsDownloadRequestListener());

        /* Sets the _drugsCount to the value from the dataProgress */
        _drugCount = _dataProgress.getIndexSize();

        /* If the calculator download should start, start it */
        if(_dataProgress.shouldStartCalcsDownload()){ startCalcInfoDownload(); }

        /* If the drugs list download should start, start them */
        if(_dataProgress.shouldDrugsListStart()){ startDrugDataDownloads(); }
        if(_drugCount!=0){
            checkIfFinished();
            updateDownloadDrugProgress();
            for(String tag : _drugDataLinks.keySet()) {
                _spiceManager.addListenerIfPending(Drug[].class, "drug_download_" + tag, new drugsDownloadRequestListener(tag));

            }
        }
        super.onStart();
    }

    /**
     * Called when the activity stops (E.g. when the user presses the home button)
     */
    @Override
    protected void onStop() {
        _spiceManager.shouldStop();
        super.onStop();
    }

    /*******************************************************************************************
     **************************** REQUEST LISTENERS (INNER CLASSES) ***************************
     ******************************************************************************************/

    /**
     * Index download request listener
     *
     * Methods within this class are called when the the index download completes or fails.
     */
    private final class indexDownloadRequestListener implements RequestListener<Integer>, PendingRequestListener<Integer> {

        public indexDownloadRequestListener(){
            super();
        }

        /**
         * If the request failed, ask the user whether they'd like to try again
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestFailure(SpiceException spiceException) {
            if(_dataProgress.isLoginError()){
                invalidLogin();
                return ;
            }
            showMessage("Failed downloading index");
            onDownloadFail(INDEX_FAIL);
        }

        /**
         * When the request has finished, check that a login error did not occur. If not begin
         * downloading the calculator information
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestSuccess(final Integer indexNo) {
            if(_dataProgress.isLoginError()){
                invalidLogin();
                return ;
            }
            showMessage("Downloaded drug index");
            _drugCount = indexNo.intValue();
            startCalcInfoDownload();
        }

        public void onRequestNotFound() {}
    }


    /**
     * Drug calculator information request listener
     *
     * Methods within this class are called when the the calculator download completes or fails.
     */
    private final class calcsDownloadRequestListener implements RequestListener<Void>, PendingRequestListener<Void> {

        public calcsDownloadRequestListener(){
            super();
        }

        /**
         * If the request failed, ask the user whether they'd like to try again
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestFailure(SpiceException spiceException) {
            showMessage("Failed downloading calcs");
            onDownloadFail(CALCS_FAIL);
        }

        /**
         * Once complete, begin downloading the drug informations.
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestSuccess(final Void indexNo) {
            showMessage("Downloaded calculation");
            startDrugDataDownloads();
        }

        public void onRequestNotFound() {}
    }

    /**
     * Drug information request listener
     *
     * Methods within this class are called when a drugs list download completes or fails.
     */
    private final class drugsDownloadRequestListener implements RequestListener<Drug[]>, PendingRequestListener<Drug[]> {

        private String _tag;

        /**
         * Constructor which allows us to keep track of which link is in progress.
         * @param tag the tag of the link for this listener
         */
        public drugsDownloadRequestListener(String tag){
            super();
            _tag = tag;
        }

        /**
         * If the request failed, ask the user whether they'd like to try agian
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestFailure(SpiceException spiceException) {
            showMessage("Failed downloading " + _tag + "... drugs");
            checkIfFinished();
        }

        /**
         * If the request failed, ask the user whether they'd like to try agian
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestSuccess(final Drug[] drugs) {
            showMessage("Downloaded " + _tag + "... drugs");
            updateDownloadDrugProgress();
            checkIfFinished();
        }

        public void onRequestNotFound() {}
    }


    /*******************************************************************************************
     ************************************* MENU ITEM OPTIONS ***********************************
     ******************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.download_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle item selection */
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
