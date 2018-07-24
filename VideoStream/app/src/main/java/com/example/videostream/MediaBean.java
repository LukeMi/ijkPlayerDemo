package com.example.videostream;

import java.io.Serializable;

public class MediaBean implements Serializable {
    public static final int Media_VIDEO_TYPE = 0x000001;
    public static final int Media_AUDIO_TYPE = 0x000002;
    private String name ;
    private String path;
    private int fileType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
