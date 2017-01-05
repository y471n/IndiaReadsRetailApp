package com.indiareads.retailapp.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
import com.indiareads.retailapp.interfaces.ClickListener;
import com.indiareads.retailapp.models.Book;
import com.indiareads.retailapp.models.CartModel;
import com.indiareads.retailapp.singleton.VolleySingleton;
import com.indiareads.retailapp.utils.CommonMethods;
import com.indiareads.retailapp.utils.DefaultMaxRetries;
import com.indiareads.retailapp.utils.UrlsRetail;
import com.indiareads.retailapp.views.activities.MyCartActivity;
import com.indiareads.retailapp.views.activities.ProductPageActivity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;



public class ProductPageFragment extends Fragment {


    View fragmentRootView;
    ImageView summary_of_the_book_arrow,about_author_arrow,book_details_arrow,reviews_arrow;
    ImageView thirty_days_radio_button_image,ninty_days_radio_button_image,one_eighty_days_radio_button_image,three_sixty_days_radio_button_image;
    TextView  bookRent,bookSummary,aboutAuthor,bookReviews;
     LinearLayout bookLongDescription;
    Book bookDetails=null;

    public static final String SUMMARY_OF_THE_BOOK="summary_of_the_book";
    public static final String ABOUT_AUTHOR="about_author";
    public static final String BOOK_DETAILS="book_details";
    public static final String REVIEWS="reviews";

    String isbnNumber;

    ProgressDialog PD;
    private AlertDialog alertDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentRootView =inflater.inflate(R.layout.fragment_product_page, container, false);

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

    private void setNoInternetAccessBackgroundImage() {
//        fragmentRootView.findViewById(R.id.app_screen_background_image).setVisibility(View.VISIBLE);
//        fragmentRootView.findViewById(R.id.product_page_complete_layout).setVisibility(View.GONE);
    }

    public void showLoadingScreen(){
        fragmentRootView.findViewById(R.id.product_page_content).setVisibility(View.GONE);
        fragmentRootView.findViewById(R.id.product_page_loading_screen).setVisibility(View.VISIBLE);
    }


    private void setClickListeners(){
        setSummaryOfBookClickListener();
        setAuthorDetailsClickListener();
        setBookDetailsClickListener();
        //setReviewsClickListener();

        setThirtyDaysClickListener();
        setNintyDaysClickListener();
        setOneEightyDaysDaysClickListener();
        setThreeSixtyDaysClickListener();

        setRentNowClickListener();
        setBuyNowClickListener();
//        setAddToWishListOfBuyBookClickListener();
    }

//book_long_description

    private void fetchViewsFromXml(){

        summary_of_the_book_arrow= (ImageView)fragmentRootView.findViewById(R.id.summary_arrow);
        about_author_arrow= (ImageView)fragmentRootView.findViewById(R.id.author_arrow);
        book_details_arrow= (ImageView)fragmentRootView.findViewById(R.id.book_details_arrow);
//        reviews_arrow= (ImageView)fragmentRootView.findViewById(R.id.reviws_arrow);
        thirty_days_radio_button_image= (ImageView)fragmentRootView.findViewById(R.id.thirty_days_radio_button_image);ninty_days_radio_button_image= (ImageView)fragmentRootView.findViewById(R.id.ninty_days_radio_button_image);one_eighty_days_radio_button_image= (ImageView)fragmentRootView.findViewById(R.id.one_eighty_days_radio_button_image);three_sixty_days_radio_button_image= (ImageView)fragmentRootView.findViewById(R.id.three_sixty_days_radio_button_image);

        bookRent=(TextView)fragmentRootView.findViewById(R.id.book_rent);
        aboutAuthor=(TextView)fragmentRootView.findViewById(R.id.author_details);
        bookSummary=(TextView)fragmentRootView.findViewById(R.id.summary_of_the_book);
        bookLongDescription=(LinearLayout)fragmentRootView.findViewById(R.id.book_details);
      //  bookReviews=(TextView)fragmentRootView.findViewById(R.id.book_reviews);


    }



