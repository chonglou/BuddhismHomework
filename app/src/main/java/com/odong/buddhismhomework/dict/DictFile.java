package com.odong.buddhismhomework.dict;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DictFile {


    public DictFile(FileInputStream fio, FileOutputStream fos) {
        this.fio = fio;
        this.fos = fos;
    }


    /**
     * Get Word meaning by its offset and its meaning size.
     *
     * @param offset offset that is get in .idx file.
     * @param size   size that is get in .idx file
     * @return meaning of word data
     */
    public String getWordData(long offset, long size) throws IOException {


        DataInputStream dt = new DataInputStream(new BufferedInputStream(fio));
        dt.skip(offset);
        byte[] bt = new byte[(int) size];
        dt.read(bt, 0, (int) size);
        dt.close();
        return new String(bt, "UTF8");
    }

    /**
     * Add data to .dict file.
     *
     * @param strMeaning meaning of a paticular word.
     * @return size of strMeaning.
     */
    public long addData(String strMeaning) {
        DataOutputStream dt = null;
        long fileSize = -1;
        try {
            fileSize = fos.getChannel().size();
            dt = new DataOutputStream(fos);
            dt.write(strMeaning.getBytes("UTF8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (dt != null) {
                try {
                    dt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return fileSize;
    }

    private final FileInputStream fio;
    private final FileOutputStream fos;

}
