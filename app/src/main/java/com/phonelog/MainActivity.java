package com.phonelog;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ArrayList<CallInfoLog> callInfoLogs = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private CallLogAdapter callLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        callLogAdapter = new CallLogAdapter(getApplicationContext(), callInfoLogs);
        rv.setAdapter(callLogAdapter);
        rv.addItemDecoration(new StickyDecoration(getApplicationContext(), callInfoLogs));
    }

    private void initData() {
        //获取通话记录
        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(
                Manifest.permission.READ_CALL_LOG).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        //权限允许就获取通话记录
                        getCallLog(MainActivity.this);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(MainActivity.this, "权限拒绝", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取所有的通话记录
     *
     * @param context
     */
    public void getCallLog(Context context) {
        try {
            callInfoLogs.clear();
            ContentResolver cr = context.getContentResolver();
            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] projection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE,
                    CallLog.Calls.TYPE, CallLog.Calls.CACHED_NAME, CallLog.Calls.DURATION, CallLog.Calls.GEOCODED_LOCATION};
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            }
            Cursor cursor = cr.query(uri, projection, null, null, CallLog.Calls.DATE + " DESC");
            while (cursor.moveToNext()) {
                CallInfoLog callInfoLog = new CallInfoLog();
                String number = cursor.getString(0);//电话号码
                long date = cursor.getLong(1);//通话时间
                int type = cursor.getInt(2);//通话类型
                String name = cursor.getString(3);//名字
                String duration = cursor.getString(4);//通话时长
                String areaCode = cursor.getString(5);//归属地
                String callTime = TransitionTime.convertTimeFirstStyle(date);
                if (TransitionTime.getTodayData().equals(callTime)) {//如果是今天的话
                    callInfoLog.setCallTime("今天");
                } else if (TransitionTime.getYesData().equals(callTime)) {
                    callInfoLog.setCallTime("昨天");
                } else {
                    callInfoLog.setCallTime(callTime);
                }
                callInfoLog.setNumber(number);
                callInfoLog.setDate(date);
                callInfoLog.setType(type);
                callInfoLog.setName(name);
                callInfoLog.setCountType(1);
                callInfoLog.setDuration(duration);
                callInfoLog.setCode(areaCode);
                //筛选数据
                if (TextUtils.isEmpty(number)) {
                    callInfoLogs.add(callInfoLog);
                    continue;
                }
                boolean isadd = screenData(callInfoLogs, callInfoLog);
                if (isadd) {
                    callInfoLogs.add(callInfoLog);
                }
            }
            cursor.close();
            callLogAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 筛选数据
     * @param callInfoLogs
     * @param info
     * @return
     */
    private boolean screenData(ArrayList<CallInfoLog> callInfoLogs, CallInfoLog info) {
        if (callInfoLogs.size() > 0) {
            for (int i = 0; i < callInfoLogs.size(); i++) {
                CallInfoLog callInfoLog = callInfoLogs.get(i);
                //如果说是日期和类型全部一样的话那么这个通话记录就不要,变成一个数量归为最近一次记录里面
                if (callInfoLog.getCallTime().equals(info.getCallTime()) && callInfoLog.getType() == info.getType() && info.getNumber().equals(callInfoLog.getNumber())) {
                    callInfoLog.setCountType(callInfoLog.getCountType() + 1);//递增一次
                    //结束这次数据查找
                    return false;
                }
            }
        }
        return true;
    }
}
