package com.example.liew.ideliveryserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.liew.ideliveryserver.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public TextView staffName, staffPassword, staffRole;
    public Button btnDeleteAccount, btnEditAccount;

    public UserViewHolder(View itemView) {
        super(itemView);

        staffName = (TextView)itemView.findViewById(R.id.staff_name);
        staffPassword = (TextView)itemView.findViewById(R.id.staff_password);

        staffRole = (TextView)itemView.findViewById(R.id.staff_role);
        btnEditAccount = (Button)itemView.findViewById(R.id.btnEditStaff);
        btnDeleteAccount = (Button)itemView.findViewById(R.id.btnDeleteStaff);
    }
}
