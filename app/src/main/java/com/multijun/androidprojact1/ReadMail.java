package com.multijun.androidprojact1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ReadMail extends Fragment {
    public static final String ARG_SENDER = "sender";
    public static final String ARG_RECIPIENT = "recipient";
    public static final String ARG_TITLE = "title";
    public static final String ARG_DATE = "date";
    public static final String ARG_CONTENT = "content";
    public static final String ARG_ID = "id";
    public static final String ARG_MAJOR = "major";
    public String sender;
    public String recipient;
    public String title;
    public String date;
    public String content;
    public int id;
    Button btn_back;
    Button btn_delete;
    Button btn_resend;
    Context context;
    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_read_mail, container, false);
        TextView textViewContent = view.findViewById(R.id.textViewContent);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView textViewRecipient = view.findViewById(R.id.textViewRecipient);
        TextView textViewSender = view.findViewById(R.id.textViewSender);
        context = getContext();
        requestQueue =  Volley.newRequestQueue(getContext());


        textViewContent.setText(content);
        textViewDate.setText(date);
        textViewRecipient.setText(recipient);
        textViewSender.setText(sender);
        textViewTitle.setText(title);

        btn_resend = view.findViewById(R.id.btn_resend);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_back = view.findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "뒤로", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                Toast.makeText(getContext(), "삭제", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mail mail = new Mail();
                mail.setRecipient(recipient);
                mail.setSender(sender);
                mail.setContent(content);
                mail.setTitle(title);
                mail.setId(id);


                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainContaner, SendMailReply.newInstance(mail));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(getContext(), "답장", Toast.LENGTH_SHORT).show();

            }
        });



        return view;
    }
    private void delete(){
        String url = "http://yyy9942.cafe24.com/hnu/deleteMail.php";

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
                                Toast.makeText(context, "삭제 성공!", Toast.LENGTH_SHORT).show();
                            }else{

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
                params.put("mailId", id+"");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }
    public static ReadMail newInstance(Mail mail) {

        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, mail.getContent());
        args.putString(ARG_TITLE, mail.getTitle());
        args.putString(ARG_DATE, mail.getSendTime());
        args.putString(ARG_SENDER, mail.getSender());
        args.putString(ARG_RECIPIENT, mail.getRecipient());
        args.putInt(ARG_ID, mail.getId());


        ReadMail fragment = new ReadMail();
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
