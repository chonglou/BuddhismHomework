package com.odong.buddhismhomework.dict;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This class is used for reading .ifo file.
 *
 * @author kien
 */
public class IfoFile {


    public IfoFile(FileInputStream fis, FileOutputStream fos) throws IOException {
        this.fis = fis;
        this.fos = fos;

        if (isLoaded()) {
            return;
        }

        load();

    }


    public void write() throws IOException {
        OutputStreamWriter opw = new OutputStreamWriter(fos, "UTF8");
        opw.write("StarDict's dict ifo file\n");
        opw.write("version=" + getVersion() + "\n");
        opw.write("wordcount=" + getWordCount() + "\n");
        opw.write("idxfilesize=" + getIdxFileSize() + "\n");
        opw.write("bookName=" + getBookName() + "\n");
        opw.write("author=" + author + "\n");
        opw.write("website=" + website + "\n");
        opw.write("description=" + description + "\n");
        opw.write("date=" + date + "\n");
        opw.write("sametypesequence=" + sameTypeSequence + "\n");
        opw.flush();
        opw.close();
    }


    public void reload() throws IOException {
        isLoaded = false;
        load();
    }

    private void load() throws IOException {

        DataInputStream dt = new DataInputStream(new BufferedInputStream(fis));
        byte[] bt = new byte[FIVE_HUNDRED];

        dt.read(bt);
        dt.close();
        String strInput = new String(bt, "UTF8");
        version = getStringForKey("version=", strInput);

        wordCount = getLongForKey("wordcount=", strInput);
        if (wordCount <= 0) {
            return;
        }

        idxFileSize = getLongForKey("idxfilesize=", strInput);
        if (idxFileSize <= 0) {
            return;
        }

        sameTypeSequence = getStringForKey("sametypesequence=", strInput);
        bookName = getStringForKey("bookName=", strInput);
        if (bookName == null) {
            return;
        }

        author = getStringForKey("author=", strInput);
        website = getStringForKey("website=", strInput);
        description = getStringForKey("description=", strInput);
        date = getStringForKey("date=", strInput);


        isLoaded = true;
    }

    private long getLongForKey(String strKey, String str) {
        try {
            return Long.parseLong(getStringForKey(strKey, str).trim());
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    private String getStringForKey(String strKey, String str) {
        int keyLen = strKey.length();
        int startPos = str.indexOf(strKey) + keyLen;
        if (startPos < keyLen) {
            return null;
        }

        char[] strStr = str.toCharArray();
        int endPos = startPos - 1;

        while ((strStr[++endPos] != '\n') && (strStr[endPos] != '\0')) {
        }
        return new String(strStr, startPos, endPos - startPos);
    }


    private final int FIVE_HUNDRED = 500;
    private long wordCount = 0;
    private String sameTypeSequence;
    private long idxFileSize = 0;
    private boolean isLoaded = false;
    private String version;
    private String bookName;
    private String author;
    private String website;
    private String description;
    private String date;
    private final FileInputStream fis;
    private final FileOutputStream fos;


    public long getWordCount() {
        return wordCount;
    }

    public String getSameTypeSequence() {
        return sameTypeSequence;
    }

    public long getIdxFileSize() {
        return idxFileSize;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public String getVersion() {
        return version;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getWebsite() {
        return website;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public void setIdxFileSize(long idxFileSize) {
        this.idxFileSize = idxFileSize;
    }
}