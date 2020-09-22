package com.moziz.qricedeliveryboy.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public  class APIClient {

        private  Retrofit retrofit = null;
      private  Context mContext;
       Retrofit retrofitmain =null;


        public  Retrofit getClient(Context context) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(1000, TimeUnit.SECONDS)
                    .readTimeout(1000, TimeUnit.SECONDS)
                    .writeTimeout(1000, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
            if(retrofitmain !=null) {
                return retrofitmain;
            }
            else
            {
                retrofitmain =new Retrofit.Builder()
                        .baseUrl(Api.BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                return retrofitmain;
            }

        }


//    public static Retrofit getClient(Context context) {
//            mContext = context;
//        int cacheSize = 10 * 1024 * 1024; // 10 MB
//        Cache cache = new Cache(mContext.getCacheDir(), cacheSize);
//
////        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
////        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//      //10 MB
//        OkHttpClient client = new OkHttpClient.Builder()
//                .cache(cache)
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS)
//                .writeTimeout(100, TimeUnit.SECONDS).build();
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        return retrofit;
//    }

    }




//    private static Retrofit retrofit = null;
//    private static Context mContext;
//
//    public static Retrofit getClient(Context context) {
//            mContext = context;
//        int cacheSize = 10 * 1024 * 1024; // 10 MB
//        Cache cache = new Cache(mContext.getCacheDir(), cacheSize);
//
////        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
////        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//      //10 MB
//        OkHttpClient client = new OkHttpClient.Builder()
//                .cache(cache)
////                .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
//                .addInterceptor(REWRITE_CONTENT_LENGTH_INTERCEPTOR)
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS)
//                .writeTimeout(100, TimeUnit.SECONDS).build();
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        return retrofit;
//    }
//
//    private static final Interceptor REWRITE_CONTENT_LENGTH_INTERCEPTOR = new Interceptor() {
//        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
//            Response originalResponse = chain.proceed(chain.request());
//            return originalResponse.newBuilder().removeHeader("Content-Length")
//                    .build();
//        }
//    };
//    private static final Interceptor REWRITE_RESPONSE_INTERCEPTOR = new Interceptor() {
//        @Override
//        public okhttp3.Response intercept(Chain chain) throws IOException {
//            okhttp3.Response originalResponse = chain.proceed(chain.request());
//            String cacheControl = originalResponse.header("Cache-Control");
//            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
//                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
//                return originalResponse.newBuilder()
//                        .removeHeader("Pragma")
//                        .header("Cache-Control", "public, max-age=" + 5000)
//                        .build();
//            } else {
//                return originalResponse;
//            }
//        }
//    };
//
//    private static final Interceptor REWRITE_RESPONSE_INTERCEPTOR_OFFLINE = new Interceptor() {
//        @Override
//        public okhttp3.Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            if (!Constant.isConnected(mContext)) {
//                request = request.newBuilder()
//                        .removeHeader("Pragma")
//                        .header("Cache-Control", "public, only-if-cached")
//                        .build();
//            }
//            return chain.proceed(request);
//        }
//    };
//
//
//
//
//
//
//
//
//}


