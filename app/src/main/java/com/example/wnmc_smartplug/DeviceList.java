package com.example.wnmc_smartplug;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckedTextView;


import java.util.ArrayList;
import java.util.List;


public class DeviceList extends AppCompatActivity {

    private GlobalVariable gv;
    //////////////////////////////////////////////////////
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    //////////////////////////////////////////////////////
    private ArrayList<String> myDatasetByScancallback = new ArrayList<>();
    private MyAdapter mAdapterByScancallback;
    private List<BluetoothDevice> mBluetoothDeviceListByScancallback = new ArrayList<BluetoothDevice>();//藍芽搜索回來的list
    //////////////////////////////////////////////////////
    private ArrayList<String> myDatasetByUserselect = new ArrayList<>();
    private MyAdapter mAdapterByUserselect = new MyAdapter(myDatasetByUserselect);
    private SwipeRefreshLayout startscan;


    private boolean[] ischeckedindex = new boolean[50]; //用來模擬選擇想要的裝置

    private Handler handler = new Handler();
    private final int REQUEST_ENABLE_BT = 1;

    private RecyclerView mRecyclerViewByScancallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        init();
        startscan = findViewById(R.id.startscan);

        gv = (GlobalVariable) getApplicationContext();
        gv.mBluetoothDeviceListByUserselect = new ArrayList<BluetoothDevice>();
        gv.mAdapterByUserselect = new MyAdapter(myDatasetByUserselect);
        gv.selectedflag = false;
        mRecyclerViewByScancallback = findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewByScancallback.setLayoutManager(layoutManager);
        mAdapterByScancallback = new MyAdapter(myDatasetByScancallback);
        mRecyclerViewByScancallback.setAdapter(mAdapterByScancallback);
        mAdapterByUserselect = new MyAdapter(myDatasetByUserselect);


        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        startscan.setRefreshing(true);
        bluetoothLeScanner.startScan(scanCallback);
        handler.postDelayed(runnable, 5000);
        startscan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bluetoothLeScanner.startScan(scanCallback);
                handler.postDelayed(runnable, 3000);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (int index = 0; index < mAdapterByScancallback.getItemCount(); index++) {
            if (ischeckedindex[index]) {
                if (!gv.mBluetoothDeviceListByUserselect.contains(mBluetoothDeviceListByScancallback.get(index))) {

                    myDatasetByUserselect.add(myDatasetByScancallback.get(index));
                    mAdapterByUserselect = new MyAdapter(myDatasetByUserselect);
                    gv.mAdapterByUserselect = mAdapterByUserselect;
                    gv.mBluetoothDeviceListByUserselect.add(mBluetoothDeviceListByScancallback.get(index));

                }
            }
            Log.e("選取" + index, " " + ischeckedindex[index]);
        }
        gv.connectflag = true;
        gv.selectedflag = true;
        Intent intent = new Intent(DeviceList.this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bluetoothLeScanner.stopScan(scanCallback);
            startscan.setRefreshing(false);
        }
    };


    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Log.d("msg", "設備不支援藍芽");


            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d("msg", "請求開啟藍芽");

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult results) {


            if (!mBluetoothDeviceListByScancallback.contains(results.getDevice())&&results.getDevice().getName()!=null) {
                mBluetoothDeviceListByScancallback.add(results.getDevice());


                myDatasetByScancallback.add(results.getDevice().getName() + "");

                mAdapterByScancallback = new MyAdapter(myDatasetByScancallback);

                mRecyclerViewByScancallback.setAdapter(mAdapterByScancallback);
            }
        }
    };

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;

        public MyAdapter(List<String> data) {
            mData = data;

        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_device_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mCheckedTextView.setText(mData.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    CheckedTextView chkItem = v.findViewById(R.id.checkb);
                    chkItem.setChecked(!chkItem.isChecked());
                    ischeckedindex[position] = chkItem.isChecked();
                }
            });


        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public String getItemData(int index) {
            return mData.get(index);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public CheckedTextView mCheckedTextView;

            public ViewHolder(View v) {
                super(v);

                mCheckedTextView = v.findViewById(R.id.checkb);

            }
        }


    }
}
