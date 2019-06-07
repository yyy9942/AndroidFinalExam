package com.multijun.androidprojact1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SendMailReply extends Fragment {
    Spinner sp_mail_recipient;
    EditText editTextTitle;
    EditText editTextContent;
    Button buttonSend;
    Button buttonCancle;
    Intent intent;
    String client_id;
    Spinner sp_mail;
    List<String> nameList;
    RequestQueue requestQueue;
    Context context;
    TextView textView_hide;
    public static final String ARG_SENDER = "sender";
    public static final String ARG_RECIPIENT = "recipient";
    public static final String ARG_TITLE = "title";
    public static final String ARG_DATE = "date";
    public static final String ARG_CONTENT = "content";
    public static final String ARG_ID = "id";
    public String sender;
    public String recipient;
    public String title;
    public String date;
    public String content;
    public int id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_send_mail, container, false);
        sp_mail_recipient = view.findViewById(R.id.sp_mail_name);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonCancle = view.findViewById(R.id.buttonCancle);
        requestQueue=  Volley.newRequestQueue(getContext());
        context = getContext();
        intent = getActivity().getIntent();
        client_id = intent.getStringExtra("id");
        textView_hide = view.findViewById(R.id.textView_send_mail_hide);


        sp_mail_recipient.setVisibility(View.INVISIBLE);
        textView_hide.setVisibility(View.VISIBLE);
        String str = "원문 : ==================\n";
        str += content;
        str += "\n=====================\n";
        textView_hide.setText(sender);
        editTextContent.setText(str);
        editTextTitle.setText("답장 : " + title);

        sp_mail = view.findViewById(R.id.sp_mail);
        sp_mail.setAdapter(ArrayAdapter.createFromResource(getContext(),
                R.array.data_spinner, android.R.layout.simple_spinner_item));


        sp_mail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                final String major = sp_mail.getSelectedItem().toString();


                String url = "http://yyy9942.cafe24.com/hnu/searchIdByMajor.php";

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    nameList = new ArrayList<>();
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonId = jsonObject.getJSONArray("clientId");
                                    JSONArray jsonName = jsonObject.getJSONArray("clientName");
                                    for(int i=0; i<jsonId.length(); i++){
                                        nameList.add(jsonId.get(i)+"(" + jsonName.get(i)+")");
                                        Log.d(null,"결과 => " + jsonId.get(i)+"(" + jsonName.get(i)+")");
                                    }

                                    sp_mail_recipient.setAdapter(new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, nameList));



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    sp_mail_recipient.setAdapter(new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, new ArrayList<>()));
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
                        params.put("clientMajor", major);
                        return params;
                    }
                };
                request.setShouldCache(false);
                requestQueue.add(request);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp_mail.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(getContext(),"받는이를 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextTitle.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextContent.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = "http://yyy9942.cafe24.com/hnu/sendMail.php";

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
                                        updateSentMail(id);   // 메일 오픈값을 2로 만듬
                                        Log.d(null, "success값은 : " + isSuccess);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("메일전송 성공");
                                        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("메일전송 실패");
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
//                        String recipient = sp_mail_recipient.getSelectedItem().toString();
//                        recipient = recipient.substring(0,recipient.indexOf('('));;

                        String title = editTextTitle.getText().toString();
                        String content = editTextContent.getText().toString();
                        if(title.isEmpty() || sender.isEmpty() || content.isEmpty() || client_id.isEmpty()){
                            Toast.makeText(context, "입력하지 않은 데이터가 있습니다", Toast.LENGTH_SHORT).show();
                        }
                        Mail mail = new Mail();
                        mail.setTitle(title);
                        mail.setRecipient(sender);
                        mail.setContent(content);
                        mail.setSender(client_id);
                        mail.setTime();
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("mailSender", mail.getSender());
                        params.put("mailRecipient", mail.getRecipient());
                        params.put("mailTitle", mail.getTitle());
                        params.put("mailDate", mail.getSendTime());
                        params.put("mailContent",mail.getContent());
                        Log.d(null, params.toString());
                        return params;
                    }
                };
                request.setShouldCache(false);
                requestQueue.add(request);
                Log.d(null,"요청 보냄!!");
                getFragmentManager().popBackStack();
            }
        });
        buttonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"보내기 취소", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });



        return view;


    }

    void updateSentMail(final int mail_id){
        String url = "http://yyy9942.cafe24.com/hnu/updateSentMail.php";

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
                                Log.d(null, "업데이트 메일 success값은 : " + isSuccess);
                            }else{
                                Toast.makeText(context,"메일 정보가 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
                params.put("mailId", mail_id+"");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(null,"요청 보냄!!");
    }

    public static SendMailReply newInstance(Mail mail) {

        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, mail.getContent());
        args.putString(ARG_TITLE, mail.getTitle());
        args.putString(ARG_DATE, mail.getSendTime());
        args.putString(ARG_SENDER, mail.getSender());
        args.putString(ARG_RECIPIENT, mail.getRecipient());
        args.putInt(ARG_ID, mail.getId());


        SendMailReply fragment = new SendMailReply();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            content = getArguments().getString(ARG_CONTENT);
            title = getArguments().getString(ARG_TITLE);
            date = getArguments().getString(ARG_DATE);
            sender = getArguments().getString(ARG_SENDER);
            recipient = getArguments().getString(ARG_RECIPIENT);
            id = getArguments().getInt(ARG_ID);
        }
    }
}