    private void setThirtyDaysClickListener() {
        fragmentRootView.findViewById(R.id.thirty_days_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAllRadioButtonWhite();
                thirty_days_radio_button_image.setImageResource(R.drawable.rent_selected);
                if(bookDetails.getRent().getMrp()!=null && bookDetails.getRent().getRent()[0]!=null) {
                    long saveRs = Long.parseLong(bookDetails.getRent().getMrp()) - Long.parseLong(bookDetails.getRent().getRent()[0]);
                    ((TextView) fragmentRootView.findViewById(R.id.save_rs)).setText(String.valueOf(saveRs));
                }
                if(bookDetails.getRent().getRent()[0]!=null) {
                    bookRent.setText(bookDetails.getRent().getRent()[0]);
                }

            }
        });
    }

    private void setNintyDaysClickListener() {
        fragmentRootView.findViewById(R.id.ninty_days_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeAllRadioButtonWhite();
                ninty_days_radio_button_image.setImageResource(R.drawable.rent_selected);
                if(bookDetails.getRent().getMrp()!=null && bookDetails.getRent().getRent()[1]!=null) {
                    long saveRs = Long.parseLong(bookDetails.getRent().getMrp()) - Long.parseLong(bookDetails.getRent().getRent()[1]);
                    ((TextView) fragmentRootView.findViewById(R.id.save_rs)).setText(String.valueOf(saveRs));
                }
                if((bookDetails.getRent().getRent()[1]!=null)) {
                    bookRent.setText(bookDetails.getRent().getRent()[1]);
                }
            }
        });
    }


    private void setOneEightyDaysDaysClickListener() {
        fragmentRootView.findViewById(R.id.one_eighty_days_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeAllRadioButtonWhite();
                one_eighty_days_radio_button_image.setImageResource(R.drawable.rent_selected);
                if(bookDetails.getRent().getMrp()!=null && bookDetails.getRent().getRent()[2]!=null) {
                    long saveRs = Long.parseLong(bookDetails.getRent().getMrp()) - Long.parseLong(bookDetails.getRent().getRent()[2]);
                    ((TextView) fragmentRootView.findViewById(R.id.save_rs)).setText(String.valueOf(saveRs));
                }
                if(bookDetails.getRent().getRent()[2]!=null) {
                    bookRent.setText(bookDetails.getRent().getRent()[2]);
                }
            }
        });
    }


    private void setThreeSixtyDaysClickListener() {
        fragmentRootView.findViewById(R.id.three_sixty_days_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeAllRadioButtonWhite();
                three_sixty_days_radio_button_image.setImageResource(R.drawable.rent_selected);
                if(bookDetails.getRent().getMrp()!=null && bookDetails.getRent().getRent()[3]!=null) {
                    long saveRs = Long.parseLong(bookDetails.getRent().getMrp()) - Long.parseLong(bookDetails.getRent().getRent()[3]);
                    ((TextView) fragmentRootView.findViewById(R.id.save_rs)).setText(String.valueOf(saveRs));
                }
                if(bookDetails.getRent().getRent()[3]!=null) {
                    bookRent.setText(bookDetails.getRent().getRent()[3]);
                }
            }
        });
    }


    private void makeAllRadioButtonWhite(){
        thirty_days_radio_button_image.setImageResource(R.drawable.rent_not_selected);
        ninty_days_radio_button_image.setImageResource(R.drawable.rent_not_selected);
        one_eighty_days_radio_button_image.setImageResource(R.drawable.rent_not_selected);
        three_sixty_days_radio_button_image.setImageResource(R.drawable.rent_not_selected);
    }


    private void setRentNowClickListener() {
        fragmentRootView.findViewById(R.id.rent_now_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBookAvailabilityForRent(UrlsRetail.CHECK_BOOK_AVAILABILITY_FOR_RENT);
            }
        });
    }


    private void setBuyNowClickListener() {
        fragmentRootView.findViewById(R.id.buy_now_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bookDetails.getBuy_status().equals("1")){
                    saveBookToCart("1");
                    startMyCartActivity();

               }else{
                    if(CommonMethods.isUserLogedIn(getActivity())) {
                        showBookNotAvailabileForBuyWhenUserLoginDialog();
                    }else{
                        showBookNotAvailabileForBuyWhenUserNotLoginDialog();
                    }
                }

            }
        });
    }

    public void showBookNotAvailabileForBuyWhenUserLoginDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Sorry Book Is Not Availabile For Buy.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add To WishList",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addBookToBookShelf("1", UrlsRetail.ADD_TO_BOOKSHELF_FOR_BUY);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void showBookNotAvailabileForBuyWhenUserNotLoginDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Sorry Book Is Not Availabile For Buy.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


