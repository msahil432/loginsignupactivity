package com.msahil432.szaccounts;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.msahil432.szaccounts.ForceClose.mExceptionHandler;

public class AccountActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks{

    private GoogleApiClient mGoogleApiClient;
    private boolean googleApiConnected, robotVerified;
    private String ipAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //Thread.setDefaultUncaughtExceptionHandler(new mExceptionHandler(this));
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(AccountActivity.this)
                .addOnConnectionFailedListener(AccountActivity.this)
                .build();
        mGoogleApiClient.connect();
        googleApiConnected = false;
        loadingPanel = findViewById(R.id.loadingPanel);
        robotVerified = false;


        (new AsyncTask<Context, Void, String>() {
            @Override
            protected String doInBackground(Context... params) {
                try {
                    if(WebHelper.isNetAvailable(params[0])) {
                        return WebHelper.getIPAddress(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                if(s!=null)
                    ipAddress = s;
                else
                    ipAddress = "0.0.0.0";
                Toast.makeText(AccountActivity.this,
                        "Accessing from IP Address: "+ipAddress, Toast.LENGTH_SHORT).show();
                super.onPostExecute(s);
            }
        }).execute(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
          The {@link android.support.v4.view.PagerAdapter} that will provide
          fragments for each of the sections. We use a
          {@link FragmentPagerAdapter} derivative, which will keep every
          loaded fragment in memory. If this becomes too memory intensive, it
          may be best to switch to a
          {@link android.support.v4.app.FragmentStatePagerAdapter}.
        */
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
          The {@link ViewPager} that will host the section contents.
        */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //False means no Menu
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.account_screen, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==1)
                return new SignUpScreen();
            else
                return new LoginScreen();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Log In";
                case 1:
                    return "Sign Up";
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        googleApiConnected = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiConnected = false;
    }

    String password, username, mail;
    View loadingPanel;

    private boolean valid(){
        Log.d("AccAct", "validating: "+username+password+mail);

        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Incomplete Form", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            Toast.makeText(this, "Enter Valid mail", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void loginButton(View v){
        loadingPanel.setVisibility(View.VISIBLE);
        v = v.getRootView();
        username = ((EditText)v.findViewById(R.id.wals_username)).getText().toString();
        password = ((EditText)v.findViewById(R.id.wals_password)).getText().toString();
        mail = "anyexample@gmail.com";
        if(!valid()){
            loadingPanel.setVisibility(View.GONE);
            return;
        }
        if(WebHelper.isNetAvailable(getApplicationContext())){
            try {
                if(working){
                    Toast.makeText(this,"Already processing", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject object = new JSONObject();
                object.accumulate("username",username);
                object.accumulate("password",password);
                recaptcha(object);
                Toast.makeText(this, "Sending to server "+object.toString(), Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                e.printStackTrace();
                loadingPanel.setVisibility(View.GONE);
            }
        }else {
            loadingPanel.setVisibility(View.GONE);
            Toast.makeText(this, "Net is duly required", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUpButton(View v){
        loadingPanel.setVisibility(View.VISIBLE);
        v = v.getRootView();
        username = ((EditText)v.findViewById(R.id.wass_username)).getText().toString();
        password = ((EditText)v.findViewById(R.id.wass_password)).getText().toString();
        mail =((EditText)v.findViewById(R.id.wass_mail)).getText().toString();
        if(!valid()) {
            loadingPanel.setVisibility(View.GONE);
            return;
        }
        if(WebHelper.isNetAvailable(getApplicationContext())){
            try{
                Toast.makeText(this, "Creating Account", Toast.LENGTH_SHORT).show();
                if(working){
                    Toast.makeText(this,"Already processing", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject object = new JSONObject();
                object.accumulate("username", username);
                object.accumulate("password", password);
                object.accumulate("mail", mail);
                recaptcha(object);
            }catch (Exception e){
                e.printStackTrace();
                loadingPanel.setVisibility(View.GONE);
            }
        }else {
            Toast.makeText(this, "Net is duly required", Toast.LENGTH_SHORT).show();
            loadingPanel.setVisibility(View.GONE);
        }
    }
    boolean working = false;
    private class WorkAsync extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            try {
                working = true;
                String t;
                //Post Data to API
                //t= WebHelper.instance().postJson("/account", params[0]);
                t = "{\"success\":true}";
                return t;
            }catch (Exception e){
                e.printStackTrace();
                working = false;
                return null;
            }
        }
        @Override
        protected void onPostExecute(String b1) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            working = true;
            try {
                if(b1==null) {
                    loadingPanel.setVisibility(View.GONE);
                    throw new Exception("NULL JSON RESPONSE");
                }
                JSONObject b = new JSONObject(b1);
                if (b.getBoolean("success")) {
                    boolean savingAccount = true;
                    Toast.makeText(AccountActivity.this, "Saving Account", Toast.LENGTH_SHORT).show();
                    if(savingAccount) {
                        Toast.makeText(AccountActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("account", true);
                        setResult(Activity.RESULT_OK, intent);
                    }else {
                        working = false;
                        loadingPanel.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                } else {
                    working = false;
                    loadingPanel.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                working = false;
                loadingPanel.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(b1);
        }
    }

    public void recaptcha(final JSONObject object){
        if(!googleApiConnected)
            return;
        if(robotVerified){
            (new WorkAsync()).execute(object);
            return;
        }
        working = true;
        SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient,
                "6Lfm5icUAAAAALph2pQmuG-4FeAATyEIq7FBI8en")
                .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                    @Override
                    public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
                        Status status = result.getStatus();
                        if ((status != null) && status.isSuccess()) {
                            // Indicates communication with reCAPTCHA service was
                            // successful. Use result.getTokenResult() to get the
                            // user response token if the user has completed
                            // the CAPTCHA.

                            if (!result.getTokenResult().isEmpty()) {
                                // User response token must be validated using the
                                // reCAPTCHA site verify API.
                                robotVerified = true;

                                String url;
                                try{
                                    url ="https://www.google.com/recaptcha/api/siteverify?"
                                            +"secret=6Lfm5icUAAAAAPLK0mYa5yWCftteCOSkbzbx9iOt"
                                            +"&response="+result.getTokenResult();
                                    if(ipAddress!=null) {
                                        url = url+"&remoteip=" + ipAddress;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    working = false;
                                    loadingPanel.setVisibility(View.GONE);
                                    return;
                                }
                                (new AsyncTask<String, Void, String>() {
                                    @Override
                                    protected String doInBackground(String... params) {
                                        try {
                                            if(params[0]==null){
                                                throw new Exception("Empty Parameter");
                                            }
                                            working = true;
                                            return WebHelper.instance().getJson(params[0]);
                                        }catch (Exception e){
                                            working = false;
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }
                                    @Override
                                    protected void onPostExecute(String s) {
                                        if(s!=null){
                                            try{
                                                JSONObject object2 = new JSONObject(s);
                                                if(object2.getBoolean("success")){
                                                    (new WorkAsync()).execute(object);
                                                }else {
                                                    loadingPanel.setVisibility(View.GONE);
                                                    working = false;
                                                    Toast.makeText(AccountActivity.this,
                                                            "reCaptcha Verification failed with server",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }catch (Exception e){
                                                working = false;
                                                e.printStackTrace();
                                                loadingPanel.setVisibility(View.GONE);
                                                return;
                                            }
                                        }else {
                                            working = false;
                                            loadingPanel.setVisibility(View.GONE);
                                        }
                                        super.onPostExecute(s);
                                    }
                                }).execute(url);
                            }
                        } else {
                            working = false;
                            loadingPanel.setVisibility(View.GONE);
                            Log.e("MY_APP_TAG", "Error occurred " +
                                    "when communicating with the reCAPTCHA service.");

                            // Use status.getStatusCode() to determine the exact
                            // error code. Use this code in conjunction with the
                            // information in the "Handling communication errors"
                            // section of this document to take appropriate action
                            // in your app.
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleApiConnected = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiConnected = false;
    }

}
