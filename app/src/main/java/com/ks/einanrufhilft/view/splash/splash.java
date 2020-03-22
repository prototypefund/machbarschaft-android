package com.ks.einanrufhilft.view.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ks.einanrufhilft.Database.Database;
import com.ks.einanrufhilft.R;
import com.ks.einanrufhilft.view.login.LoginMain;


/**
 *  Splashscreen which will be displayed shortly when the App is started.
 */
public class splash extends AppCompatActivity {

    private Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startLogin();
            }
        }, 900);


        /*

        Tests

         */

        /* TESTS */
        Database db = Database.getInstance();
        // String phone_number, String plz, String strasse, String hausnummer, String firstName, String lastNamme, String[] category) {
        //        this.phone_number = phone_number
        // Order o = new Order("0981238231", "12212", "myStarsse", "12a","alex",
        //       "maier", new String[]{"Einkauf"});

        //Account a = new Account("Max", "maier", "90821389123",7.5f, 30);
        //db.createAccount(a);

        db.getOrders();


        db.login("90821389123");
        //db.setOrderStatus("mofVj419q6fAxj4hLYeW", Database.Status.Confirmed);


        /* TESTS */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startLogin() {
        this.startActivity(new Intent(this, LoginMain.class));
    }
}
