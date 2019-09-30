package com.lsgdut.treeviewtextdemo.bean;

import com.lsgdut.treeviewtextdemo.R;

import java.util.ArrayList;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Space implements LayoutItemType {
    public String id;
    public String dirName;
    public String parentId;
    public String itemId;


    public Space(String itemId,String spaceName,String spaceId,String spaceParentId) {
            this.dirName = spaceName;
            this.id = spaceId;
            this.parentId = spaceParentId;
            this.itemId = itemId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_location_space_item;
    }
}
