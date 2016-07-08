package com.boxfox.dsm_boxfox.Sub;

import org.json.JSONArray;

import io.realm.RealmObject;

/**
 * Created by boxfox on 2016-05-29.
 */
public class File extends RealmObject{
    private String fileName,fileLink;
    private int num,fileType;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileLink() {
        return fileLink;
    }
}
