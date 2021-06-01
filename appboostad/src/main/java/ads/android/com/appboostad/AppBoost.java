package ads.android.com.appboostad;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.content.Context.MODE_PRIVATE;


public class AppBoost {

    private static Activity mActivity;
    private static Dialog mDialog;
    private static Dialog interstitialActivity;
    private static String PAKAGE_NAME="";

    private static String apptext;
    private static String appdesc;

    private static ArrayList<Integer> myArr;
    private static ArrayList<Integer> priorityArr;
    private static String rootName;
    private static String mchild;
    private static int randomIndex;
    private static int priorityIndex;
    private static String imageUrl;
    private static RequestOptions options;

    private static String AppPakage;
    private static onAdClosedEvent monAdClosedEvent;
    private static String checkifaddisLoaded="";
    /*private static DatabaseReference packagesRef;*/
    private static String saveCurrentDate,saveCurrentTime,pakageRandomKey;
    private static boolean functioncalledonce;

    private static RequestQueue requestQueue;
    private static String url="https://appboost.club/directory/myapi.php";
    private static String priorityUrl="https://appboost.club/directory/priorityUrl.php";

    private static String impressionUrl="https://appboost.club/directory/impressionCount.php";
    private static String img_path;

    private static int impressions=0;
    private static int adClicks;

    private static ArrayList<Integer> pakagesArray;
    private static int savedval;
    private static Boolean state = false;
    public static Boolean isAdLoaded =false;


    private static Animation topAnimInterstitial,bottomAnimInterstitial;
   private static String AppPakageWithExt;

    public AppBoost(Dialog mDialog) {
        this.mDialog = mDialog;
    }

    public static void setonAdClosedEvent(onAdClosedEvent eventListener)
    {
        monAdClosedEvent = eventListener;
    }


    public static void stop()
    {
        if(monAdClosedEvent != null)
        {
            monAdClosedEvent.onAdClosed();
        }

    }

    public static void initialize(Activity activity)
    {
        mActivity=activity;


        //check=true;


        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200);

        requestQueue = Volley.newRequestQueue(activity);

        myArr = new ArrayList<Integer>();
        priorityArr = new ArrayList<Integer>();

        pakagesArray = new ArrayList<Integer>();


        readfromDb();

        AppPakage = activity.getPackageName();
      AppPakageWithExt=AppPakage+"&hl=en";


        storePackageAndImpressionstoDb();

        adloaderManager();



    }

//    public static void initialize(Activity activity)
//    {
//        mActivity=activity;
//
//        options = new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .override(200, 200);
//
//        requestQueue = Volley.newRequestQueue(activity);
//
//        myArr = new ArrayList<Integer>();
//
//        pakagesArray = new ArrayList<Integer>();
//
//
//
//        readfromDb();
//
//        AppPakage = activity.getPackageName();
//
//
//        storePackageAndImpressionstoDb();
//
//
//
//
//    }

   /* private static void storePackages()
    {

        if(functioncalledonce)
            return;
        functioncalledonce=true;

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        pakageRandomKey=saveCurrentDate + saveCurrentTime;


        HashMap<String, Object> packageMap=new HashMap<>();

        packageMap.put("packageId",pakageRandomKey);
        packageMap.put("packageName",APP_PACKAGE_NAME);

        packagesRef.child(pakageRandomKey).updateChildren(packageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {


                        }else
                        {

                        }

                    }
                });


    }
*/


    private static void readfromDb() {

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {


                for (int i = 0; i < response.length(); i++) {
                    myArr.add(i);
                }

                randomIndex = (int) (Math.random() * myArr.size());


                if (randomIndex == savedval) {
                   // randomIndex =1;
                   showPriorityAds();

                    state = true;
                }


                int rootName = myArr.get(randomIndex);
                try {


                    apptext = response.getJSONObject(rootName).getString("appName");
                    PAKAGE_NAME = response.getJSONObject(rootName).getString("appUrl");
                    appdesc = response.getJSONObject(rootName).getString("shortDescription");
                    imageUrl = response.getJSONObject(rootName).getString("appIcon");

                    img_path = "https://appboost.club/directory/images/" + imageUrl;

                    //storing last displayed ad in variable

                    savedval = randomIndex;


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        requestQueue.add(jsonArrayRequest);
        requestQueue.getCache().clear();



    }

    private static void showPriorityAds()
    {
            final JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, priorityUrl, null, new Response.Listener<JSONArray>() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(JSONArray response) {


                    for (int i = 0; i < response.length(); i++) {
                        priorityArr.add(i);
                    }


                    priorityIndex = (int) (Math.random() * priorityArr.size());

                    int rootName = priorityArr.get(priorityIndex);
                    try {


                        apptext = response.getJSONObject(rootName).getString("appName");
                        PAKAGE_NAME = response.getJSONObject(rootName).getString("appUrl");
                        appdesc = response.getJSONObject(rootName).getString("shortDescription");
                        imageUrl = response.getJSONObject(rootName).getString("appIcon");

                        img_path = "https://appboost.club/directory/images/" + imageUrl;

                        //storing last displayed ad in variable


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(jsonArrayRequest2);
            requestQueue.getCache().clear();



    }


    private static void storePackageAndImpressionstoDb()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://appboost.club/directory/storePakage.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    JSONObject jsonObject = new JSONObject(response);


                    String success = jsonObject.getString("success");
                    if(success.equals("1"))
                    {
                       // Toast.makeText(mActivity, "Pakage store Successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                       // Toast.makeText(mActivity, "Failed to store pakage!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Toast.makeText(mActivity, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String extension ="&hl=en";
                String impValue = String.valueOf(impressions);
                Map<String, String> params = new HashMap<>();
                params.put("AppPakage",AppPakage+extension);
                params.put("Impressions",impValue);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);

    }
    private static void showAds()
    {

        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Button myButton;
        TextView appName;
        TextView appDescription;
        final ImageView appImage, closeImg;



        mDialog=new Dialog(mActivity);
        mDialog.setContentView(R.layout.popup);
        myButton=(Button) mDialog.findViewById(R.id.installbtn);
        appName=(TextView) mDialog.findViewById(R.id.poptxt);
        appDescription=(TextView) mDialog.findViewById(R.id.desctxt);
        appImage=(ImageView) mDialog.findViewById(R.id.mImage);
        closeImg=(ImageView) mDialog.findViewById(R.id.close_btn);



        Glide.with(mActivity.getApplicationContext()).applyDefaultRequestOptions(options).load(img_path).into(appImage);



         closeImg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 mDialog.dismiss();
             }
         });

       appName.setText(apptext);
       appDescription.setText(appdesc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PAKAGE_NAME)));
                } catch (ActivityNotFoundException anfe) {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PAKAGE_NAME)));
                }
               // mDialog.dismiss();

                adClicksManager();

            }
        });
        mDialog.show();

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                stop();
            }
        });

    }


    //Coming Soon
    private static void showInterstitialAds()
    {
        topAnimInterstitial = AnimationUtils.loadAnimation(mActivity,R.anim.interstitial_top_animation);
        bottomAnimInterstitial = AnimationUtils.loadAnimation(mActivity,R.anim.interstitial_bottom_animation);


        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button myButton,closebtn;
        TextView appName;
        TextView appDescription;
        LinearLayout itemsLayout;
        final ImageView appImage, closeImg,graphicImage;

        mDialog=new Dialog(mActivity,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        mDialog.setContentView(R.layout.appboost_interstitial);
        myButton=(Button) mDialog.findViewById(R.id.installbtn);

        closebtn=(Button) mDialog.findViewById(R.id.closebtn);
        appName=(TextView) mDialog.findViewById(R.id.poptxt);
        appDescription=(TextView) mDialog.findViewById(R.id.desctxt);
        appImage=(ImageView) mDialog.findViewById(R.id.mImage);
        closeImg=(ImageView) mDialog.findViewById(R.id.close_btn);
        graphicImage=(ImageView) mDialog.findViewById(R.id.graphicImage);
        itemsLayout=(LinearLayout) mDialog.findViewById(R.id.itemsLayout);

        graphicImage.setAnimation(topAnimInterstitial);
        itemsLayout.setAnimation(bottomAnimInterstitial);

       // Glide.with(mActivity.getApplicationContext()).applyDefaultRequestOptions(options).load(img_path).into(appImage);

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });

