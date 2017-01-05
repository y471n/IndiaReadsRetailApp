package com.indiareads.retailapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Order;
import com.indiareads.retailapp.viewholder.MyOrdersViewHolder;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by vijay on 9/4/2015.
 */
public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrdersViewHolder> {

    List<Order> ordersLists = Collections.emptyList();
    LayoutInflater mLayoutInflater;
    Context context;
    private ClickListener clickListener;

    public MyOrderListAdapter(Context context, List<Order> ordersLists){
        this.ordersLists=ordersLists;
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyOrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.my_order_list_items, parent, false);
        MyOrdersViewHolder myOrdersViewHolder = new MyOrdersViewHolder(view, clickListener);


        return myOrdersViewHolder;
    }

    @Override
    public void onBindViewHolder(MyOrdersViewHolder myOrdersViewHolder, int position) {

        Order orderItem = ordersLists.get(position);

        if(orderItem.getBookshelf_order_id()!=null) {
            myOrdersViewHolder.getOrderId()
                    .setText(orderItem.getBookshelf_order_id());
        }
        if(orderItem.getOrder_date()!=null) {
            myOrdersViewHolder.getOrderDate().setText(new StringTokenizer(orderItem.getOrder_date()).nextToken());
        }

        if(orderItem.getTitle()!=null) {
               myOrdersViewHolder.getBookName().setText(orderItem.getTitle());
        }

        if(orderItem.getContributor_name1()!=null) {
            myOrdersViewHolder.getAuthorName().setText(orderItem.getContributor_name1());
        }

        if(orderItem.getOrder_status()!=null) {
            if(orderItem.getOrder_status().equals("0")){
                myOrdersViewHolder.getOrderStatus().setText("Incomplete Order");
                myOrdersViewHolder.getOrderStatusDate().setVisibility(View.GONE);
                myOrdersViewHolder.getDotText().setVisibility(View.GONE);
            }else if(orderItem.getOrder_status().equals("-1")){
                myOrdersViewHolder.getOrderStatus().setText("Cancelled");
                myOrdersViewHolder.getOrderStatusDate().setVisibility(View.GONE);
                myOrdersViewHolder.getDotText().setVisibility(View.GONE);
            }else if(orderItem.getOrder_status().equals("8")) {
                myOrdersViewHolder.getOrderStatus().setText("Returned");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getReturned()).nextToken()));
            } else if(orderItem.getOrder_status().equals("7")) {
                myOrdersViewHolder.getOrderStatus().setText("Send To Courier");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getReq_to_fedex()).nextToken()));
            } else if(orderItem.getOrder_status().equals("6")) {
                myOrdersViewHolder.getOrderStatus().setText("Pick Up Processing");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getPickup_processing()).nextToken()));
            }else if(orderItem.getOrder_status().equals("5")) {
                myOrdersViewHolder.getOrderStatus().setText("Pick Up request Receveied");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getNew_pickup()).nextToken()));
            }else if(orderItem.getOrder_status().equals("4")) {
                myOrdersViewHolder.getOrderStatus().setText("Delivered");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getDelivered()).nextToken()));
            }else if(orderItem.getOrder_status().equals("3")) {
                myOrdersViewHolder.getOrderStatus().setText("Dispatched");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getDispatched()).nextToken()));
            }else if(orderItem.getOrder_status().equals("2")) {
                myOrdersViewHolder.getOrderStatus().setText("Preparing For Dispatch");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getDelivery_processing()).nextToken()));
            }else if(orderItem.getOrder_status().equals("1")) {
                myOrdersViewHolder.getOrderStatus().setText("Request Received");
                myOrdersViewHolder.getOrderStatusDate().setText((new StringTokenizer(orderItem.getNew_delivery()).nextToken()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return ordersLists.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