//    private void setAddToWishListOfBuyBookClickListener() {
//
//        fragmentRootView.findViewById(R.id.buy_add_to_wishlist_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addBookToBookShelf("1");
//            }
//        });
//
//    }

    public void showAddToWishListLoadingDialog(){
        PD = new ProgressDialog(getActivity());
        PD.setTitle(getResources().getString(R.string.progress_dialog_please_wait));
        PD.setMessage(getResources().getString(R.string.adding_book_to_your_wishlist));
        PD.setCancelable(false);
        PD.show();
    }



    private boolean isUserLogedIn() {
            SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
              if(settings.contains(UrlsRetail.SAVED_USER_TOKEN)){
                  return  true;
              }else{
                  return  false;
              }
    }


    public void startMyCartActivity(){
        Intent intent = new Intent(getActivity(), MyCartActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

//    private void setReviewsClickListener() {
//        fragmentRootView.findViewById(R.id.reviews_linear_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                changeVisibilityOfCollapsableMenu(REVIEWS);
//
//            }
//        });
//    }



    public void changeVisibilityOfCollapsableMenu(String text){

           switch(text){

               case SUMMARY_OF_THE_BOOK:          actionOnClickOfSummaryOfBook();
                   break;

               case ABOUT_AUTHOR       :          actionOnClickOfAboutAuthor();
                   break;

               case BOOK_DETAILS       :          actionOnClickOfBookDetails();
                   break;

//               case REVIEWS            :          actionOnClickOfReviews();
//                   break;

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


//    public void actionOnClickOfReviews(){
//        if(bookReviews.getVisibility()==View.GONE){
//            makeVisibleReviewsHideAndOthers();
//        }else{
//            bookReviews.setVisibility(View.GONE);
//            reviews_arrow.setImageResource(R.drawable.down_arrow);
//        }
//    }


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

    public void makeVisibleReviewsHideAndOthers(){
        bookSummary.setVisibility(View.GONE);
        aboutAuthor.setVisibility(View.GONE);
        bookLongDescription.setVisibility(View.GONE);
     //   bookReviews.setVisibility(View.VISIBLE);
        reviews_arrow.setImageResource(R.drawable.up_arrow);
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

        // createBooksList(R.id.similar_books_recycler_view);

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


//    private void checkBuyStatusAndEnableDisableBuyButton() {
//        if(bookDetails.getBuy_status().equals("1")){
//          //  enableBuyBookButton();
//            Toast.makeText(getActivity(),"available for buy",Toast.LENGTH_SHORT).show();
//        }else{
//            enableAddToBookShelfOfBuyButton();
//            disableBuyBookButton()
//            Toast.makeText(getActivity(),"not available for buy",Toast.LENGTH_SHORT).show();
//        }
//    }



//    public void enableBuyBookButton(){
//        fragmentRootView.findViewById(R.id.buy_now_button).setVisibility(View.VISIBLE);
//    }

    public void disableBuyBookButton(){
       // fragmentRootView.findViewById(R.id.buy_now_button).setBackgroundColor(getResources().getColor(R.color.grey));
        fragmentRootView.findViewById(R.id.buy_now_button).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey));

       // fragmentRootView.findViewById(R.id.buy_now_button).setClickable(false);
    }

    public void enableAddToBookShelfOfBuyButton(){
        fragmentRootView.findViewById(R.id.buy_now_button).setVisibility(View.GONE);
//        fragmentRootView.findViewById(R.id.buy_add_to_wishlist_button).setVisibility(View.VISIBLE);
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
      //  ((TextView)fragmentRootView.findViewById(R.id.book_short_description)).setText(bookDetails.getShortDesc());

        ((TextView)fragmentRootView.findViewById(R.id.book_rent)).setText(bookDetails.getRent().getRent()[0]);
        if(bookDetails.getRent().getMrp()!=null && bookDetails.getRent().getRent()[0]!=null) {
            long saveRs = Long.parseLong(bookDetails.getRent().getMrp()) - Long.parseLong(bookDetails.getRent().getRent()[0]);
            ((TextView)fragmentRootView.findViewById(R.id.save_rs)).setText(String.valueOf(saveRs)+"");
        }
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
        ImageLoader  mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        (fragmentRootView.findViewById(R.id.book_image)).setBackgroundResource(R.drawable.test);
        String imageUrl= UrlsRetail.IMAGES+bookDetails.getIsbn13()+".jpg";
        ((NetworkImageView)fragmentRootView.findViewById(R.id.book_image)).setImageUrl(imageUrl, mImageLoader);

        ((TextView)fragmentRootView.findViewById(R.id.initial_payable)).setText(bookDetails.getRent().getInitialPayable());

        // setting rent Rs cost
        ((TextView) fragmentRootView.findViewById(R.id.book_mrp_2)).setText(bookDetails.getRent().getMrp());


        // setting buy info
      if(bookDetails.getBuy_status().equals("1")) {
          ((TextView) fragmentRootView.findViewById(R.id.book_mrp_1)).setText(bookDetails.getBuy().getBuy_mrp());
          long price=Long.parseLong(bookDetails.getBuy().getBuy_mrp())-Long.parseLong(bookDetails.getBuy().getDiscount());
          long discountPrice= Math.round(price);
          ((TextView)fragmentRootView.findViewById(R.id.buy_book_mrp)).setText(discountPrice+"");
      }else{
          fragmentRootView.findViewById(R.id.buy_details_linear_layout).setVisibility(View.GONE);
          disableBuyBookButton();
      }

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
                   // Log.e("similar books resp",fetchedJson.toString());
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
            makeEveryAmountRoundOf(bookDetails);
            fillBookDetailsOnProductPage(bookDetails);


        } catch (JSONException e) {
          //  Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
         //   Log.e("error ",e.toString());
            e.printStackTrace();
        }
  }

    private void makeEveryAmountRoundOf(Book bookDetails) {
        // make buy mrp round of
        if(bookDetails.getBuy()!=null){
              if(bookDetails.getBuy().getBuy_mrp()!=null && bookDetails.getBuy().getBuy_mrp().length()>0){
                     long l=Math.round(Double.parseDouble(bookDetails.getBuy().getBuy_mrp()));
                     bookDetails.getBuy().setBuy_mrp(String.valueOf(l));
              }
        }

        // make buy discount round of
        if(bookDetails.getBuy()!=null){
            if(bookDetails.getBuy().getDiscount()!=null && bookDetails.getBuy().getDiscount().length()>0){
                long l=Math.round(Double.parseDouble(bookDetails.getBuy().getDiscount()));
                bookDetails.getBuy().setDiscount(String.valueOf(l));
            }
        }

        // make rent mrp round of
        if(bookDetails.getRent()!=null){
            if(bookDetails.getRent().getMrp()!=null && bookDetails.getRent().getMrp().length()>0){
                long l=Math.round(Double.parseDouble(bookDetails.getRent().getMrp()));
                bookDetails.getRent().setMrp(String.valueOf(l));
            }
        }

        // make rent initial payable round of
        if(bookDetails.getRent()!=null){
            if(bookDetails.getRent().getInitialPayable()!=null && bookDetails.getRent().getInitialPayable().length()>0){
                long l=Math.round(Double.parseDouble(bookDetails.getRent().getInitialPayable()));
                bookDetails.getRent().setInitialPayable(String.valueOf(l));
            }
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
        RecyclerView   mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.similar_books_recycler_view);
        HorizontalBookListAdapter  bookListAdapter = new HorizontalBookListAdapter(getActivity(), booksList);
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





    public void addBookToBookShelf(String status,String url){
        showAddToWishListLoadingDialog();
        sendBookDetailsToServerToAddInBookShelf(url, status);
    }

//    public String getAddToBookShelfUrl() {
//        return UrlsRetail.ADD_TO_BOOKSHELF;
//    }



    private void sendBookDetailsToServerToAddInBookShelf(String url,String status) {
        try {
            JsonObjectRequest jsonObjectRequest = getJsonRequest(url, status);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        }catch(Exception e){

        }
    }

    private JsonObjectRequest getJsonRequest(String url,String status) throws JSONException {


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,createAddToBookShelfJsonData(status), getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                if(error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(),"No Internet connection found",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(),"Oops Something Went Wrong ",Toast.LENGTH_LONG).show();
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
    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject fetchedJson = new JSONObject(response.toString());
                    PD.dismiss();
                    Toast.makeText(getActivity(),"Book Added Successfully",Toast.LENGTH_LONG).show();
                   // Log.e("wish list response ", fetchedJson.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }




    public JSONObject createAddToBookShelfJsonData(String status) throws JSONException {
        JSONObject obj=new JSONObject();
        obj.put("email",getEmailId());
        obj.put("isbn13",bookDetails.getIsbn13());
        obj.put("title",bookDetails.getTitle());
        obj.put("author",bookDetails.getAuthor1());
        obj.put("user_id",getUserId());
        obj.put("status",status);
      //  Log.e("add to bookshelf json",obj.toString());
        return obj;
    }

    public String getUserId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_USER_ID,"");
    }

    public String getEmailId(){
        SharedPreferences settings = getActivity().getSharedPreferences(UrlsRetail.SAVED_USER_DETAILS, getActivity().MODE_PRIVATE);
        // Reading from SharedPreferences
        return settings.getString(UrlsRetail.SAVED_EMAIL_ID,"");
    }


    public void saveBookToCart(String toBuy)
    {
      CartModel cartItem;
      if(toBuy.equals("1")){
        //  Log.e("data save for ","buy");
           cartItem = new CartModel(bookDetails.getTitle(), bookDetails.getAuthor1(),bookDetails.getIsbn13(),bookDetails.getRent().getMrp(),bookDetails.getRent().getInitialPayable(),bookDetails.getRent().getBookId(),bookDetails.getRent().getMerchantLibrary(),bookDetails.getRent().getBookLibrary(),toBuy,bookDetails.getBuy().getBuy_mrp(),bookDetails.getBuy().getDiscount());
           cartItem.save();
      }else{
        //  Log.e("data save for ","rent");

          CartModel existingBook =
                  new Select().from(CartModel.class).where("isbn13 = ?", bookDetails.getIsbn13())
                          .where("toBuy = ?", "0")
                          .executeSingle();
            if(existingBook==null) {
                  cartItem = new CartModel(bookDetails.getTitle(), bookDetails.getAuthor1(), bookDetails.getIsbn13(), bookDetails.getRent().getMrp(), bookDetails.getRent().getInitialPayable(), bookDetails.getRent().getBookId(), bookDetails.getRent().getMerchantLibrary(), bookDetails.getRent().getBookLibrary(), toBuy, null, null);
                cartItem.save();
             }

      }

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
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = getCheckBookAvailabilityForRentJsonRequest(urls);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JsonObjectRequest getCheckBookAvailabilityForRentJsonRequest(String url) throws JSONException {

        JSONObject obj=new JSONObject();
        obj.put("isbn13",bookDetails.getIsbn13());
        obj.put("init_pay",bookDetails.getRent().getInitialPayable());
        obj.put("mrp",bookDetails.getRent().getMrp());
        obj.put("title",bookDetails.getTitle());
        obj.put("contributor_name_1",bookDetails.getAuthor1());

        if(CommonMethods.isUserLogedIn(getActivity())){
            obj.put("is_user_login","1");
        }else{
            obj.put("is_user_login","0");
        }
        if(CommonMethods.isUserLogedIn(getActivity())) {
            obj.put("user_id", getUserId());
        }
     //   Log.e("check rent json is ", obj.toString());

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
                 //   Log.e("availability response ",fetchedJson.toString());
                    PD.dismiss();
                   boolean status=fetchedJson.getBoolean("status");
                    if(status==true){
                        saveBookToCart("0");
//                        if (isUserLogedIn()) {
                            startMyCartActivity();
//                        } else {
//                            startLoginActivity();
//                        }
                    }else{
                        if(CommonMethods.isUserLogedIn(getActivity())) {
                            showBookNotAvailabileForRentUserLoginDialog();
                        }else{
                            showBookNotAvailabileForRentUserNotLoginDialog();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public void showNoIntenetDuringCheckingBookAvailablityForRent(){
      final  AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("No internet Connectivity detected. Please Reconnect and Try again!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
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
                        checkBookAvailabilityForRent(UrlsRetail.CHECK_BOOK_AVAILABILITY_FOR_RENT);
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
        alertDialog.setMessage("Error in Checking Book is available For Rent");
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
                        checkBookAvailabilityForRent(UrlsRetail.CHECK_BOOK_AVAILABILITY_FOR_RENT);
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


    public void showBookNotAvailabileForRentUserLoginDialog(){
       AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Sorry Book Is Not Available For Rent.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add To WishList",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addBookToBookShelf("0", UrlsRetail.ADD_TO_BOOKSHELF_FOR_RENT);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void showBookNotAvailabileForRentUserNotLoginDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Sorry Book Is Not Availabile For Rent.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


}
