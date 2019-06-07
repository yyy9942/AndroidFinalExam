package com.multijun.androidprojact1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubActivity2 extends Fragment {
    MyAdapter adapter;
    View view;
    ArrayList<Mail> arr;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    String client_id;
    RequestQueue requestQueue;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_sub2, container, false);
        requestQueue =  Volley.newRequestQueue(getContext());
        context = getContext();

        Intent intent = getActivity().getIntent();
        client_id = intent.getStringExtra("id");
        getList();


        return view;
    }

    ArrayList<Mail> getList(){
        arr = new ArrayList<>();

        String url = "http://yyy9942.cafe24.com/hnu/getMailList.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray senderList = jsonObject.getJSONArray("senderList");
                            JSONArray titleList = jsonObject.getJSONArray("titleList");
                            JSONArray openList = jsonObject.getJSONArray("openList");
                            JSONArray timeList = jsonObject.getJSONArray("timeList");
                            JSONArray contentList = jsonObject.getJSONArray("contentList");
                            JSONArray idList = jsonObject.getJSONArray("idList");

                            for(int i=0; i<senderList.length(); i++){
                                Mail mail = new Mail();
                                mail.setTitle(titleList.get(i).toString());
                                mail.setContent(contentList.get(i).toString());
                                mail.setSender(senderList.get(i).toString());
                                mail.setRecipient(client_id);
                                mail.setOpen(openList.getInt(i));
                                mail.setSendTime(timeList.get(i).toString());
                                mail.setId(idList.getInt(i));
                                arr.add(mail);
                            }
                            adapter = new MyAdapter(getContext(), R.layout.mail, arr);
                            ListView myList;
                            myList = (ListView) view.findViewById(R.id.list_mail);
                            myList.setAdapter(adapter);
                            myList.setOnItemClickListener(mItemClickListener);

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

        return arr;
    }

    AdapterView.OnItemClickListener mItemClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    String url = "http://yyy9942.cafe24.com/hnu/openMail.php";

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
                                            Log.d(null, "success값은 : " + isSuccess);
                                        }else{
                                            Toast.makeText(context,"메일 열기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
                            params.put("mailId", arr.get(position).getId()+"");
                            return params;
                        }
                    };

                    request.setShouldCache(false);




                    Mail mail = arr.get(position);
                    if(mail.getOpen()<1){
                        requestQueue.add(request);
                        Log.d(null,"요청 보냄!!");
                    }

                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.mainContaner, ReadMail.newInstance(mail));
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            };




}
