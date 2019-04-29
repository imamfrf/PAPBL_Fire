package com.papblfire.imamf.papbl_fire;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.papblfire.imamf.papbl_fire.Model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> listItems;
    private Context context;
    private OnItemClicked mListener;
    private MyInterface myInterface;

    public UserAdapter(List<User> listItems, Context context, OnItemClicked listener, MyInterface myInterface) {
        this.listItems = listItems;
        this.context = context;
        this.mListener = listener;
        this.myInterface = myInterface;

    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {
        //binding value dari list item ke holder
        User listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.nama.setText(listItem.getNama());
        holder.noTelp.setText(listItem.getNoTelp());

        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myInterface.editData(holder.id.getText().toString());
            }
        });

        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myInterface.deleteData(holder.id.getText().toString());
            }
        });
        }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nama, noTelp, id;
        public Button bt_edit, bt_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.tv_nama);
            noTelp = itemView.findViewById(R.id.tv_telp);
            id = itemView.findViewById(R.id.tv_id);
            bt_edit = itemView.findViewById(R.id.bt_edit);
            bt_delete = itemView.findViewById(R.id.bt_delete);

        }
    }
    public interface OnItemClicked {
        void onItemClick(int position);
    }
}





