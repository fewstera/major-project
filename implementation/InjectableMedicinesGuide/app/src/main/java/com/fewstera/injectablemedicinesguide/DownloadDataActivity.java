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
import java.util.Date;

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
    private final char[] _letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    int _drugCount = 0;
    DataProgress _dataProgress;
    ProgressBar _progressBar;
    TextView _progressText;

    private String _encodedUsername, _encodedPassword;

    /* The failure types for a request */
    private final int INDEX_FAIL = 0;
    private final int CALCS_FAIL = 1;
    private final int LETTERS_FAIL = 2;

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
    }

    /**
     *  Starts the requests to download the drug indexes
     */
    private void startIndexDownload(){
        this.setProgressBarIndeterminateVisibility(true);
        _progressText.setText("Downloading drug index information");
        DownloadIndexRequest downloadRequest = new DownloadIndexRequest(getApplicationContext(), _encodedUsername, _encodedPassword);
        _spiceManager.execute(downloadRequest, "index_download", -1, new indexDownloadRequestListener());
    }

    /**
     * Starts the request to download the calculator informations
     */
    private void startCalcInfoDownload(){
        _progressText.setText("Downloading drug calculation information");
        _progressBar.setProgress(5);
        DownloadCalculationsRequest downloadRequest = new DownloadCalculationsRequest(getApplicationContext(), _encodedUsername, _encodedPassword);
        _spiceManager.execute(downloadRequest, "calcs_download", -1, new calcsDownloadRequestListener());
    }

    /**
     * Starts the download for all drug letters
     */
    private void startLetterDownloads(){
        _dataProgress.lettersHasStarted();
        updateDownloadLetterProgress();
        for (char letter : _letters) {
            startLetterDownload(letter);
        }
    }

    /**
     * Starts the request to download drug information's for the drug begining with the provided letter
     *
     * @param letter to begin download drug information's for
     */
    private void startLetterDownload(char letter) {
        DownloadDrugsRequest downloadRequest = new DownloadDrugsRequest(getApplicationContext(), _encodedUsername, _encodedPassword, letter);
        _spiceManager.execute(downloadRequest, "drug_download_" + letter, -1, new drugsDownloadRequestListener(letter));
    }


    private void updateDownloadLetterProgress(){
        _progressText.setText("Downloading (" + _dataProgress.getDrugList().size() + " of " + _drugCount + ")");
        _progressBar.setProgress(10 + Math.round((float) _dataProgress.getDrugList().size() / (float) _drugCount * 90));

    }

    /**
     * Checks if all downloads have finished, if they have open the MainActivity
     */
    private void checkIfFinished() {
        /* If all requests have finished or drug list is full */
        if((_dataProgress.getFinishedCount()>=(_letters.length+2)) || (_dataProgress.getDrugList().size()>=_drugCount)){
            /* Cancel loading spinner  */
            this.setProgressBarIndeterminateVisibility(false);
            if(_dataProgress.getSucceededLetters().size()<_letters.length){
                /* Decrease the amount of finished services to allow the user to try again. */
                _dataProgress.decreaseFinishedCountBy(_letters.length-_dataProgress.getSucceededLetters().size());
                onDownloadFail(LETTERS_FAIL);
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
        }else if(type==LETTERS_FAIL){
            String failedLetterString = "";
            final ArrayList<Character> failedLetters = new ArrayList<Character>();
            /* Determine the letters which have failed, from the letters that succeeded */
            for(char c : _letters){
                if(!_dataProgress.getSucceededLetters().contains(new Character(c))){
                    failedLetterString = failedLetterString + c + ", ";
                    failedLetters.add(new Character(c));
                }
            }
            failedLetterString = failedLetterString.substring(0 , failedLetterString.length() - 1);
            message = "Failed to download drugs starting with the letters " +
                    failedLetterString + "\n\nWould you like to try again?";

            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for(Character failedLetter : failedLetters){
                        startLetterDownload(failedLetter.charValue());
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
     * Alerts the user that their credentials are incorrect and logs them out.
     */
    private void invalidLogin(){
        Toast.makeText(DownloadDataActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
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

        /* If the letters download should start, start them */
        if(_dataProgress.shouldStartLetters()){ startLetterDownloads(); }
        if(_drugCount!=0){
            checkIfFinished();
            updateDownloadLetterProgress();
            for(char letter : _letters) {
                _spiceManager.addListenerIfPending(Drug[].class, "drug_download_" + letter, new drugsDownloadRequestListener(letter));

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
            Toast.makeText(DownloadDataActivity.this, "Failed downloading index", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(DownloadDataActivity.this, "Downloaded drug index", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(DownloadDataActivity.this, "Failed downloading calcs", Toast.LENGTH_SHORT).show();
            onDownloadFail(CALCS_FAIL);
        }

        /**
         * Once complete, begin downloading the drug informations.
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestSuccess(final Void indexNo) {
            Toast.makeText(DownloadDataActivity.this, "Downloaded calculation", Toast.LENGTH_SHORT).show();
            startLetterDownloads();
        }

        public void onRequestNotFound() {}
    }

    /**
     * Drug information request listener
     *
     * Methods within this class are called when a drug letter download completes or fails.
     */
    private final class drugsDownloadRequestListener implements RequestListener<Drug[]>, PendingRequestListener<Drug[]> {

        private char _letter;

        /**
         * Constructor which allows us to keep track of which letter is in question.
         * @param letter the letter of this listener
         */
        public drugsDownloadRequestListener(char letter){
            super();
            _letter = letter;
        }

        /**
         * If the request failed, ask the user whether they'd like to try agian
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(DownloadDataActivity.this, "Failed downloading " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            checkIfFinished();
        }

        /**
         * If the request failed, ask the user whether they'd like to try agian
         * @see RequestListener, PendingRequestListener
         */
        public void onRequestSuccess(final Drug[] drugs) {
            Toast.makeText(DownloadDataActivity.this, "Downloaded " + _letter + "... drugs", Toast.LENGTH_SHORT).show();
            updateDownloadLetterProgress();
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
