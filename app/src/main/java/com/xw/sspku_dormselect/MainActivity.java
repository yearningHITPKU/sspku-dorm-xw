package com.xw.sspku_dormselect;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xw.sspku_dormselect.bean.Dormitory;
import com.xw.sspku_dormselect.bean.ResponseBean;
import com.xw.sspku_dormselect.bean.Student;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends FragmentActivity {

    private static final int ROOM_INFO_SUCCESS = 1;

    private Spinner spinner;
    private ArrayAdapter<String> mArrayAdapter;
    private String[] mLevel;
    private Intent mIntent;
    private Student mStudent;
    private Dormitory mDormitory;
    private int mNowDepartmentNum = 5;// 初始化为5号楼

    private Fragment selfFragment;
    private Fragment selfSelectedFragment;
    private Fragment checkInFragment;
    private LinearLayout selfTab;
    private ImageView selfIcon;
    private TextView selfTxt;
    private LinearLayout checkInTab;
    private ImageView checkInIcon;
    private TextView checkInTxt;
    private int nowIndex = -1;

    // 退出
    private ImageView ivSelfLogout;
    private ImageView ivSelectedLogout;

    // 未完成选择界面
    private TextView tvName;
    private TextView tvStudentid;
    private TextView tvGender;
    private TextView tvVcode;

    // 已完成选择界面
    private TextView tvName2;
    private TextView tvStudentid2;
    private TextView tvGender2;
    private TextView tvBuilding;
    private TextView tvRoom;

    // 办理入住界面
    private TextView tvCheckinName;
    private TextView tvCheckinStuid;
    private TextView tvCheckinVcode;
    private EditText editCheckinStuid_1;
    private EditText editCheckinVcode_1;
    private EditText editCheckinStuid_2;
    private EditText editCheckinVcode_2;
    private EditText editCheckinStuid_3;
    private EditText editCheckinVcode_3;
    private TextView tvFreeBedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mIntent = getIntent();
        Bundle bundle = mIntent.getExtras();//.getExtras()得到intent所附带的额外数据
        String str = bundle.getString("responseJSON");//getString()返回指定key的值

        // 解析json格式的字符串
        ResponseBean<Student> responseBean;
        Gson gson = new Gson();
        responseBean = gson.fromJson(str, new TypeToken<ResponseBean<Student>>(){}.getType());
        mStudent = responseBean.getData();
        Log.d("mStudent", mStudent.toString());

        mDormitory = new Dormitory();

        initView();
        initAction();

        selectTab(0);

        // 根据是否已经分配宿舍显示个人信息页面
        if(mStudent.getRoom() != null){
            getSupportFragmentManager().beginTransaction().hide(checkInFragment).hide(selfFragment).show(selfSelectedFragment).commit();
        }else {
            getSupportFragmentManager().beginTransaction().hide(checkInFragment).hide(selfSelectedFragment).show(selfFragment).commit();
        }
    }

    private void initView() {

        // 获得控件
        ivSelfLogout = (ImageView)findViewById(R.id.img_self_logout);
        ivSelectedLogout = (ImageView)findViewById(R.id.img_Selected_logout);

        tvName = (TextView)findViewById(R.id.tv_stuName);
        tvStudentid = (TextView)findViewById(R.id.tv_studentid);
        tvGender = (TextView)findViewById(R.id.tv_stuGender);
        tvVcode = (TextView)findViewById(R.id.tv_vcode);

        tvName2 = (TextView)findViewById(R.id.tv_stuName_2);
        tvStudentid2 = (TextView)findViewById(R.id.tv_studentid_2);
        tvGender2 = (TextView)findViewById(R.id.tv_stuGender_2);
        tvBuilding = (TextView)findViewById(R.id.tv_apartmentID);
        tvRoom = (TextView)findViewById(R.id.tv_dormID);

        // 设置控件内容
        if(mStudent.getRoom() != null){// 已选
            tvName2.setText(mStudent.getName());
            tvStudentid2.setText(mStudent.getStudentid());
            tvGender2.setText(mStudent.getGender());
            tvBuilding.setText(mStudent.getBuilding());
            tvRoom.setText(mStudent.getRoom());
            ivSelectedLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        }else{// 未选
            tvName.setText(mStudent.getName());
            tvStudentid.setText(mStudent.getStudentid());
            tvGender.setText(mStudent.getGender());
            tvVcode.setText(mStudent.getVcode());
            ivSelfLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        }

        tvCheckinName = (TextView)findViewById(R.id.checkin_stuName);
        tvCheckinStuid = (TextView)findViewById(R.id.checkin_stuid);
        tvCheckinVcode = (TextView)findViewById(R.id.checkin_vcode);
        editCheckinStuid_1 = (EditText)findViewById(R.id.checkin_stuid_1);
        editCheckinStuid_2 = (EditText)findViewById(R.id.checkin_stuid_2);
        editCheckinStuid_3 = (EditText)findViewById(R.id.checkin_stuid_3);
        editCheckinVcode_1 = (EditText)findViewById(R.id.checkin_vcode_1);
        editCheckinVcode_2 = (EditText)findViewById(R.id.checkin_vcode_2);
        editCheckinVcode_3 = (EditText)findViewById(R.id.checkin_vcode_3);

        // 用5号楼的剩余床位数初始化控件
        tvFreeBedNum = (TextView)findViewById(R.id.free_bed_num);
        getRoomInfo();
        tvFreeBedNum.setText(String.valueOf(getBedNum()));

        tvCheckinName.setText(mStudent.getName());
        tvCheckinStuid.setText(mStudent.getStudentid());
        tvCheckinVcode.setText(mStudent.getVcode());

        selfFragment = getSupportFragmentManager().findFragmentById(R.id.fg_self);
        selfSelectedFragment = getSupportFragmentManager().findFragmentById(R.id.fg_selfSelected);
        checkInFragment = getSupportFragmentManager().findFragmentById(R.id.fg_check_in);
        selfTab = (LinearLayout) findViewById(R.id.btn_f);
        selfIcon = (ImageView) findViewById(R.id.img_icon_f);
        selfTxt = (TextView) findViewById(R.id.txt_title_f);
        checkInTab = (LinearLayout) findViewById(R.id.btn_s);
        checkInIcon = (ImageView) findViewById(R.id.img_icon_s);
        checkInTxt = (TextView) findViewById(R.id.txt_title_s);
        spinner = (Spinner)findViewById(R.id.spinner_apartID);

        // 初始化下拉菜单内容
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item);
        mLevel = getResources().getStringArray(R.array.apartmentIDArr);//资源文件
        for (int i = 0; i < mLevel.length; i++) {
            mArrayAdapter.add(mLevel[i]);
            System.out.println(mLevel[i]);
        }
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mArrayAdapter);

        //spinner设置监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this,"你选的是的是第" + position,Toast.LENGTH_SHORT).show();

                // 设置当前需要查询的楼号
                mNowDepartmentNum = Integer.parseInt(mLevel[position]);
                // 向服务器请求剩余床位信息
                getRoomInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean getRoomInfo(){
        // 在子线程中调用接口，查询用户是否已经选择过宿舍
        new Thread(new Runnable() {
            ResponseBean<Dormitory> responseBean;
            String address = "https://api.mysspku.com/index.php/V1/MobileCourse/getRoom?gender=" + mStudent.getGender();
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
                        //Log.d("result",str);
                    }
                    String responseStr = response.toString();
                    Log.d("result", responseStr);

                    // 解析json格式的字符串
                    JSONObject object = new JSONObject(responseStr);
                    JSONObject dataObject = new JSONObject(object.getJSONObject("data").toString());
                    mDormitory.setApartment5(dataObject.getInt("5"));
                    mDormitory.setApartment8(dataObject.getInt("8"));
                    mDormitory.setApartment9(dataObject.getInt("9"));
                    mDormitory.setApartment13(dataObject.getInt("13"));
                    mDormitory.setApartment14(dataObject.getInt("14"));
                    Log.d("mDormitory", mDormitory.toString());

                    Message msg = new Message();
                    msg.what = ROOM_INFO_SUCCESS;
                    msg.obj = mDormitory;
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

    private void initAction() {
        selfTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(0);
            }
        });

        checkInTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
            }
        });
    }

    private void selectTab(int index) {

        if(nowIndex == index){
            return;
        }

        // 如果点击的是办理入住，则判断是否已经完成办理
        if(index == 1){
            if(mStudent.getRoom() != null){
                showDialog();
                //return;
            }
        }

        nowIndex = index;
        selfIcon.setImageDrawable(getResources().getDrawable(index == 0 ? R.drawable.icon_self_cllick : R.drawable.icon_self));
        selfTxt.setTextColor(Color.parseColor(index == 0 ? "#1296db" : "#333333"));
        checkInIcon.setImageDrawable(getResources().getDrawable(index == 1 ? R.drawable.icon_check_in_click : R.drawable.icon_check_in));
        checkInTxt.setTextColor(Color.parseColor(index == 1 ? "#1296db" : "#333333"));

        if(mStudent.getRoom() != null){
            getSupportFragmentManager().beginTransaction().hide(index == 0 ? checkInFragment : selfSelectedFragment).show(index == 0 ? selfSelectedFragment : checkInFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().hide(index == 0 ? checkInFragment : selfFragment).show(index == 0 ? selfFragment : checkInFragment).commit();
            getRoomInfo();
            updateBedNum();
        }
    }

    private int getBedNum(){

        int bedNum;
        switch (mNowDepartmentNum){
            case 5:
                bedNum = mDormitory.getApartment5();
                break;
            case 8:
                bedNum = mDormitory.getApartment8();
                break;
            case 9:
                bedNum = mDormitory.getApartment9();
                break;
            case 13:
                bedNum = mDormitory.getApartment13();
                break;
            case 14:
                bedNum = mDormitory.getApartment14();
                break;
            default:
                bedNum = mDormitory.getApartment5();
                break;
        }
        return bedNum;
    }

    private void updateBedNum(){
        tvFreeBedNum.setText(String.valueOf(getBedNum()));
    }

    // 该函数用于处理子线程发送的msg消息
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 获取用户信息成功，跳转到主界面
                case ROOM_INFO_SUCCESS:
                    updateBedNum();
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.see_pwd);//设置图标
        builder.setTitle(mStudent.getName() + "同学：");//设置对话框的标题
        builder.setMessage("您已经分配过宿舍了!\n楼号："+ mStudent.getBuilding() + "\n宿舍号：" + mStudent.getRoom());//设置对话框的内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this, "自宫成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this, "取消自宫",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog b=builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理
    }
}
