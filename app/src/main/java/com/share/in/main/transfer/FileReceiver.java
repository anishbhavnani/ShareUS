package com.share.in.main.transfer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.share.in.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FileReceiver extends AppCompatActivity {

    Thread Thread1 = null;
    Socket clientSocket;
    String IP = "";
    int SERVERPORT = 8080;
    boolean connected = false;
    boolean sending = false;
    Handler handler;
    IntentIntegrator qrScan;

    int PERMISSION_REQUEST_CODE = 1;

    TextView clientStatus;
    EditText serverIP;

    int filesize = 1024; // filesize temporary hardcoded

    long start = System.currentTimeMillis();
    int bytesRead;
    int current = 0;

    String segments[];
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_receiver);
        handler = new Handler();

        qrScan = new IntentIntegrator(this);
        serverIP = (EditText)findViewById(R.id.edit_serverIP);
        clientStatus = (TextView)findViewById(R.id.text_clientStatus);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Toast.makeText(this,"permission already granted",Toast.LENGTH_LONG).show();
            }
        }
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    Toast.makeText(this,"123 : "+result.getContents(),Toast.LENGTH_LONG).show();
                    segments = result.getContents().split("/");
                    IP = "192.168.43.82";
                    fileName = segments[1];
                    filesize = Byte.parseByte(segments[2]);
                    serverIP.setText(IP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void scanIP(View view){

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            qrScan.initiateScan();
            Thread clientThread = new Thread(new ClientThread());
            clientThread.start();
        }

    }

    public void connectServer(View view){
        //IP = serverIP.getText().toString();
        //clientSocket = new Socket(IP, SERVERPORT);
        System.out.println("This is IP : "+IP);
        /*if(IP.equals("0.0.0.0") || IP.equals("")){
            Toast.makeText(this, "Invalid Sender", Toast.LENGTH_SHORT).show();
            return;
        }*/
        /*Thread1 = new Thread(new Thread1());
        Thread1.start();*/
        Thread clientThread = new Thread(new ClientThread());
        clientThread.start();
    }

    private PrintWriter output;
    private BufferedReader input;
    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket("192.168.43.82", SERVERPORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clientStatus.setText("Connected\n");
                    }
                });
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //tvMessages.append("server: " + message + "\n");
                                File ShareFile = new File(Environment.getExternalStorageDirectory()+File.separator+"ShareFile");

                                System.out.println(ShareFile.toString());

                                if(!ShareFile.exists() && !ShareFile.isDirectory())
                                {
                                    // create empty directory
                                    ShareFile.mkdirs();
                                }
                                Log.e("Actual Location : ",new File(ShareFile,FileReceiver.this.fileName).toString());

                                byte [] mybytearray  = new byte [FileReceiver.this.filesize];
                                InputStream is = null;
                                try {
                                    is = clientSocket.getInputStream();

                                FileOutputStream fos = new FileOutputStream(new File(ShareFile,FileReceiver.this.fileName));
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                bytesRead = is.read(mybytearray,0,mybytearray.length);
                                current = bytesRead;

                                do {
                                    bytesRead =is.read(mybytearray, current, (mybytearray.length-current));
                                    if(bytesRead > 0) {
                                        current += bytesRead;
                                    }
                                } while(bytesRead > 0);

                                bos.write(mybytearray, 0 , current);
                                bos.flush();
                                long end = System.currentTimeMillis();
                                System.out.println(end-start);
                                bos.close();
                                clientSocket.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
     //       output.write(message);
       //     output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    clientStatus.setText("");
                }
            });
        }
    }
    class ClientThread implements Runnable{
        public void run(){
            //IP = serverIP.getText().toString();
            try {
                clientSocket = new Socket("192.168.43.82", SERVERPORT);
                connected = true;
                Log.e("FileReceiver","started");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(connected) {
                            clientStatus.setText("connected");
                        }
                    }
                });

                File ShareFile = new File(Environment.getExternalStorageDirectory()+File.separator+"ShareFile");

                System.out.println(ShareFile.toString());

                if(!ShareFile.exists() && !ShareFile.isDirectory())
                {
                    // create empty directory
                    ShareFile.mkdirs();
                }
                FileReceiver.this.fileName="text.jpg";
                Log.e("Actual Location : ",new File(ShareFile,FileReceiver.this.fileName).toString());

                byte [] mybytearray  = new byte [FileReceiver.this.filesize];
                InputStream is = clientSocket.getInputStream();
                FileOutputStream fos = new FileOutputStream(new File(ShareFile,FileReceiver.this.fileName));
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bytesRead = is.read(mybytearray,0,mybytearray.length);
                current = bytesRead;

                do {
                    bytesRead =is.read(mybytearray, current, (mybytearray.length-current));
                    if(bytesRead > 0) {
                        current += bytesRead;
                    }
                } while(bytesRead > 0);

                bos.write(mybytearray, 0 , current);
                bos.flush();
                long end = System.currentTimeMillis();
                System.out.println(end-start);
                bos.close();
                clientSocket.close();
                sending = true;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sending){
                            clientStatus.setText("received");
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}