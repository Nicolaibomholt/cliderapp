package com.example.nicolai.clider.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nicolai.clider.Activities.BrowsingActivities.BrowseActivity;
import com.example.nicolai.clider.Adapters.ClotheAdapter;
import com.example.nicolai.clider.R;
import com.example.nicolai.clider.Services.BackgroundService;
import com.example.nicolai.clider.Utils.Globals;
import com.example.nicolai.clider.model.Clothe;
import com.example.nicolai.clider.model.ClotheCardView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ListView clothesListView;
    ArrayList<Clothe> arrayList = new ArrayList<>();
    ClotheAdapter clotheAdapter;
    private BackgroundService backgroundService;
    private ServiceConnection backgroundServiceConnection;
    boolean serviceBound;
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        clothesListView = findViewById(R.id.clothesListView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView = findViewById(R.id.webView);

        setUpConnectionToBackgroundService();
        bindToBackgroundService();
    }

    void bindToBackgroundService() {
        bindService(new Intent(ListActivity.this,
                BackgroundService.class), backgroundServiceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;
    }

    //Connecting to BG-service. When connected retrieve list of like clothes ID's.
    private void setUpConnectionToBackgroundService(){
        backgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                backgroundService = ((BackgroundService.BackgroundServiceBinder)service).getService();
                Log.d("From service", "onServiceConnected: Connected");
                backgroundService.retriveLikedClothesIds();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
                Log.d("From service", "onServiceDisconnected: disconnected");
            }
        };
    }
    //Building the list with our adapter, when the background service makes the apropriate broadcast. Also sets onlick listeners for the link to the clothes
    private BroadcastReceiver onLikedClotheResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "Broadcast reveiced from card");
            String message = intent.getStringExtra(Globals.swipedClotheUpdated);
            if (message.equals(Globals.swipedClotheUpdated)){
                arrayList = backgroundService.getLikedCloth();
                clotheAdapter = new ClotheAdapter(getApplicationContext(), arrayList);
                clothesListView.setAdapter(clotheAdapter);
                clothesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        webView.loadUrl(arrayList.get(position).getWebshopUrl());
                        webView.setVisibility(View.VISIBLE);

                    }
                });
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(onLikedClotheResult, new IntentFilter(Globals.swipedClotheBroadcast));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindFromBackgroundService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onLikedClotheResult);
    }

    void unBindFromBackgroundService() {
        if (serviceBound) {
            // Detach our existing connection.
            unbindService(backgroundServiceConnection);
            serviceBound = false;
        }
    }

    //Small "hack" making the webview invisible when the app is resumed.
    @Override
    protected void onResume() {
        super.onResume();
        webView.setVisibility(View.INVISIBLE);
    }
}
