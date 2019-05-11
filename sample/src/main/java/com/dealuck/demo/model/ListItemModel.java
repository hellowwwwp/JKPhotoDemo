package com.dealuck.demo.model;

import java.io.Serializable;
import java.util.List;

public class ListItemModel implements Serializable{

    public static final long serialVersionUID = 1L;

    public String name;
    public List<ImageModel> images;

}
