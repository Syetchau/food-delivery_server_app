package com.example.liew.ideliveryserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.liew.ideliveryserver.Interface.ItemClickListener;
import com.example.liew.ideliveryserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderDate, txtOrderName, txtOrderPrice;
    public Button btnEdit, btnRemove, btnDetail, btnDirection;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderDate = (TextView)itemView.findViewById(R.id.order_date);
        txtOrderName = (TextView)itemView.findViewById(R.id.order_name);
        txtOrderPrice = (TextView)itemView.findViewById(R.id.order_price);

         btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
         btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
         btnDetail = (Button)itemView.findViewById(R.id.btnDetail);
         btnDirection = (Button)itemView.findViewById(R.id.btnDirection);

    }
}
