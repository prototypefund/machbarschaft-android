package jetzt.machbarschaft.android.database;

import android.util.Log;

import java.util.AbstractMap;
import java.util.LinkedHashMap;

import javax.security.auth.callback.Callback;

import jetzt.machbarschaft.android.database.callback.CollectionLoadedCallback;
import jetzt.machbarschaft.android.database.callback.WasSuccessfullCallback;
import jetzt.machbarschaft.android.database.entitie.Account;
import jetzt.machbarschaft.android.database.entitie.Order;
import jetzt.machbarschaft.android.database.entitie.OrderAccount;

/**
 * Handles the access to with the database.
 */
public class DataAccess extends Database {

    private static DataAccess dataAccess;

    private DataAccess() {
    }

    public static DataAccess getInstance() {
        if (dataAccess == null) {
            dataAccess = new DataAccess();
        }
        return dataAccess;
    }

    /**
     * The status of a order. It can be open, which means that help is wanted.
     * Confirmed means, that a user accepted the order and its closed once the order is finished.
     * <p>
     * Use {@link Order.Status} instead.
     */
    @Deprecated
    public enum Status {
        OPEN, CONFIRMED, CLOSED;
    }

    public void createAccount(Account account) {
        createAccount(account, null);
    }

    public void createAccount(Account account, WasSuccessfullCallback callback) {
        super.addDocument(CollectionName.Account, account, callback);
    }

    public void login(String phone_number, CollectionLoadedCallback callback) {

        super.getOneDocumentByCondition(CollectionName.Account, new AbstractMap.SimpleEntry<>("phone_number", phone_number),
                document -> {
                    Storage.getInstance().setUserID((String) document.getId());
                    if (callback != null) {
                        Account account = new Account(document);
                        callback.onOrderLoaded(account);
                    }
                });
    }

    public void getMyOrder(String phone_number) {


        LinkedHashMap<String, Object> conditions = new LinkedHashMap<>();
        conditions.put("phone_number", phone_number);
        conditions.put("status", "confirmed");
        super.getOneDocumentByTwoConditions(CollectionName.Order_Account, conditions,
                document -> {
                    super.getDocumentById(CollectionName.Order, document.getId(),
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
        getOrders(null);
    }

    public void getOrders(WasSuccessfullCallback callback) {
        super.getCollection(CollectionName.Order, documents -> {
            OrderHandler.getInstance().addCollection(documents);
            //TODO:
            OrderHandler.getInstance().setUserPosition(OrderHandler.Type.Besteller, 50.555809, 9.680845);
            if (callback != null) {
                if (documents != null) {
                    callback.wasSuccessful(true);
                } else {
                    callback.wasSuccessful(false);
                }
            }
        });
    }

    public void setOrderStatus(String orderId, Status status) {

        if (status == Status.CONFIRMED) {
            // update Status in Collection Order
            super.updateDocument(CollectionName.Order, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()));

            //  Adds Entry in Account order
            OrderAccount orderAccount = new OrderAccount();
            orderAccount.setStatus(status.toString());
            orderAccount.setAccountId(Storage.getInstance().getUserID());
            orderAccount.setOrderId(orderId);
            super.addDocument(CollectionName.Order_Account, orderAccount);
        } else if (status == Status.CLOSED) {
            // update status in Collection Order
            super.updateDocument(CollectionName.Order, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()));

            // update Status in Order_Account
            super.updateDocument(CollectionName.Order_Account, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()));
        }
    }

    public void setOrderStatus(String orderId, Status status, WasSuccessfullCallback callback) {
        if (status == Status.CONFIRMED) {
            // update Status in Collection Order
            super.updateDocument(CollectionName.Order, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()), successful -> {
                if (successful) {
                    //  Adds Entry in Account order
                    OrderAccount orderAccount = new OrderAccount();
                    orderAccount.setStatus(status.toString());
                    orderAccount.setAccountId(Storage.getInstance().getUserID());
                    orderAccount.setOrderId(orderId);
                    super.addDocument(CollectionName.Order_Account, orderAccount, innerSuccessful -> {
                        if (innerSuccessful) {
                            callback.wasSuccessful(true);
                        } else {
                            callback.wasSuccessful(false);
                        }
                    });
                } else {
                    callback.wasSuccessful(false);
                }
            });
        } else if (status == Status.CLOSED) {
            // update status in Collection Order
            super.updateDocument(CollectionName.Order, orderId, new AbstractMap.SimpleEntry<String, Object>("status", status.toString()), successful -> {
                if (successful) {

                    super.getOneDocumentByCondition(CollectionName.Order_Account, new AbstractMap.SimpleEntry<String, Object>("order_id", (Object)orderId),
                            document -> {
                                if (document.getId() != null) {
                                    // update Status in Order_Account
                                    super.updateDocument(CollectionName.Order_Account, document.getId(), new AbstractMap.SimpleEntry<String, Object>("status", status.toString()), innerSuccessful -> {
                                        if (innerSuccessful) {
                                            callback.wasSuccessful(true);
                                        } else {
                                            callback.wasSuccessful(false);
                                        }
                                    });
                                } else {
                                    callback.wasSuccessful(false);
                                }
                            });
                } else {
                    callback.wasSuccessful(false);
                }
            });


        }
    }

}
