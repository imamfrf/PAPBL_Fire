package com.papblfire.imamf.papbl_fire;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.papblfire.imamf.papbl_fire.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InsertDataActivity extends AppCompatActivity implements MyInterface{

    private FirebaseDatabase db;
    private FirebaseFirestore db2;

    public EditText et_nama, et_telp, et_search;
    private Button bt_save, bt_update, bt_search, bt_refresh;
    private RecyclerView recV_list;
    public RecyclerView.Adapter adapter;
    private String edit_id;
    private RadioGroup rg_filter, rg_chooseDb;



    public List<User> listUser = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

        db = FirebaseDatabase.getInstance();
        db2 = FirebaseFirestore.getInstance();

        et_nama = findViewById(R.id.et_nama);
        et_telp = findViewById(R.id.et_telp);
        et_search = findViewById(R.id.et_search);

        bt_save = findViewById(R.id.bt_save);
        bt_update = findViewById(R.id.bt_update);
        bt_search = findViewById(R.id.bt_search);
        bt_refresh = findViewById(R.id.bt_refresh);

        rg_filter = findViewById(R.id.rg_filter);
        rg_filter.check(R.id.rb_name);

        rg_chooseDb = findViewById(R.id.rg_chooseDb);
        rg_chooseDb.check(R.id.rb_realtime);

        recV_list = findViewById(R.id.recV_list);
        recV_list.setHasFixedSize(true);
        recV_list.setLayoutManager(new LinearLayoutManager(InsertDataActivity.this));

        refreshData();

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addData();
               et_nama.setText("");
               et_telp.setText("");
            }
        });


        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                bt_update.setVisibility(View.GONE);
                et_nama.setText("");
                et_telp.setText("");
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_search.getText().toString() != ""){
                    searchData(et_search.getText().toString(), rg_filter.getCheckedRadioButtonId());
                }
                else {
                    refreshData();
                }
            }
        });

        bt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
    }

    @Override
    public void editData(final String str) {
        bt_update.setVisibility(View.VISIBLE);
        edit_id = str;
        if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_realtime){
            db.getReference("User").child(str).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    et_nama.setText(dataSnapshot.child("nama").getValue(String.class));
                    et_telp.setText(dataSnapshot.child("noTelp").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_firestore){

            db2.collection("user").document(str).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            et_nama.setText(document.get("nama").toString());
                            et_telp.setText(document.get("noTelp").toString());
                        }
                    }
                }
            });
        }

    }

    @Override
    public void deleteData(String str) {
        bt_update.setVisibility(View.GONE);
        if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_realtime){
            db.getReference("User").child(str).removeValue();
        }
        else if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_firestore){
            db2.collection("user").document(str).delete();
        }

        refreshData();

    }

    public void addData(){
        String id = db.getReference("User").push().getKey();
        User user = new User(et_nama.getText().toString(), et_telp.getText().toString(), id);

        if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_realtime) {
            db.getReference("User").child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(InsertDataActivity.this, "Insert data to Realtime Database Success", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_firestore){
            db2.collection("user").document(id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(InsertDataActivity.this, "Insert data to Cloud Firestore Success", Toast.LENGTH_SHORT).show();

                }
            });
        }

        refreshData();

    }

    public void updateData(){
        if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_realtime) {

            db.getReference("User").child(edit_id).child("nama").setValue(et_nama.getText().toString());
            db.getReference("User").child(edit_id).child("noTelp").setValue(et_telp.getText().toString());
        }
        else if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_firestore){
            db2.collection("user").document(edit_id)
                    .update("nama", et_nama.getText().toString(),
                            "noTelp", et_telp.getText().toString());
        }

        refreshData();

        et_nama.setText("");
        et_telp.setText("");
    }

    public void refreshData(){
        if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_realtime) {

            db.getReference("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listUser.clear();
                    for (DataSnapshot sn : dataSnapshot.getChildren()) {
                        listUser.add(new User(sn.child("nama").getValue().toString(), sn.child("noTelp").getValue().toString(), sn.getKey()));
                    }

                    adapter = new UserAdapter(listUser, InsertDataActivity.this, new UserAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }, InsertDataActivity.this);

                    recV_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_firestore){
            db2.collection("user")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                listUser.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    listUser.add(new User(document.get("nama").toString(), document.get("noTelp").toString(), document.getId()));
                                }
                                adapter = new UserAdapter(listUser, InsertDataActivity.this, new UserAdapter.OnItemClicked() {
                                    @Override
                                    public void onItemClick(int position) {

                                    }
                                }, InsertDataActivity.this);

                                recV_list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                               Toast.makeText(InsertDataActivity.this, "Fetch data from firestore error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void searchData(final String keyword, int filter){
        if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_realtime) {

            if (filter == R.id.rb_name) {
                db.getReference("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listUser.clear();
                        for (DataSnapshot sn : dataSnapshot.getChildren()) {
                            if (sn.child("nama").getValue().toString().toLowerCase().contains(keyword.toLowerCase())) {
                                listUser.add(new User(sn.child("nama").getValue().toString(), sn.child("noTelp").getValue().toString(), sn.getKey()));
                                Log.d("tes3", keyword);
                            }
                        }

                        adapter = new UserAdapter(listUser, InsertDataActivity.this, new UserAdapter.OnItemClicked() {
                            @Override
                            public void onItemClick(int position) {

                            }
                        }, InsertDataActivity.this);

                        recV_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                db.getReference("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listUser.clear();
                        for (DataSnapshot sn : dataSnapshot.getChildren()) {
                            if (sn.child("noTelp").getValue().toString().toLowerCase().contains(keyword.toLowerCase())) {
                                listUser.add(new User(sn.child("nama").getValue().toString(), sn.child("noTelp").getValue().toString(), sn.getKey()));

                            }
                        }

                        adapter = new UserAdapter(listUser, InsertDataActivity.this, new UserAdapter.OnItemClicked() {
                            @Override
                            public void onItemClick(int position) {

                            }
                        }, InsertDataActivity.this);

                        recV_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        else if (rg_chooseDb.getCheckedRadioButtonId() == R.id.rb_firestore){
            if (rg_filter.getCheckedRadioButtonId() == R.id.rb_name){
                db2.collection("user").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    listUser.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("tes5", document.get("nama").toString());
                                        if (document.get("nama").toString().toLowerCase().contains(keyword.toLowerCase())){
                                            listUser.add(new User(document.get("nama").toString(), document.get("noTelp").toString(), document.getId()));
                                        }
                                    }
                                    adapter = new UserAdapter(listUser, InsertDataActivity.this, new UserAdapter.OnItemClicked() {
                                        @Override
                                        public void onItemClick(int position) {

                                        }
                                    }, InsertDataActivity.this);

                                    recV_list.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(InsertDataActivity.this, "Fetch data from firestore error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else if (rg_filter.getCheckedRadioButtonId() == R.id.rb_phone){
                db2.collection("user").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    listUser.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.get("noTelp").toString().toLowerCase().contains(keyword.toString().toLowerCase())){
                                            listUser.add(new User(document.get("nama").toString(), document.get("noTelp").toString(), document.getId()));
                                        }
                                    }
                                    adapter = new UserAdapter(listUser, InsertDataActivity.this, new UserAdapter.OnItemClicked() {
                                        @Override
                                        public void onItemClick(int position) {

                                        }
                                    }, InsertDataActivity.this);

                                    recV_list.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(InsertDataActivity.this, "Fetch data from firestore error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
    }



