package com.example.coleb19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;


public class AnnounceRecovery extends AppCompatActivity {


    //public String Name,Age,Phone,Longitude,Latitude,Blood_Type,date;

    DatabaseReference reference;
    DatabaseReference reference2;

    ProgressDialog progressDialog;

    Covid19Patient covid19Patient;

    boolean exists = false;
    //boolean exists2 = false;

    //int test = 0;

    public void checkIfPatientExists() {

        Log.i("check", "check");

        CommonMethods.displayLoadingScreen(progressDialog);

        reference2 = FirebaseDatabase.getInstance().getReference().child("COVID19Patients");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                        exists = true;
                        Log.i("check2", "check2");
                        CommonMethods.hideProgressDialog(progressDialog);
                        //test = 1;
                    }
                    else{
                        CommonMethods.hideProgressDialog(progressDialog);
                    }
                    //test = 1;
                }
                else {
                    //test = 1;
                    CommonMethods.hideProgressDialog(progressDialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("after delay", "executed");
            }
        }, 3100);

        Log.i("after after delay", "executedz");*/
        //return exists;

    } // end method




    public void announceRecovery(View view) {

        //exists2 = checkIfPatientExists();


        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/

        if (exists) {

            CommonMethods.displayLoadingScreen(progressDialog);

            Log.i("if", "entered");

            //CommonMethods.displayLoadingScreen(progressDialog);

            // get patient data
            reference = FirebaseDatabase.getInstance().getReference().child("COVID19Patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String name = snapshot.child("Name").getValue().toString();
                        String age = snapshot.child("Age").getValue().toString();
                        String bloodType = snapshot.child("Blood_Type").getValue().toString();
                        String lat = snapshot.child("Latitude").getValue().toString();
                        String lon = snapshot.child("Longitude").getValue().toString();
                        String phone = snapshot.child("Phone").getValue().toString();
                        String date = String.valueOf(LocalDate.now());

                        covid19Patient = new Covid19Patient(name, age, phone, lon, lat, bloodType, date);
                        //Log.i("object", snapshot.getValue().toString());
                    }
                    else {
                        //Toast.makeText(AnnounceRecovery.this, "You're not a patient!", Toast.LENGTH_SHORT).show();
                        Log.i("not a patient", "not a patient");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // add patient data to recovered patients section
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference("RecoveredCOVID19Patients")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(covid19Patient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.i("added to recovered patients", "success");
                            }
                            else {
                                Log.i("added to recovered patients", "failed");
                            }
                        }
                    });
                }
            }, 2700);


            // remove patient info from patients section
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference("COVID19Patients")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AnnounceRecovery.this, "Recovery announced!", Toast.LENGTH_SHORT).show();
                                Log.i("announce recovery", "success");
                                CommonMethods.hideProgressDialog(progressDialog);
                            }
                            else {
                                Toast.makeText(AnnounceRecovery.this, "Cannot announce your recovery :( Please try again later.", Toast.LENGTH_SHORT).show();
                                Log.i("announce recovery", "failed");
                                CommonMethods.hideProgressDialog(progressDialog);
                            }
                        }
                    });
                }
            }, 5100);

            //CommonMethods.hideProgressDialog(progressDialog);

        }//end if

        else {
            Log.i("else", "entered");
            Toast.makeText(AnnounceRecovery.this, "You're not a COVID-19 patient!", Toast.LENGTH_SHORT).show();
            CommonMethods.hideProgressDialog(progressDialog);
        }

            //}
        //}, 6911);




    }// end announceRecovery method


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce_recovery);

        progressDialog = new ProgressDialog(AnnounceRecovery.this);
        //exists2 = checkIfPatientExists();
        checkIfPatientExists();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem register=menu.findItem(R.id.registerMI);
        MenuItem announceRecovery=menu.findItem(R.id.recovered);
        MenuItem signOut = menu.findItem(R.id.sign_out);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.registerMI:
                Intent intent = new Intent(getApplicationContext(), RegisterAsCovid19PatientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.recovered:
                Intent intent1 = new Intent(getApplicationContext(), AnnounceRecovery.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;
            case R.id.sign_out:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("checkuser","" + user.getEmail());
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Log.d("signout","successful");

                    SharedPreferences sharedPreferences;
                    sharedPreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("key", 0);
                    editor.apply();

                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent2);
                }else {
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;

    }

    /*public void removeFromPatients() {
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("COVID19Patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference3.removeValue();

    }*/

    /*public void announceRecoveryy(View view){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("COVID19Patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    moveToRecoveredPatients();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeFromPatients();
                        }
                    }, 2700);
                   //removeFromPatients();
                }
                else {
                    Toast.makeText(announceRecovery.this, "You are not registered as a COVID19 patient!", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }*/



    //@RequiresApi(api = Build.VERSION_CODES.O)
   // public void moveToRecoveredPatients() {
        /*final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("COVID19Patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("RecoveredCOVID19Patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        moveFirebaseRecord(reference,reference2);*/
        //reference.removeEventListener(this);
      /* reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Name = snapshot.child(id).child("Name").getValue(String.class);
                Age = snapshot.child(id).child("Age").getValue(String.class);
                Phone = snapshot.child(id).child("Phone").getValue(String.class);
                Longitude = snapshot.child(id).child("Longitude").getValue(String.class);
                Latitude = snapshot.child(id).child("Latitude").getValue(String.class);
                Blood_Type = snapshot.child(id).child("Blood_Type").getValue(String.class);

               // final Covid19Patient recoveredPatient = new Covid19Patient(Name, Age, Phone, Longitude, Latitude, Blood_Type, date);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    //}



    /*public void moveFirebaseRecord(DatabaseReference fromPath, final DatabaseReference toPath)
    {
        fromPath.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        return;
                    }
                });
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/



} //end activity