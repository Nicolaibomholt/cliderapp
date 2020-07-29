package com.example.nicolai.clider.Activities.BrowsingActivities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nicolai.clider.Activities.ListActivity;
import com.example.nicolai.clider.Activities.MapsActivity;
import com.example.nicolai.clider.Activities.UserActivity;
import com.example.nicolai.clider.R;
import com.example.nicolai.clider.Services.BackgroundService;
import com.example.nicolai.clider.Utils.Globals;
import com.example.nicolai.clider.Utils.Utils;
import com.example.nicolai.clider.model.Clothe;
import com.example.nicolai.clider.model.ClotheCardView;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private boolean serviceBound;

    private BackgroundService backgroundService;
    private ServiceConnection backgroundServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse1);

        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        setUpConnectionToBackgroundService();
        bindToBackgroundService();


    }

    //Create the menu with the menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Top menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.preferencesTab:
                Intent toPreferences = new Intent(this, UserActivity.class);
                this.startActivity(toPreferences);
                return true;
            case R.id.myListTab:
                Intent toMyList = new Intent(this, ListActivity.class);
                this.startActivity(toMyList);
                return true;
            case  R.id.mapsID:
                Intent toMaps = new Intent(this, MapsActivity.class);
                this.startActivity(toMaps);
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    //Reference mindorks library https://blog.mindorks.com/android-tinder-swipe-view-example-3eca9b0d4794
    private void buildSwipeView() {
        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeMaxChangeAngle(2f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_msg)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_msg));

        List<Clothe> clothes = backgroundService.getClotheByPreferences();
        for(Clothe clothe : clothes){
            mSwipeView.addView(new ClotheCardView(clothe, mContext, mSwipeView));
        }
    }

    //Connect to backgroundservice and building the view when connected, since the list of clothes is being created from the backgroundservice
    private void setUpConnectionToBackgroundService(){
        backgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                backgroundService = ((BackgroundService.BackgroundServiceBinder)service).getService();
                buildSwipeView();
                Log.d("From service", "onServiceConnected: Connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
                Log.d("From service", "onServiceDisconnected: disconnected");
            }
        };
    }

    void bindToBackgroundService() {
        bindService(new Intent(BrowseActivity.this,
                BackgroundService.class), backgroundServiceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;
    }

    void unBindFromBackgroundService() {
        if (serviceBound) {
            // Detach our existing connection.
            unbindService(backgroundServiceConnection);
            serviceBound = false;
        }
    }

    //Broadcast reciever listening for broadcasts from ClotheCardView (On image click and On swipe in)
    private BroadcastReceiver onCardSwipeResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "Broadcast reveiced from card");
            if (intent.getStringExtra("message").equals(Globals.cardSwipeMessage)) {
                Log.d("BROWSEACTIVITY", "onReceive: added ");
                Clothe clothe = (Clothe) intent.getSerializableExtra(Globals.cardSwipeMessage);
                backgroundService.addClothe(clothe);
            }
            if (intent.getStringExtra("message").equals(Globals.clickMessage)) {
                Log.d("BROWSE", "onReceive: clicked ");
                Clothe clothe = (Clothe) intent.getSerializableExtra(Globals.cardSwipeMessage);
                showDialog(clothe);
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(onCardSwipeResult, new IntentFilter(Globals.clotheBroadcast));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindFromBackgroundService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onCardSwipeResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindFromBackgroundService();
    }

    //Creating the alertdialog that show price etc
    private void showDialog(Clothe clothe){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(BrowseActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(BrowseActivity.this);
        }
        builder.setTitle(getResources().getString(R.string.modelName) + " " + clothe.getName())
                .setMessage(getResources().getString(R.string.locationName) + " " + clothe.getLocation() + "\n" +
                getResources().getString(R.string.price) + " " +clothe.getPrice()+ getResources().getString(R.string.suffixPrice))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.btn_star)
                .show();

    }
}
