package com.github.pires.obd.reader.io;

import com.felhr.usbserial.SerialInputStream;
import com.felhr.usbserial.SerialOutputStream;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by speedfox on 3/22/16.
 */
public class SerialObdSocket implements ObdSocket {

    UsbSerialInterface dev;
    SerialInputStream inStream;
    SerialOutputStream outStream;

    public SerialObdSocket(UsbSerialInterface dev){
        this.dev = dev;
        inStream = new SerialInputStream(dev);
        outStream = new SerialOutputStream(dev);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return outStream;
    }

    @Override
    public void close() throws IOException {
        dev.close();
    }
}
