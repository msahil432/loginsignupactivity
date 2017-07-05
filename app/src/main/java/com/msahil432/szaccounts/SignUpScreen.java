package com.msahil432.szaccounts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class SignUpScreen extends Fragment {
    ImageView image;
    Pattern userPattern = Pattern.compile("([a-z]{2}[0-9]{4})");
    usernameunique bgtask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView;
        rootView = (ViewGroup) inflater.inflate(R.layout.signup_screen, container, false);
        image = (ImageView) rootView.findViewById(R.id.username_indicator);
        EditText edittext= (EditText) rootView.findViewById(R.id.wass_username);
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!userPattern.matcher(s.toString()).matches()){
                    image.setVisibility(View.VISIBLE);
                }else {
                    if(bgtask!=null){
                        bgtask.cancel(true);
                    }
                    bgtask = new usernameunique();
                    bgtask.execute(s.toString());
                    Toast.makeText(getContext(), "Checking with server for unique", Toast.LENGTH_SHORT).show();
                    image.setVisibility(View.GONE);
                }
            }
        });
        return rootView;
    }
    private class usernameunique extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Toast.makeText(getContext(), "It is unique", Toast.LENGTH_SHORT).show();
            image.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            return null;
        }
    }
}
