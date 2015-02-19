package com.odong.buddhismhomework.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by flamen on 15-2-18.
 */
public class FileHelper {
    public FileHelper(Context context) {
        this.context = context;
    }


    public String readFile(Integer... files) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i : files) {
            if (i > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i)));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
            }

            sb.append("\n\n");
        }
        return sb.toString();
    }

    private Context context;
}
