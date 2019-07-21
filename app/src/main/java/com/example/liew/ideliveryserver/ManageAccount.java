package com.example.liew.ideliveryserver;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liew.ideliveryserver.Common.Common;
import com.example.liew.ideliveryserver.Model.Shipper;
import com.example.liew.ideliveryserver.Model.User;
import com.example.liew.ideliveryserver.ViewHolder.ShipperViewHolder;
import com.example.liew.ideliveryserver.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ManageAccount extends AppCompatActivity {

    FloatingActionButton fabAddStaff;
    //Firebase
    FirebaseDatabase db;
    DatabaseReference users;
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


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

        setContentView(R.layout.activity_inc_account);

        //Init view
        //Init View
        fabAddStaff = (FloatingActionButton) findViewById(R.id.fab_add_staff);
        fabAddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAccountDialog();
            }
        });

        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.Staff_TABLE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_account);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadAccount();
    }

    private void loadAccount() {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(users, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder viewHolder, final int position, @NonNull final User model) {
                viewHolder.staffName.setText(model.getPhone());
                viewHolder.staffPassword.setText(model.getName());
                viewHolder.staffRole.setText(Common.convertRole(model.getIsstaff()));

                //new event
                viewHolder.btnEditAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditAccountDialog(adapter.getRef(position).getKey(), model);
                    }
                });


                viewHolder.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteAccountDialog(adapter.getRef(position).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_manage_account, parent, false);
                return new UserViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void showCreateAccountDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageAccount.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("CREATE STAFF ACCOUNT");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_account = inflater.inflate(R.layout.create_account_layout, null);

        final MaterialEditText account_phone = (MaterialEditText) layout_account.findViewById(R.id.create_account_phone);
        final MaterialEditText account_name = (MaterialEditText) layout_account.findViewById(R.id.create_account_name);
        final MaterialEditText account_password = (MaterialEditText) layout_account.findViewById(R.id.create_account_password);
        account_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        account_password.setTransformationMethod(new PasswordTransformationMethod());
        alertDialog.setView(layout_account);
        alertDialog.setIcon(R.drawable.ic_create_black_24dp);


        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //create account

                if (TextUtils.isEmpty(account_phone.getText())) {
                    Toast.makeText(ManageAccount.this, "Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(account_name.getText())) {
                    Toast.makeText(ManageAccount.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(account_password.getText())) {
                    Toast.makeText(ManageAccount.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if (account_phone.getText().length() < 11    ){
                    Toast.makeText(ManageAccount.this, "Phone Number cannot less than 11 digts!", Toast.LENGTH_SHORT).show();
                }
                else if (account_phone.getText().length() >13) {
                    Toast.makeText(ManageAccount.this, "Phone Number cannot exceed 13 digits!", Toast.LENGTH_SHORT).show();
                }
                else {
                    User user = new User();
                    user.setPhone(account_phone.getText().toString());
                    user.setName(account_name.getText().toString());
                    user.setPassword(account_password.getText().toString());
                    user.setIsstaff("true");
                    user.setIsadmin("false");

                    users.child(account_phone.getText().toString())
                            .setValue(user)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(ManageAccount.this, "Staff Created Successfully!", Toast.LENGTH_SHORT).show();
                               }
                           })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ManageAccount.this, "Failed to Create Account!", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

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


    private void showEditAccountDialog(final String key, final User model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageAccount.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("UPDATE ACCOUNT");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_account = inflater.inflate(R.layout.create_account_layout, null);

        final MaterialEditText account_phone = (MaterialEditText) layout_account.findViewById(R.id.create_account_phone);
        final MaterialEditText account_name = (MaterialEditText) layout_account.findViewById(R.id.create_account_name);
        final MaterialEditText account_password = (MaterialEditText) layout_account.findViewById(R.id.create_account_password);
        account_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        account_password.setTransformationMethod(new PasswordTransformationMethod());

        //set data
        account_name.setText(model.getName());
        account_password.setText(model.getPassword());
        account_phone.setText(model.getPhone());
        account_phone.setEnabled(false);

        alertDialog.setView(layout_account);
        alertDialog.setIcon(R.drawable.ic_create_black_24dp);


        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //create account

                if (TextUtils.isEmpty(account_phone.getText())) {
                    Toast.makeText(ManageAccount.this, "Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(account_name.getText())) {
                    Toast.makeText(ManageAccount.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if (account_phone.getText().length() < 11    ){
                    Toast.makeText(ManageAccount.this, "Phone Number cannot less than 11 digts!", Toast.LENGTH_SHORT).show();
                }
                else if (account_phone.getText().length() >13) {
                    Toast.makeText(ManageAccount.this, "Phone Number cannot exceed 13 digits!", Toast.LENGTH_SHORT).show();
                }
                else {
                    User user = new User();
                    user.setPhone(account_phone.getText().toString());
                    user.setName(account_name.getText().toString());
                    user.setPassword(account_password.getText().toString());
                    user.setIsstaff("true");
                    user.setIsadmin("false");

                    users.child(account_phone.getText().toString())
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ManageAccount.this, "Staff Created Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ManageAccount.this, "Failed to Create Account!", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

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


    private void showDeleteAccountDialog(final String key) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageAccount.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout,null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                users.child(key).removeValue();
                Toast.makeText(ManageAccount.this, "Account Delete Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
            adapter.startListening();
            loadAccount();
    }
}
