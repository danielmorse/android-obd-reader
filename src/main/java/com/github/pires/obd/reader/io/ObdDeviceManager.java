package com.github.pires.obd.reader.io;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.pires.obd.reader.activity.ConfigActivity;

public class ObdDeviceManager {
	
    private static final String TAG = ObdDeviceManager.class.getName();
    /*
     * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     * #createRfcommSocketToServiceRecord(java.util.UUID)
     *
     * "Hint: If you are connecting to a Bluetooth serial board then try using the
     * well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you
     * are connecting to an Android peer then please generate your own unique
     * UUID."
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public static ObdSocket connect(Context ctx) throws IOException
    {
        BluetoothDevice dev = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String remoteDevice = prefs.getString(ConfigActivity.BLUETOOTH_LIST_KEY, null);
        if (remoteDevice == null || "".equals(remoteDevice))
            throw new IOException("No Bluetooth device selected");

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        dev = btAdapter.getRemoteDevice(remoteDevice);
        btAdapter.cancelDiscovery();
        return ObdDeviceManager.connect(dev);
    }

    /**
     * Instantiates a BluetoothSocket for the remote device and connects it.
     * <p/>
     * See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701#18786701
     *
     * @param dev The remote device to connect to
     * @return The BluetoothSocket
     * @throws IOException
     */
    private static ObdSocket connect(BluetoothDevice dev) throws IOException {
    	BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        Log.d(TAG, "Starting Bluetooth connection..");
    	try {
    		sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
    		sock.connect();
        } catch (Exception e1) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..", e1);
            Class<?> clazz = sock.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                sockFallback.connect();
                sock = sockFallback;
            } catch (Exception e2) {
                Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection.", e2);
                throw new IOException(e2.getMessage());
            }
        }

    	return new BluetoothObdSocket(sock);
    }
}