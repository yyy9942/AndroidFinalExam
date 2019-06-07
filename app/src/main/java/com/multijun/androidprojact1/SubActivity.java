package com.multijun.androidprojact1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.BaseKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubActivity extends AppCompatActivity {
    ArrayList<Client> arr;
    String id;
    String pw;
    String name;
    String img="기본이미지";
    public static RequestQueue requestQueue;
    public static Context context;
    boolean ck;
    Spinner sp_major;
    private static int PICK_IMAGE_REQUEST = 1;
    ImageButton btn_image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        getSupportActionBar().hide();
        requestQueue =  Volley.newRequestQueue(getApplicationContext());
        sp_major = findViewById(R.id.sp_major);
        sp_major.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.data_spinner, android.R.layout.simple_spinner_item));
        btn_image = findViewById(R.id.btn_image);



    }

    public void onImageClick(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*"); //이미지만 보이게
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                btn_image.setImageBitmap(scaled);



                getIntent().putExtra("img",uri.toString());
                img = uri.toString();
            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    public void mOnClick(View v){
        id = ((EditText)findViewById(R.id.editText_mId)).getText().toString();
        pw = ((EditText)findViewById(R.id.editText_mPw)).getText().toString();
        name = ((EditText)findViewById(R.id.editText_mName)).getText().toString();

        ck = getIntent().getBooleanExtra("ck", true);
        if(id.isEmpty() || pw.isEmpty() || name.isEmpty()){
            Toast.makeText(getApplicationContext(), "ID, PW, Name을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if(ck){
            Toast.makeText(getApplicationContext(), "중복체크를 먼저 하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            Intent intent = new Intent();
            intent.putExtra("ClientOut", new Client(id, pw, name));
            setResult(RESULT_OK,intent);


                String url = "http://yyy9942.cafe24.com/hnu/register.php";

                //StringRequest를 만듬 (파라미터구분을 쉽게하기위해 엔터를 쳐서 구분하면 좋다)
                //StringRequest는 요청객체중 하나이며 가장 많이 쓰인다고한다.
                //요청객체는 다음고 같이 보내는방식(GET,POST), URL, 응답성공리스너, 응답실패리스너 이렇게 4개의 파라미터를 전달할 수 있다.(리퀘스트큐에 ㅇㅇ)
                //화면에 결과를 표시할때 핸들러를 사용하지 않아도되는 장점이있다.

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {  //응답을 문자열로 받아서 여기다 넣어달란말임(응답을 성공적으로 받았을 떄 이메소드가 자동으로 호출됨
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean isSuccess = jsonResponse.getBoolean("success");
                                    if(isSuccess){
                                        Log.d(null, "success값은 : " + isSuccess);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
                                        builder.setMessage("회원가입 성공!");
                                        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                                        builder.create().show();
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
                                        builder.setMessage("회원가입 실패..");
                                        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
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
                        params.put("clientId", id);
                        params.put("clientPw", pw);
                        params.put("clientName", name);
                        params.put("clientMajor", (String)sp_major.getItemAtPosition(sp_major.getSelectedItemPosition()));
                        params.put("clientImg",img);
                        Log.d(null,(String)sp_major.getItemAtPosition(sp_major.getSelectedItemPosition()));
                        return params;
                    }
                };

                request.setShouldCache(false);
                requestQueue.add(request);
                Log.d(null,"요청 보냄!!");
        }


        }
        public void mOnCheck(View v){
            id = ((EditText)findViewById(R.id.editText_mId)).getText().toString();
            checkId();
            return;

        }
        public boolean checkId(){
            id = ((EditText)findViewById(R.id.editText_mId)).getText().toString();
            boolean result = false;

            String url = "http://yyy9942.cafe24.com/hnu/checkId.php";

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean check = jsonResponse.getBoolean("check");
                                if(!check){
                                    Intent intent = getIntent();
                                    intent.putExtra("result", false);
                                    intent.putExtra("ck", false);
                                    Log.d(null, "check값은 : " + check);
                                    Toast.makeText(SubActivity.this, "GOOD ID!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent intent = new Intent();
                                    intent.putExtra("result",true);
                                    Toast.makeText(SubActivity.this, "ID가 중복되었습니다!", Toast.LENGTH_SHORT).show();

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
                    params.put("clientId", id);
                    return params;
                }
            };
            request.setShouldCache(false);
            requestQueue.add(request);
            Log.d(null,"요청 보냄!!");

            result = getIntent().getBooleanExtra("result",true);


            return result;
        }



}
