package com.odong.buddhismhomework.dict;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to read idx, ifo,dict files get dictionary information, word and meaning.
 */
public class StarDict {
    /**
     * Create a new object of StarDict with files: ifo, idx, dict.
     *
     * @param ifoFile  info file
     * @param idxFile  index file
     * @param dictFile data dictionary file
     */
    public StarDict(IfoFile ifoFile, IdxFile idxFile, DictFile dictFile) {
        this.ifoFile = ifoFile;
        this.idxFile = idxFile;
        this.dictFile = dictFile;

        if (ifoFile.isLoaded() && idxFile.isLoaded()) {
            available = true;
        }
    }


    public StarDict(String url) throws IOException {

        ifoFile = new IfoFile(
                new FileInputStream(url + EXT_INFO),
                new FileOutputStream(url + EXT_INFO)
        );
        idxFile = new IdxFile(
                new FileInputStream(url + EXT_INDEX),
                new FileOutputStream(url + EXT_INDEX),
                ifoFile.getWordCount(),
                ifoFile.getIdxFileSize()
        );
        dictFile = new DictFile(
                new FileInputStream(url + EXT_DICT),
                new FileOutputStream(url + EXT_DICT, true)
        );

    }


    /**
     * Create an empty Stardict-based dictionary.
     * The dictionary includes 3 files dictName.dict, dictName.idx, dictName.ifo.
     * These 3 files will be generated at path: repoPath + "/" + dictName.
     *
     * @param repoPath folder contains the dictionary files.
     * @param dictName name of the dictionary. It's used for naming of files
     * @return true if success; false if the dictionary is existed or error.
     */
    public static boolean createDict(String repoPath, String dictName, DictInfo dictInfo) {
        String dictPath = repoPath + File.separator + dictName;
        String dictFilePath = dictPath + File.separator + dictName + EXT_DICT;
        String idxFilePath = dictPath + File.separator + dictName + EXT_INDEX;
        String ifoFilePath = dictPath + File.separator + dictName + EXT_INFO;

        if (((new File(dictFilePath)).exists()) || ((new File(idxFilePath)).exists())
                || ((new File(ifoFilePath)).exists())) {
            return false;
        }

        // Create the path folder for dictionary files.
        File dictPathFile = new File(dictPath);
        if (!dictPathFile.exists() || !dictPathFile.isDirectory()) {
            if (!dictPathFile.mkdirs()) {
                return false;
            }
        }

        try {
            DataOutputStream dt = new DataOutputStream(new FileOutputStream(dictFilePath));
            // java.io.FileWriter fw = new java.io.FileWriter(dictFile);
            String strFirstWord = "@00-database-info\nThis is the " + dictName
                    + " dictionary database of the " + dictName;
            dt.write(strFirstWord.getBytes());
            dt.flush();
            dt.close();

            dt = new DataOutputStream(new FileOutputStream(idxFilePath));
            dt.write('\0');
            dt.write(convertAnInt32(0));
            dt.write(convertAnInt32(strFirstWord.length()));
            dt.flush();
            dt.close();
            FileWriter fw = new FileWriter(ifoFilePath);
            fw.write(dictInfo.toString());
            fw.write("sametypesequence=m\n");
            fw.flush();
            fw.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            (new File(dictFilePath)).delete();
            (new File(idxFilePath)).delete();
            (new File(ifoFilePath)).delete();
        }

        return false;
    }

    /**
     * get book name of dictionary.
     *
     * @return Book name
     */
    public String getDictName() {
        return ifoFile.getBookName().replace("\r", "").trim();
    }

    /**
     * get book version.
     *
     * @return version of a dictionary
     */
    public String getDictVersion() {
        return ifoFile.getVersion();
    }

    /**
     * get amount of words in a StarDict dictionary (within 3 files).
     *
     * @return a long totalWord.
     * @author LongNX
     */
    public int getTotalWords() {
        return getWordEntry().size();
    }

    /**
     * get word content from an idx. let say the stardict-dictd-easton-2.4.2, we give this method the idx 1000 and it
     * return us the "diana".
     *
     * @param idx index
     * @return word
     * @author LongNX
     */
    public String getWordByIndex(int idx) {
        String word = getWordEntry().get(idx).getLowerWord();
        return word;
    }

    /**
     * lookup a word by its index.
     *
     * @param idx index of a word
     * @return word data
     */
    public String lookupWord(int idx) throws IOException {
        if (idx < 0 || idx >= idxFile.getWordCount()) {
            return "not found";
        }
        WordEntry tempEntry = idxFile.getEntryList().get(idx);

        return dictFile.getWordData(tempEntry.getOffset(), tempEntry.getSize());
    }

    /**
     * lookup a word.
     *
     * @param word that is looked up in database.
     * @return word data
     */
    public String lookupWord(String word) throws IOException {
        if (!available) {
            return "the dictionary is not available";
        }
        int idx = (int) idxFile.findIndexForWord(word);

        return lookupWord(idx);
    }

