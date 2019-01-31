package jillpena.c323finalproj.com.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class UserProfileActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText phoneEditText;
    EditText emailEditText;
    EditText passwordEditText;
    Button saveButton;
    SQLiteDatabase usersDB = null;
    String username;
    String phonenumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent myIntent = getIntent();
        username = myIntent.getStringExtra("user");
        phonenumber = myIntent.getStringExtra("phonenumber");
        Log.i("FINAL_EXAM", "USER PROFILE: " + username + " " + phonenumber);
        createDatabase();
        nameEditText = findViewById(R.id.editTextNameEdit);
        phoneEditText = findViewById(R.id.editTextPhoneEdit);
        emailEditText = findViewById(R.id.editTextEmailEdit);
        passwordEditText = findViewById(R.id.editTextPasswordEdit);
        saveButton = findViewById(R.id.buttonSave);
        setUserInfo();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOnClick();
            }
        });
    }

    private void setUserInfo() {
        Cursor cursor = usersDB.rawQuery("SELECT userName, password, phoneNumber, email from userTable", null);
        int indxColumn_username = cursor.getColumnIndex("userName");
        int indxColumn_password = cursor.getColumnIndex("password");
        int indxColumn_phoneNumber = cursor.getColumnIndex("phoneNumber");
        int indxColumn_email = cursor.getColumnIndex("email");
        cursor.moveToFirst();
        Boolean found = false;
        String userName="";
        String password1="";
        String phoneNumber="";
        String email = "";

        if(cursor != null && cursor.getCount() > 0){
            do{
                userName = cursor.getString(indxColumn_username);
                password1 = cursor.getString(indxColumn_password);
                phoneNumber = cursor.getString(indxColumn_phoneNumber);
                email = cursor.getString(indxColumn_email);

                if (userName.equals(username)){
                    found = true;
                    break;
                }
            } while(cursor.moveToNext());

            if (found){
                nameEditText.setText(userName);
                emailEditText.setText(email);
                phoneEditText.setText(phoneNumber);
                passwordEditText.setText(password1);

            } else{
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "No Users!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void handleOnClick() {
        String user = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (user.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill out all fields!", Toast.LENGTH_LONG).show();
        } else {
            usersDB.execSQL("INSERT INTO userTable (userName, password, email, phoneNumber) VALUES('" + user + "','" + password + "','" + email + "','" + phone + "');");
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void createDatabase() {

        try{
            usersDB = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
            usersDB.execSQL("CREATE TABLE IF NOT EXISTS userTable "+"(userName VARCHAR, password VARCHAR, email VARCHAR, phoneNumber VARCHAR);");
            File database = getApplicationContext().getDatabasePath("MyUsers.db");
            if (!database.exists()) {
                //Toast.makeText(this, "Database Created!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "Database doesn't exist!", Toast.LENGTH_SHORT).show();
            }

        } catch(Exception e){
            Log.v("DB_ERROR", "Error Creating the Database!");
        }

    }


}
