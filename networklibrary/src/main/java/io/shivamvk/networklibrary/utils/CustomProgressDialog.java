package io.shivamvk.networklibrary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.shivamvk.networklibrary.R;

public class CustomProgressDialog {

    private Activity activity;
    private AlertDialog dialog;

    public CustomProgressDialog(Activity activity){
        this.activity = activity;
    }

    public void show(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.progress_dialog, null);
        TextView progressText = view.findViewById(R.id.progress_text);
        progressText.setText(text);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
