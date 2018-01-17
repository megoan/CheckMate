package com.example.user.check_mate.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MainActivity extends AppCompatActivity {
    BackEndFunc backEndFunc = FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
    BackEndFunc backEndFuncForFirebase = FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);
    ImageView btnLoc;
    TextView locationText;
    RecyclerView eventRecyclerView;
    FrameLayout frameLayout;
    Button refreshGPSButton;
    Button addEventButton;
    Location myLocation;
    Location tmpLocation = null;
    ArrayList<Events> events=new ArrayList<>();
    EventAdapter eventAdapter;
    AlertDialog alertDialog;
    LinearLayoutManager linearLayoutManager;
    String userID;
    Events myEvents;
    private ProgressDialog progDailog;
    String id;
    //private Bitmap bitmap;
    FirebaseStorage storage;
    boolean stillLoading = false;
    MyLocation mLocation = new MyLocation();

    DatabaseReference eventsDataBase;

    LocationManager locationManagerGPS;
   // LocationManager locationManagerINTERNET;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setGPS();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Events> e = (ArrayList<Events>) ListDataSource.eventsList;
        btnLoc = (ImageView) findViewById(R.id.btnGetLoc);
        eventRecyclerView = findViewById(R.id.events);
        locationText = findViewById(R.id.locationText);
        addEventButton = findViewById(R.id.addEvent);
        frameLayout = findViewById(R.id.refreshGPS);
        eventRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventRecyclerView.setLayoutManager(linearLayoutManager);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        locationManagerGPS = (LocationManager) getSystemService(LOCATION_SERVICE);
        eventAdapter=new EventAdapter(this,events);
        eventRecyclerView.setAdapter(eventAdapter);
        //locationManagerINTERNET = (LocationManager) getSystemService(LOCATION_SERVICE);
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
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        } else {
            setGPS();
        }


        //locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);















       /* LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled)
        {
            buildAlertMessageNoGps();
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/
        
        
        
        
        /*Intent intent = getIntent();
        id = intent.getStringExtra("id");
        if (id != null) {
            stillLoading = true;
            storage = FirebaseStorage.getInstance();
            new getUserAsync().execute();
        }*/

        //openGPS();
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openGPS();
                if (myLocation != null) {
                    //frameLayout.setVisibility(View.GONE);
                    locationManagerGPS.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    //locationManagerINTERNET.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, locationListener);
                    // initializeRecyclerView();
                    // new BackGroundLoadEvents().execute();
                }
            }
        });

        /*if (myLocation != null) {
            mLocation.setLatitude(myLocation.getLatitude());
            mLocation.setLongitude(myLocation.getLongitude());
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                mLocation.setCountry(addresses.get(0).getCountryName());
                mLocation.setCity(addresses.get(0).getLocality());
                frameLayout.setVisibility(View.GONE);
               // initializeRecyclerView();
            }

           // new BackGroundLoadEvents().execute();
           // initializeRecyclerView();
        }
*/
        userID = Me.ME.get_id();

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEvents = new Events();

                myEvents.setmMyLocation(mLocation);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.add_event_dialog, null);
                final EditText editText = mView.findViewById(R.id.eventNameView);
                Button button = mView.findViewById(R.id.addEventbutton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString() == null || editText.getText().toString().length() == 0) {
                            Toast.makeText(getApplicationContext(), "can't leave event name empty!", Toast.LENGTH_LONG).show();
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


                                //TODO add event
                                BackEndFunc backEndFuncforFirebase = FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);


                                backEndFuncforFirebase.addEvent(myEvents);

                                alertDialog.dismiss();
                                //initializeRecyclerView();
                                //initializeAdapter();
                            } catch (Exception e1) {
                                e1.printStackTrace();
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
        locationManagerGPS.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //locationManagerINTERNET.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, locationListener);
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
            }

            break;
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

    public void openGPS() {
        GpsTracker gt = new GpsTracker(getApplicationContext(), MainActivity.this);
        tmpLocation = gt.getLocation();

        if (tmpLocation != null && myLocation != null && tmpLocation.distanceTo(myLocation) > 200) {
            //new BackGroundLoadEvents().execute();
        }
        myLocation = tmpLocation;
        if (myLocation == null) {
            Toast.makeText(getApplicationContext(), "GPS unable to get Value please try again soon!", Toast.LENGTH_SHORT).show();
        } else {
            mLocation.setLatitude(myLocation.getLatitude());
            mLocation.setLongitude(myLocation.getLongitude());
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                mLocation.setCountry(addresses.get(0).getCountryName());
                mLocation.setCity(addresses.get(0).getLocality());
                String address = addresses.get(0).getAddressLine(0);
                locationText.setText(address);
                //initializeRecyclerView();
                //new BackGroundLoadEvents().execute();
                frameLayout.setVisibility(View.GONE);
                //initializeRecyclerView();
                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "your in a bad location!", Toast.LENGTH_SHORT).show();
            }
        }
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
                                    Toast.makeText(MainActivity.this, "can't leave field empty!", Toast.LENGTH_LONG).show();
                                } else {
                                    if (Me.ME.isAtEvent()) {


                                        backEndFuncForFirebase.removePersonFromEvent(Me.ME.getEventId(), Me.ME.get_id());
                                    }

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
                                    Me.ME.setEventId(event.getFirebaseID());
                                    Me.ME.setKashur(connectedView.getText().toString());
                                    Me.ME.setAtEvent(true);

                                    backEndFuncForFirebase.addPersonToEvent(event, Me.ME);
                                    backEndFuncForFirebase.updatePerson(Me.ME);
                                    //backEndFunc.updateEvent(event);
                                    //backEndFunc.updatePerson(Me.ME);
                                    alertDialog.dismiss();
                                    startActivity(intent);
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

    public void initializeRecyclerView(final MyLocation myLocation) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("events").child(myLocation.getCountry()).child(myLocation.getCity());
        final List<Events> connectedEvents = new ArrayList<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot item : dataSnapshot.getChildren()) {
                    Events events = item.getValue(Events.class);
                    connectedEvents.add(events);
                }
                events = backEndFuncForFirebase.getAllEventsByLocationDistance(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, (ArrayList<Events>) connectedEvents);
                eventAdapter = new EventAdapter(MainActivity.this, events);

                //eventAdapter.notifyDataSetChanged();
                eventRecyclerView.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class BackGroundLoadEvents extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("we found you :) wait a moment till we upload the events in your area");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();*/
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //initializeRecyclerView();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           /* eventRecyclerView.setLayoutManager(linearLayoutManager);
            eventRecyclerView.setAdapter(eventAdapter);
            progDailog.dismiss();*/
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit Check Mate");
        builder.setMessage("are you sure u want to leave? ");
        builder.setPositiveButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void initializeAdapter() {
        eventRecyclerView.setLayoutManager(linearLayoutManager);
        eventRecyclerView.setAdapter(eventAdapter);
    }



    public void foo(Context context) {
        // when you need location
        // if inside activity context = this;

        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                    }
                });
    }
    // Acquire a reference to the system Location Manager


    public void makeUseOfNewLocation(Location location) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        MyLocation myLocation = new MyLocation();

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            myLocation.setCountry(addresses.get(0).getCountryName());
            myLocation.setCity(addresses.get(0).getLocality());
            myLocation.setLatitude(location.getLatitude());
            myLocation.setLongitude(location.getLongitude());

            String address = addresses.get(0).getAddressLine(0);
            locationText.setText(address);
            //initializeRecyclerView();
            //new BackGroundLoadEvents().execute();
            //frameLayout.setVisibility(View.GONE);
            getEvents(myLocation);
            //new LoadEvents(myLocation).execute();
