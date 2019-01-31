package jillpena.c323finalproj.com.finalproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsActivity extends AppCompatActivity {

    ArrayList<Person> personList = new ArrayList<>();
    ImageButton searchButton;
    EditText searchEditText;
    ListView listView;
    MyListAdapter adapter;
    int MY_SMS_PERMISSIONS_REQUEST =1;
    ArrayList<String> nameList;
    ArrayList<Integer> photoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        searchButton = findViewById(R.id.imageButtonSearch);
        searchEditText = findViewById(R.id.editTextName);
        listView = findViewById(R.id.dynamicListView);

       Intent myIntent = getIntent();
       nameList = myIntent.getStringArrayListExtra("NAMES_LIST");
       Log.i("FINAL EXAM", "Got Name List: " + nameList);
       photoList = myIntent.getIntegerArrayListExtra("PHOTO_LIST");
       Log.i("FINAL EXAM", "Got Photo List: " + photoList);

       populatePersonList();

        adapter = new MyListAdapter(this, personList);
        listView.setAdapter(adapter);

    }

    public void populatePersonList(){

        if (personList.isEmpty()) {
            int nameListSize = nameList.size();
            int photoListSize = photoList.size();
            for (int i = 0; i < nameListSize; i++) {
                for (int j = 0; j < photoListSize; j++) {
                    Person p = new Person(nameList.get(i), photoList.get(j));
                    if (!personList.contains(p)) {
                        personList.add(p);
                    }
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_freinds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_addPerson:
                addPerson();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void addPerson() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        edittext.setHint("Phone Number");
        alert.setMessage("Invite Friend");
        alert.setTitle("Send an SMS");

        alert.setView(edittext);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String phoneNumber = edittext.getText().toString();
                sendSMSText(phoneNumber);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }

    private void sendSMSText(String phoneNumber) {
        if (phoneNumber!=null) {
            String dial = "smsto:%s" + phoneNumber;
            String message =  "Hi! I am using YouTube Lite application. Join Me 😊";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("SMS access needed");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setMessage("please confirm SMS access");//TODO put real question
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(
                                        new String[]{Manifest.permission.SEND_SMS}, MY_SMS_PERMISSIONS_REQUEST);
                            }
                        });
                        builder.show();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS}, MY_SMS_PERMISSIONS_REQUEST);
                    }
                } else {
                    try {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(dial));
                        smsIntent.putExtra("sms_body", message);
                        Log.i("FINAL_EXAM", "ABOUT TO SEND MESSAGE");
                        startActivity(smsIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "Sorry, I can't send SMS.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                try {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(dial));
                    smsIntent.putExtra("sms_body", message);
                    Log.i("FINAL_EXAM", "ABOUT TO SEND MESSAGE");
                    startActivity(smsIntent);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Sorry, I can't send SMS.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(this, "Please Provide a Phone Number to Text.", Toast.LENGTH_LONG).show();
        }

    }


    public void onClickSearch(View view) {
        try {
            String searchString = searchEditText.getText().toString();
            if (!searchString.isEmpty()) {
                adapter.getFilter().filter(searchString);
            } else{
                personList.clear();
                populatePersonList();
                adapter.notifyDataSetChanged();
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private class MyListAdapter extends BaseAdapter implements Filterable {

        Context context;
        ArrayList<Person> orignalArray, tempArray;
        CustomFilter customFilter;

        public MyListAdapter(Context c, ArrayList<Person> originalArray) {
            this.context = c;
            this.orignalArray = originalArray;
            this.tempArray = originalArray;
        }

        @Override
        public int getCount() {
            return orignalArray.size();
        }

        @Override
        public Object getItem(int i) {
            return orignalArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.person_list_item, parent, false);

            Person currentPerson= orignalArray.get(position);

            TextView nameTextView = itemView.findViewById(R.id.nameTextView);
            ImageView profileImageView = itemView.findViewById(R.id.profileImageView);

            nameTextView.setText(currentPerson.getName());
            profileImageView.setImageResource(currentPerson.getImage());
            return itemView;
        }

        @Override
        public Filter getFilter() {
            if (customFilter == null){
                customFilter = new CustomFilter();
            }

            return customFilter;
        }

        class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null && charSequence.length() > 0) {
                    charSequence = charSequence.toString().toUpperCase();
                    ArrayList<Person> filters = new ArrayList<>();
                    boolean found = false;
                    for (int i = 0; i < tempArray.size(); i++) {
                        if (tempArray.get(i).getName().toUpperCase().contains(charSequence)) {
                            Person p = new Person(tempArray.get(i).getName(), tempArray.get(i).getImage());
                            filters.add(p);
                            found = true;
                        }
                    }

                    if (!found){
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                    filterResults.count = filters.size();
                    filterResults.values = filters;
                }
                else{
                    filterResults.count = tempArray.size();
                    filterResults.values = tempArray;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                orignalArray = (ArrayList<Person>)filterResults.values;
                notifyDataSetChanged();
            }
        }
    }


}
