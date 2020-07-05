package com.share.in.main.transfer;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.share.in.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileSender extends AppCompatActivity {

    Thread Thread1 = null;
    ServerSocket serverSocket;
    Socket sSocket;
    int SERVERPORT = 8080;
    Handler handler;

    public final static int QRcodeWidth = 500 ;
    int PERMISSION_REQUEST_CODE = 1;
    Bitmap bitmap ;

    TextView listenText;
    TextView serverStatus;
    ImageView img_QR;

    String filePath;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sender);

        Intent fileIntent = getIntent();
        filePath = fileIntent.getStringExtra("path");

        Toast.makeText(this,filePath,Toast.LENGTH_SHORT).show();

        img_QR = (ImageView)findViewById(R.id.imageView_QR);

        listenText = (TextView)findViewById(R.id.text_listen);
        listenText.setText("Not Listening");
        serverStatus = (TextView)findViewById(R.id.text_serverStatus);
        serverStatus.setText("Disconnected");

        handler = new Handler();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //Toast.makeText(this,"permission WIFI_ACCESS",Toast.LENGTH_SHORT).show();
                IP = IPgen();
                Toast.makeText(this,IP,Toast.LENGTH_LONG).show();
            }
        }
    }



    public void startServer(View view){

        Thread serverThread = new Thread(new ServerThread());
        serverThread.start();

        try {
            IP=IPgen();
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        Thread1 = new Thread(new Thread1());
        Thread1.start();
*/
        //ipGenerator();
    }

    public void ipGenerator(){
        try {
            Toast.makeText(this,IP,Toast.LENGTH_LONG).show();
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_REQUEST_CODE);
            } else {
                //Toast.makeText(this,"permission for WIFI already granted",Toast.LENGTH_SHORT).show();
                IP = IPgen();
                Toast.makeText(this,IP,Toast.LENGTH_LONG).show();
            }
            System.out.println("File path is :"+filePath);
            String [] segments = filePath.split("/");

            System.out.println("File Size in Byte /"+(new File(filePath).length()));
            Toast.makeText(this,segments[(segments.length)-1],Toast.LENGTH_LONG).show();
            bitmap = TextToImageEncode(IP+"/"+segments[(segments.length)-1]+"/"+(new File(filePath).length()));
            img_QR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    String IPgen(){
        /*WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip; */
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        try {
            return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    class ServerThread implements Runnable{

        public void run(){FileInputStream fis = null;
            BufferedInputStream bis = null;
            OutputStream os = null;
            ServerSocket servsock = null;
            Socket sock = null;
            try {
                IP = IPgen();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listenText.setText("ip "+IP+":"+SERVERPORT);
                    }
                });
                servsock = new ServerSocket(SERVERPORT);
                while (true) {
                    serverStatus.setText("Waiting...");
                    try {
                        sock = servsock.accept();

                        listenText.setText(IP+":"+SERVERPORT);
                        serverStatus.setText("Accepted connection : " + sock);
                        // send file
                        File myFile = new File (filePath);
                        byte [] mybytearray  = new byte [(int)myFile.length()];
                        fis = new FileInputStream(myFile);
                        bis = new BufferedInputStream(fis);
                        bis.read(mybytearray,0,mybytearray.length);
                        os = sock.getOutputStream();
                        serverStatus.setText("Sending " + filePath + "(" + mybytearray.length + " bytes)");
                        os.write(mybytearray,0,mybytearray.length);
                        os.flush();
                        serverStatus.setText("Done.");
                    }
                    finally {
                        if (bis != null) bis.close();
                        if (os != null) os.close();
                        if (sock!=null) sock.close();
                    }
                }
            } catch (FileNotFoundException e) {
                Log.e("FileSender error",e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("FileSender io error",e.toString());
                e.printStackTrace();
            } finally {
                if (servsock != null) {
                    try {
                        servsock.close();
                    } catch (IOException e) {
                        Log.e("FileSender io1 error",e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private PrintWriter output;
    private BufferedReader input;
    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Not connected");
                        listenText.setText(IP+":"+SERVERPORT);
                    }
                });
                try {
                    socket = serverSocket.accept();
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Connected\n");
                        }
                    });
                    new Thread(new Thread2()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class Thread2 implements Runnable {
        BufferedInputStream bis = null;
        OutputStream os = null;
        ServerSocket servsock = null;
        Socket sock = null;
        FileInputStream fis = null;
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                File myFile = new File (filePath);
                                byte [] mybytearray  = new byte [(int)myFile.length()];
                                try {
                                    fis = new FileInputStream(myFile);

                                bis = new BufferedInputStream(fis);

                                    bis.read(mybytearray,0,mybytearray.length);

                                    os = sock.getOutputStream();

                                serverStatus.setText("Sending " + filePath + "(" + mybytearray.length + " bytes)");

                                    os.write(mybytearray,0,mybytearray.length);

                                os.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //tvMessages.append("client:" + message + "\n");
                            }
                        });
                    } else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class Thread3 implements Runnable {
        private String message;
        Thread3(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

}


