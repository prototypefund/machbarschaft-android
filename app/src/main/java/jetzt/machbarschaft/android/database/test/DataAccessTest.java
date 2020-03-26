package jetzt.machbarschaft.android.database.test;

import android.util.Log;

import jetzt.machbarschaft.android.database.DataAccess;
import jetzt.machbarschaft.android.database.entitie.Account;

public class DataAccessTest {
    private static DataAccessTest test;

    private DataAccessTest() {
    }

    ;

    public static DataAccessTest getInstance() {
        if (test == null) {
            test = new DataAccessTest();
        }
        return test;
    }

    public void runTests() {
        Log.i("DataAccessTest", "Tests start:");

        //createAccount(); //passed
        // login(); // passed
        // getOrders(); // passed
        // setOrderStatus(); // passed
    }

    private void createAccount() {
        Account a = new Account();
        a.setFirst_name("TestFirstName");
        a.setLast_name("TestLastName");
        a.setRadius(10f);
        a.setPhone_number("0157 12345678");
        a.setCredits(10);
        DataAccess.getInstance().createAccount(a, success -> {
            if (success == true) {
                Log.i("DataAccessTest", "createAccount: Success");
                return;
            } else {
                Log.i("DataAccessTest", "createAccount: Failure");
            }
        });
    }

    private void login() {
        DataAccess.getInstance().login("0157 12345678", collection -> {
            if (collection != null) {
                Log.i("DataAccessTest", "login: Success");
                Log.i("DataAccessTest", "login: " + collection);
            } else {
                Log.i("DataAccessTest", "login: Failure");
            }
        });
    }

    private void getOrders() {
        DataAccess.getInstance().getOrders(successful -> {
            if(successful) {
                Log.i("DataAccessTest", "getOrders: Success");
            } else {
                Log.i("DataAccessTest", "getOrders: Failure");
            }
        });
    }

    private void setOrderStatus() {
        // Confirmed
        DataAccess.getInstance().setOrderStatus("3Bh8isxyUHQ1Gny3G0Nn",DataAccess.Status.CONFIRMED, successful -> {
            if (successful) {
                Log.i("DataAccessTest", "setOrderStatus: CONFIRMED:Success");
                return;
            } else {
                Log.i("DataAccessTest", "setOrderStatus: CONFIRMED:Failure");
            }
        });

        // Closed
        DataAccess.getInstance().setOrderStatus("3Bh8isxyUHQ1Gny3G0Nn",DataAccess.Status.CLOSED, successful -> {
            if (successful) {
                Log.i("DataAccessTest", "setOrderStatus: CLOSED:Success");
                return;
            } else {
                Log.i("DataAccessTest", "setOrderStatus: CLOSED:Failure");
            }
        });
    }

}
