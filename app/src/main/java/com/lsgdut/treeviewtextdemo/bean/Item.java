package com.lsgdut.treeviewtextdemo.bean;


import com.lsgdut.treeviewtextdemo.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Item implements LayoutItemType {

    public String fileName;
    public String id;
    public String parentId;
    public String spaceId;


    public Item(String fileName, String id, String parentId, String spaceId) {

        this.fileName = fileName;
        this.id = id;
        this.parentId = parentId;
        this.spaceId = spaceId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_location_item;
    }

}
