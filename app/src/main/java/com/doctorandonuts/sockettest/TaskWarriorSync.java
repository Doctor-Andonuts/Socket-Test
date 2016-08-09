package com.doctorandonuts.sockettest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by jgowing on 8/9/2016.
 * ASync Task Warrior Syncing
 */
public class TaskWarriorSync extends AsyncTask<Void, Void, String> {

    CertDetails certDetails = new CertDetails();
    private InputStream in = null;
    private OutputStream out = null;

    @Override
    protected String doInBackground(Void... params) {
        try {


            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket();
            sslSocket.connect(new InetSocketAddress(certDetails.server, certDetails.port));
            Log.d("ASync", "Connection");

            

            sslSocket.startHandshake();
            Log.d("ASync", "Handshake Made");

            this.out = sslSocket.getOutputStream();
            this.in = sslSocket.getInputStream();
            Log.d("ASync", "Setup Streams");

            // SEND DATA
            DataOutputStream dos = new DataOutputStream(out);
            String data = "send: protocol: v1\n" +
                    "org: Main\n" +
                    "key: cf5a3fa3-5508-4e28-9497-5b44113d45a8\n" +
                    "user: Doctor Andonuts\n" +
                    "type: sync";
            byte[] utf8 = data.getBytes("UTF-8");
            dos.writeInt(utf8.length);
            dos.write(utf8);
            dos.flush();
            dos.close();
            Log.d("ASync", "Data Sent");


            final byte[] header = new byte[4];
            this.in.read(header);
            final Scanner scanner = new Scanner(this.in);
            final Scanner s = scanner.useDelimiter("\\A");
            final String result = s.hasNext() ? s.next() : "";
            s.close();
            scanner.close();
            Log.d("ASync", "Data Received");

            Log.d("ASync", result);


            // CLOSE IT DOWN
            out.flush();
            in.close();
            out.close();
            sslSocket.close();
            Log.d("ASync", "Closing Connection");

            return "";
        } catch (Exception e) {
            Log.d("ASync", e.toString());
            return null;
        }
    }

    protected void onPostExecute(String payloadData) {
        super.onPostExecute(payloadData);
        // TODO: check this.exception
        // TODO: do something with the feed

        Log.d("ASync", "Done with onPostExecute");
    }
}
