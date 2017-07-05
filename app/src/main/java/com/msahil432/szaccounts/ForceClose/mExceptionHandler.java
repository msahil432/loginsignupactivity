package com.msahil432.szaccounts.ForceClose;

/**
 * Created by sahil on 6/4/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class mExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity myContext;

    public mExceptionHandler(Activity context) {
        myContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append(stackTrace.toString());
        Log.d("EXP", errorReport.toString());

        Bundle b = new Bundle();
        b.putString("CAUSE_OF_ERROR", exception.getMessage()+stackTrace.toString());

        Intent intent = new Intent(myContext, FCActivity.class);
        intent.putExtras(b);
        myContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}
