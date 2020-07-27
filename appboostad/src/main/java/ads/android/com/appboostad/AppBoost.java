package ads.android.com.appboostad;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class AppBoost {

    private static Activity mActivity;
    private static Dialog mDialog;
    private static String PAKAGE_NAME="";

    private static String apptext;
    private static String appdesc;

    private static ArrayList<Integer> myArr;
    private static String rootName;
    private static String mchild;
    private static int randomIndex;
    private static String imageUrl;
    private static RequestOptions options;

    private static String AppPakage;
    private static onAdClosedEvent monAdClosedEvent;
    private static String checkifaddisLoaded="";
    /*private static DatabaseReference packagesRef;*/
    private static String saveCurrentDate,saveCurrentTime,pakageRandomKey;
    private static boolean functioncalledonce;

    private static RequestQueue requestQueue;
    private static String url="https://appboost.org/dashboard/myapi.php";
    private static String impressionUrl="https://appboost.org/dashboard/impressionCount.php";
    private static String img_path;

    private static int impressions=0;
    private static int adClicks;

    private static ArrayList<Integer> pakagesArray;
    private static int savedval;
    private static Boolean state = false;



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

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200);

        requestQueue = Volley.newRequestQueue(activity);

        myArr = new ArrayList<Integer>();

        pakagesArray = new ArrayList<Integer>();



        readfromDb();

        AppPakage = activity.getPackageName();


        storePackageAndImpressionstoDb();




    }

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


    private static void readfromDb()
    {
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {


                for (int i=0;i<response.length();i++)
                {
                    myArr.add(i);
                }

               randomIndex = (int) (Math.random() * myArr.size());
                // save randomIndex using sharedPrefs.

                    if(randomIndex == savedval)
                    {
                        state=true;
                    }

                    int rootName = myArr.get(randomIndex);
                    try {


                        apptext = response.getJSONObject(rootName).getString("appName");
                        PAKAGE_NAME = response.getJSONObject(rootName).getString("appUrl");
                        appdesc = response.getJSONObject(rootName).getString("shortDescription");
                        imageUrl = response.getJSONObject(rootName).getString("appIcon");

                        img_path = "https://appboost.org/dashboard/images/" + imageUrl;

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


    private static void storePackageAndImpressionstoDb()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://appboost.org/dashboard/storePakage.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    Log.d("Problem ",response);
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

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


      mDialog.getWindow().getAttributes().windowAnimations=R.style.DialogSlide;
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://appboost.org/dashboard/adClickCount.php", new Response.Listener<String>() {
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


    public static void showAd()
    {
            if(apptext == null || apptext.isEmpty() || state == true)  {

               //Toast.makeText(mActivity, "Values are the Same!", Toast.LENGTH_SHORT).show();
                checkifaddisLoaded="Ad_Failed_To_Load";

                stop();
                state=false;
                return;

            }

            else {
                checkifaddisLoaded="Ad_Loaded_Successfully!";



                    showAds();

                    impressionManager();
            }

        }

    private static void impressionManager()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://appboost.org/dashboard/impressionCount.php", new Response.Listener<String>() {
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