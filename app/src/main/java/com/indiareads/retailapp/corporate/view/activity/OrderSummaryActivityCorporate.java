package com.indiareads.retailapp.corporate.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.BookShelfActivity;
import com.indiareads.retailapp.views.activities.MyAccountActivity;
import com.indiareads.retailapp.views.activities.MyCartActivity;
import com.indiareads.retailapp.views.activities.MyOrderActivity;
import com.indiareads.retailapp.views.activities.StoreCreditsActivity;
import com.indiareads.retailapp.views.activities.ThankYouActivity;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

public class OrderSummaryActivityCorporate extends AppCompatActivity {

    Toolbar toolbar;
    public static final String TAG = "PayUMoney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary_corporate);
            createToolbar();

    }
    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setLogo(R.drawable.logo);

    }

    public void startThankuPageActivity(String paymentId) {
        Intent intent = new Intent(this, ThankYouActivityCorporate.class);
        intent.putExtra("paymentId",paymentId);
        startActivity(intent);
        this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                //Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                //   showDialogMessage( "Payment Success Id : " + paymentId);
                startThankuPageActivity(paymentId);
            } else if (resultCode == RESULT_CANCELED) {
                //Log.i(TAG, "failure");
                showDialogMessage("cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                //Log.i("app_activity", "failed");

//                if (data != null) {
//                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {
//
//                    } else {
                showDialogMessage("failed");
//                    }
            }
            //Write your code if there's no result
        } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
            //Log.i(TAG, "User returned without login");
//            showDialogMessage("User returned without login");
        }
    }



    private void showDialogMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(TAG);
        builder.setMessage("Payment "+message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_corporate_short, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_book_shelf:
                intent = new Intent(this, BookShelfActivityCorporate.class);
                intent.putExtra("OpenFragment", "0");
                startActivity(intent);
                return true;
            case R.id.action_return_books:
                intent = new Intent(this, BookShelfActivityCorporate.class);
                intent.putExtra("OpenFragment", "1");
                startActivity(intent);
                return true;
            case R.id.action_my_orders:
                intent = new Intent(this, MyOrderActivityCorporate.class);
                startActivity(intent);
                return true;
            case R.id.my_profile:
                intent = new Intent(this, MyAccountActivityCorporate.class);
                startActivity(intent);
                return true;
            case R.id.action_home:
                intent = new Intent(this, MainActivityCorporate.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case android.R.id.home:
                finish();

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
