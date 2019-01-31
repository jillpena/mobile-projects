package jillpena.c323finalproj.com.finalproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    SQLiteDatabase usersDB = null;

    EditText userNameEditText;
    EditText phoneNumberEditText;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userNameEditText = findViewById(R.id.editTextUserName);
        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        createDatabase();

    }

    public void createDatabase() {

        try{
            usersDB = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
            usersDB.execSQL("CREATE TABLE IF NOT EXISTS userTable "+"(userName VARCHAR, password VARCHAR, email VARCHAR, phoneNumber VARCHAR);");
            File database = getApplicationContext().getDatabasePath("MyUsers.db");
            if (!database.exists()) {
                Toast.makeText(this, "Database Created!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "Database doesn't exist!", Toast.LENGTH_SHORT).show();
            }

        } catch(Exception e){
            Log.v("DB_ERROR", "Error Creating the Database!");
        }

    }


    public void OnClickSignupFinal(View view)  {
        String user = userNameEditText.getText().toString();
        String phone = phoneNumberEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (user.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill out all fields!", Toast.LENGTH_LONG).show();
        } else {
            usersDB.execSQL("INSERT INTO userTable (userName, password, email, phoneNumber) VALUES('" + user + "','" + password + "','" + email + "','" + phone + "');");
            Toast.makeText(this, "Item Added to Database", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
