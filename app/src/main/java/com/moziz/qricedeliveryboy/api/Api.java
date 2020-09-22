package com.moziz.qricedeliveryboy.api;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://demo.moziztech.com/qrice/webservices/";

    // BASE URL
    @POST("deliveryboy_login")
    Call<ResponseBody> login(@Body RequestBody body);
    @POST("deliveryboy_dashboard")
    Call<ResponseBody> dashBoard(@Header("deliveryboy-id") int deliveryBoyID);
    @POST("deliveryboy_forgotpassword")
    Call<ResponseBody> forgotPassword(@Body RequestBody jsonObject);
    @POST("deliveryboy_forgot_resetpassword  ")
    Call<ResponseBody> resetPassword(@Header("deliveryboy-id") int userID, @Body RequestBody jsonObject);
    @POST("deliveryboy_verifyotp ")
    Call<ResponseBody> otpVerification(@Header("deliveryboy-id") int userID, @Body RequestBody jsonObject);
    @POST("update_userlocation")
    Call<ResponseBody> updateLocation(@Header("user_id") int userID, @Body RequestBody body);

    @POST("deliveryboy_profile")
    Call<ResponseBody> getProfile(@Header("deliveryboy-id") int userID);
    @POST("deliveryboy_updateprofile")
    Call<ResponseBody> updateProfile(@Header("deliveryboy-id") int userID, @Body RequestBody js);
    @POST("getaddress/{addressID}")
    Call<ResponseBody> getAddress(@Header("user_id") int userID, @Path("addressID") int addressID);
    @POST("deleteaddress/{addressID}")
    Call<ResponseBody> deleteAddress(@Header("user_id") int userID, @Path("addressID") int addressID);
    @POST("addaddress")
    Call<ResponseBody> addAddress(@Header("user_id") int userID, @Body RequestBody js);
    @POST("deliveryboy_orders")
    Call<ResponseBody> deliveryOrdersList(@Header("deliveryboy-id") int userID, @Query("status") String status);
    @POST("products")
    Call<ResponseBody> productCategoryList(@Header("user_id") int userID, @Query("page_id") int pageID, @Query("limit") int limit, @Query("category_id") int categoryID);
    @POST("products")
    Call<ResponseBody> productBrandList(@Header("user_id") int userID, @Query("page_id") int pageID, @Query("limit") int limit, @Query("brand_id") int brandID);
    @GET("home")
    Call<ResponseBody> homeSlider();
    @POST("updateaddress/{addressID}")
    Call<ResponseBody> updateAddress(@Header("user_id") int userID, @Path("addressID") int addressID, @Body RequestBody responseBody);
    @POST("changepassword")
    Call<ResponseBody> changePassword(@Header("user_id") int userID, @Body RequestBody js);
    @POST("addtowishlist/{productID}")
    Call<ResponseBody> addToWishlist(@Header("user_id") int userID, @Path("productID") int productID);
    @POST("removewishlist/{wishlistID}")
    Call<ResponseBody> deleteToWishlist(@Header("user_id") int userID, @Path("wishlistID") int wishlistID);
//    @Headers("Content-Type: application/json")
    @POST("wishlists")
    Call<ResponseBody> wishlistsList(@Header("user_id") int userID);
    @POST("addtocart")
    Call<ResponseBody> addToCart(@Header("user_id") int userID, @Body RequestBody js);
    @POST("productdetail/{productID}")
    Call<ResponseBody> productDetail(@Header("user_id") int userID, @Path("productID") int productID);
    @POST("placeorder")
    Call<ResponseBody> placeOrder(@Header("user_id") int userID, @Body RequestBody js);
//    @POST("forgot_resetpassword")
//    Call<ResponseBody> resetPassword(@Header("user_id") int userID, @Body RequestBody js);
    @POST("alladdress")
    Call<ResponseBody> allAddress(@Header("user_id") int userID);
    @GET("contactus")
    Call<ResponseBody> contactUs();
    @POST("aboutus")
    Call<ResponseBody> aboutUs();
    @POST("deliveryboy_notifications")
    Call<ResponseBody> notificationList(@Header("deliveryboy-id") int userID);
    @GET("offers")
    Call<ResponseBody> offersList(@Header("user_id") int userID);
    @POST("user_orders")
    Call<ResponseBody> pastOrderList(@Header("user_id") int userID, @Query("status") String status);
    @POST("privacypolicy")
    Call<ResponseBody> privacyPolicy();

    @POST("terms")
    Call<ResponseBody> termsandConditions();
    @POST("cartpage")
    Call<ResponseBody> cartPageList(@Header("user_id") int userID);
    @POST("user_orders")
    Call<ResponseBody> currentOrder(@Header("user_id") int userID);
    @POST("deliveryboy_orderdetail/{orderID}")
    Call<ResponseBody> orderDetails(@Header("deliveryboy-id") int userID, @Path("orderID") int orderID);
    @POST("checkoutpage")
    Call<ResponseBody> checkOut(@Header("user_id") int userID);
    @POST("faqs")
    Call<ResponseBody> faqsList(@Header("user_id") int userID);
    @POST("deliveryboy_updateorderstatus/{orderID}")
    Call<ResponseBody> updateOrderStatus(@Header("deliveryboy-id") int userID,@Path("orderID") int orderID, @Body RequestBody js);
    @POST("applypromocode")
    Call<ResponseBody> applPromoCode(@Header("user_id") int userID, @Body RequestBody js);
    @POST("removecart/{productID}")
    Call<ResponseBody> removeToCart(@Header("user_id") int userID, @Path("productID") int productID);
    @POST("deliveryboy_token")
    Call<ResponseBody> updateToken(@Header("deliveryboy-id") int userID,@Body RequestBody js);
    @POST("donotdisturb")
    Call<ResponseBody> doNotDisturbCall(@Body RequestBody js);
    @POST("ven_payment_history")
    Call<ResponseBody> paymentHistory(@Body RequestBody js);
    @POST("ven_credit_history")
    Call<ResponseBody> creditHistory(@Body RequestBody js);
    @POST("ven_recharge_success")
    Call<ResponseBody> rechargeSuccess(@Body RequestBody js);
    @POST("call_status_update")
    Call<ResponseBody> callStatusUpdate(@Body RequestBody js);
    @POST("ven_profilescorepage_description_update")
    Call<ResponseBody> postDescription(@Body RequestBody js);
}
