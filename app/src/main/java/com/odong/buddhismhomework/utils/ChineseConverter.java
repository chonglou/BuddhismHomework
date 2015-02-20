package com.odong.buddhismhomework.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChineseConverter {

    public ChineseConverter(String filename, String charset) throws IOException {
        this(new FileInputStream(filename), charset);
    }

    public ChineseConverter(InputStream file, String charset) throws IOException {
        List<Character> cs = loadTable(file, charset);
        ts = new HashMap<Character, Character>();
        st = new HashMap<Character, Character>();

        for (int i = 0; i < cs.size(); i = i + 2) {
            ts.put(cs.get(i), cs.get(i + 1));
            st.put(cs.get(i + 1), cs.get(i));
        }
    }

    public String t2s(final String s) {
        char[] cs = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            cs[i] = t2s(s.charAt(i));
        }
        return new String(cs);
    }


    public String s2t(final String s) {
        char[] cs = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            cs[i] = s2t(s.charAt(i));
        }
        return new String(cs);
    }


    public Character t2s(final char c) {
        if (ts.get(c) == null) {
            return c;
        }
        return ts.get(c);
    }


    public Character s2t(final char c) {
        if (st.get(c) == null) {
            return c;
        }
        return st.get(c);
    }

    private List<Character> loadTable(InputStream is, String charset) throws IOException {
        List<Character> cs = loadChar(is, charset);
        if ((cs.size() % 2) != 0) {
            throw new IOException("The conversion table may be damaged or not exists");
        }
        return cs;

    }


    private List<Character> loadChar(InputStream is, String charset) throws IOException {
        List<Character> content = new ArrayList<Character>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
        int c;
        while ((c = br.read()) != -1) {
            content.add((char) c);
        }
        br.close();
        return content;
    }

    private Map<Character, Character> ts;
    private Map<Character, Character> st;
}