    /**
     * get a list of word entry.
     *
     * @return list of word entry
     */
    public List<WordEntry> getWordEntry() {
        return idxFile.getEntryList();
    }

    /**
     * load index file and info file.
     */
    public void reLoad() throws IOException {
        available = false;
        ifoFile.reload();
        idxFile.reload();

        if (ifoFile.isLoaded() && idxFile.isLoaded()) {
            available = true;
        }
    }

    /**
     * get the NEAREST of the chosen word.
     *
     * @param word that is looked up in database
     * @return a list of NEAREST word.
     */
    public List<Word> getNearestWords(String word) {
        if (available) {
            int idx = (int) idxFile.findIndexForWord(word);
            int nMax = NEAREST + idx;
            if (nMax > idxFile.getWordCount()) {
                nMax = (int) idxFile.getWordCount();
            }
            List<Word> wordList = new ArrayList<Word>();
            for (int i = idx; i < nMax; i++) {
                if (i != 0) {
                    Word tempWord = new Word();
                    tempWord.setWord(idxFile.getEntryList().get(i).getWord());
                    tempWord.setIndex(i);
                    wordList.add(tempWord);
                }
            }
            return wordList;
        }
        return null;
    }

    /**
     * check if a word is in dictionary.
     *
     * @param word that is looked up in database
     * @return true if exists, false otherwise
     */
    public boolean existWord(String word) {
        int wordIndex = (int) idxFile.findIndexForWord(word);

        if (wordIndex >= idxFile.getWordCount()) {
            return false;
        }

        String lwrWord = word.toLowerCase();
        if (lwrWord.equals(idxFile.getEntryList().get(wordIndex).getLowerWord())) {
            return true;
        }

        return false;
    }

    /**
     * Add list of word to idx, dict file, modify size .ifo file.
     *
     * @param pWord word that is added
     * @param pMean word mean
     * @return true if success
     */
    public boolean addListOfWords(String[] pWord, String[] pMean) {
        if (pWord.length != pMean.length || pWord.length == 0) {
            return false;
        }
        try {
            for (int i = 0; i < pWord.length; i++) {
                String strLwrWord = pWord[i].toLowerCase();
                int pos = (int) idxFile.findIndexForWord(strLwrWord);
                boolean bExist = false;
                if (pos < (int) idxFile.getWordCount()) {
                    if (strLwrWord.compareTo(((WordEntry) idxFile.getEntryList().get(pos)).getLowerWord()) == 0) {
                        bExist = true;
                    }
                }
                long nextOffset = dictFile.addData(pMean[i]);
                if (nextOffset >= 0) {
                    if (!bExist) {
                        idxFile.addEntry(pWord[i], nextOffset, pMean[i].length(), pos);
                    } else {
                        WordEntry tempEntry = idxFile.getEntryList().get(pos);
                        tempEntry.setOffset(nextOffset);
                        tempEntry.setSize(pMean[i].length());
                    }
                }
            }
            idxFile.write();
            ifoFile.setIdxFileSize(idxFile.getIdxFileSize());
            ifoFile.setWordCount(idxFile.getWordCount());
            ifoFile.write();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Add a word to .dict file and .idx file, modify the size of ifo file.
     *
     * @param word word that is needed to add.
     * @param mean word meaning.
     * @return true if add complete.
     */
    public boolean addOneWord(String word, String mean) {
        String[] pWord = new String[1];
        String[] pMean = new String[1];
        pWord[0] = word;
        pMean[0] = mean;

        return addListOfWords(pWord, pMean);
    }

    /**
     * Get file name without extension. For example: input: a:\b.a - output: a:\b
     *
     * @param url path of a file
     * @return original file name
     */
    public static String getFileNameWithoutExtension(String url) {
        int dot = url.lastIndexOf(".");

        return (dot > -1) ? url.substring(0, dot) : null;
    }

    /**
     * get extension of file.
     *
     * @param url path to file
     * @return extension of file
     */
    public static String getExtension(String url) {
        int dot = url.lastIndexOf(".");
        return url.substring(dot + 1);
    }

    /**
     * Convert int into 32-bit integer.
     *
     * @param val integer
     * @return 4 bytes of 32-bit integer
     */
    public static byte[] convertAnInt32(int val) {
        byte[] str = new byte[4];

        str[0] = (byte) ((val & 0xFF000000) >> 24);
        str[1] = (byte) ((val & 0x00FF0000) >> 16);
        str[2] = (byte) ((val & 0x0000FF00) >> 8);
        str[3] = (byte) ((val & 0x000000FF));
        return str;
    }


    private static final String EXT_DICT = ".dict";

    private static final String EXT_INDEX = ".idx";

    private static final String EXT_INFO = ".ifo";


    private final int NEAREST = 10;

    private boolean available = false;


    private IfoFile ifoFile = null;


    private IdxFile idxFile = null;


    private DictFile dictFile = null;

}
