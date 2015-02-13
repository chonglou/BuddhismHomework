package com.odong.buddhismhomework.dict;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class is used for reading .idx file.
 */
public class IdxFile {
    /**
     * constructor.
     *
     * @param fis       path to .idx file.
     * @param wordCount number of word.
     * @param fileSize  the file size.
     */
    public IdxFile(FileInputStream fis, FileOutputStream fos, long wordCount, long fileSize) {
        this.wordCount = wordCount;
        this.idxFileSize = fileSize;
        this.fis = fis;
        this.fos = fos;
        load();
    }

    /**
     * load properties.
     */
    public void load() {
        if (isLoaded) {
            return;
        }
        try {
            DataInputStream dt = new DataInputStream(new BufferedInputStream(fis));
            byte[] bt = new byte[(int) idxFileSize];
            dt.read(bt);
            dt.close();
            entryList = new ArrayList<WordEntry>();
            int startPos; // start position of entry
            int endPos = 0; // end position of entry
            WordEntry tempEntry = null;
            for (long i = 0; i < wordCount; i++) {
                tempEntry = new WordEntry();
                // read the word
                startPos = endPos;
                while (bt[endPos] != '\0') {
                    endPos++;
                }
                tempEntry.setWord(new String(bt, startPos, endPos - startPos, "UTF8"));
                tempEntry.setLowerWord(tempEntry.getWord().toLowerCase());
                // read the offset of the meaning (in .dict file)
                ++endPos;
                tempEntry.setOffset(readAnInt32(bt, endPos));
                // read the size of the meaning (in .dict file)
                endPos += NO_FOUR;
                tempEntry.setSize(readAnInt32(bt, endPos));
                endPos += NO_FOUR;
                entryList.add(tempEntry);
            }
            isLoaded = true;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    /**
     * reload .idx file.
     */
    public void reload() {
        isLoaded = false;
        load();
    }


    /**
     * return the index of a word in entry list.
     *
     * @param word the chosen word
     * @return index of this word
     */
    public long findIndexForWord(String word) {
        if (!isLoaded) {
            return wordCount;
        }
        long first = 0;
        long last = (int) wordCount - 1;
        long mid;
        String lwrWord = word.toLowerCase();
        // use binary search
        do {
            mid = (first + last) / 2;
            int cmp = lwrWord.compareTo(((WordEntry) entryList.get((int) mid)).getLowerWord());
            if (cmp == 0) {
                return mid; // return index if found
            }
            if (cmp > 0) {
                first = mid + 1;
            } else {
                last = mid - 1;
            }
        } while (first <= last);
        // if not found
        /*
         * if (first < wordCount) { while (first < wordCount) { if (((WordEntry) entryList.get( (int)
         * first)).getLowerWord().compareTo(lwrWord) > 0) { break; } else { first++; } } }
         */
        first = -1;
        return first;
    }

    /**
     * Write to an .idx file.
     */
    public void write() throws IOException {

        DataOutputStream dt = new DataOutputStream(fos);
        // dt.write(firstBytes,0,Constants.byteFirst_POS_INDEX_FILE);
        WordEntry tempEntry = null;
        for (int i = 0; i < (int) wordCount; i++) {
            tempEntry = entryList.get(i);
            dt.write(tempEntry.getWord().getBytes("UTF8"));
            dt.write('\0');
            dt.write(convertAnInt32((int) tempEntry.getOffset()));
            dt.write(convertAnInt32((int) tempEntry.getSize()));
        }
        dt.flush();
        dt.close();

    }


    /**
     * add word, offset, size to the entryList.
     *
     * @param word   the chosen word.
     * @param offset the size of .dict file.
     * @param size   the size of word meaning.
     * @param addPos position of added word
     * @return true if success.
     */
    public boolean addEntry(String word, long offset, long size, int addPos) {
        WordEntry etr = new WordEntry();
        etr.setWord(word);
        etr.setLowerWord(word.toLowerCase());
        etr.setOffset(offset);
        etr.setSize(size);
        if (addPos == -1) {
            addPos = (int) findIndexForWord(etr.getLowerWord());
        }
        if (addPos == wordCount) {
            entryList.add(etr);
            this.wordCount++;
            idxFileSize += (NO_NINE + word.length());
            return true;
        } else if (etr.getLowerWord().compareTo(((WordEntry) this.entryList.get(addPos)).getLowerWord()) != 0) {
            entryList.add(addPos, etr);
            this.wordCount++;
            idxFileSize += (NO_NINE + word.length());
            return true;
        }
        return false;
    }

    /**
     * remove a word from entryList.
     *
     * @param word the chosen word.
     * @return true if remove success.
     */
    public boolean removeEntry(String word) {
        String strLwrWord = word.toLowerCase();
        int pos = (int) findIndexForWord(strLwrWord);
        if (pos == wordCount) {
            return false;
        } else if (strLwrWord.compareTo(((WordEntry) entryList.get(pos)).getLowerWord()) != 0) {
            return false;
        } else {
            this.wordCount--;
            idxFileSize -= (NO_NINE + word.length());
            entryList.remove(pos);
            return true;
        }
    }

    /**
     * convert 4 char array to an integer.
     *
     * @param str      array of byte that is read from .idx file.
     * @param beginPos the position of a word.
     * @return a long.
     */
    private long readAnInt32(byte[] str, int beginPos) {
        int firstByte = (FIRST_BYTE & ((int) str[beginPos]));
        int secondByte = (FIRST_BYTE & ((int) str[beginPos + 1]));
        int thirdByte = (FIRST_BYTE & ((int) str[beginPos + 2]));
        int fourthByte = (FIRST_BYTE & ((int) str[beginPos + NO_THREE]));

        return ((long) (firstByte << NO_TWENTY_FOUR | secondByte << NO_SIXTEEN | thirdByte << NO_EIGHT | fourthByte))
                & FULL_BYTE;
    }

    /**
     * convert an integer to a char array.
     *
     * @param val an integer
     * @return a char array
     */
    private byte[] convertAnInt32(int val) {
        byte[] str = new byte[NO_FOUR];
        str[0] = (byte) ((val & FOURTH_BYTE) >> NO_TWENTY_FOUR);
        str[1] = (byte) ((val & THIRD_BYTE) >> NO_SIXTEEN);
        str[2] = (byte) ((val & SECOND_BYTE) >> NO_EIGHT);
        str[NO_THREE] = (byte) ((val & FIRST_BYTE));
        return str;
    }


    private final int FIRST_BYTE = 0x000000FF;
    private final int SECOND_BYTE = 0x0000FF00;
    private final int THIRD_BYTE = 0x00FF0000;
    private final int FOURTH_BYTE = 0xFF000000;
    private final long FULL_BYTE = 0xFFFFFFFFL;
    private final int NO_THREE = 3;
    private final int NO_EIGHT = 8;
    private final int NO_NINE = 9;
    private final int NO_SIXTEEN = 16;
    private final int NO_TWENTY_FOUR = 24;
    private final int NO_FOUR = 4;
    private boolean isLoaded = false;
    private long wordCount;
    private long idxFileSize;
    private final FileInputStream fis;
    private final FileOutputStream fos;
    private List<WordEntry> entryList;


    public boolean isLoaded() {
        return isLoaded;
    }

    public long getWordCount() {
        return wordCount;
    }

    public long getIdxFileSize() {
        return idxFileSize;
    }

    public List<WordEntry> getEntryList() {
        return entryList;
    }
}
