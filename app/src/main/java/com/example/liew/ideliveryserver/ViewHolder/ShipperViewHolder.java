package com.example.liew.ideliveryserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.liew.ideliveryserver.Interface.ItemClickListener;
import com.example.liew.ideliveryserver.R;

import org.w3c.dom.Text;

import info.hoang8f.widget.FButton;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView shipper_name, shipper_phone, shipper_password;
    public Button btn_edit, btn_remove;
    private ItemClickListener itemClickListener;

    public ShipperViewHolder(View itemView) {
        super(itemView);

        shipper_name = (TextView)itemView.findViewById(R.id.shipper_name);
        shipper_phone = (TextView)itemView.findViewById(R.id.shipper_phone);
        shipper_password = (TextView)itemView.findViewById(R.id.shipper_password);

        btn_edit = (Button)itemView.findViewById(R.id.btnEditShipper);
        btn_remove = (Button)itemView.findViewById(R.id.btnDeleteShipper);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
