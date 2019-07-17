package com.example.wnmc_smartplug;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceList extends AppCompatActivity {

    private GlobalVariable gv;
    private Dialog dialog;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private ArrayList<String> myDatasetByScancallback = new ArrayList<>();
    private MyAdapter mAdapterByScancallback;
    private List<BluetoothDevice> mBluetoothDeviceListByScancallback = new ArrayList<BluetoothDevice>();//藍芽搜索回來的list

    private ArrayList<String> myDatasetByUserselect = new ArrayList<>();
    private MyAdapter mAdapterByUserselect = new MyAdapter(myDatasetByUserselect);



    private boolean[] ischeckedindex = new boolean[20];

    private Handler handler = new Handler();
    private final int REQUEST_ENABLE_BT = 1;

    private RecyclerView mRecyclerViewByScancallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        init();


        gv = (GlobalVariable) getApplicationContext();
        gv.mBluetoothDeviceListByUserselect = new ArrayList<BluetoothDevice>();
        gv.mAdapterByUserselect=new MyAdapter(myDatasetByUserselect);
        gv.selectedflag=false;
        mRecyclerViewByScancallback = findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewByScancallback.setLayoutManager(layoutManager);
        mAdapterByScancallback = new MyAdapter(myDatasetByScancallback);
        mRecyclerViewByScancallback.setAdapter(mAdapterByScancallback);
        mAdapterByUserselect = new MyAdapter(myDatasetByUserselect);


        mRecyclerViewByScancallback.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        dialog = ProgressDialog.show(DeviceList.this,
                "搜尋中", "請等待5秒...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                }


            }
        }).start();


        bluetoothLeScanner.startScan(scanCallback);
        handler.postDelayed(runnable, 5000);


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
            Log.e("選取"+index," "+ischeckedindex[index]);
        }
        gv.connectflag = true;
        gv.selectedflag=true;
        Intent intent = new Intent(DeviceList.this, MainActivity.class);
        startActivity(intent);
    finish();
        return true;

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bluetoothLeScanner.stopScan(scanCallback);

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


            if (!mBluetoothDeviceListByScancallback.contains(results.getDevice())) {
                mBluetoothDeviceListByScancallback.add(results.getDevice());
                String UUIDx = UUID.nameUUIDFromBytes(results.getScanRecord().getBytes()).toString();

                myDatasetByScancallback.add(results.getDevice().getName() + "\n" + results.getDevice().getAddress() + "\n" + UUIDx);

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

                    Toast.makeText(DeviceList.this, "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
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
            return  mData.get(index);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public CheckedTextView mCheckedTextView;
            public ImageView connectlight;
            public ViewHolder(View v) {
                super(v);

                mCheckedTextView = v.findViewById(R.id.checkb);

            }
        }




    }
}
