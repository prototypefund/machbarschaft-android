package com.ks.einanrufhilft.Database;

import android.util.Log;

import com.ks.einanrufhilft.Database.Callback.CollectionLoadedCallback;
import com.ks.einanrufhilft.Database.Entitie.Account;
import com.ks.einanrufhilft.Database.Entitie.Order;
import com.ks.einanrufhilft.Database.Entitie.Order_Account;

import java.util.AbstractMap;
import java.util.LinkedHashMap;

/**
 * Handles the access to with the database.
 */
public class DataAccess extends Database {

    private static DataAccess dataAccess;

    private DataAccess() {}

    public static DataAccess getInstance() {
        if (dataAccess == null) {
            dataAccess = new DataAccess();
        }
        return dataAccess;
    }

    /**
     * The status of a order. It can be open, which means that help is wanted.
     * Confirmed means, that a user accepted the order and its closed once the order is finished.
     */
    public enum Status
    {
        Open, Confirmed, Closed;
    }

    public void createAccount(Account account) {
        Log.i("TEST", "createAccount: ");

        super.addDocument(CollectionName.Account, account);
    }

    public void login(String phone_number, CollectionLoadedCallback callback) {
        Log.i("TEST", "login: ");

        super.getOneDocumentByCondition(CollectionName.Account, new AbstractMap.SimpleEntry<>("phone_number", phone_number),
                document -> {
                    Storage.getInstance().setUserID((String) document.getId());
                    if(callback != null) {
                        Account account = new Account(document);
                        callback.onOrderLoaded(account);
                    }
                });
    }

    public void getMyOrder(String phone_number) {
        Log.i("TEST", "getMyOrder: ");

        LinkedHashMap<String, Object> conditions = new LinkedHashMap<>();
        conditions.put("phone_number", phone_number);
        conditions.put("status", "confirmed");
        super.getOneDocumentByTwoConditions(CollectionName.Order_Account, conditions,
                document -> {
                    super.getDocumentById(CollectionName.Order ,document.getId(),
                            document2 -> {
                                Storage.getInstance().setCurrentOrder(new Order(document2));
                            });
                });
    }

    public void getOrderById(String orderId, CollectionLoadedCallback callback) {

        super.getDocumentById(CollectionName.Order, orderId, document -> {
            if (document != null) {
                Order order = new Order(document);
                callback.onOrderLoaded(order);
            }
        });
    }

    public void getOrders() {
        super.getCollection(CollectionName.Order, documents -> {
            OrderHandler.getInstance().addCollection(documents);
            //TODO:
            OrderHandler.getInstance().setLieferant(OrderHandler.Type.Besteller, 50.555809, 9.680845);

        });
    }

    public void setOrderStatus(String orderId, Status status) {
        Log.i("TEST", "setOrderStatus: ");

        if (status == Status.Confirmed) {
            // update Status in Collection Order
            super.updateDocument(CollectionName.Order, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()));

            //  Adds Entry in Account order
            Order_Account orderAccount = new Order_Account();
            orderAccount.setStatus(status.toString());
            orderAccount.setAccount_id(Storage.getInstance().getUserID());
            orderAccount.setOrder_id(orderId);
            super.addDocument(CollectionName.Order_Account, orderAccount);
        } else if(status == Status.Closed) {
            // update status in Collection Order
            super.updateDocument(CollectionName.Order, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()));

            // update Status in Order_Account
            super.updateDocument(CollectionName.Order_Account, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()));
        }
    }




}
