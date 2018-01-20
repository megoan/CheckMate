package com.example.user.check_mate.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoritesActivity extends AppCompatActivity {
    BackEndFunc backEndFunc = FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
    RecyclerView personRecyclerView;
    FavoritePersonAdapter personAdapter;
    LinearLayoutManager linearLayoutManager;
    int selectedPosition = -1;

    String[] people3;
    Events event;
    String eventName = "Check Mate";
    String eventID;
    String eventCountry;
    String eventCity;
    CircleImageView mCircleImageView;
    Person person;
    File localFile = null;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
       /* getSupportActionBar().setTitle(eventName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        personRecyclerView = findViewById(R.id.recyclerView);
        personRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personRecyclerView.setLayoutManager(linearLayoutManager);
        personAdapter = new FavoritePersonAdapter(this);
        personRecyclerView.setAdapter(personAdapter);
        getSupportActionBar().setTitle(R.string.my_favorites);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_manu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class FavoritePersonAdapter extends RecyclerView.Adapter<FavoritePersonAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private Context context;


        public FavoritePersonAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            this.context = context;

        }


        @Override
        public void onBindViewHolder(final FavoritesActivity.FavoritePersonAdapter.MyViewHolder holder, final int position) {
            person = Me.favorites.get(position);
            holder.eventName.setText(Me.events.get(position));
            holder.myNane.setText(person.getName());
            holder.myAge.setText(String.valueOf(person.getAge()));
            holder.kashur.setText(person.getKashur());
            holder.loadImageProgress.setVisibility(View.VISIBLE);
            if (person.getImageUrl() != null) {
                if (person.getGender() == Gender.FEMALE) {
                    Picasso.with(FavoritesActivity.this)
                            .load(person.getImageUrl())
                            .fit()
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.woman)
                            .into(holder.circleImageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.loadImageProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    holder.loadImageProgress.setVisibility(View.GONE);
                                }
                            });
                } else {
                    Picasso.with(FavoritesActivity.this)
                            .load(person.getImageUrl())
                            .fit()
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.boy)
                            .into(holder.circleImageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.loadImageProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    holder.loadImageProgress.setVisibility(View.GONE);
                                }
                            });
                }
            }
            holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    person = Me.favorites.get(position);

                    final Dialog mDialog = new Dialog(FavoritesActivity.this);
                    int p = position;
                    mDialog.setContentView(R.layout.single_picture);
                    ImageView imageView2 = mDialog.findViewById(R.id.close);
                    ImageView showImage = mDialog.findViewById(R.id.goProDialogImage);
                    final ProgressBar progressBar = mDialog.findViewById(R.id.loadImage);

                    if (person.getGender() == Gender.FEMALE) {
                        Picasso.with(FavoritesActivity.this)
                                .load(person.getImageUrl())
                                .fit()
                                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.woman)

                                .into(showImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        Picasso.with(FavoritesActivity.this)
                                .load(person.getImageUrl())
                                .fit()
                                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.boy)
                                .into(showImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
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
            holder.removeFromFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    person=Me.favorites.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                    builder.setTitle(R.string.delete_from_favorites);
                    builder.setMessage(getString(R.string.are_you_sure_you_want_to_remove)+person.getName()+getString(R.string.from_favorites));
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Me.removeFromFavorites(person.get_id(), eventName);
                            addStringToArrayAndSaveToInternalStorage();
                            notifyDataSetChanged();
                            Toast.makeText(FavoritesActivity.this, person.getName() +" "+getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    person = Me.favorites.get(position);

                }
            });
            holder.sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    person = Me.favorites.get(position);
                    //new DownloadImage().execute();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(FavoritesActivity.this);
                    builder2.setTitle(R.string.send_info);
                    // add a radio button list
                    String[] options = {getString(R.string.send_info_to_friend), getString(R.string.get_more_info)};
                    int checkedItem = 0; // cow
                    builder2.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user checked an item
                        }
                    });
                    // add OK and Cancel buttons
                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                            if (checkedItem == getString(R.string.send_info_to_friend)) {
                                message = getString(R.string.hi_what_do_you_think);

                            } else {
                                message = getString(R.string.hi_i_saw) + person.getName() + getString(R.string.at_the_wedding_can_you_send_more_info_please);
                            }
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageReference = storage.getReference().child("images").child(person.get_id());
                            try {
                                File outputDir = FavoritesActivity.this.getExternalCacheDir();
                                localFile = File.createTempFile("images", ".jpg", outputDir);
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
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
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
                                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                                        startActivity(emailIntent);
                                        try {
                                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                            finish();
                                            Log.i("Finished Data", "");
                                        } catch (android.content.ActivityNotFoundException ex2) {
                                            Toast.makeText(FavoritesActivity.this,
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
                    builder2.setNegativeButton("Cancel", null);
                    // create and show the alert dialog
                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();

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
        public FavoritesActivity.FavoritePersonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.favorite_card_layout, parent, false);
            FavoritesActivity.FavoritePersonAdapter.MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public int getItemCount() {
            return Me.favorites.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView myNane;
            TextView myAge;
            TextView kashur;
            CircleImageView circleImageView;
            ImageView sendImage;
            ImageView removeFromFavorite;
            TextView eventName;
            ProgressBar loadImageProgress;

            public MyViewHolder(View itemView) {
                super(itemView);
                myNane = (TextView) itemView.findViewById(R.id.myName);
                myAge = (TextView) itemView.findViewById(R.id.myAge);
                kashur = (TextView) itemView.findViewById(R.id.kashur);
                sendImage = (ImageView) itemView.findViewById(R.id.sendImage);
                circleImageView = (CircleImageView) itemView.findViewById(R.id.imageView);
                loadImageProgress=(ProgressBar)itemView.findViewById(R.id.loadImageProgress);
                mCircleImageView = circleImageView;
                removeFromFavorite = (ImageView) itemView.findViewById(R.id.removeFavorite);
                eventName = (TextView)itemView.findViewById(R.id.eventName);
                if (CheckRTL.isRTL()) {
                    sendImage.setScaleX(-1);
                }
            }

        }
    }

    private void addStringToArrayAndSaveToInternalStorage() {
        String favorites = "";
        for (Person person : Me.favorites) {
            favorites += convertFavoriteToString(person);
        }

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput("favorite.txt", Context.MODE_PRIVATE);
            outputStream.write(favorites.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertFavoriteToString(Person me) {
        int favoriteNumber = Me.favorites.size();
        String favorite = "favorite_" + (favoriteNumber + 1) + "~";
        favorite += me.getName() + "~";
        favorite += me.getAge() + "~";
        favorite += me.getKashur() + "~";
        favorite += me.getGender() + "~";
        favorite += me.get_id() + "~";
        favorite += eventName + "~~";
        return favorite;
    }
}
