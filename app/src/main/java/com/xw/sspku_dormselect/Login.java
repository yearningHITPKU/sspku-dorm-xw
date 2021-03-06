package com.xw.sspku_dormselect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xw.sspku_dormselect.app.SelectDormApp;
import com.xw.sspku_dormselect.bean.ResponseBean;
import com.xw.sspku_dormselect.bean.Student;
import com.xw.sspku_dormselect.util.NetUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xw on 2017/11/30.
 */

public class Login extends ActionBarActivity implements View.OnClickListener {

    private static final int INFO_QUERY_SUCCESS = 1;
    private static final int LOGIN_CHECK_FAILED = -1;
    private static final int LOGIN_CHECK_SUCCESS = 0;

    private AutoCompleteTextView actv_userNames;
    //private EditText username;
    private EditText password;
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private Button login;
    private boolean isOpen = false;

    private String mStuid;
    private String mPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 测试网络是否联通
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d(String.valueOf(R.string.app_name), "网络OK");
            //Toast.makeText(Login.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else {
            Log.d(String.valueOf(R.string.app_name), "网络挂了");
            //Toast.makeText(Login.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("xw", MODE_PRIVATE);
        mStuid = sharedPreferences.getString("stuid", null);
        mPwd = sharedPreferences.getString("pwd", null);
        if( mStuid != null && mPwd != null){
            if( !mStuid.isEmpty() && !mPwd.isEmpty() ){
                loginCheckFirst();
            }
        }
        initView();
    }

    private void initView() {

        // 获得记录用户名控件
        actv_userNames = (AutoCompleteTextView)findViewById(R.id.actv_username);
        actv_userNames.setThreshold(1);
        actv_userNames.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences sp = (SharedPreferences)getSharedPreferences("xw", MODE_PRIVATE);
                String[] allUserName = new String[sp.getAll().size()];
                allUserName = sp.getAll().values().toArray(new String[0]);
                // sp.getAll()返回一张hash map
                // sp.getAll().size()返回的是有多少个键值对
                // keySet()得到的是a set of the keys.
                // hash map是由key-value组成的

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        Login.this,
                        android.R.layout.simple_dropdown_item_1line,
                        allUserName);

                actv_userNames.setAdapter(adapter);// 设置数据适配器

                // 获得文本框中的用户
                String user = actv_userNames.getText().toString().trim();
                if ("".equals(user)) {
                    // 用户名为空,设置按钮不可见
                    bt_username_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 用户名不为空，设置按钮可见
                    bt_username_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
/*
        username = (EditText) findViewById(R.id.username);
        // 监听文本框内容变化
        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 获得文本框中的用户
                String user = username.getText().toString().trim();
                if ("".equals(user)) {
                    // 用户名为空,设置按钮不可见
                    bt_username_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 用户名不为空，设置按钮可见
                    bt_username_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        password = (EditText) findViewById(R.id.password);
        // 监听文本框内容变化
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 获得文本框中的用户
                String pwd = password.getText().toString().trim();
                if ("".equals(pwd)) {
                    // 用户名为空,设置按钮不可见
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 用户名不为空，设置按钮可见
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_username_clear.setOnClickListener(this);

        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_clear.setOnClickListener(this);

        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        bt_pwd_eye.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

    }

    private boolean loginCheckFirst(){

        // 在子线程中调用接口，获取用户名和密码
        new Thread(new Runnable() {

            ResponseBean<Object> responseBean;

            String address = "https://api.mysspku.com/index.php/V1/MobileCourse/Login?username="+ mStuid + "&password=" + mPwd;

            @Override
            public void run() {
                HttpURLConnection con = null;
                System.out.println(address);
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("result",str);
                    }
                    String responseStr = response.toString();
                    Log.d("result", responseStr);

                    // 解析json格式的字符串
                    Gson gson = new Gson();
                    responseBean = gson.fromJson(responseStr, new TypeToken<ResponseBean<Object>>(){}.getType());
                    Log.d("responseBean", responseBean.toString());

                    Message msg = new Message();

                    if(responseBean.getErrcode() == 0){
                        msg.what = LOGIN_CHECK_SUCCESS;

                    }else{
                        msg.what = LOGIN_CHECK_FAILED;
                    }
                    msg.obj = responseBean;
                    mHandler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null)
                        con.disconnect();
                }
            }
        }).start();

        return true;
    }

    private boolean loginCheck(){

        // 在子线程中调用接口，获取用户名和密码
        new Thread(new Runnable() {

            ResponseBean<Object> responseBean;

            String address = "https://api.mysspku.com/index.php/V1/MobileCourse/Login?username="+ actv_userNames.getText().toString() + "&password=" + password.getText().toString();

            @Override
            public void run() {
                HttpURLConnection con = null;
                System.out.println(address);
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("result",str);
                    }
                    String responseStr = response.toString();
                    Log.d("result", responseStr);

                    // 解析json格式的字符串
                    Gson gson = new Gson();
                    responseBean = gson.fromJson(responseStr, new TypeToken<ResponseBean<Object>>(){}.getType());
                    Log.d("responseBean", responseBean.toString());

                    Message msg = new Message();

                    if(responseBean.getErrcode() == 0){
                        msg.what = LOGIN_CHECK_SUCCESS;

                    }else{
                        msg.what = LOGIN_CHECK_FAILED;
                    }
                    msg.obj = responseBean;
                    mHandler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null)
                        con.disconnect();
                }
            }
        }).start();

        return true;
    }

    private boolean isSelected(){

        // 在子线程中调用接口，查询用户是否已经选择过宿舍
        new Thread(new Runnable() {

            ResponseBean<Student> responseBean;

            String address = "https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid=" + (mStuid == null || mStuid.isEmpty() ? actv_userNames.getText().toString() : mStuid) ;

            @Override
            public void run() {
                HttpURLConnection con = null;
                System.out.println(address);
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("result",str);
                    }
                    String responseStr = response.toString();
                    Log.d("result", responseStr);

                    // 解析json格式的字符串
                    Gson gson = new Gson();
                    responseBean = gson.fromJson(responseStr, new TypeToken<ResponseBean<Student>>(){}.getType());
                    Log.d("responseBean", responseBean.toString());

                    Message msg = new Message();
                    msg.what = INFO_QUERY_SUCCESS;
                    msg.obj = responseStr;
                    mHandler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null)
                        con.disconnect();
                }
            }
        }).start();

        return true;
    }

    // 该函数用于处理子线程发送的msg消息
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 获取用户信息成功，跳转到主界面
                case INFO_QUERY_SUCCESS:

                    // 解析json格式的字符串
                    ResponseBean<Student> responseBean;
                    Gson gson = new Gson();
                    responseBean = gson.fromJson(msg.obj.toString(), new TypeToken<ResponseBean<Student>>(){}.getType());

                    if(responseBean.getErrcode() == 40001){
                        Toast.makeText(Login.this,"学号不存在",Toast.LENGTH_SHORT).show();
                        SharedPreferences settings = (SharedPreferences)getSharedPreferences("xw", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("stuid", null);
                        editor.putString("pwd", null);
                        editor.commit();
                        mStuid = null;
                        mPwd = null;
                    }else if(responseBean.getErrcode() == 40002){
                        Toast.makeText(Login.this,"密码错误",Toast.LENGTH_SHORT).show();
                        SharedPreferences settings = (SharedPreferences)getSharedPreferences("xw", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("stuid", null);
                        editor.putString("pwd", null);
                        editor.commit();
                        mStuid = null;
                        mPwd = null;
                    }else if(responseBean.getErrcode() == 40009){
                        Toast.makeText(Login.this,"参数错误",Toast.LENGTH_SHORT).show();
                        SharedPreferences settings = (SharedPreferences)getSharedPreferences("xw", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("stuid", null);
                        editor.putString("pwd", null);
                        editor.commit();
                        mStuid = null;
                        mPwd = null;
                    }else{
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        // 个人信息需要传递过去，并且通过宿舍号判断是否已经选择过宿舍
                        intent.putExtra("responseJSON", msg.obj.toString());
                        startActivity(intent);
                    }
                    break;

                // 登录失败
                case LOGIN_CHECK_FAILED:
                    if(((ResponseBean<Object>)(msg.obj)).getErrcode() == 40001){
                        Toast.makeText(Login.this,"学号不存在",Toast.LENGTH_SHORT).show();
                    }else if(((ResponseBean<Object>)(msg.obj)).getErrcode() == 40002){
                        Toast.makeText(Login.this,"密码错误",Toast.LENGTH_SHORT).show();
                    }else if(((ResponseBean<Object>)(msg.obj)).getErrcode() == 40009){
                        Toast.makeText(Login.this,"参数错误",Toast.LENGTH_SHORT).show();
                    }
                    break;

                // 登录成功
                case LOGIN_CHECK_SUCCESS:

                    // 如果两个编辑框有一个为空则说明是在进行自动登录，则什么也不需要修改
                    if(!actv_userNames.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                        SharedPreferences settings = (SharedPreferences)getSharedPreferences("xw", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("stuid", actv_userNames.getText().toString());
                        editor.putString("pwd", password.getText().toString());
                        editor.commit();
                        mStuid = settings.getString("stuid", null);
                        mPwd = settings.getString("pwd", null);
                    }
                    // 查询个人信息
                    isSelected();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_username_clear:
                // 清除登录名
                //username.setText("");
                actv_userNames.setText("");
                break;
            case R.id.bt_pwd_clear:
                // 清除密码
                password.setText("");
                break;
            case R.id.bt_pwd_eye:
                // 密码可见与不可见的切换
                if (isOpen) {
                    isOpen = false;
                } else {
                    isOpen = true;
                }

                // 默认isOpen是false,密码不可见
                changePwdOpenOrClose(isOpen);

                break;
            case R.id.login:
                loginCheck();

                break;

            default:
                break;
        }
    }

    /**
     * 密码可见与不可见的切换
     *
     * @param flag
     */
    private void changePwdOpenOrClose(boolean flag) {
        // 第一次过来是false，密码不可见
        if (flag) {
            // 密码可见
            bt_pwd_eye.setBackgroundResource(R.drawable.see_pwd);
            // 设置EditText的密码可见
            password.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());
        } else {
            // 密码不接见
            bt_pwd_eye.setBackgroundResource(R.drawable.hide_pwd);
            // 设置EditText的密码隐藏
            password.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        }
    }
}
