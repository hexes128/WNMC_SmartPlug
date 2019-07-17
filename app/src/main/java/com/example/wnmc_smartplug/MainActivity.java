package com.example.wnmc_smartplug;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {


    private Context context;
    private Toolbar toolbar;
    private DeviceList.MyAdapter mAdapterBygvGet;
    private List<BluetoothDevice> mBluetoothDeviceListBygvGet;

    private ArrayList<String> myDatasetByUserselect = new ArrayList<>();

    private List<Boolean> connectindex = new ArrayList<Boolean>();

    private int mBluetoothGattselectindex = 0;
    private boolean accidentdisconnect0 = false, accidentdisconnect1 = false, accidentdisconnect2 = false;
    private BluetoothGatt mBluetoothGatt0, mBluetoothGatt1, mBluetoothGatt2 = null;

    private BluetoothGattService Service;
    private BluetoothGattCharacteristic chara0, chara1, chara2;

    private RecyclerView mRecyclerView;
    private int selectedindex0 = -1, selectedindex1 = -1, selectedindex2 = -1;
    private int scheduleindex = -1;

    private ProgressDialog progressDialog;


    private Handler timer = new Handler();

    private List<Integer> getrecord = new ArrayList<Integer>();
    private List<Integer> year = new ArrayList<Integer>();
    private List<Integer> month = new ArrayList<Integer>();
    private List<Double> date = new ArrayList<Double>();


    private List<String> current = new ArrayList<String>();
    private List<String> voltage = new ArrayList<String>();
    private List<String> power = new ArrayList<String>();
    private List<String> frequency = new ArrayList<String>();
    private List<String> pf = new ArrayList<String>();
    private List<Integer> YEAR = new ArrayList<>();
    private List<Integer> MON = new ArrayList<>();
    private List<Integer> DATE = new ArrayList<>();
    private List<Integer> DAY = new ArrayList<>();
    private List<Integer> HR = new ArrayList<>();
    private List<Integer> MIN = new ArrayList<>();
    private List<Integer> SEC = new ArrayList<>();
    private List<Boolean> toggleischecked = new ArrayList<>();
    private MyAdapter mAdapterByUserselect;

    GlobalVariable gv;
    private Runnable GattDatasend0, GattDatasend1, GattDatasend2;

    private int receive0, receive1, receive2;
    private int send0 = 0, send1 = 0, send2 = 0;
    private List<String> getschedule = new ArrayList<>();


    private boolean firstrecordindex = false;
    private boolean systemsetable0 = true, systemsetable1 = true, systemsetable2 = true;
    private boolean usersetable0 = false, usersetable1 = false, usersetable2 = false;
    private boolean swaskable = true;
    public static String scheduletest = "scheduletest";
    private byte Schedulenum = 0x00;
    private Intent scheduleintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!getPackageManager().hasSystemFeature(getPackageManager().FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getBaseContext(), "No_sup_ble", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        progressDialog = new ProgressDialog(MainActivity.this);

        toolbar = findViewById(R.id.toolbar);

        context = this;
        mRecyclerView = findViewById(R.id.list_view);
        gv = (GlobalVariable) getApplicationContext();


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        scheduleintent = new Intent(scheduletest);

        if (gv.selectedflag) {
            mAdapterBygvGet = gv.mAdapterByUserselect;
            mBluetoothDeviceListBygvGet = gv.mBluetoothDeviceListByUserselect;
            for (int index = 0; index < mAdapterBygvGet.getItemCount(); index++) {
                myDatasetByUserselect.add(mAdapterBygvGet.getItemData(index)); //將gv的RECYCLEVIEW資料轉移到這裡的LIST
                toggleischecked.add(false);
                connectindex.add(false);
                current.add("");
                voltage.add("");
                power.add("");
                frequency.add("");
                pf.add("");
                YEAR.add(0);
                MON.add(0);
                DATE.add(0);
                DAY.add(0);
                DAY.add(0);
                HR.add(0);
                MIN.add(0);
                SEC.add(0);

            }

            mAdapterByUserselect = new MyAdapter(myDatasetByUserselect, connectindex, current, voltage, power, frequency, pf, YEAR, MON, DATE, DAY, HR, MIN, SEC, toggleischecked);
            mRecyclerView.setAdapter(mAdapterByUserselect);


        }


        GattDatasend0 = new Runnable() {
            @Override
            public void run() {

                if (mBluetoothGatt0 != null && chara0 != null) {
                    if (gv.schedulesetflag && scheduleindex == selectedindex0) {
                        send0 = 7;
                    }


                    switch (send0) {
                        case (0): {

                            chara0.setValue(new byte[]{-2, 0x00, 1, 0, 1, 0, 1, 0, 1}); //查詢電力
                            receive0 = 0;
                            break;
                        }

                        case (3): {

                            chara0.setValue(new byte[]{-2, 0x03, 0x01, 0, 1, 0, 1, 0, 1}); //查詢紀錄
                            receive0 = 3;
                            break;
                        }
                        case (5): {

                            chara0.setValue(new byte[]{-2, 0x05, Schedulenum, 0, 1, 0, 1, 0, 1});//查詢排程
                            receive0 = 5;
                            break;
                        }
                        case (7): {

                            chara0.setValue(gv.schedulebyte); //設定排程
                            receive0 = 7;
                            break;
                        }

                        case (9): {

                            toggleischecked.set(selectedindex0, true);
                            chara0.setValue(new byte[]{-2, 0x09, 0x01, 0, 1, 0, 1, 0, 1}); //開關on
                            receive0 = 9;
                            break;
                        }
                        case (10): {

                            toggleischecked.set(selectedindex0, false);
                            chara0.setValue(new byte[]{-2, 0x09, 0x00, 0, 1, 0, 1, 0, 1}); //開關off
                            receive0 = 10;
                            break;
                        }
                    }
                    mBluetoothGatt0.writeCharacteristic(chara0);

                }
            }
        };
        GattDatasend1 = new Runnable() {
            @Override
            public void run() {

                if (mBluetoothGatt1 != null && chara1 != null) {
                    if (gv.schedulesetflag && scheduleindex == selectedindex1) {
                        send1 = 7;
                    }


                    switch (send1) {
                        case (0): {

                            chara1.setValue(new byte[]{-2, 0x00, 1, 0, 1, 0, 1, 0, 1}); //查詢電力
                            receive1 = 0;
                            break;
                        }

                        case (3): {

                            chara1.setValue(new byte[]{-2, 0x03, 0x01, 0, 1, 0, 1, 0, 1}); //查詢紀錄
                            receive1 = 3;
                            break;
                        }
                        case (5): {

                            chara1.setValue(new byte[]{-2, 0x05, Schedulenum, 0, 1, 0, 1, 0, 1});//查詢排程
                            receive1 = 5;
                            break;
                        }
                        case (7): {

                            chara1.setValue(gv.schedulebyte); //設定排程
                            receive1 = 7;
                            break;
                        }

                        case (9): {

                            toggleischecked.set(selectedindex1, true);
                            chara1.setValue(new byte[]{-2, 0x09, 0x01, 0, 1, 0, 1, 0, 1}); //開關on
                            receive1 = 9;
                            break;
                        }
                        case (10): {

                            toggleischecked.set(selectedindex1, false);
                            chara1.setValue(new byte[]{-2, 0x09, 0x00, 0, 1, 0, 1, 0, 1}); //開關off
                            receive1 = 10;
                            break;
                        }
                    }
                    mBluetoothGatt1.writeCharacteristic(chara1);

                }
            }
        };



    }

    private final BluetoothGattCallback mGattCallback0 = new BluetoothGattCallback() {


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic0) {
            byte[] rec = characteristic0.getValue();

            switch (receive0) {
                case (0): {

                    if (rec.length != 10) {
                        return;
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    String vRms = decimalFormat.format(((double) ((rec[0] & 0xFF) + (rec[1] & 0xFF) * 256)) / 64.0);
                    String iRms = decimalFormat.format(((double) ((rec[2] & 0xFF) + (rec[3] & 0xFF) * 256)) / 64.0);
                    String pow = decimalFormat.format(((double) ((rec[4] & 0xFF) + (rec[5] & 0xFF) * 256)) / 64.0);
                    String freq = decimalFormat.format(((double) ((rec[6] & 0xFF) + (rec[7] & 0xFF) * 256)) / 64.0);
                    String Pf = decimalFormat.format(((double) ((rec[8] & 0xFF) + (rec[9] & 0xFF) * 256)) / 64.0);

                    current.set(selectedindex0, "電流 " + iRms);
                    voltage.set(selectedindex0, "電壓 " + vRms);
                    power.set(selectedindex0, "功率 " + pow);
                    frequency.set(selectedindex0, "頻率 " + freq);
                    pf.set(selectedindex0, "功率因數 " + Pf);


                    chara0.setValue(new byte[]{-2, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //查詢時間
                    mBluetoothGatt0.writeCharacteristic(chara0);
                    receive0 = 1;
                    break;

                }
                case (1): {

                    if (rec.length != 7) {
                        return;
                    }
                    int year = (rec[6] / 16 * 10) + rec[6] % 16;
                    int month = (rec[5] / 16 * 10) + rec[5] % 16;
                    int date = (rec[4] / 16 * 10) + rec[4] % 16;
                    int day = (rec[3] / 16 * 10) + rec[3] % 16;
                    int hour = (rec[2] / 16 * 10) + rec[2] % 16;
                    int min = (rec[1] / 16 * 10) + rec[1] % 16;
                    int sec = (rec[0] / 16 * 10) + rec[0] % 16;


                    if (sec != SEC.get(selectedindex0)) {
                        YEAR.set(selectedindex0, year);
                        MON.set(selectedindex0, month);
                        DATE.set(selectedindex0, date);
                        DAY.set(selectedindex0, day);
                        HR.set(selectedindex0, hour);
                        MIN.set(selectedindex0, min);
                        SEC.set(selectedindex0, sec);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapterByUserselect.notifyDataSetChanged();

                            }
                        });
                    }
                    chara0.setValue(new byte[]{-2, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //查詢時間
                    mBluetoothGatt0.writeCharacteristic(chara0);
                    receive0 = 8;


                    break;
                }
                case (8): {


                    if (swaskable) {

                        if (rec[0] == 1 && !toggleischecked.get(selectedindex0)) {
                            toggleischecked.set(selectedindex0, true);
                        }
                        if (rec[0] == 0 && toggleischecked.get(selectedindex0)) {
                            toggleischecked.set(selectedindex0, false);
                        }
                    }


                    timer.postDelayed(GattDatasend0, 1);


                    break;
                }
                case (9): {
                    send0 = 0;
                    swaskable = true;
                    timer.postDelayed(GattDatasend0, 1);
                    break;
                }
                case (10): {
                    send0 = 0;
                    swaskable = true;
                    timer.postDelayed(GattDatasend0, 1);
                    break;
                }

                case (5): {

                    if (rec.length != 4) {
                        return;
                    }
                    if (Byte.toUnsignedInt(rec[2]) <= 7 && Byte.toUnsignedInt(rec[2]) >= 1) {
                        getschedule.add("day " + Byte.toUnsignedInt(rec[2]) + " " + Byte.toUnsignedInt(rec[1]) + "時" + " " + Byte.toUnsignedInt(rec[0]) + "分 " + (Byte.toUnsignedInt(rec[3]) == 1 ? "ON" : "OFF"));

                    } else {
                        getschedule.add("無排程");
                    }
                    if (Schedulenum < 0x07) {
                        Schedulenum++;

                        if (mBluetoothGatt0 != null && chara0 != null) {

                            chara0.setValue(new byte[]{-2, 0x05, Schedulenum, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
                            mBluetoothGatt0.writeCharacteristic(chara0);
                            send0 = 5;
                        }

                    } else {
                        Schedulenum = 0x00;
                        for (int i = 0; i < getschedule.size(); i++) {
                            Log.e("排程", "" + i + " " + getschedule.get(i));
                        }
                        systemsetable0 = true;
                        usersetable0 = false;
                        progressDialog.dismiss();
                        gv.schedulex = getschedule;
                        Intent intent = new Intent(MainActivity.this, schedule.class);
                        startActivity(intent);
                        send0 = 0;
                        timer.postDelayed(GattDatasend0, 1);

                    }

                    break;
                }
                case (7): {

                    if (rec[0] == 79 && rec[1] == 75) {
                        sendBroadcast(scheduleintent);
                        gv.schedulesetflag = false;
                    } else {
                        Log.e("設定", "失敗");
                    }


                    send0 = 0;
                    timer.postDelayed(GattDatasend0, 1);
                    break;
                }


                case (3): {

                    firstrecordindex = rec.length == 20 ? true : firstrecordindex;

                    if (!firstrecordindex) {
                        return;
                    }


                    progressDialog.setMessage("正在取得紀錄 " + (double) (int) ((((double) getrecord.size()) / 1024 * 100) + 0.5) + "%");

                    for (int ind = 0; ind < rec.length; ind++) {

                        getrecord.add(Byte.toUnsignedInt(rec[ind]));


                    }
                    Log.e("資料長度", getrecord.size() + "");
                    if (getrecord.size() == 1024) {
                        progressDialog.dismiss();

                        Log.e("資料", "讀取完畢");
                        date.clear();
                        month.clear();
                        year.clear();

                        int i = 0;
                        while (i < getrecord.size()) {
                            if (getrecord.get(i) == 255) {
                                for (int j = i; j < i + 64; j++) {
                                    getrecord.remove(j);
                                    i = 0;
                                }

                            } else {
                                i = i + 64;
                            }


                        }


                        for (int getrecordindex0 = 0; getrecordindex0 < getrecord.size(); getrecordindex0++) {
                            if (getrecordindex0 % 64 == 0) {
                                int monthcaculate = getrecord.get(getrecordindex0) / 16 * 10 + getrecord.get(getrecordindex0) % 16;

                                month.add(monthcaculate);

                            } else if (getrecordindex0 % 64 == 1) {
                                int yearcaculate = getrecord.get(getrecordindex0) / 16 * 10 + getrecord.get(getrecordindex0) % 16;

                                year.add(yearcaculate);

                            } else {
                                if (getrecordindex0 % 2 == 0) {
                                    double powercaculate = (getrecord.get(getrecordindex0) + getrecord.get(getrecordindex0 + 1) * 256) / 64.0;

                                    if (powercaculate != 65535) {
                                        date.add(powercaculate);
                                    } else {
                                        date.add(0.0);
                                    }
                                }
                            }


                        }


                        firstrecordindex = false;


                        timer.postDelayed(GattDatasend0, 1);
                        send0 = 0;
                        gv.year = year;
                        gv.month = month;
                        gv.date = date;
                        Intent intent = new Intent(MainActivity.this, powerdraw.class);
                        startActivity(intent);

                    }


                    break;

                }


            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {
                progressDialog.dismiss();

                Service = gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                chara0 = Service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (gatt != null && chara0 != null) {

                    gatt.setCharacteristicNotification(chara0, true);
                }

                byte[] password = {-2, 0x0A, '1', '2', '3', '4', '5', '6', '7'};
                chara0.setValue(password);
                gatt.writeCharacteristic(chara0);
                mBluetoothGatt0.writeCharacteristic(chara0);
                if (!accidentdisconnect0) {
                    mBluetoothGattselectindex++;

                } else {

                    accidentdisconnect0 = false;
                }
                timer.postDelayed(GattDatasend0, 2000);

            }

        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {


            if (newState == BluetoothGatt.STATE_CONNECTED) {

                connectindex.set(selectedindex0, true);
                current.set(selectedindex0, "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gatt.discoverServices();
                        mAdapterByUserselect.notifyDataSetChanged();
                    }
                });
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {

                connectindex.set(selectedindex0, false);
                current.set(selectedindex0, "正在重新連線");
                voltage.set(selectedindex0, "");
                power.set(selectedindex0, "");
                frequency.set(selectedindex0, "");
                pf.set(selectedindex0, "");
                toggleischecked.set(selectedindex0, false);
                firstrecordindex = false;
                accidentdisconnect0 = true;

                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapterByUserselect.notifyDataSetChanged();
                    }
                });
            }


        }


    };

    private final BluetoothGattCallback mGattCallback1 = new BluetoothGattCallback() {


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic1) {
            byte[] rec = characteristic1.getValue();

            switch (receive1) {
                case (0): {

                    if (rec.length != 10) {
                        return;
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    String vRms = decimalFormat.format(((double) ((rec[0] & 0xFF) + (rec[1] & 0xFF) * 256)) / 64.0);
                    String iRms = decimalFormat.format(((double) ((rec[2] & 0xFF) + (rec[3] & 0xFF) * 256)) / 64.0);
                    String pow = decimalFormat.format(((double) ((rec[4] & 0xFF) + (rec[5] & 0xFF) * 256)) / 64.0);
                    String freq = decimalFormat.format(((double) ((rec[6] & 0xFF) + (rec[7] & 0xFF) * 256)) / 64.0);
                    String Pf = decimalFormat.format(((double) ((rec[8] & 0xFF) + (rec[9] & 0xFF) * 256)) / 64.0);

                    current.set(selectedindex1, "電流 " + iRms);
                    voltage.set(selectedindex1, "電壓 " + vRms);
                    power.set(selectedindex1, "功率 " + pow);
                    frequency.set(selectedindex1, "頻率 " + freq);
                    pf.set(selectedindex1, "功率因數 " + Pf);


                    chara1.setValue(new byte[]{-2, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //查詢時間
                    mBluetoothGatt1.writeCharacteristic(chara1);
                    receive1 = 1;
                    break;

                }
                case (1): {

                    if (rec.length != 7) {
                        return;
                    }
                    int year = (rec[6] / 16 * 10) + rec[6] % 16;
                    int month = (rec[5] / 16 * 10) + rec[5] % 16;
                    int date = (rec[4] / 16 * 10) + rec[4] % 16;
                    int day = (rec[3] / 16 * 10) + rec[3] % 16;
                    int hour = (rec[2] / 16 * 10) + rec[2] % 16;
                    int min = (rec[1] / 16 * 10) + rec[1] % 16;
                    int sec = (rec[0] / 16 * 10) + rec[0] % 16;


                    if (sec != SEC.get(selectedindex1)) {
                        YEAR.set(selectedindex1, year);
                        MON.set(selectedindex1, month);
                        DATE.set(selectedindex1, date);
                        DAY.set(selectedindex1, day);
                        HR.set(selectedindex1, hour);
                        MIN.set(selectedindex1, min);
                        SEC.set(selectedindex1, sec);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                mAdapterByUserselect.notifyDataSetChanged();

                            }
                        });
                    }
                    chara1.setValue(new byte[]{-2, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //查詢時間
                    mBluetoothGatt1.writeCharacteristic(chara1);
                    receive1 = 8;


                    break;
                }
                case (8): {


                    if (swaskable) {

                        if (rec[0] == 1 && !toggleischecked.get(selectedindex1)) {
                            toggleischecked.set(selectedindex1, true);
                        }
                        if (rec[0] == 0 && toggleischecked.get(selectedindex1)) {
                            toggleischecked.set(selectedindex1, false);
                        }
                    }


                    timer.postDelayed(GattDatasend1, 1);


                    break;
                }
                case (9): {
                    send1 = 0;
                    swaskable = true;
                    timer.postDelayed(GattDatasend1, 1);
                    break;
                }
                case (10): {
                    send1 = 0;
                    swaskable = true;
                    timer.postDelayed(GattDatasend1, 1);
                    break;
                }

                case (5): {

                    if (rec.length != 4) {
                        return;
                    }
                    if (Byte.toUnsignedInt(rec[2]) <= 7 && Byte.toUnsignedInt(rec[2]) >= 1) {
                        getschedule.add("day " + Byte.toUnsignedInt(rec[2]) + " " + Byte.toUnsignedInt(rec[1]) + "時" + " " + Byte.toUnsignedInt(rec[0]) + "分 " + (Byte.toUnsignedInt(rec[3]) == 1 ? "ON" : "OFF"));

                    } else {
                        getschedule.add("無排程");
                    }
                    if (Schedulenum < 0x07) {
                        Schedulenum++;

                        if (mBluetoothGatt1 != null && chara1 != null) {

                            chara1.setValue(new byte[]{-2, 0x05, Schedulenum, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
                            mBluetoothGatt1.writeCharacteristic(chara1);
                            send1 = 5;
                        }

                    } else {
                        Schedulenum = 0x00;
                        for (int i = 0; i < getschedule.size(); i++) {
                            Log.e("排程", "" + i + " " + getschedule.get(i));
                        }
                        systemsetable0 = true;
                        usersetable0 = false;
                        progressDialog.dismiss();
                        gv.schedulex = getschedule;
                        Intent intent = new Intent(MainActivity.this, schedule.class);
                        startActivity(intent);
                        send1 = 0;
                        timer.postDelayed(GattDatasend1, 1);

                    }

                    break;
                }
                case (7): {

                    if (rec[0] == 79 && rec[1] == 75) {
                        sendBroadcast(scheduleintent);
                        gv.schedulesetflag = false;
                    } else {
                        Log.e("設定", "失敗");
                    }


                    send1 = 0;
                    timer.postDelayed(GattDatasend1, 1);
                    break;
                }


                case (3): {

                    firstrecordindex = rec.length == 20 ? true : firstrecordindex;

                    if (!firstrecordindex) {
                        return;
                    }


                    progressDialog.setMessage("正在取得紀錄 " + (double) (int) ((((double) getrecord.size()) / 1024 * 100) + 0.5) + "%");

                    for (int ind = 0; ind < rec.length; ind++) {

                        getrecord.add(Byte.toUnsignedInt(rec[ind]));


                    }
                    Log.e("資料長度", getrecord.size() + "");
                    if (getrecord.size() == 1024) {
                        progressDialog.dismiss();

                        Log.e("資料", "讀取完畢");
                        date.clear();
                        month.clear();
                        year.clear();

                        int i = 0;
                        while (i < getrecord.size()) {
                            if (getrecord.get(i) == 255) {
                                for (int j = i; j < i + 64; j++) {
                                    getrecord.remove(j);
                                    i = 0;
                                }

                            } else {
                                i = i + 64;
                            }


                        }


                        for (int getrecordindex1 = 0; getrecordindex1 < getrecord.size(); getrecordindex1++) {
                            if (getrecordindex1 % 64 == 0) {
                                int monthcaculate = getrecord.get(getrecordindex1) / 16 * 10 + getrecord.get(getrecordindex1) % 16;

                                month.add(monthcaculate);

                            } else if (getrecordindex1 % 64 == 1) {
                                int yearcaculate = getrecord.get(getrecordindex1) / 16 * 10 + getrecord.get(getrecordindex1) % 16;

                                year.add(yearcaculate);

                            } else {
                                if (getrecordindex1 % 2 == 0) {
                                    double powercaculate = (getrecord.get(getrecordindex1) + getrecord.get(getrecordindex1 + 1) * 256) / 64.0;

                                    if (powercaculate != 65535) {
                                        date.add(powercaculate);
                                    } else {
                                        date.add(0.0);
                                    }
                                }
                            }


                        }


                        firstrecordindex = false;


                        timer.postDelayed(GattDatasend1, 1);
                        send1 = 0;
                        gv.year = year;
                        gv.month = month;
                        gv.date = date;
                        Intent intent = new Intent(MainActivity.this, powerdraw.class);
                        startActivity(intent);

                    }


                    break;

                }


            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {
                progressDialog.dismiss();

                Service = gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                chara1 = Service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (gatt != null && chara1 != null) {

                    gatt.setCharacteristicNotification(chara1, true);
                }

                byte[] password = {-2, 0x0A, '1', '2', '3', '4', '5', '6', '7'};
                chara1.setValue(password);
                gatt.writeCharacteristic(chara1);
                mBluetoothGatt1.writeCharacteristic(chara1);
                if (!accidentdisconnect1) {
                    mBluetoothGattselectindex++;

                } else {

                    accidentdisconnect1 = false;
                }
                timer.postDelayed(GattDatasend1, 2000);

            }

        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {


            if (newState == BluetoothGatt.STATE_CONNECTED) {

                connectindex.set(selectedindex1, true);
                current.set(selectedindex1, "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gatt.discoverServices();
                        mAdapterByUserselect.notifyDataSetChanged();
                    }
                });
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {

                connectindex.set(selectedindex1, false);
                current.set(selectedindex1, "正在重新連線");
                voltage.set(selectedindex1, "");
                power.set(selectedindex1, "");
                frequency.set(selectedindex1, "");
                pf.set(selectedindex1, "");
                toggleischecked.set(selectedindex1, false);
                firstrecordindex = false;
                accidentdisconnect1 = true;

                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapterByUserselect.notifyDataSetChanged();
                    }
                });
            }


        }


    };
    private final BluetoothGattCallback mGattCallback2 = new BluetoothGattCallback() {


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic2) {
            byte[] rec = characteristic2.getValue();

            switch (receive2) {
                case (0): {

                    if (rec.length != 10) {
                        return;
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    String vRms = decimalFormat.format(((double) ((rec[0] & 0xFF) + (rec[1] & 0xFF) * 256)) / 64.0);
                    String iRms = decimalFormat.format(((double) ((rec[2] & 0xFF) + (rec[3] & 0xFF) * 256)) / 64.0);
                    String pow = decimalFormat.format(((double) ((rec[4] & 0xFF) + (rec[5] & 0xFF) * 256)) / 64.0);
                    String freq = decimalFormat.format(((double) ((rec[6] & 0xFF) + (rec[7] & 0xFF) * 256)) / 64.0);
                    String Pf = decimalFormat.format(((double) ((rec[8] & 0xFF) + (rec[9] & 0xFF) * 256)) / 64.0);

                    current.set(selectedindex2, "電流 " + iRms);
                    voltage.set(selectedindex2, "電壓 " + vRms);
                    power.set(selectedindex2, "功率 " + pow);
                    frequency.set(selectedindex2, "頻率 " + freq);
                    pf.set(selectedindex2, "功率因數 " + Pf);


                    chara2.setValue(new byte[]{-2, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //查詢時間
                    mBluetoothGatt2.writeCharacteristic(chara2);
                    receive2 = 1;
                    break;

                }
                case (1): {

                    if (rec.length != 7) {
                        return;
                    }
                    int year = (rec[6] / 16 * 10) + rec[6] % 16;
                    int month = (rec[5] / 16 * 10) + rec[5] % 16;
                    int date = (rec[4] / 16 * 10) + rec[4] % 16;
                    int day = (rec[3] / 16 * 10) + rec[3] % 16;
                    int hour = (rec[2] / 16 * 10) + rec[2] % 16;
                    int min = (rec[1] / 16 * 10) + rec[1] % 16;
                    int sec = (rec[0] / 16 * 10) + rec[0] % 16;


                    if (sec != SEC.get(selectedindex2)) {
                        YEAR.set(selectedindex2, year);
                        MON.set(selectedindex2, month);
                        DATE.set(selectedindex2, date);
                        DAY.set(selectedindex2, day);
                        HR.set(selectedindex2, hour);
                        MIN.set(selectedindex2, min);
                        SEC.set(selectedindex2, sec);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapterByUserselect.notifyDataSetChanged();

                            }
                        });
                    }
                    chara2.setValue(new byte[]{-2, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //查詢時間
                    mBluetoothGatt2.writeCharacteristic(chara2);
                    receive2 = 8;


                    break;
                }
                case (8): {


                    if (swaskable) {

                        if (rec[0] == 1 && !toggleischecked.get(selectedindex2)) {
                            toggleischecked.set(selectedindex2, true);
                        }
                        if (rec[0] == 0 && toggleischecked.get(selectedindex2)) {
                            toggleischecked.set(selectedindex2, false);
                        }
                    }


                    timer.postDelayed(GattDatasend2, 1);


                    break;
                }
                case (9): {
                    send2 = 0;
                    swaskable = true;
                    timer.postDelayed(GattDatasend2, 1);
                    break;
                }
                case (10): {
                    send2 = 0;
                    swaskable = true;
                    timer.postDelayed(GattDatasend2, 1);
                    break;
                }

                case (5): {

                    if (rec.length != 4) {
                        return;
                    }
                    if (Byte.toUnsignedInt(rec[2]) <= 7 && Byte.toUnsignedInt(rec[2]) >= 1) {
                        getschedule.add("day " + Byte.toUnsignedInt(rec[2]) + " " + Byte.toUnsignedInt(rec[1]) + "時" + " " + Byte.toUnsignedInt(rec[0]) + "分 " + (Byte.toUnsignedInt(rec[3]) == 1 ? "ON" : "OFF"));

                    } else {
                        getschedule.add("無排程");
                    }
                    if (Schedulenum < 0x07) {
                        Schedulenum++;

                        if (mBluetoothGatt2 != null && chara2 != null) {

                            chara2.setValue(new byte[]{-2, 0x05, Schedulenum, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
                            mBluetoothGatt2.writeCharacteristic(chara2);
                            send2 = 5;
                        }

                    } else {
                        Schedulenum = 0x00;
                        for (int i = 0; i < getschedule.size(); i++) {
                            Log.e("排程", "" + i + " " + getschedule.get(i));
                        }
                        systemsetable0 = true;
                        usersetable0 = false;
                        progressDialog.dismiss();
                        gv.schedulex = getschedule;
                        Intent intent = new Intent(MainActivity.this, schedule.class);
                        startActivity(intent);
                        send2 = 0;
                        timer.postDelayed(GattDatasend2, 1);

                    }

                    break;
                }
                case (7): {

                    if (rec[0] == 79 && rec[1] == 75) {
                        sendBroadcast(scheduleintent);
                        gv.schedulesetflag = false;
                    } else {
                        Log.e("設定", "失敗");
                    }


                    send2 = 0;
                    timer.postDelayed(GattDatasend2, 1);
                    break;
                }


                case (3): {

                    firstrecordindex = rec.length == 20 ? true : firstrecordindex;

                    if (!firstrecordindex) {
                        return;
                    }


                    progressDialog.setMessage("正在取得紀錄 " + (double) (int) ((((double) getrecord.size()) / 1024 * 100) + 0.5) + "%");

                    for (int ind = 0; ind < rec.length; ind++) {

                        getrecord.add(Byte.toUnsignedInt(rec[ind]));


                    }
                    Log.e("資料長度", getrecord.size() + "");
                    if (getrecord.size() == 1024) {
                        progressDialog.dismiss();

                        Log.e("資料", "讀取完畢");
                        date.clear();
                        month.clear();
                        year.clear();

                        int i = 0;
                        while (i < getrecord.size()) {
                            if (getrecord.get(i) == 255) {
                                for (int j = i; j < i + 64; j++) {
                                    getrecord.remove(j);
                                    i = 0;
                                }

                            } else {
                                i = i + 64;
                            }


                        }


                        for (int getrecordindex2 = 0; getrecordindex2 < getrecord.size(); getrecordindex2++) {
                            if (getrecordindex2 % 64 == 0) {
                                int monthcaculate = getrecord.get(getrecordindex2) / 16 * 10 + getrecord.get(getrecordindex2) % 16;

                                month.add(monthcaculate);

                            } else if (getrecordindex2 % 64 == 1) {
                                int yearcaculate = getrecord.get(getrecordindex2) / 16 * 10 + getrecord.get(getrecordindex2) % 16;

                                year.add(yearcaculate);

                            } else {
                                if (getrecordindex2 % 2 == 0) {
                                    double powercaculate = (getrecord.get(getrecordindex2) + getrecord.get(getrecordindex2 + 1) * 256) / 64.0;

                                    if (powercaculate != 65535) {
                                        date.add(powercaculate);
                                    } else {
                                        date.add(0.0);
                                    }
                                }
                            }


                        }


                        firstrecordindex = false;


                        timer.postDelayed(GattDatasend2, 1);
                        send2 = 0;
                        gv.year = year;
                        gv.month = month;
                        gv.date = date;
                        Intent intent = new Intent(MainActivity.this, powerdraw.class);
                        startActivity(intent);

                    }


                    break;

                }


            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {
                progressDialog.dismiss();

                Service = gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                chara2 = Service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (gatt != null && chara2 != null) {

                    gatt.setCharacteristicNotification(chara2, true);
                }

                byte[] password = {-2, 0x0A, '1', '2', '3', '4', '5', '6', '7'};
                chara2.setValue(password);
                gatt.writeCharacteristic(chara2);
                mBluetoothGatt2.writeCharacteristic(chara2);
                if (!accidentdisconnect2) {
                    mBluetoothGattselectindex++;

                } else {

                    accidentdisconnect2 = false;
                }
                timer.postDelayed(GattDatasend2, 2000);

            }

        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {


            if (newState == BluetoothGatt.STATE_CONNECTED) {

                connectindex.set(selectedindex2, true);
                current.set(selectedindex2, "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gatt.discoverServices();
                        mAdapterByUserselect.notifyDataSetChanged();
                    }
                });
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {

                connectindex.set(selectedindex2, false);
                current.set(selectedindex2, "正在重新連線");
                voltage.set(selectedindex2, "");
                power.set(selectedindex2, "");
                frequency.set(selectedindex2, "");
                pf.set(selectedindex2, "");
                toggleischecked.set(selectedindex2, false);
                firstrecordindex = false;
                accidentdisconnect2 = true;

                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapterByUserselect.notifyDataSetChanged();
                    }
                });
            }


        }


    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        timer.removeCallbacks(GattDatasend0);
        timer.removeCallbacks(GattDatasend1);
        timer.removeCallbacks(GattDatasend2);
        if (mBluetoothDeviceListBygvGet != null) {
            mBluetoothDeviceListBygvGet.clear();
        }
        if (mBluetoothGatt0 != null) {
            mBluetoothGatt0.disconnect();
            mBluetoothGatt0.close();
            mBluetoothGatt0 = null;
        }

        if (mBluetoothGatt1 != null) {
            mBluetoothGatt1.disconnect();
            mBluetoothGatt1.close();
            mBluetoothGatt1 = null;
        }

        if (mBluetoothGatt2 != null) {
            mBluetoothGatt2.disconnect();
            mBluetoothGatt2.close();
            mBluetoothGatt2 = null;
        }

        Intent intent = new Intent(MainActivity.this, DeviceList.class);
        startActivity(intent);
        mBluetoothGattselectindex = 0;

        finish();
        return true;


    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;
        private List<Boolean> mconnectindex;
        private List<String> mvoltage;
        private List<String> mcurrent;
        private List<String> mpower;
        private List<String> mfrequency;
        private List<String> mpf;
        private List<Boolean> mischecked;
        private List<Integer> mYEAR;
        private List<Integer> mMON;
        private List<Integer> mDATE;
        private List<Integer> mDAY;
        private List<Integer> mHR;
        private List<Integer> mMIN;
        private List<Integer> mSEC;

        public MyAdapter(List<String> data, List<Boolean> connectindex,
                         List<String> current, List<String> voltage, List<String> power, List<String> frequency, List<String> pf,
                         List<Integer> YEAR, List<Integer> MON, List<Integer> DATE, List<Integer> DAY, List<Integer> HR, List<Integer> MIN, List<Integer> SEC, List<Boolean> tischecked) {
            mData = data;
            mconnectindex = connectindex;
            mcurrent = current;
            mvoltage = voltage;
            mpower = power;
            mfrequency = frequency;
            mpf = pf;
            mYEAR = YEAR;
            mMON = MON;
            mDATE = DATE;
            mDAY = DAY;
            mHR = HR;
            mMIN = MIN;
            mSEC = SEC;
            mischecked = tischecked;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView devicename;
            public ImageView connectlight;
            public TextView current, voltage, power, frequency, pf;
            public TextView time;
            public ToggleButton sw;
            public Button record;
            public Button schedule;


            public ViewHolder(View v) {
                super(v);
                schedule = v.findViewById(R.id.schedule);
                devicename = v.findViewById(R.id.deviceName);
                connectlight = v.findViewById(R.id.connectstate);
                current = v.findViewById(R.id.current);
                voltage = v.findViewById(R.id.voltage);
                power = v.findViewById(R.id.power);
                frequency = v.findViewById(R.id.frequency);
                pf = v.findViewById(R.id.pf);
                time = v.findViewById(R.id.time);
                sw = v.findViewById(R.id.togglebtn);
                record = v.findViewById(R.id.record);

            }
        }


        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_name_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.devicename.setText(mData.get(position));
            holder.current.setText(mcurrent.get(position));
            holder.voltage.setText(mvoltage.get(position));
            holder.power.setText(mpower.get(position));
            holder.frequency.setText(mfrequency.get(position));
            holder.pf.setText(mpf.get(position));
            holder.time.setText("20" + mYEAR.get(position) + "/" + mMON.get(position) + "/" + mDATE.get(position) + " 星期" + mDAY.get(position) + " " + mHR.get(position) + ":" + mMIN.get(position) + ":" + mSEC.get(position));

            if (connectindex.get(position)) {
                holder.connectlight.setImageResource(R.drawable.connect);
            }
            if (!connectindex.get(position)) {
                holder.connectlight.setImageResource(R.drawable.unconnect);
            }
            if (mischecked.get(position)) {
                holder.sw.setChecked(true);

            }
            if (!mischecked.get(position)) {
                holder.sw.setChecked(false);

            }

            holder.record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    getrecord.clear();


                    if (position == selectedindex0) {
                        send0 = 3;
                        progressDialog.setMessage("正在取得紀錄 0%");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    if (position == selectedindex1) {
                        send1 = 3;
                        progressDialog.setMessage("正在取得紀錄 0%");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    }
                    if (position == selectedindex2 && mBluetoothGatt2 != null) {
                        send2 = 3;
                        progressDialog.setMessage("正在取得紀錄 0%");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    }

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBluetoothGattselectindex == getItemCount()) {
                        return;
                    }
                    progressDialog.setMessage("連線中");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    if (!accidentdisconnect0 && !accidentdisconnect1 && !accidentdisconnect2) {
                        switch (mBluetoothGattselectindex) {
                            case (0): {
                                if (connectindex.get(position)) {
                                    return;
                                }
                                mBluetoothGatt0 = mBluetoothDeviceListBygvGet.get(position).connectGatt(getApplicationContext(), true, mGattCallback0);


                                selectedindex0 = position;
                                break;
                            }

                            case (1): {
                                if (connectindex.get(position)) {
                                    return;
                                }
                                mBluetoothGatt1 = mBluetoothDeviceListBygvGet.get(position).connectGatt(getApplicationContext(), true, mGattCallback1);


                                selectedindex1 = position;
                                break;
                            }
                            case (2): {
                                if (connectindex.get(position)) {
                                    return;
                                }
                                mBluetoothGatt2 = mBluetoothDeviceListBygvGet.get(position).connectGatt(getApplicationContext(), true, mGattCallback2);


                                selectedindex2 = position;
                                break;
                            }


                        }
                    }


                }
            });
            holder.schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scheduleindex = position;
                    getschedule.clear();
                    progressDialog.setMessage("正在取得排程");
                    progressDialog.setCancelable(true);
                    progressDialog.show();


                    send0 = position == selectedindex0 ? 5 : send0;
                    send1 = position == selectedindex1 ? 5 : send1;
                    send2 = position == selectedindex2 ? 5 : send2;


                }
            });


            holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!buttonView.isPressed()) {
                        return;
                    }
                    swaskable = false;

                    if (position == selectedindex0) {
                        send0 = isChecked ? 9 : 10;
                    }

                    if (position == selectedindex1) {
                        send1 = isChecked ? 9 : 10;
                    }
                    if (position == selectedindex2) {
                        send2 = isChecked ? 9 : 10;
                    }


                }

            });


        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


    }

}
