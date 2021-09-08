package com.example.coleb19;

import android.app.ProgressDialog;
import android.util.Patterns;

class CommonMethods {


    public static int PASSWORD_LENGTH = 6;


    public static boolean isNotAnEmail(String email){
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkIfPassLengthNotValid(String password){
        return password.length() < PASSWORD_LENGTH;
    }


    public static void displayLoadingScreen(ProgressDialog progressDialog) {
        try{
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void hideProgressDialog(ProgressDialog progressDialog) {
        try {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final Exception e) {
            // Handle or log or ignore
            e.printStackTrace();
        } finally {
            progressDialog = null;
        }
    }

    public static boolean checkIfConfirmPassMatchesPass(String confPass, String pass){
        return confPass.equals(pass);
    }

}
