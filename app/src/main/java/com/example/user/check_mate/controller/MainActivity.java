package com.example.user.check_mate.controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.backend.BackEndFunc;
import com.example.user.check_mate.model.backend.DataSourceType;
import com.example.user.check_mate.model.backend.FactoryMethod;
import com.example.user.check_mate.model.backend.SelectedDataSource;
import com.example.user.check_mate.model.datasource.ListDataSource;
import com.example.user.check_mate.model.entities.Events;
import com.example.user.check_mate.model.entities.MyDate;
import com.example.user.check_mate.model.entities.MyLocation;
import com.example.user.check_mate.model.entities.Person;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    BackEndFunc backEndFunc = FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
    BackEndFunc backEndFuncForFirebase = FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);
    ImageView btnLoc;
    TextView locationText;
    RecyclerView eventRecyclerView;
    FrameLayout frameLayout;

    Button addEventButton;
    Location myLocation;
    Location tmpLocation = null;
    ArrayList<Events> events = new ArrayList<>();
    EventAdapter eventAdapter;
    AlertDialog alertDialog;
    LinearLayoutManager linearLayoutManager;
    String userID;
    Events myEvents;
    private ProgressDialog progDailog;
    String id;
    Events events3;
    LinearLayout linearLayout;

    FrameLayout frameLayoutGPSTURNON;
    Button turnOnGPSButton;
    //private Bitmap bitmap;
    FirebaseStorage storage;
    boolean stillLoading = false;
    MyLocation mLocation = new MyLocation();

    DatabaseReference eventsDataBase;

    LocationManager locationManagerInternet;
    LocationManager locationManagerGPS;
    LocationListener locationListener;

    MyLocation myOwnLocation = new MyLocation();

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_RQUEST = 7172;
    private boolean mRequestLocationUpdates = true;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                    }
                    //setGPS();

                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }


        ArrayList<Events> e = (ArrayList<Events>) ListDataSource.eventsList;
        btnLoc = (ImageView) findViewById(R.id.btnGetLoc);
        eventRecyclerView = findViewById(R.id.events);
        locationText = findViewById(R.id.locationText);
        addEventButton = findViewById(R.id.addEvent);
        frameLayout = findViewById(R.id.refreshGPS);
        frameLayoutGPSTURNON=findViewById(R.id.turnOnGpsLayout);
        turnOnGPSButton=findViewById(R.id.turnOnGpsButton);
        linearLayout = findViewById(R.id.loadingEvents);

        eventRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventRecyclerView.setLayoutManager(linearLayoutManager);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        locationManagerInternet = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) getSystemService(LOCATION_SERVICE);
        eventAdapter = new EventAdapter(this, events);
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                linearLayout.setVisibility(View.GONE);
                eventRecyclerView.removeOnLayoutChangeListener(this);
            }
        });
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
                return;
            }
        } else {
            //setGPS();
        }

        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOnGPS();
                if (googleApiClient != null) {
                    googleApiClient.connect();
                }

            }
        });

        userID = Me.ME.get_id();

        turnOnGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=0;
                i++;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, MY_PERMISSION_REQUEST_CODE);
                } else {
                    switchOnGPS();
                }

               // if( mRequestLocationUpdates){

               // }
            }
        });


        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEvents = new Events();

                myEvents.setmMyLocation(myOwnLocation);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.add_event_dialog, null);
                final EditText editText = mView.findViewById(R.id.eventNameView);
                Button button = mView.findViewById(R.id.addEventbutton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString() == null || editText.getText().toString().length() == 0) {
                            Toast.makeText(getApplicationContext(), R.string.dont_leave_name_empty, Toast.LENGTH_LONG).show();
                        } else {
                            myEvents.setEventName(editText.getText().toString());
                            try {

                                Date date = Calendar.getInstance().getTime();
                                MyDate myDate = new MyDate();
                                myDate.setDay(date.getDay());
                                myDate.setMonth(date.getMonth());
                                myDate.setYear(date.getYear());
                                myDate.setHour(date.getHours());
                                myDate.setMinute(date.getMinutes());
                                myDate.setSecond(date.getSeconds());
                                myEvents.setmDate(myDate);

                                BackEndFunc backEndFuncforFirebase = FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);

                                backEndFuncforFirebase.addEvent(myEvents);
                                alertDialog.dismiss();
                                eventAdapter.events.add(myEvents);
                                eventAdapter.notifyDataSetChanged();
                                //new LoadEvents(myOwnLocation).execute();


                            } catch (Exception e1) {
                                e1.printStackTrace();
                                alertDialog.dismiss();
                            }
                        }
                    }
                });
                mBuilder.setView(mView);
                alertDialog = mBuilder.create();
                alertDialog.show();
            }
        });

    }


    public void setGPS() {
        // locationManagerInternet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //locationManagerInternet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_manu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_item: {
                Intent intentProfile = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(intentProfile);
                break;
            }
            case R.id.favorites: {

                Intent intentProfile = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intentProfile);
                break;
            }

            case R.id.contact: {

                Intent intentProfile = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intentProfile);
                break;
            }
            case R.id.delete_account:{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle(R.string.delete_account);

                builder.setMessage(R.string.delete_account_message);

                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        backEndFuncForFirebase.deletePerson(Me.ME.get_id(),MainActivity.this);


                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode)) {
                GooglePlayServicesUtil.getErrorDialog(resultcode, this, PLAY_SERVICES_RESOLUTION_RQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "this device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void getGoogleLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            makeUseOfNewLocation(mLastLocation);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getGoogleLocation();
        if (mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        getGoogleLocation();
    }


    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private Context context;
        public ArrayList<Events> events;

        public EventAdapter(Context context, ArrayList<Events> events) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.events = events;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.event_card_layout, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Events event = events.get(position);
            final String evntN = events.get(position).getEventName();
            holder.singleEvent.setText(evntN);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Me.ME.getEventId() != null && Me.ME.getEventId().equals(event.getFirebaseID())) {
                        Intent intent = new Intent(MainActivity.this, MyEventActivity.class);
                        intent.putExtra("id", event.getFirebaseID());
                        intent.putExtra("name", event.getEventName());
                        Me.ME.setEventId(event.getFirebaseID());
                        Me.ME.setAtEvent(true);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.join_event_dialog, null);
                        TextView joinButton = mView.findViewById(R.id.joinView);
                        TextView cancelButton = mView.findViewById(R.id.cancelView);
                        TextView eventName = mView.findViewById(R.id.eventNameView);
                        final EditText connectedView = mView.findViewById(R.id.connectedView);
                        eventName.setText(evntN);
                        joinButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (connectedView.getText().toString() == null || connectedView.getText().toString().length() == 0) {
                                    Toast.makeText(MainActivity.this, R.string.cant_leave_field_empty, Toast.LENGTH_LONG).show();
                                } else {
                                    alertDialog.dismiss();
                                    ProgressDialog progDailog = new ProgressDialog(MainActivity.this);
                                    progDailog.setMessage(getString(R.string.joining_event));
                                    progDailog.setIndeterminate(false);
                                    progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progDailog.setCancelable(false);
                                    progDailog.show();
                                    Intent intent = new Intent(MainActivity.this, MyEventActivity.class);
                                    intent.putExtra("id", event.getFirebaseID());
                                    intent.putExtra("name", event.getEventName());
                                    intent.putExtra("country", event.getmMyLocation().getCountry());
                                    intent.putExtra("city", event.getmMyLocation().getCity());
                                    String[] array = event.getPeopleAtevent().toArray(new String[event.getPeopleAtevent().size() + 1]);
                                    for (int i = 0; i < event.getPeopleAtevent().size(); i++) {
                                        array[i] = event.getPeopleAtevent().get(i);
                                    }
                                    array[event.getPeopleAtevent().size()] = Me.ME.get_id();
                                    intent.putExtra("atEvent", array);


                                    if (Me.ME.isAtEvent()) {
                                        backEndFuncForFirebase.removeAndAddPersonToEventAndEnterActivity(Me.ME,event,MainActivity.this,intent,progDailog);
                                    }
                                    else {
                                        backEndFuncForFirebase.addPersonToEventAndEnterActivity(event,Me.ME,MainActivity.this,intent,progDailog);
                                    }
                                    Me.ME.setEventId(event.getFirebaseID());
                                    Me.ME.setKashur(connectedView.getText().toString());
                                    Me.ME.setAtEvent(true);
                                    Me.ME.setEventCountry(event.getmMyLocation().getCountry());
                                    Me.ME.setEventCity(event.getmMyLocation().getCity());
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("ID", Me.ME.get_id());
                                    editor.putString("NAME", Me.ME.getName());
                                    editor.putString("GENDER", String.valueOf(Me.ME.getGender()));
                                    editor.putInt("AGE", Me.ME.getAge());
                                    editor.putString("IMAGEURL", Me.ME.getImageUrl());
                                    editor.putBoolean("ATEVENT", Me.ME.isAtEvent());
                                    editor.putString("EVENTID", Me.ME.getEventId());
                                    editor.putString("KASHUR", Me.ME.getKashur());
                                    editor.putString("EVENTCOUNTRY",Me.ME.getEventCountry());
                                    editor.putString("EVENTCITY",Me.ME.getEventCity());
                                    editor.commit();
                                }
                            }
                        });
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });
                        mBuilder.setView(mView);
                        alertDialog = mBuilder.create();
                        alertDialog.show();
                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView singleEvent;

            public MyViewHolder(View itemView) {
                super(itemView);
                singleEvent = (TextView) itemView.findViewById(R.id.text1);
            }
        }
    }



    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(R.string.exit_check_mate);

        builder.setMessage(R.string.are_you_sure_you_want_to_leave);

        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ExitActivity.exitApplicationAndRemoveFromRecent(MainActivity.this);
                finish();
                //System.exit(0);

            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void makeUseOfNewLocation(Location location) {
        List<Address> addresses = null;
        List<Address> addressesEnglish = null;
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        Geocoder geocoderEnglish = new Geocoder(MainActivity.this, Locale.ENGLISH);


        try {
            addressesEnglish = geocoderEnglish.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0 && addressesEnglish != null && addressesEnglish.size() > 0) {
            myOwnLocation.setCountry(addressesEnglish.get(0).getCountryName());
            myOwnLocation.setCity(addressesEnglish.get(0).getLocality());
            myOwnLocation.setLatitude(location.getLatitude());
            myOwnLocation.setLongitude(location.getLongitude());

            String address = addresses.get(0).getAddressLine(0);
            locationText.setText(address);
            new LoadEvents(myOwnLocation).execute();
            locationManagerInternet.removeUpdates(locationListener);


            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            if (googleApiClient != null) {
                googleApiClient.disconnect();
            }
        }
    }

    public class LoadEvents extends AsyncTask<Void, Events, Void> {
        MyLocation myLocation;

        public LoadEvents(MyLocation myLocation) {
            this.myLocation = myLocation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayout.setVisibility(View.VISIBLE);
            eventAdapter.events.clear();
        }

        @Override
        protected void onProgressUpdate(Events... values) {
            super.onProgressUpdate(values);
            if(values[0].getFirebaseID().equals("there are no events"))
            {
                linearLayout.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, R.string.there_are_no_events_in_your_location,Toast.LENGTH_LONG).show();
                return;
            }
            if (values[0] != null) {
                for (Events events5 : eventAdapter.events) {
                    if (events5.getFirebaseID().equals(values[0].getFirebaseID())) {
                        return;
                    }
                }
                eventAdapter.events.add(values[0]);
            }
            eventAdapter.notifyDataSetChanged();
            linearLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            frameLayout.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference("events").child(myLocation.getCountry()).child(myLocation.getCity());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String empty = "there are no events";
                    int i = 0;
                    for (final DataSnapshot item : dataSnapshot.getChildren()) {
                        events3 = item.getValue(Events.class);
                        events3 = backEndFuncForFirebase.getEventByLocation(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, events3);
                        if (events != null) {
                            i++;
                            publishProgress(events3);
                        }
                    }
                    if (i == 0) {
                        Events emptyEvent=new Events();
                        emptyEvent.setFirebaseID(empty);
                        publishProgress(emptyEvent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

    @Override
    protected void onStart() {
        switchOnGPS();
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        Log.d("Tag","sdfkjdsfgkdljhgdgfsgdfgjdskfhgdsfgsfdgfdg");
        // getGoogleLocation();
    }

    @Override
    protected void onStop() {

        if (googleApiClient!=null) {
            if(googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                if (googleApiClient != null) {
                    googleApiClient.disconnect();
                }
            }
        }
        Log.d("Tag","sdfkjdsfgkdljhgdgfsgdfgjdskfhgdsfgsfdgfdg");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Tag","sdfkjdsfgkdljhgdgfsgdfgjdskfhgdsfgsfdgfdg");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case 11:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        frameLayoutGPSTURNON.setVisibility(View.GONE);
                        // All required changes were successfully made
                        Toast.makeText(this, R.string.location_enabled_by_user, Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, R.string.location_not_enabled_user_cancelled, Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }
    private void switchOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    mRequestLocationUpdates = true;
                    frameLayoutGPSTURNON.setVisibility(View.GONE);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:{
                            frameLayoutGPSTURNON.setVisibility(View.GONE);
                            break;
                        }
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            try {
                                resolvableApiException.startResolutionForResult(MainActivity.this, 11);

                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //open setting and switch on GPS manually
                            break;
                    }
                }
            }
        });
        //Give permission to access GPS
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);

    }
}

