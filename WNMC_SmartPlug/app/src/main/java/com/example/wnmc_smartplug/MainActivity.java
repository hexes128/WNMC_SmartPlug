package com.example.wnmc_smartplug;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private boolean disable;
    private ArrayAdapter resultAdapter;
    private Context context;
    private Toolbar toolbar;
    private DeviceList.MyAdapter mAdapterBygvGet;
    private List<BluetoothDevice> mBluetoothDeviceListBygvGet;

    private ArrayList<String> myDatasetByUserselect = new ArrayList<>();
    private MyAdapter mAdapterByUserselect = new MyAdapter(myDatasetByUserselect,false);

    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattService Service;
    private BluetoothGattCharacteristic chara;

    private RecyclerView mRecyclerView;
private int selectedindex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!getPackageManager().hasSystemFeature(getPackageManager().FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getBaseContext(), "No_sup_ble", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }


        toolbar = findViewById(R.id.toolbar);

        context = this;

        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        mBluetoothDeviceListBygvGet = gv.mBluetoothDeviceListByUserselect;
        mRecyclerView = findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);


        if (gv.selectedflag) {
            mAdapterBygvGet = gv.mAdapterByUserselect;
            for (int index = 0; index < mAdapterBygvGet.getItemCount(); index++) {
                myDatasetByUserselect.add(mAdapterBygvGet.getItemData(index));
            }
            mBluetoothDeviceListBygvGet = gv.mBluetoothDeviceListByUserselect;
         MyAdapter   mAdapterByUserselect = new MyAdapter(myDatasetByUserselect,false);
            mRecyclerView.setAdapter(mAdapterByUserselect);

        }


    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.e("onConnec中中中", "發現服務");
                disable = false;
                Service = mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                chara = Service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (mBluetoothGatt != null && chara != null) {
                    mBluetoothGatt.setCharacteristicNotification(chara, true);
                }

            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {//连接状态改变
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                mAdapterByUserselect =new  MyAdapter(myDatasetByUserselect,true);
                mRecyclerView.setAdapter(mAdapterByUserselect);
                        mBluetoothGatt.discoverServices();
                    }
                });







            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapterByUserselect =new  MyAdapter(myDatasetByUserselect,false);
                        mRecyclerView.setAdapter(mAdapterByUserselect                                                                                              );
                        mBluetoothGatt.discoverServices();
                    }
                });
                Log.e("onConnec中中中", "斷線成功:");
                disable = true;
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


        Intent intent = new Intent(MainActivity.this, DeviceList.class);
        startActivity(intent);
        finish();
        return true;

    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;
        private boolean mconnectstate;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView devicename;
            public  ImageView connectstate;

            public ViewHolder(View v,boolean con) {
                super(v);

                 connectstate=v.findViewById(R.id.unconnect);
                 if(con){
                 connectstate.setImageResource(R.drawable.connect);}
                 else {
                     connectstate.setImageResource(R.drawable.unconnect);
                 }
                devicename = v.findViewById(R.id.deviceName);
            }
        }

        public MyAdapter(List<String> data,boolean connectstate) {
            mData = data;
            mconnectstate=connectstate;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicename_list, parent, false);
            ViewHolder vh = new ViewHolder(v,mconnectstate);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.devicename.setText(mData.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBluetoothGatt = mBluetoothDeviceListBygvGet.get(position).connectGatt(getApplicationContext(), false, mGattCallback);
                    selectedindex=position;

                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }



    }

}
