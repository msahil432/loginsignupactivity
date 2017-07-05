package com.msahil432.szaccounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.regex.Pattern;

/**
 * Created by sahil on 04/4/17.
 *
 */

public class LoginScreen extends Fragment {

    Pattern userPattern = Pattern.compile("([a-z]{2}[0-9]{4})");
    ImageView image;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView;
        rootView = (ViewGroup) inflater.inflate(R.layout.login_screen, container, false);
        image = (ImageView) rootView.findViewById(R.id.username_indicator);
        EditText edittext= (EditText) rootView.findViewById(R.id.wals_username);
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
                    image.setVisibility(View.GONE);
                }
            }
        });
        return rootView;
    }
}
