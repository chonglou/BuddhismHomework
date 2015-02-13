package com.odong.buddhismhomework.dict;


public class DictInfo {


    @Override
    public String toString() {
        return "VERSION: " + version + "\nWord Count: " + version + "\nIdx File Size: " + idxFileSize +
                "Book Name: " + bookName + "\nAuthor: " + author + "\nWeb Site: " + website + "\nDescription: " +
                description + "\nDate: " + date;

    }

    private String intro;


    private String version;


    private String wordCount;


    private String idxFileSize;


    private String bookName;


    private String author;


    private String website;


    private String description;


    private String date;


    public String getIntro() {
        return intro;
    }


    public void setIntro(String intro) {
        this.intro = intro;
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }


    public String getWordCount() {
        return wordCount;
    }


    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }


    public String getIdxFileSize() {
        return idxFileSize;
    }


    public void setIdxFileSize(String idxFileSize) {
        this.idxFileSize = idxFileSize;
    }


    public String getBookName() {
        return bookName;
    }


    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public String getWebsite() {
        return website;
    }


    public void setWebsite(String website) {
        this.website = website;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
