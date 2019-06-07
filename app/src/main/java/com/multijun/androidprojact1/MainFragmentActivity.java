package com.multijun.androidprojact1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainFragmentActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {
    TextView textViewIntro;
    Intent intent;
    String client_id;
    ImageView client_img;
    TextView textViewNavName;
    TextView textViewNavMajor;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("메인 화면");
        intent = this.getIntent();
        textViewIntro = (TextView)findViewById(R.id.textViewIntro);
        client_id = intent.getStringExtra("id");
        String introStr = "반갑습니다 ";
        introStr += client_id +"님";
        requestQueue =  Volley.newRequestQueue(getApplicationContext());
        textViewIntro.setText(introStr);
        updateData();




        //
        SubActivity2 su2 = new SubActivity2();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.beginTransaction();






        //
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        textViewNavName = headerView.findViewById(R.id.textViewNavName);
        textViewNavMajor = headerView.findViewById(R.id.textViewNavMajor);
        client_img = headerView.findViewById(R.id.imageViewNav);





    }

    public void updateData(){
        String url = "http://yyy9942.cafe24.com/hnu/searchId.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String client_name = jsonResponse.getString("clientName");
                            String client_major = jsonResponse.getString("clientMajor");
                            String img = jsonResponse.getString("clientImg");
                            textViewNavMajor.setText(client_major);
                            textViewNavName.setText(client_name);

                            if(client_img.equals("기본이미지")){
                                return;
                            }

                            Uri uri = Uri.parse(img);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                                client_img.setImageBitmap(scaled);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(null,"응답 => " + response);

                    }
                },
                new Response.ErrorListener(){ //에러발생시 호출될 리스너 객체
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(null,"에러 => "+ error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", client_id);
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(null,"요청 보냄!!");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_send) {
            getSupportActionBar().setTitle("메세지 작성");
            fragmentTransaction.replace(R.id.mainContaner, new SendMail());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_read) {
            getSupportActionBar().setTitle("받은 메세지 함");
            fragmentTransaction.replace(R.id.mainContaner, new SubActivity2());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
//

        }else if(id == R.id.nav_send_mail){
            getSupportActionBar().setTitle("보낸 메세지 함");
            fragmentTransaction.replace(R.id.mainContaner, new SentMail());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        }else if (id == R.id.nav_setting) {
            getSupportActionBar().setTitle("개인 정보 설정");
            fragmentTransaction.replace(R.id.mainContaner, new InfoSetting());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_exit) {
            getSupportActionBar().setTitle("로그아웃");
            // 로그아웃
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






}


