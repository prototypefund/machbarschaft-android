package jetzt.machbarschaft.android.view.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import jetzt.machbarschaft.android.R;
import jetzt.machbarschaft.android.database.DataAccess;
import jetzt.machbarschaft.android.database.Database;
import jetzt.machbarschaft.android.database.Storage;
import jetzt.machbarschaft.android.database.entitie.Order;
import jetzt.machbarschaft.android.services.OrderInProgressNotification;
import jetzt.machbarschaft.android.view.home.Home;

/**
 * Displays the Details of an order, after someone accepts it.
 */
public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = "OrderDetailActivity";
    public static final String EXTRA_ORDER_ID = "orderId";

    private Order mOrder;
    private GoogleMap map;

    private TextView mNameView;
    private TextView mNeedsView;
    private TextView mUrgencyView;
    private TextView mAddressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        loadOrder();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        Button btnAccept = findViewById(R.id.btn_accept_order);
        btnAccept.setOnClickListener(v -> acceptOrder());

        mNameView = findViewById(R.id.order_detail_name);
        mNeedsView = findViewById(R.id.order_detail_needs);
        mUrgencyView = findViewById(R.id.order_detail_urgency);
        mAddressView = findViewById(R.id.order_detail_address);
    }

    /**
     * Applies the order details to the views.
     */
    private void applyOrderToViews() {
        if (mOrder == null) {
            return;
        }

        mNameView.setText(mOrder.getClientName());
        mNeedsView.setText(mOrder.getType().getTitle());
        mUrgencyView.setText(mOrder.getUrgency().getTitle());
        StringBuilder address = new StringBuilder();
        address.append(mOrder.getStreet());
        address.append(" ");
        address.append(mOrder.getHouseNumber());
        address.append(", ");
        address.append(mOrder.getZipCode());
        mAddressView.setText(address);
    }

    private void acceptOrder() {
        if (mOrder == null) {
            return;
        }

        Database database = Database.getInstance();
        DataAccess.getInstance().setOrderStatus(mOrder.getId(), DataAccess.Status.CONFIRMED);


        Storage storage = Storage.getInstance();
        //storage.setCurrentOrder(mOrder);
        Storage.setOrderInProgress(getApplicationContext(), mOrder);
        startOrderNotification();
        storage.setActiveOrder(getApplicationContext(), true);
        startActivity(new Intent(this, OrderAcceptActivity.class));
        finishAfterTransition();
    }

    private void loadOrder() {
        Intent intent = getIntent();
        if (intent != null) {
            final String orderId = intent.getStringExtra(EXTRA_ORDER_ID);

            Database database = Database.getInstance();
            DataAccess.getInstance().getOrderById(orderId, order -> {
                mOrder = (Order) order;
                Log.i("TEST", "loadOrder->getOrderById" + mOrder.toString());

                applyOrderToViews();
                if (map != null) {
                    updateMapWithOrder();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (mOrder != null) {
            updateMapWithOrder();
        }
    }

    /**
     * Updates the map with the order details.
     */
    private void updateMapWithOrder() {
        if (map == null || mOrder == null) {
            return;
        }

        LatLng orderLocation = new LatLng(mOrder.getLatitude(), mOrder.getLongitude());
        // Add marker
        map.addMarker(new MarkerOptions()
                .flat(true)
                .draggable(false)
                .position(orderLocation));

        // Zoom map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(orderLocation, 5f));
    }

    private void startOrderNotification() {
        Intent serviceIntent = new Intent(this, OrderInProgressNotification.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}