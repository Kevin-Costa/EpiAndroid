package com.destan.epiandroid;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by bury_a.
 */
public class RequestClass extends AsyncTask<String, Void, Object> {

    private Object getJSON(HttpURLConnection conn)
    {
        Object json;
        StringBuilder total = new StringBuilder();
        String string;

        try
        {
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);

            /*
            ** @TODO ajouter un isCancelled
             */
            while ((string = br.readLine()) != null) {
                total.append(string);
            }
            try {
                json = new JSONObject(total.toString());
            }
            catch (JSONException e)
            {
                json = new JSONArray(total.toString());
            }
            in.close();
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return (null);
        }
        catch (JSONException exception) {
            exception.printStackTrace();
            return (null);
        }
        return (json);
    }

    @Override
    protected Object doInBackground(String... args)
    {
        String urlParameters = args[2];
        Object json = null;
        HttpURLConnection conn;
        try {
            URL url = new URL(args[0]);
            conn = (HttpURLConnection) url.openConnection();
            if (!args[1].equals("GET"))
                conn.setRequestMethod(args[1]);
            if (args[1].equals("POST")) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Length", Integer.toString(urlParameters.length()));
            try {
                if (!urlParameters.equals("")) {
                    OutputStreamWriter output = new OutputStreamWriter(conn.getOutputStream());
                    BufferedWriter wr = new BufferedWriter(output);
                    wr.write(urlParameters);
                    wr.flush();
                    wr.close();
                }
                conn.connect();
                try {
                    int code = conn.getResponseCode();
                    if (code - 200 >= 0 && code - 200 < 100)
                        json = this.getJSON(conn);
                }
                catch (IOException ignored){
                }
            }
            catch (IOException except){
                except.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
        }
        catch (SocketTimeoutException except) {
            return (null);
        }
        catch (IOException except) {
            except.printStackTrace();
            return (null);
        }
        return (json);
    }
}
