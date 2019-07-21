package com.example.liew.ideliveryserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.liew.ideliveryserver.Common.Common;
import com.example.liew.ideliveryserver.Model.Banner;
import com.example.liew.ideliveryserver.Model.Food;
import com.example.liew.ideliveryserver.ViewHolder.BannerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.liew.ideliveryserver.Common.Common.PICK_IMAGE_REQUEST;

public class BannerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    RelativeLayout rootLayout;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference banners;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    //Add new banner
    MaterialEditText edtName, edtFoodId;
    FButton btnUpload, btnSelect;
    Banner newBanner;
    Uri filePath;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());


        setContentView(R.layout.activity_banner);

        //Init firebase
        db = FirebaseDatabase.getInstance();
        banners = db.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init View
        recyclerView = (RecyclerView)findViewById(R.id.recycler_banner);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        //fab
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddBanner();
            }
        });
        loadListBanner();
    }

    private void loadListBanner() {

        FirebaseRecyclerOptions<Banner> allBanner = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banners, Banner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(allBanner) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.banner_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(holder.banner_image);
            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_layout, parent, false);
                return  new BannerViewHolder(itemView);
            }
        };
        adapter.startListening();

        //set adapter
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showAddBanner() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this,  R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Add New Banner");
        alertDialog.setMessage("Please fill full formation");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_banner,null);

        edtFoodId = add_menu_layout.findViewById(R.id.edtFoodId);
        edtName = add_menu_layout.findViewById(R.id.edtFoodName);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //set event for select picture from phone
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        
        //set event for upload picture from phone
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_laptop_black_24dp);

        //set button for dialog
        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newBanner != null)
                   banners.push().setValue(newBanner);
                else
                    Toast.makeText(BannerActivity.this, "Failed to Create Banner", Toast.LENGTH_SHORT).show();
                loadListBanner();
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
             }
        });

        alertDialog.show();
    }

    private void uploadImage() {

        if(filePath != null){

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(BannerActivity.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //set value for newCategory if image upload and we can get download link
                            newBanner = new Banner();
                            newBanner.setId(edtFoodId.getText().toString());
                            newBanner.setName(edtName.getText().toString());
                            newBanner.setImage(uri.toString());
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() /taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading" + progress +" % ");
                        }
                    });
        }
    }


    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null
                && data.getData()!= null){

            filePath = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){

            showUpdateBannerDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){

            deleteBanner(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteBanner(String key) {

        banners.child(key).removeValue();
    }

    private void showUpdateBannerDialog(final String key, final Banner item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Edit Banner");
        alertDialog.setMessage("Please fill full formation");

        LayoutInflater inflater = this.getLayoutInflater();
        View edit_banner = inflater.inflate(R.layout.add_new_banner,null);

        edtName = edit_banner.findViewById(R.id.edtFoodName);
        edtFoodId = edit_banner.findViewById(R.id.edtFoodId);
        btnSelect = edit_banner.findViewById(R.id.btnSelect);
        btnUpload = edit_banner.findViewById(R.id.btnUpload);

        //set default value for view
        edtName.setText(item.getName());
        edtFoodId.setText(item.getId());

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //let users select image from gallery and save URL of this image
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //upload image
                changeImage(item);
            }
        });

        alertDialog.setView(edit_banner);
        alertDialog.setIcon(R.drawable.ic_laptop_black_24dp);

        //set Button
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                item.setName(edtName.getText().toString());
                item.setId(edtFoodId.getText().toString());

                //make update
                Map<String, Object> update = new HashMap<>();
                update.put("name", item.getName());
                update.put("id", item.getId());
                update.put("image", item.getImage());

                banners.child(key).updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(rootLayout, "Updated", Snackbar.LENGTH_SHORT).show();
                                loadListBanner();
                            }
                        });

                Snackbar.make(rootLayout, " Food " + item.getName()+ " was edited ",
                        Snackbar.LENGTH_SHORT).show();
                loadListBanner();

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                loadListBanner();
            }
        });

        alertDialog.show();

    }

    private void changeImage(final Banner item) {

        if(filePath != null){

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(BannerActivity.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //set value for newCategory if image upload and we can get download link
                            item.setImage(uri.toString());
                            Toast.makeText(BannerActivity.this, "Image Changed Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() /taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading" + progress +" % ");
                        }
                    });
        }
    }
}
