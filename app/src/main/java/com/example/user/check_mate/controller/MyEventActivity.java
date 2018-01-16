package com.example.user.check_mate.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.backend.BackEndFunc;
import com.example.user.check_mate.model.backend.FactoryMethod;
import com.example.user.check_mate.model.backend.SelectedDataSource;
import com.example.user.check_mate.model.entities.Events;
import com.example.user.check_mate.model.entities.Gender;
import com.example.user.check_mate.model.entities.Person;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyEventActivity extends AppCompatActivity {
    BackEndFunc backEndFunc = FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
    RecyclerView personRecyclerView;
    UploadPersonAdapter personAdapter;
    LinearLayoutManager linearLayoutManager;
    int selectedPosition=-1;
    ArrayList<Person> people = new ArrayList<>();
    String[] people3;
    Events event;
    String eventName = "Check Mate";
    String eventID;
    String eventCountry;
    String eventCity;
    CircleImageView mCircleImageView;
    Person person;
    File localFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        Intent intent = getIntent();


        if (intent != null) {
            eventName = intent.getStringExtra("name");
            eventID = intent.getStringExtra("id");
            eventCountry = intent.getStringExtra("country");
            eventCity = intent.getStringExtra("city");
            people3 = intent.getStringArrayExtra("atEvent");
        }
        getSupportActionBar().setTitle(eventName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personRecyclerView = findViewById(R.id.user_recyclerView);
        personRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personRecyclerView.setLayoutManager(linearLayoutManager);
        personAdapter = new MyEventActivity.UploadPersonAdapter(this, people);
        personRecyclerView.setAdapter(personAdapter);
        //initializeRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_manu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.profile_item: {
                Intent intentProfile = new Intent(MyEventActivity.this, EditProfileActivity.class);
                startActivity(intentProfile);
            }
            case R.id.refresh_list: {
               initializeRecyclerView();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class UploadPersonAdapter extends RecyclerView.Adapter<UploadPersonAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private Context context;


        public UploadPersonAdapter(Context context, ArrayList<Person> people2) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            people = people2;
        }

        @Override
        public UploadPersonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.person_card_layout, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            person = people.get(position);
            holder.myNane.setText(person.getName());
            holder.myAge.setText(String.valueOf(person.getAge()));
            holder.kashur.setText(person.getKashur());
            if (person.getImageUrl() != null) {
                if(person.getGender()== Gender.FEMALE){
                    Picasso.with(MyEventActivity.this)
                            .load(person.getImageUrl())
                            .fit()
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.woman)
                            .into(holder.circleImageView);
                }
                else {
                    Picasso.with(MyEventActivity.this)
                            .load(person.getImageUrl())
                            .fit()
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.boy)
                            .into(holder.circleImageView);
                }
            }
            holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    person = people.get(position);
                    final Dialog mDialog = new Dialog(MyEventActivity.this);
                    int p=position;
                    mDialog.setContentView(R.layout.single_picture);
                    ImageView imageView2 = mDialog.findViewById(R.id.close);
                    ImageView showImage = mDialog.findViewById(R.id.goProDialogImage);
                    if(person.getGender()== Gender.FEMALE){
                        Picasso.with(MyEventActivity.this)
                                .load(person.getImageUrl())
                                .fit()
                                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.woman)
                                .into(showImage);
                    }
                    else {
                        Picasso.with(MyEventActivity.this)
                                .load(person.getImageUrl())
                                .fit()
                                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.boy)
                                .into(showImage);
                    }
                    //showImage.setImageBitmap(person.getImg().getBitmap());
                    imageView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mDialog.show();

                }
            });

            holder.sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    person = people.get(position);
                    //new DownloadImage().execute();
                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    StorageReference storageReference=storage.getReference().child("images").child(person.get_id());
                    try {
                        File outputDir = MyEventActivity.this.getExternalCacheDir();
                        localFile = File.createTempFile("images", ".jpg",outputDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created
                            Uri imageUri = null;
                            imageUri = Uri.fromFile(localFile);

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            //Target whatsapp:
                            shareIntent.setPackage("com.whatsapp");
                            //Add text and then Image URI
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "HI, I saw " + person.getName() + " at the wedding. can u send me more info please?");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.setType("image/jpeg");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            try {
                                startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {

                                Toast.makeText(getApplicationContext(), "you don't have whatsApp installed!", Toast.LENGTH_SHORT).show();
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setData(Uri.parse("mailto:"));
                                emailIntent.setType("text/html");
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi what's up?");
                                emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the details attached....");
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n" + "HI, I saw " + person.getName() + " at the wedding. can u send me more info please?");
                                startActivity(emailIntent);
                                try {
                                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                    finish();
                                    Log.i("Finished Data", "");
                                } catch (android.content.ActivityNotFoundException ex2) {
                                    Toast.makeText(MyEventActivity.this,
                                            "No way you can share Reciepies,enjoy alone :-P", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
            });

        }
        private Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap2, Picasso.LoadedFrom from) {
               /* File mFile = savebitmap(bitmap2);
                Uri imageUri = null;
                imageUri = Uri.fromFile(mFile);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                //Target whatsapp:
                shareIntent.setPackage("com.whatsapp");
                //Add text and then Image URI
                shareIntent.putExtra(Intent.EXTRA_TEXT, "HI, I saw "+person.getName()+" at the wedding. can u send me more info please?");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {

                    Toast.makeText(getApplicationContext(),"you don't have whatsApp installed!", Toast.LENGTH_SHORT).show();
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/html");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi what's up?");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the details attached....");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n" + "HI, I saw "+person.getName()+" at the wedding. can u send me more info please?");
                    startActivity(emailIntent);
                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        finish();
                        Log.i("Finished Data", "");
                    } catch (android.content.ActivityNotFoundException ex2) {
                        Toast.makeText(MyEventActivity.this,
                                "No way you can share Reciepies,enjoy alone :-P", Toast.LENGTH_SHORT).show();
                    }

                }
*/


            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };





        @Override
        public int getItemCount() {
            return people.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView myNane;
            TextView myAge;
            TextView kashur;
            CircleImageView circleImageView;
            ImageView sendImage;
            ImageView addToFavorite;

            public MyViewHolder(View itemView) {
                super(itemView);
                myNane = (TextView) itemView.findViewById(R.id.myName);
                myAge = (TextView) itemView.findViewById(R.id.myAge);
                kashur = (TextView) itemView.findViewById(R.id.kashur);
                sendImage = (ImageView) itemView.findViewById(R.id.sendImage);
                circleImageView = (CircleImageView) itemView.findViewById(R.id.imageView);
                mCircleImageView = circleImageView;
                addToFavorite=(ImageView)itemView.findViewById(R.id.addToFavorite);
            }

        }
    }

    public void initializeRecyclerView() {
        initializeList();
    }

    public void initializeList() {

        people.clear();
        DatabaseReference persontAtEvent = FirebaseDatabase.getInstance().getReference("peopleAtEvent").child(eventID);
        persontAtEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot item : dataSnapshot.getChildren()) {
                    Person person = item.getValue(Person.class);
                    people.add(person);
                    if (people.size() % 5 == 0) {
                        personAdapter.notifyDataSetChanged();
                    }
                }
                personAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //personAdapter=new MyEventActivity.UploadPersonAdapter(this,people);


        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference("events").child(mLocation.getCountry()).child(mLocation.getCity());

        final List<Events> connectedEvents = new ArrayList<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot item : dataSnapshot.getChildren()) {
                    Events events = item.getValue(Events.class);
                    connectedEvents.add(events);
                }
                events = backEndFuncForFirebase.getAllEventsByLocationDistance(new MyLocation(myLocation.getLatitude(), myLocation.getLongitude()), 200, (ArrayList<Events>) connectedEvents);
                eventAdapter = new MainActivity.EventAdapter(MainActivity.this, events);

                //eventAdapter.notifyDataSetChanged();
                eventRecyclerView.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        /*event= backEndFunc.getEvent(eventID);
        people=new ArrayList<>();
        for(String name:event.getPeopleAtevent())
        {
            people.add(backEndFunc.getPerson(name));
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
    }

    public class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        Bitmap b=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            InputStream input = null;


                /*InputStream inputStream = new URL(person.getImageUrl()).openStream();   // Download Image from URL
                Bitmap b= BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return b;*/

            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }

    }
    private File savebitmap(Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "check-mate" + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "check-mate" + ".png");
        }

        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
}
