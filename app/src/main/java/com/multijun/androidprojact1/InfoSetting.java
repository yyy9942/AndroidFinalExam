package com.multijun.androidprojact1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;

public class InfoSetting extends Fragment {
    TextView textView_id;
    EditText editText_newPw;
    EditText editText_newName;
    Button buttonChange;
    Intent intent;
    String client_id;
    String client_pw;
    String client_major;
    String client_name;
    String client_img;
    ImageButton btn_image_set;
    Spinner spinner;
    static RequestQueue requestQueue;
    Context context;
    private static int PICK_IMAGE_REQUEST = 1;
    View view;
    MainFragmentActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_infoset, container, false);
        requestQueue =  Volley.newRequestQueue(getContext());
        textView_id = view.findViewById(R.id.textViewMyId);
        editText_newName = view.findViewById(R.id.editText_newName);
        editText_newPw = view.findViewById(R.id.editText_newPw);
        buttonChange = view.findViewById(R.id.btn_change);
        btn_image_set = view.findViewById(R.id.btn_image_set);
        intent = getActivity().getIntent();
        client_id = intent.getStringExtra("id");
        spinner = view.findViewById(R.id.sp_infoset);
        spinner.setAdapter(ArrayAdapter.createFromResource(getContext(),
                R.array.data_spinner, android.R.layout.simple_spinner_item));
        context = getContext();
        btn_image_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*"); //이미지만 보이게
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        mainActivity = ((MainFragmentActivity)getActivity());


        String url = "http://yyy9942.cafe24.com/hnu/searchId.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            client_pw = jsonResponse.getString("clientPw");
                            client_name = jsonResponse.getString("clientName");
                            client_major = jsonResponse.getString("clientMajor");
                            client_img = jsonResponse.getString("clientImg");
                            Log.d(null, "이미지값" + client_img);
                            textView_id.setText(client_id);
                            Log.d(null, "받은 값 : " + client_pw  + "이름 : " + client_name+ "전공" + client_major);


                            if(client_img.equals("기본이미지")){
                                return;
                            }

                            Uri uri = Uri.parse(client_img);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                                btn_image_set.setImageBitmap(scaled);
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



        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(editText_newName.getText().toString().isEmpty() && editText_newPw.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"값을 먼저 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(!editText_newName.getText().toString().isEmpty() && !editText_newPw.getText().toString().isEmpty()){
                        String url = "http://yyy9942.cafe24.com/hnu/updateClient.php";

                        StringRequest request = new StringRequest(
                                Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean isSuccess = jsonResponse.getBoolean("success");
                                            if(isSuccess){

                                                Toast.makeText(context,"회원 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                mainActivity.updateData();
                                                Log.d(null, "success값은 : " + isSuccess);
                                            }else{
                                                Toast.makeText(context,"회원 정보가 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Log.d(null,"응답 => " + response);

                                    }
                                },
                                new Response.ErrorListener(){
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
                                params.put("clientPw", editText_newPw.getText().toString());
                                params.put("clientName", editText_newName.getText().toString());
                                params.put("clientMajor", (String)spinner.getItemAtPosition(spinner.getSelectedItemPosition()));
                                if(client_img == null){
                                    params.put("clientImg", "기본이미지");
                                }else{
                                    params.put("clientImg", client_img);
                                }
                                return params;
                            }
                        };

                        request.setShouldCache(false);
                        requestQueue.add(request);
                        Log.d(null,"요청 보냄!!");



                    }

                    getFragmentManager().popBackStack();
                }

            }

        });

        return view;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                btn_image_set.setImageBitmap(scaled);
                client_img = uri.toString();
                Log.d(null,"이미지 경로 : "+uri.toString());

            } else {
                Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
