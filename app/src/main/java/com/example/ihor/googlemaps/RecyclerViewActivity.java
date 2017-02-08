package com.example.ihor.googlemaps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    private DB db;
    private ArrayList Names ;
    private SQLiteDatabase dbConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recicler_view);
        ButterKnife.bind(this);
        Names = new ArrayList();
        dbConnect = db.getInstance(this).getWritableDatabase();
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter(getDBNames());
        mRecyclerView.setAdapter(mAdapter);

    }


    private ArrayList getDBNames() {
        ArrayList Names = new ArrayList();
        Names.add("Васька");
        Cursor c = dbConnect.query("mytable", null, null, null, null, null, null);


        if (c.moveToFirst()) {


            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int latitudeColIndex = c.getColumnIndex("latitude");
            int longitudeColIndex = c.getColumnIndex("longitude");

            do {
                Names.add(c.getString(nameColIndex));

            } while (c.moveToNext());
        } else
            Log.wtf("test", "0 rows");
        c.close();

        return Names;
    }


}

