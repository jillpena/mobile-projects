package jillpena.c323finalproj.com.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase usersDB = null;
    int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

    public void OnClickSignup(View view) {
        Intent myIntent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(myIntent);
    }

    public void OnClickLogin(View view) {
        EditText usernameEditText = findViewById(R.id.editTextUsername);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phonenumber = "";

        Cursor cursor = usersDB.rawQuery("SELECT username, password, phoneNumber from userTable", null);
        int indxColumn_username = cursor.getColumnIndex("userName");
        int indxColumn_password = cursor.getColumnIndex("password");
        int indxColumn_phoneNumber = cursor.getColumnIndex("phoneNumber");
        cursor.moveToFirst();
        Boolean found = false;

        if(cursor != null && cursor.getCount() > 0){
            do{
                String userName = cursor.getString(indxColumn_username);
                String password1 = cursor.getString(indxColumn_password);
                String phoneNumber = cursor.getString(indxColumn_phoneNumber);

                if (userName.equals(username) && password.equals(password1)){
                    found = true;
                    phonenumber = phoneNumber;
                    break;
                }
            } while(cursor.moveToNext());

            if (found){
                //Go to home activity
                Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(LoginActivity.this, DrawerActivity.class);
                myIntent.putExtra("USER_NAME", username);
                myIntent.putExtra("PHONE_NUMBER", phonenumber);
                startActivity(myIntent);
            } else{
                Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "No Users!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

}
