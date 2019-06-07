package com.multijun.androidprojact1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final static int REQCODE_ACTEDIT = 930;
    final static int REQCODE_LOGIN = 940;
    EditText editText_Id;
    EditText editText_Pw;
    CheckBox saveLogin;
    ArrayList<Client> arr = new ArrayList<Client>();
    private static RequestQueue requestQueue;
    private final int PERMISSIONS_REQUEST_RESULT = 100; // 콜백함수 호출시 requestCode로 넘어가는 구분자



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        requestPermissionCamera();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);



        requestQueue =  Volley.newRequestQueue(getApplicationContext());
        editText_Id = (EditText)findViewById(R.id.editText_Id);
        editText_Pw = (EditText)findViewById(R.id.editText_Pw);

        SharedPreferences pref = getSharedPreferences("autoLogin", MODE_PRIVATE);
        String prefId = pref.getString("id", "");
        String prefPw = pref.getString("pw", "");
        if(!prefId.equals("")){
            editText_Id.setText(prefId);
        }
        if(!prefPw.equals("")){
            editText_Pw.setText(prefPw);
        }

    }

    public void mOnSign(View v){
        Intent intent = new Intent(this, SubActivity.class);
        startActivityForResult(intent, REQCODE_ACTEDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQCODE_ACTEDIT:
                if(resultCode == RESULT_OK) {
                    // 현재 사용하지 않는 부분
                }
                break;
            case REQCODE_LOGIN:
                // 현재 사용하지 않는 부분
                break;
        }
    }

    public void mOnLogin(View v){
        editText_Id = (EditText)findViewById(R.id.editText_Id);
        editText_Pw = (EditText)findViewById(R.id.editText_Pw);

        String url = "http://yyy9942.cafe24.com/hnu/login.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean check = jsonResponse.getBoolean("success");
                            if(check){
                                Intent intent = new Intent(MainActivity.this, MainFragmentActivity.class);
                                intent.putExtra("id", editText_Id.getText().toString());
                                storePref();
                                startActivityForResult(intent, REQCODE_LOGIN);
                            }else{
                                Toast.makeText(MainActivity.this, "로그인 정보가 잘못되었습니다!", Toast.LENGTH_SHORT).show();
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
                String id = editText_Id.getText().toString();
                String pw = editText_Pw.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", id);
                params.put("clientPw",pw);
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(null,"요청 보냄!!");





    }

    public Client checkClient(String id, String pw){
        Client cl = null;
        for(Client client : arr){
            if(client.checkId(id, pw)){
                cl = client;
                break;
            }
        }
        return cl;
    }

    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;

        // 해당 단말기의 안드로이드 OS버전체크
        if(sdkVersion >= Build.VERSION_CODES.M) {
            // 버전 6.0 이상일 경우

            // 해당 퍼미션이 필요한지 체크 - true : 퍼미션 동의가 필요한 권한일 때 , false : 퍼미션 동의가 필요하지 않은 권한일 때.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // true : 퍼미션 동의가 필요한 권한일 때

                // 사용자가 최초 퍼미션 체크를 했는지 확인한다. - true : 사용자가 최초 퍼미션 요청시 '거부'했을 때, false : 퍼미션 요청이 처음일 경우
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // true : 사용자가 최초 퍼미션 요청시 '거부'해서 재요청일 때
                } else {
                    // false : 퍼미션 요청이 처음일 경우.

                    // 퍼미션의 동의 여부를 다이얼로그를 띄워 요청한다. 이 때 '동의', '거부'의 결과값이 onRequestPermissionsResult 으로 콜백된다.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_RESULT);
                }
            }else {
                // false : 퍼미션 동의가 필요하지 않은 권한일 때.
            }
        }else{
            // version 6 이하일 때에는 별도의 작업이 필요없다.
        }

        return true;

    }




    public void storePref(){
        SharedPreferences pref = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        saveLogin = (CheckBox)findViewById(R.id.saveLogin);
        if(saveLogin.isChecked()){
            edit.putString("id", editText_Id.getText().toString());
            edit.putString("pw", editText_Pw.getText().toString());
            edit.commit();
        }else{
            edit.clear();
            edit.commit();
        }


    }
}
