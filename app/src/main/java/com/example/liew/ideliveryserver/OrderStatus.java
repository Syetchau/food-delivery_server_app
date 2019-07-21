package com.example.liew.ideliveryserver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.liew.ideliveryserver.Common.Common;
import com.example.liew.ideliveryserver.Interface.ItemClickListener;
import com.example.liew.ideliveryserver.Model.MyResponse;
import com.example.liew.ideliveryserver.Model.Notification;
import com.example.liew.ideliveryserver.Model.Request;
import com.example.liew.ideliveryserver.Model.Sender;
import com.example.liew.ideliveryserver.Model.Token;
import com.example.liew.ideliveryserver.Remote.APIService;
import com.example.liew.ideliveryserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;

    MaterialSpinner spinner, shipperSpinner;

    APIService mService;


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

        setContentView(R.layout.activity_order_status);

        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        //Init service
        mService = Common.getFCMClient();

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();
    }

    private void loadOrders() {

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests, Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.txtOrderName.setText(model.getName());
                viewHolder.txtOrderPrice.setText(model.getTotal());

                //New event Button
                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmDeleteDialog(adapter.getRef(position).getKey());

                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingOrder = new Intent(OrderStatus.this, TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(trackingOrder);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void showUpdateDialog(String key, final Request item) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                alertDialog.setTitle("Update Order");
                alertDialog.setMessage("Please Choose Status");

                LayoutInflater inflater = this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.update_order_layout, null);

                spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
                spinner.setItems("Placed", "Preparing Orders", "Shipping", "Delivered");

                shipperSpinner = (MaterialSpinner) view.findViewById(R.id.shipperSpinner);

                //load all shipper to spinner
                final List<String> shipperList = new ArrayList<>();
                FirebaseDatabase.getInstance().getReference(Common.SHIPPER_TABLE)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot shipperSnapshot:dataSnapshot.getChildren())
                                    shipperList.add(shipperSnapshot.getKey());
                                shipperSpinner.setItems(shipperList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                alertDialog.setView(view);

                final String localKey = key;
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                        if (item.getStatus().equals("2"))
                        {
                            //copy item to table "OrdersNeedShip"
                            FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE)
                                    .child(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString())
                                    .child(localKey)
                                    .setValue(item);

                            requests.child(localKey).setValue(item);
                            adapter.notifyDataSetChanged(); //add to update item size

                            sendOrderStatusToUser(localKey, item);
                            sendOrderShipRequestToShipper(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString(), item);
                        }

                        else
                        {
                            requests.child(localKey).setValue(item);
                            adapter.notifyDataSetChanged(); //add to update item size

                            sendOrderStatusToUser(localKey, item);
                        }

                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                alertDialog.show();

            }

    private void sendOrderShipRequestToShipper(String shipperPhone, Request item) {

        DatabaseReference tokens = db.getReference("Tokens");

        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Token token = dataSnapshot.getValue(Token.class);

                    //make raw payload
                    Notification notification = new Notification("iDeliveryServer", "You have new order need ship");
                    Sender content = new Sender(token.getToken(), notification);

                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.body().success == 1) {
                                Toast.makeText(OrderStatus.this, "Sent to Shippers!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderStatus.this, "Failed to send notification!"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("ERROR", t.getMessage());

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void ConfirmDeleteDialog(final String key) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this,  R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                alertDialog.setTitle("Confirm Delete?");

                LayoutInflater inflater = this.getLayoutInflater();
                View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout,null);
                alertDialog.setView(confirm_delete_layout);
                alertDialog.setIcon(R.drawable.ic_delete_black_24dp);

                alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requests.child(key).removeValue();
                        Toast.makeText(OrderStatus.this, "Order Deleted Successfully!", Toast.LENGTH_SHORT).show();
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

            private void sendOrderStatusToUser(final String key, final Request item) {
                DatabaseReference tokens = db.getReference("Tokens");
                tokens.child(item.getPhone())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);

                            //make raw payload
                            Notification notification = new Notification("iDeliveryServer", "Your order " + key + " was updated");
                            Sender content = new Sender(token.getToken(), notification);

                            mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(OrderStatus.this, "Order was updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OrderStatus.this, "Order was updated but failed to send notification!"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }


                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
         }
