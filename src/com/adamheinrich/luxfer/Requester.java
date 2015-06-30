/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adamheinrich.luxfer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import javax.swing.SwingUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author adam
 */
public class Requester implements Runnable {
    public static String DEFAULT_URL = "http://luxvery.jan-husak.cz/homepage/getCells";
    
    private String prevJson = "";

    private static final Color[] COLORS = {
        parseColor("F80000"),
        parseColor("FF4500"),
        parseColor("00FF00"),
        parseColor("99FF00"),
        parseColor("FFFF00"),
        parseColor("FF00FF"),
        parseColor("66FFFF"),
        parseColor("3300FF"),
        parseColor("0000FF"),
        parseColor("FFFFCC"),
        parseColor("660099"),
        parseColor("FFC0CB"),
        parseColor("222222")
    };

    private static final int CANVAS_COLS_COUNT = 40;
    private static final int CANVAS_ROWS_COUNT = 30;

    private Thread thread;
    private JLuxfer luxfer;
    private boolean running = false;
    private String request;

    public Requester() {
    }

    public void setUrl(String url) {
        this.request = url;
    }

    public void init(JLuxfer luxfer) {
        this.luxfer = luxfer;
        thread = new Thread(this);
        thread.start();
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    void parseData(String json) {

        if (prevJson.equals(json)) {
            return;
        }

        prevJson = json;

        //System.out.println(json);
        /*int cellId = 0;
         for (int i = 0; i < json.length(); i++) {
         if (json.charAt(i) == '#' && i + 7 < json.length()) {
         String color = json.substring(i, i + 7);
         //System.out.println("Color: "+color);

         //System.out.print(cellId+": ");
         luxfer.setCell(cellId++, parseColor(color));
         //System.out.println(color);
         i += 7;
         }
         }*/
        // System.out.println(json);
        JSONObject obj = new JSONObject(json);
        if (obj != null) {
            JSONObject selection = obj.getJSONObject("selection");

            if (selection != null) {
                JSONArray cells = obj.getJSONArray("cells");

                if (cells != null) {
                    String background = obj.getString("background");

                    if (background != null) {

                        Color backgroundColor = parseColor(background);
                        int rowsCount = luxfer.getMatrix().getRowCount();
                        int colsCount = luxfer.getMatrix().getColumnCount();

                        int startX = selection.getInt("x");
                        int startY = selection.getInt("y");

                        if (startX+colsCount > CANVAS_COLS_COUNT) {
                            startX = CANVAS_COLS_COUNT-colsCount;
                        }
                        
                        if (startY+rowsCount > CANVAS_ROWS_COUNT) {
                            startY = CANVAS_ROWS_COUNT-rowsCount;
                        }
                        
                        int x = 0;
                        int y = 0;

                        for (int i = 0; i < cells.length(); i++) {
                            int colorId = cells.getInt(i);

                            int posX = x - startX;
                            int posY = y - startY;

                            if (posX >= 0 && posX < colsCount) {
                                if (posY >= 0 && posY < rowsCount) {
                                    Color color = (colorId == 0) ? backgroundColor : COLORS[colorId - 1];
                                    luxfer.getMatrix().getCell(posY, posX).setDesiredColor(color, luxfer.getAnimationStepsCount());
                                }
                            }

                            x++;
                            if (x >= CANVAS_COLS_COUNT) {
                                x = 0;
                                y++;

                                if (y >= CANVAS_ROWS_COUNT) {
                                    y = CANVAS_ROWS_COUNT - 1;
                                }
                            }
                        }
                    }
                }
            }
        }

        //System.out.println("----------------");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                luxfer.updateColors();
            }
        });
    }

    void makeRequest() {
        try {
            /* String urlParameters = "slice=1";
             byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
             int postDataLength = postData.length; */
            URL url = new URL(request);
            HttpURLConnection cox = (HttpURLConnection) url.openConnection();
            cox.setDoOutput(true);
            cox.setDoInput(true);
            cox.setInstanceFollowRedirects(false);
            //cox.setRequestMethod("POST");
            cox.setRequestMethod("GET");
            cox.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            cox.setRequestProperty("charset", "utf-8");
            //cox.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            cox.setRequestProperty("Content-Length", "0");
            cox.setUseCaches(false);
            cox.setReadTimeout(800);

            /*DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
             wr.write(postData);
             wr.flush();
             wr.close();*/
            int code = cox.getResponseCode();
            if (code == 200) {

                BufferedReader in = new BufferedReader(new InputStreamReader(cox.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                parseData(response.toString());
            }

        } catch (IOException e) {
        }
    }

    public void run() {
        int delay;

        while (true) {
            long start = System.currentTimeMillis();
            if (running) {
                makeRequest();
            }
            long time = System.currentTimeMillis() - start;

            delay = Math.max(100, 500 - (int) time);

            try {
                //System.out.println(delay);
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
            }
        }
    }

    private static Color parseColor(String rgb) {
        return Color.decode("#" + rgb.toLowerCase());
    }
}
