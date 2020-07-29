package com.example.nicolai.clider.Activities;

import android.app.ProgressDialog;
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
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.clider.Activities.BrowsingActivities.BrowseActivity;
import com.example.nicolai.clider.R;
import com.example.nicolai.clider.Services.BackgroundService;
import com.example.nicolai.clider.Utils.Globals;
import com.example.nicolai.clider.model.TagItem;
import com.example.nicolai.clider.model.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    Button logOut, browse;
    TextView userEmail;
    FirebaseAuth firebaseAuth;
    RadioButton rbWoman, rbMan;
    UserPreferences userPreferences;
    DatabaseReference firebaseDatabase;
    NumberPicker pickerAge;
    ListView clothesList;
    List<String> clothes;
    RadioGroup radioGroup;
    HashMap<String, Boolean> clothesPreferences;
    ProgressDialog progressDialog;
    boolean serviceBound;
    List<TagItem> tagList;
    ArrayList<String> tagPreferences = new ArrayList<>();

    private BackgroundService backgroundService;
    private ServiceConnection backgroundServiceConnection;

    int age;
    String sex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initializeComponents();
        setUpConnectionToBackgroundService();
        bindToBackgroundService();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        userEmail.setText(getResources().getString(R.string.profil));

        clothesPreferences = new HashMap<String, Boolean>();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });
        pickerAge.setMinValue(12);
        pickerAge.setMaxValue(90);
        setUpClothesList();
        progressDialog.setMessage(getResources().getString(R.string.fetch));
        progressDialog.show();

        //For checking which items in the preference list is checked.
        SparseBooleanArray checkedItems = clothesList.getCheckedItemPositions();
        for (int j = 0; j < checkedItems.size() ; j++) {
            if (checkedItems.get(j)){
                clothesList.setItemChecked(j, true);
            } else {
                clothesList.setItemChecked(j, false);}
            }
            //Check & uncheck tagitems for sending preferences to firebase.
        clothesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (tagList.get(i).isChecked()){
                    tagList.get(i).setChecked(false);
                }
                else {
                    tagList.get(i).setChecked(true);
                }
            }
        });
    }
    //Saving the users preferences by reading the selected values in the view. Calling BG-service saveuserInfo with the created userpreference object
    private void saveUserInfo(){
        if (rbMan.isChecked()){
            sex = Globals.male;
        }
        if (rbWoman.isChecked()){
            sex  = Globals.female;
        }
        age = pickerAge.getValue();
        tagPreferences = new ArrayList<>();
        for (TagItem tag: tagList) {
            if (tag.isChecked()){
                tagPreferences.add(tag.getTagName());
            }
        }
        userPreferences = new UserPreferences(age, sex, tagPreferences);
        backgroundService.saveUserInfo(userPreferences);
        Toast.makeText(this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, BrowseActivity.class));
        finish();
    }

    //Creating a simple checklist with androids own multi-chice layout.
    private void setUpClothesList(){
        clothes = new ArrayList<String>();
        for (TagItem tagitem: tagList) {
            clothes.add(tagitem.getName());
        }
        Adapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, clothes);
        clothesList = (ListView) findViewById(R.id.clothes_list);
        clothesList.setAdapter((ListAdapter) adapter);
        clothesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void initializeComponents() {
        logOut = findViewById(R.id.buttonLogOut);
        userEmail = findViewById(R.id.userEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        rbWoman = findViewById(R.id.radioButtonWoman);
        rbMan = findViewById(R.id.radioButtonMan);
        browse = findViewById(R.id.browse_btn);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        pickerAge = findViewById(R.id.numberPicker_age);
        progressDialog = new ProgressDialog(this);
        radioGroup = findViewById(R.id.radioGroup);
        setUpTagList();


    }

    //Using "TagItems" we store type, posistion in the array, if checked, and their tag.
    private void setUpTagList() {
        tagList = new ArrayList<>();
        tagList.add(new TagItem(getResources().getString(R.string.Shoes), 0, false, Globals.shoeTag));
        tagList.add(new TagItem(getResources().getString(R.string.Dresses), 1, false, Globals.dressTag));
        tagList.add(new TagItem(getResources().getString(R.string.Tshirts), 2, false, Globals.tshirtTag));
        tagList.add(new TagItem(getResources().getString(R.string.Hats), 3, false, Globals.hatTag));
        tagList.add(new TagItem(getResources().getString(R.string.Accessories), 4, false, Globals.accessoriesTag));
        tagList.add(new TagItem(getResources().getString(R.string.SportsClothes), 5, false, Globals.sportTag));
        tagList.add(new TagItem(getResources().getString(R.string.Jackets), 6, false, Globals.jacketTag));
        tagList.add(new TagItem(getResources().getString(R.string.Shorts), 7, false, Globals.shortsTag));
    }

    //Retrieving userpreferences when connected to BGservice.
    private void setUpConnectionToBackgroundService(){
        backgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                backgroundService = ((BackgroundService.BackgroundServiceBinder)service).getService();
                Log.d("From service", "onServiceConnected: Connected");
                backgroundService.retriveUserPreferences();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
                Log.d("From service", "onServiceDisconnected: disconnected");
            }
        };
    }

    void bindToBackgroundService() {
        bindService(new Intent(UserActivity.this,
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

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(onUserPreferencesRecieved, new IntentFilter(Globals.userPreferencesBroadCast));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindFromBackgroundService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onUserPreferencesRecieved);
    }

    //Retrieving the users preferences(if any) when broadcasted. And sets them accrodingly. Persistance is done through firebase.
    private BroadcastReceiver onUserPreferencesRecieved = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "Broadcast recived, userpreferences updated");
            UserPreferences userPreferences = backgroundService.getUserPreferences();
            if (userPreferences!=null){
                pickerAge.setValue(userPreferences.getAge());
                Log.d("Age", "onReceive: Age set");
                if (userPreferences.getSex()!=null){
                    Log.d("get Sex", "onReceive: " + userPreferences.getSex());
                }
                if (userPreferences.getSex().equalsIgnoreCase(Globals.female)){
                    Log.d("radiobutton", "onReceive: trying to set radiobutton female");
                    radioGroup.check(R.id.radioButtonWoman);
                }
                if (userPreferences.getSex().equalsIgnoreCase(Globals.male)){
                    Log.d("radiobutton", "onReceive: trying to set radiobutton male");
                    radioGroup.check(R.id.radioButtonMan);
                }
                for (TagItem tagItem : tagList) {
                    if (userPreferences.getTags().contains(tagItem.getTagName())){
                        tagItem.setChecked(true);
                        clothesList.setItemChecked(tagItem.getPosistion(), true);
                    }
                    else {
                       tagItem.setChecked(false);
                        clothesList.setItemChecked(tagItem.getPosistion(), false);
                    }
                }
            }
            progressDialog.dismiss();
        }
    };
}
