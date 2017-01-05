package com.indiareads.retailapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.indiareads.retailapp.R;
import com.indiareads.retailapp.interfaces.ClickListener;

/**
 * Created by vijay on 9/4/2015.
 */
public class StoreCreditsViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView issueDate;
    private TextView amount;
    private TextView orderId;
//    private TextView deductionReason;
    private TextView status;

    private ClickListener clickListener;

    public TextView getIssueDate() {
        return issueDate;
    }

    public TextView getAmount() {
        return amount;
    }

    public TextView getOrderId() {
        return orderId;
    }

//    public TextView getDeductionReason() {
//        return deductionReason;
//    }

    public TextView getStatus() {
        return status;
    }

    public StoreCreditsViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;
        this.issueDate = (TextView) itemView.findViewById(R.id.issue_date);
        this.amount = (TextView) itemView.findViewById(R.id.amount);
        this.orderId = (TextView) itemView.findViewById(R.id.order_id);
//        this.deductionReason = (TextView) itemView.findViewById(R.id.deduction_reason);
        this.status = (TextView) itemView.findViewById(R.id.status);

    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.itemClicked(v, getAdapterPosition());
        }
    }
}