//            initializeRecyclerView(myLocation);
            locationManagerGPS.removeUpdates(locationListener);
            //locationManagerINTERNET.removeUpdates(locationListener);
        }
    }

    public class LoadEvents extends AsyncTask<Void,Events,Void>
    {
        MyLocation myLocation;

        public LoadEvents(MyLocation myLocation) {
            this.myLocation = myLocation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Events... values) {
            super.onProgressUpdate(values);
            eventAdapter.events.add(values[0]);
            eventAdapter.notifyDataSetChanged();

            //frameLayout.setVisibility(View.GONE);
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
                    for (final DataSnapshot item : dataSnapshot.getChildren()) {
                        Events events = item.getValue(Events.class);
                        events=backEndFuncForFirebase.getEventByLocation(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, events);
                        if(events!=null){
                            publishProgress(events);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //events = backEndFuncForFirebase.getAllEventsByLocationDistance(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, (ArrayList<Events>) connectedEvents);
                    // eventAdapter = new EventAdapter(MainActivity.this, events);
                    //eventAdapter.notifyDataSetChanged();
                    // eventRecyclerView.setAdapter(eventAdapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

    public void getEvents(final MyLocation myLocation)
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("events").child(myLocation.getCountry()).child(myLocation.getCity());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot item : dataSnapshot.getChildren()) {
                    Events events = item.getValue(Events.class);
                    events=backEndFuncForFirebase.getEventByLocation(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, events);
                    if(events!=null){
                        eventAdapter.events.add(events);

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                frameLayout.setVisibility(View.GONE);
                                eventAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                }
                frameLayout.setVisibility(View.GONE);
                //events = backEndFuncForFirebase.getAllEventsByLocationDistance(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, (ArrayList<Events>) connectedEvents);
                // eventAdapter = new EventAdapter(MainActivity.this, events);
                //eventAdapter.notifyDataSetChanged();
                // eventRecyclerView.setAdapter(eventAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

