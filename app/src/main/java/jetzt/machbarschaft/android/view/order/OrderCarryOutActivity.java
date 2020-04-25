package jetzt.machbarschaft.android.view.order;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import jetzt.machbarschaft.android.R;
import jetzt.machbarschaft.android.database.Storage;
import jetzt.machbarschaft.android.database.entitie.Order;
import jetzt.machbarschaft.android.database.entitie.OrderSteps;
import jetzt.machbarschaft.android.view.login.LoginMain;

public class OrderCarryOutActivity extends AppCompatActivity {
    private Order mOrder;

    @Override
    public void onBackPressed() {
            //do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_carry_out);

        ImageView imageView = findViewById(R.id.step_2_graphic);
        imageView.setClipToOutline(true);

        // Get UI elements
        Button btnStartNow = findViewById(R.id.btn_order_execute_now);
        Button btnStartLater = findViewById(R.id.btn_order_execute_later);
        Button btnStartFailed = findViewById(R.id.btn_order_execute_failed);


        // Load active order from Database
        loadOrder();
        Storage.getInstance().setCurrentStep(getApplicationContext(), OrderSteps.STEP2_CarryOut);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCarryOutOrder);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(), OrderAcceptActivity.class)));
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        // Button click handlers
        btnStartNow.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderEnRouteActivity.class));
            finishAfterTransition();
        });

        btnStartLater.setOnClickListener(v -> {

        });

        btnStartFailed.setOnClickListener(v -> {

        });
    }

    /**
     * Load users active order from database
     */
    private void loadOrder() {
        // mOrder = Storage.getInstance().getCurrentOrder();
        mOrder = Storage.getInstance().getOrderInProgress(getApplicationContext());
    }

    /**
     * Open phone app with number from order
     */
    private void callUser() {
        Uri callUri = Uri.parse("tel:" + (mOrder == null ? "0000000" : mOrder.getPhoneNumber()));
        startActivity(new Intent(Intent.ACTION_VIEW, callUri));
    }
}
