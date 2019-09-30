package com.lsgdut.treeviewtextdemo.viewbinder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.lsgdut.treeviewtextdemo.R;
import com.lsgdut.treeviewtextdemo.bean.Item;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class FileNodeBinder extends TreeViewBinder<FileNodeBinder.ViewHolder> {

    private Drawable left = null;
    private onFileNodeHLongClickListener onFileNodeHLongClickListener;
    private Context context;


    public FileNodeBinder(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {

        Item fileNode = (Item) node.getContent();
        holder.tvName.setText(fileNode.fileName);

    }

    public interface onFileNodeHLongClickListener {
        public void onItemClick(View view, int position);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_location_item;
    }

    public class ViewHolder extends TreeViewBinder.ViewHolder {
        public TextView tvName;

        public ViewHolder(View rootView) {
            super(rootView);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
            //left = context.getResources().getDrawable(R.drawable.ic_insert_drive_file_light_blue_700_24dp);
            //left.setBounds(0, 0, 70, 70);
            //tvName.setCompoundDrawables(left, null, null, null);
            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onFileNodeHLongClickListener != null) {
                        onFileNodeHLongClickListener.onItemClick(view, getLayoutPosition());

                    }
                    return false;
                }
            });
        }
    }

    public void setOnFileNodeHLongClickListener(onFileNodeHLongClickListener onFileNodeHLongClickListener) {
        this.onFileNodeHLongClickListener = onFileNodeHLongClickListener;
    }
}