//        appName.setText(apptext);
//        appDescription.setText(appdesc);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PAKAGE_NAME)));
                } catch (ActivityNotFoundException anfe) {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PAKAGE_NAME)));
                }
                // mDialog.dismiss();

                adClicksManager();

            }
        });
        mDialog.show();

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                stop();
            }
        });


    }

    private static void adClicksManager()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://appboost.club/directory/adClickCount.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String success = jsonObject.getString("success");
                    if(success.equals("1"))
                    {
                        // Toast.makeText(mActivity, "Pakage store Successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // Toast.makeText(mActivity, "Failed to store pakage!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Toast.makeText(mActivity, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {



                Map<String, String> params = new HashMap<>();

                int imp=0;
                params.put("AppPakage",PAKAGE_NAME);
                imp++;
                String impValue = String.valueOf(imp);
                params.put("clicks",impValue);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);


    }


    public static void showPopUpAds()
   {
//            if(apptext == null || apptext.isEmpty() || state == true || AppPakageWithExt.equals(PAKAGE_NAME))  {

       if(apptext == null || apptext.isEmpty() || AppPakageWithExt.equals(PAKAGE_NAME))  {

           //Toast.makeText(mActivity, "Values are the Same!", Toast.LENGTH_SHORT).show();

                checkifaddisLoaded="Ad_Failed_To_Load";


                Log.d("PAKAGENAMES",String.valueOf(savedval));

                stop();
               //
                state=false;

                return;

            }

            else {
                checkifaddisLoaded="Ad_Loaded_Successfully!";


                    showAds();

                    impressionManager();
            }


        }

    
        private static void adloaderManager()
        {

            if(apptext == null || apptext.isEmpty() || state == true || AppPakageWithExt.equals(PAKAGE_NAME))  {



                isAdLoaded=false;
                state=false;

                return;

            }

            else {

                isAdLoaded=true;

            }

        }
    private static void impressionManager()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://appboost.club/directory/impressionCount.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String success = jsonObject.getString("success");
                    if(success.equals("1"))
                    {
                        // Toast.makeText(mActivity, "Pakage store Successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // Toast.makeText(mActivity, "Failed to store pakage!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Toast.makeText(mActivity, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {



                Map<String, String> params = new HashMap<>();

                int imp=0;
                params.put("myPakage",PAKAGE_NAME);
                imp++;
                String impValue = String.valueOf(imp);
                params.put("count",impValue);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);


    }

    


}