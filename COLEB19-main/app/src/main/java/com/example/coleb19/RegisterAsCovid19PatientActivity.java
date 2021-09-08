package com.example.coleb19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;

public class RegisterAsCovid19PatientActivity extends AppCompatActivity implements LocationListener {



    static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    EditText age,fName,pNumber;
    RadioGroup bloodType,rhesus;
    String lat = "";
    String lon = "";
    LocationManager locationManager;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_covid19_patient);

        Button getCurrentLocation = findViewById(R.id.rGetLocation);
        getCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();

            }
        });

        progressDialog = new ProgressDialog(RegisterAsCovid19PatientActivity.this);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.registerMI).setVisible(false);
        MenuItem announceRecovery=menu.findItem(R.id.recovered);
        MenuItem signOut = menu.findItem(R.id.sign_out);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recovered:
                Intent intent = new Intent(getApplicationContext(), AnnounceRecovery.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.sign_out:
                //Log.d("logout1","accessed");
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

                    Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }else {
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;

    }





    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onLocationChanged(Location location) {
        lat=String.valueOf(location.getLatitude());
        lon=String.valueOf(location.getLongitude());
   // Toast.makeText(getApplicationContext(),"Your location was successfully obtained!"+lat,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }




    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("COLEB19 requires permission to access your current location.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterAsCovid19PatientActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show();

                getLocation();
            } else {
                Toast.makeText(this, "Can't access your location because the permission for location access was denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Register(View view) {

        age = (EditText) findViewById(R.id.rAge);
        fName = (EditText) findViewById(R.id.rFullName);
        pNumber = (EditText) findViewById(R.id.rPhoneNo);
        bloodType = (RadioGroup) findViewById(R.id.radioGroup);
        rhesus = (RadioGroup) findViewById(R.id.radioGroup2);
        String ageValue = age.getText().toString();
        String fNameValue = fName.getText().toString();
        String pNumberValue = pNumber.getText().toString();
        int bType = bloodType.getCheckedRadioButtonId();
        int Rhesus = rhesus.getCheckedRadioButtonId();
        String bloodTypeValue = "";




        if (bType == R.id.radioButtonA) {
            bloodTypeValue = "A";
        } else if (bType == R.id.radioButtonB) {
            bloodTypeValue = "B";
        } else if (bType == R.id.radioButtonAB) {
            bloodTypeValue = "AB";
        } else if (bType == R.id.radioButtonO) {
            bloodTypeValue = "O";
        }
        if (Rhesus == R.id.radioButtonP) {
            bloodTypeValue = bloodTypeValue + "+";

        } else if (Rhesus == R.id.radioButtonN) {
            bloodTypeValue = bloodTypeValue + "-";

        }
        if (fNameValue.isEmpty()) {
            fName.setError("Name is required!");
            fName.requestFocus();
            return;
        }
        if (ageValue.isEmpty()) {
            age.setError("Age is required!");
            age.requestFocus();
            return;
        }
        if (pNumberValue.isEmpty()) {
            pNumber.setError("Phone Number is required!");
            pNumber.requestFocus();
            return;
        }
        if (bType == -1) {
            Toast.makeText(this, "Make sure to select your blood type!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Rhesus == -1) {
            Toast.makeText(this, "Make sure to select your Rhesus group!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lat.equals("") && lon.equals("")) {
            Toast.makeText(this, "Your current location is required", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonMethods.displayLoadingScreen(progressDialog);

        final Covid19Patient patient = new Covid19Patient(fNameValue, ageValue, pNumberValue, lon, lat, bloodTypeValue, String.valueOf(LocalDate.now()));


        databaseReference = FirebaseDatabase.getInstance().getReference().child("COVID19Patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterAsCovid19PatientActivity.this, "You was successfully registered as COVID19 patient!", Toast.LENGTH_SHORT).show();
                    CommonMethods.hideProgressDialog(progressDialog);
                }
                else {
                    Toast.makeText(RegisterAsCovid19PatientActivity.this, "Failed to register you as a COVID-19 patient. Please try again later.", Toast.LENGTH_SHORT).show();
                    CommonMethods.hideProgressDialog(progressDialog);
                }
            }
        });
    }

}


