package com.dealuck.demo.model;

import java.io.Serializable;

public class ImageModel implements Serializable {

    public static final long serialVersionUID = 1L;

    public int resId;
    public String name;

    public ImageModel(int resId, String name) {
        this.resId = resId;
        this.name = name;
    }
}
