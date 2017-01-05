package com.indiareads.retailapp.corporate.view.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.indiareads.retailapp.R;
import com.indiareads.retailapp.adapters.HorizontalBookListAdapter;
import com.indiareads.retailapp.corporate.view.activity.MyCartActivityCorporate;
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.corporate.models.CartModelCorporate;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsCorporate;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProductPageFragmentCorporate extends Fragment {



    View fragmentRootView;
    ImageView summary_of_the_book_arrow,about_author_arrow,book_details_arrow;
    TextView bookSummary,aboutAuthor;
    LinearLayout bookLongDescription;
    Book bookDetails=null;

    public static final String SUMMARY_OF_THE_BOOK="summary_of_the_book";
    public static final String ABOUT_AUTHOR="about_author";
    public static final String BOOK_DETAILS="book_details";

    String isbnNumber;

    ProgressDialog PD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRootView= inflater.inflate(R.layout.fragment_product_page_corporate, container, false);

        setRetryButtonClickListener();
        checkInternetConnectivity();

        return fragmentRootView;
    }


    private void setRetryButtonClickListener() {
        fragmentRootView.findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRootView.findViewById(R.id.error_linear_layout).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.product_page_complete_layout).setVisibility(View.VISIBLE);
                executeOtherMethods();
            }
        });
    }

    private void checkInternetConnectivity() {
        if(!CommonMethods.hasActiveInternetConnection(getActivity())){
            showNetworkNotAvailable();
        }else{
            executeOtherMethods();
        }
    }

    public void showNetworkNotAvailable(){
        (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
        ((TextView)fragmentRootView.findViewById(R.id.product_page_msg)).setText(R.string.no_internet_connection_msg);
        (fragmentRootView.findViewById(R.id.product_page_complete_layout)).setVisibility(View.GONE);
    }

    private void executeOtherMethods() {
        isbnNumber=getActivity().getIntent().getExtras().getString("isbn_num");

        fetchViewsFromXml();
        showLoadingScreen();
        fetchBookDetails();
        setClickListeners();
    }


    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.product_page_content).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.product_page_loading_screen).setVisibility(View.VISIBLE);
    }


    private void
    setClickListeners(){
        setSummaryOfBookClickListener();
        setAuthorDetailsClickListener();
        setBookDetailsClickListener();
        setOrderNowClickListener();
    }


    private void fetchViewsFromXml(){

        summary_of_the_book_arrow= (ImageView)fragmentRootView.findViewById(R.id.summary_arrow);
        about_author_arrow= (ImageView)fragmentRootView.findViewById(R.id.author_arrow);
        book_details_arrow= (ImageView)fragmentRootView.findViewById(R.id.book_details_arrow);
        aboutAuthor=(TextView)fragmentRootView.findViewById(R.id.author_details);
        bookSummary=(TextView)fragmentRootView.findViewById(R.id.summary_of_the_book);
        bookLongDescription=(LinearLayout)fragmentRootView.findViewById(R.id.book_details);
    }





    private void setOrderNowClickListener() {
        fragmentRootView.findViewById(R.id.order_now_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveBookToCart();
//                startMyCartActivity();
                checkBookAvailabilityForRent(UrlsCorporate.CHECK_BOOK_AVAILABILITY_FOR_RENT);
            }
        });
    }


    private void setSummaryOfBookClickListener() {
        fragmentRootView.findViewById(R.id.summary_of_book_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeVisibilityOfCollapsableMenu(SUMMARY_OF_THE_BOOK);
            }
        });
    }

    private void setAuthorDetailsClickListener() {
        fragmentRootView.findViewById(R.id.author_details_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeVisibilityOfCollapsableMenu(ABOUT_AUTHOR);


            }
        });
    }

    private void setBookDetailsClickListener() {
        fragmentRootView.findViewById(R.id.book_detail_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeVisibilityOfCollapsableMenu(BOOK_DETAILS);

            }
        });
    }



    public void changeVisibilityOfCollapsableMenu(String text){

        switch(text){

            case SUMMARY_OF_THE_BOOK:          actionOnClickOfSummaryOfBook();
                break;

            case ABOUT_AUTHOR       :          actionOnClickOfAboutAuthor();
                break;

            case BOOK_DETAILS       :          actionOnClickOfBookDetails();
                break;


        }

    }

    public void actionOnClickOfSummaryOfBook(){

        if(bookSummary.getVisibility()==View.GONE){
            makeVisibleSummaryOfBooksAndHideOthers();
        }else{
            bookSummary.setVisibility(View.GONE);
            summary_of_the_book_arrow.setImageResource(R.drawable.down_arrow);
        }

    }

    public void actionOnClickOfAboutAuthor(){
        if(aboutAuthor.getVisibility()==View.GONE){
            makeVisibleAboutAuthorAndHideOthers();
        }else{
            aboutAuthor.setVisibility(View.GONE);
            about_author_arrow.setImageResource(R.drawable.down_arrow);
        }
    }


    public void actionOnClickOfBookDetails(){
        if(bookLongDescription.getVisibility()==View.GONE){
            makeVisibleBookDetailsAndHideOthers();
        }else{
            bookLongDescription.setVisibility(View.GONE);
            book_details_arrow.setImageResource(R.drawable.down_arrow);

        }

    }



    public void makeVisibleSummaryOfBooksAndHideOthers(){
        bookSummary.setVisibility(View.VISIBLE);
        aboutAuthor.setVisibility(View.GONE);
        bookLongDescription.setVisibility(View.GONE);
        //  bookReviews.setVisibility(View.GONE);
        summary_of_the_book_arrow.setImageResource(R.drawable.up_arrow);

    }

    public void makeVisibleAboutAuthorAndHideOthers(){
        bookSummary.setVisibility(View.GONE);
        aboutAuthor.setVisibility(View.VISIBLE);
        bookLongDescription.setVisibility(View.GONE);
//        bookReviews.setVisibility(View.GONE);
        about_author_arrow.setImageResource(R.drawable.up_arrow);
    }

    public void makeVisibleBookDetailsAndHideOthers(){
        bookSummary.setVisibility(View.GONE);
        aboutAuthor.setVisibility(View.GONE);
        bookLongDescription.setVisibility(View.VISIBLE);
//        bookReviews.setVisibility(View.GONE);
        book_details_arrow.setImageResource(R.drawable.up_arrow);
    }




    private void fetchBookDetails() {
        String url=getBookDetailsUrl();
        fetchBookDetailsFromRemoteServer(url);
    }

    public String getBookDetailsUrl(){
//        Log.e("product url is ", UrlsRetail.BOOK_DETAILS + "/" + isbnNumber);
        return UrlsRetail.BOOK_DETAILS+"/"+ isbnNumber;
    }

    private void fetchSimilarBooks(){
        String url=getSimilarBooksDetailsUrl();
        fetchSimilarBooksFromRemoteServer(url);
    }

    public String getSimilarBooksDetailsUrl(){

        return UrlsRetail.SIMILAR_BOOKS+"/"+ bookDetails.getIsbn13();
    }


    private void fetchBookDetailsFromRemoteServer(String url) {

        JsonObjectRequest jsonObjectRequest = getBooksDetailsJsonRequest(url);
        jsonObjectRequest.setTag(UrlsRetail.BOOK_DETAILS_VOLLEY_TAG);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


    private void fetchSimilarBooksFromRemoteServer(String url) {

        JsonObjectRequest jsonObjectRequest = getSimilarBooksJsonRequest(url);
        jsonObjectRequest.setTag(UrlsRetail.SIMILAR_BOOKS_VOLLEY_REQUEST_TAG);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        // createBooksList(R.id.similar_books_recycler_view);

    }


    private JsonObjectRequest getBooksDetailsJsonRequest(String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, getBookDetailsResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    showNetworkNotAvailable();
                }else{
                    (fragmentRootView.findViewById(R.id.error_linear_layout)).setVisibility(View.VISIBLE);
                    ((TextView)fragmentRootView.findViewById(R.id.product_page_msg)).setText(R.string.error_in_fetching_data);
                    (fragmentRootView.findViewById(R.id.product_page_complete_layout)).setVisibility(View.GONE);
                }
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.PRODUCT_PAGE_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }

    @NonNull
    private Response.Listener<JSONObject> getBookDetailsResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    createBookInfo(fetchedJson);
                    hideLoadingScreen();
                    showLoadingScreenForSimilarBooks();
                    fetchSimilarBooks();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }




    private void showLoadingScreenForSimilarBooks() {
        fragmentRootView.findViewById(R.id.similar_books_loading_screen).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.similar_books_recycler_view).setVisibility(View.GONE);
    }
    private void hideLoadingScreenForSimilarBooks() {
        fragmentRootView.findViewById(R.id.similar_books_loading_screen).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.similar_books_recycler_view).setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        fragmentRootView.findViewById(R.id.product_page_content).setVisibility(View.VISIBLE);
        fragmentRootView.findViewById(R.id.product_page_loading_screen).setVisibility(View.GONE);
    }

    public void fillBookDetailsOnProductPage(Book bookDetails){

        ((TextView)fragmentRootView.findViewById(R.id.book_title)).setText(bookDetails.getTitle());
        ((TextView)fragmentRootView.findViewById(R.id.author_name)).setText(bookDetails.getAuthor1());


        if(bookDetails.getBookSummary().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.summary_of_the_book)).setText(bookDetails.getBookSummary());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.summary_of_the_book)).setText("Summary Not Available");
        }
        if(bookDetails.getAuthorBio().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.author_details)).setText(bookDetails.getAuthorBio());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.author_details)).setText("Author information not available");
        }


        // fetching images
        ImageLoader mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        (fragmentRootView.findViewById(R.id.book_image)).setBackgroundResource(R.drawable.test);
        String imageUrl= UrlsRetail.IMAGES+bookDetails.getIsbn13()+".jpg";
        ((NetworkImageView)fragmentRootView.findViewById(R.id.book_image)).setImageUrl(imageUrl, mImageLoader);


        if(bookDetails.getIsbn13()!=null && bookDetails.getIsbn13().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_isbn)).setText(bookDetails.getIsbn13());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_isbn)).setText("NA");
        }



        if(bookDetails.getPublisherName()!=null && bookDetails.getPublisherName().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_publisher)).setText(bookDetails.getPublisherName());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_publisher)).setText("NA");
        }

        if(bookDetails.getImprintName()!=null && bookDetails.getImprintName().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_imprint)).setText(bookDetails.getImprintName());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_imprint)).setText("NA");
        }

        if(bookDetails.getTotalPages()!=null && bookDetails.getTotalPages().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_number_of_pages)).setText(bookDetails.getTotalPages());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_number_of_pages)).setText("NA");
        }

        if(bookDetails.getPublicationDate()!=null && bookDetails.getPublicationDate().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_publication_date)).setText(bookDetails.getPublicationDate());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_publication_date)).setText("NA");
        }

        if(bookDetails.getTextLanguage()!=null && bookDetails.getTextLanguage().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_language)).setText(bookDetails.getTextLanguage());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_language)).setText("NA");
        }

        if(bookDetails.getBinding()!=null && bookDetails.getBinding().length()>0) {
            ((TextView) fragmentRootView.findViewById(R.id.book_binding)).setText(bookDetails.getBinding());
        }else{
            ((TextView) fragmentRootView.findViewById(R.id.book_binding)).setText("NA");
        }


    }


    private JsonObjectRequest getSimilarBooksJsonRequest(String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, getSimilarBooksResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getSimilarBooksResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    createBooksList(fetchedJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }



    public void createBookInfo(JSONObject fetchedJson){

        try {
            JSONObject List = fetchedJson.getJSONObject("data");
            Gson gson = new Gson();
            bookDetails = gson.fromJson(List
                    .toString(), Book.class);
            fillBookDetailsOnProductPage(bookDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void createBooksList(JSONObject fetchedJson){

        List<Book> displayBookInfo = new ArrayList<>();
        try {
            JSONArray List = fetchedJson.getJSONArray("data");
            Gson gson = new Gson();
            if(List.length()>0) {
                for (int i = 0; i < List.length(); i++) {
                    JSONObject jsonObject = List.getJSONObject(i);
                    Book book = gson.fromJson(jsonObject.toString(), Book.class);

                    displayBookInfo.add(book);
                }
                createRecycleListView(displayBookInfo);
                hideLoadingScreenForSimilarBooks();
            }else{
                fragmentRootView.findViewById(R.id.similar_books_loading_screen).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.similar_books_recycler_view).setVisibility(View.GONE);
                fragmentRootView.findViewById(R.id.similar_books_title).setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createRecycleListView(final List<Book> booksList) {
        if(getActivity()==null){
            return;
        }
        RecyclerView mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.similar_books_recycler_view);
        HorizontalBookListAdapter bookListAdapter = new HorizontalBookListAdapter(getActivity(), booksList);
        mRecyclerView.setAdapter(bookListAdapter);
        bookListAdapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                intent.putExtra("isbn_num",booksList.get(position).getIsbn13());
//                intent.putExtra("isbn_num",booksList.get(position).getIsbn13());
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    public void showLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.processing));
        PD.setCancelable(false);
        PD.show();
    }

    private void checkBookAvailabilityForRent(String urls) {
        showLoadingDialog();
        JsonObjectRequest jsonObjectRequest;
        try {
            jsonObjectRequest = getCheckBookAvailabilityForRentJsonRequest(urls);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }

    private JsonObjectRequest getCheckBookAvailabilityForRentJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("isbn13",bookDetails.getIsbn13());
        obj.put("title",bookDetails.getTitle());
        obj.put("contributor_name_1",bookDetails.getAuthor1());
        obj.put("user_id", getUserId());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,obj, getCheckBookAvailabilityForRentResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    showNoIntenetDuringCheckingBookAvailablityForRent();
                }else{
                    showErrorInCheckingBookForRent();
                }
            }
        });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultMaxRetries.APP_API_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonRequest;
    }


    @NonNull
    private Response.Listener<JSONObject> getCheckBookAvailabilityForRentResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    Log.e("rent response",fetchedJson.toString());
                    PD.dismiss();
                    boolean status=fetchedJson.getBoolean("status");
                    if(status){
                        saveBookToCart();
                        startMyCartActivity();
                    }else{
                        showBookNotAvailableForRent();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void startMyCartActivity(){
        Intent intent = new Intent(getActivity(), MyCartActivityCorporate.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void saveBookToCart()
    {
        CartModelCorporate cartItem;
        CartModelCorporate existingBook =
                    new Select().from(CartModelCorporate.class).where("isbn13 = ?", bookDetails.getIsbn13())
                            .executeSingle();
            if(existingBook==null) {
                cartItem = new CartModelCorporate(bookDetails.getTitle(), bookDetails.getAuthor1(), bookDetails.getIsbn13(), bookDetails.getRent().getBookId(), bookDetails.getRent().getMerchantLibrary(), bookDetails.getRent().getBookLibrary());
                cartItem.save();
            }
    }


    public void showBookNotAvailableForRent(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Sorry Book Is Not Available For Rent.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void showNoIntenetDuringCheckingBookAvailablityForRent(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("No internet Connectivity detected. Please Reconnect and Try again!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        checkBookAvailabilityForRent(UrlsCorporate.CHECK_BOOK_AVAILABILITY_FOR_RENT);
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public void showErrorInCheckingBookForRent(){
        final  AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Oops something went wrong!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBookAvailabilityForRent(UrlsCorporate.CHECK_BOOK_AVAILABILITY_FOR_RENT);
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }






}
