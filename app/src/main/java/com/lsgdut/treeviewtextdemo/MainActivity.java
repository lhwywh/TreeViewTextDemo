package com.lsgdut.treeviewtextdemo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lsgdut.treeviewtextdemo.bean.Item;
import com.lsgdut.treeviewtextdemo.bean.Space;
import com.lsgdut.treeviewtextdemo.viewbinder.DirectoryNodeBinder;
import com.lsgdut.treeviewtextdemo.viewbinder.FileNodeBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv;    //左边的recyclerView
    private RecyclerView rv2;   //右边的
    private TreeViewAdapter adapter;
    private final String TAG = "ItemsLocationFragment";
    private TreeViewAdapter adapter2;
    List<TreeNode> childNodesLeft = new ArrayList<>();
    List<TreeNode> childNodesRight = new ArrayList<>();
    private CardView cardViewLeft, cardViewRight;    //左右两边的cardView
    private View mPopWindow;
    private PopupWindow mPopupWindow;
    private TextView btnPopMove, btnPopDelete;  //这里的btn实际上是textView,popWindow上的按钮
    private TreeNode moveNode, moveNodeParent, moveNodeNewParent1;  //moveNode:被移动的节点,moveNodeParent:节点移动前的父级
    //moveNodeNewParent1:移动后的父级
    private int tag = 0;        //TagModel==0,没有点击移动按钮,TagModel=2,右边移动到左边,TagModel=1,左边移动到右边
    private String itemId;
    private int deleteOrMoveItemPosition;
    private String parentId = "20000";
    private TextView tvShowDetail;
    private TextView tvName;
    private TextView tvName2;
    private boolean isLocat;
    private TreeNode moveNodeNewParent2;
    private TextView tvEdit;
    public static final String EDIT_ITEM = "editItem";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPopWindow();
        initView();
        initListener();
        initData();
        initRv1();
        initData2();
        initRv2();
    }

    private void initView() {
        //RecyclerView
        Log.e(TAG, "initView");
        rv = (RecyclerView) findViewById(R.id.rv);
        rv2 = (RecyclerView) findViewById(R.id.rv2);

        cardViewLeft = findViewById(R.id.item_location_left_cv);
        cardViewRight = findViewById(R.id.item_location_right_cv);

        //CardView
        cardViewLeft = findViewById(R.id.item_location_left_cv);
        cardViewRight = findViewById(R.id.item_location_right_cv);

        btnPopMove = (TextView) mPopWindow.findViewById(R.id.item_location_popWindow_move);
        btnPopDelete = (TextView) mPopWindow.findViewById(R.id.item_location_popWindow_delete);
        tvShowDetail = (TextView) mPopWindow.findViewById(R.id.item_detail_tv);
        tvEdit = (TextView) mPopWindow.findViewById(R.id.edit_info_tv);
        CardView cvLocaiton = (CardView) mPopWindow.findViewById(R.id.location_pop_window_view_group);
    }


    private void initListener() {
        cardViewLeft.setOnClickListener(this);
        cardViewRight.setOnClickListener(this);

        rv.setOnClickListener(this);
        rv2.setOnClickListener(this);

        btnPopDelete.setOnClickListener(this);
        btnPopMove.setOnClickListener(this);
        tvShowDetail.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
    }

    public void ShowPopWindow() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            // 设置PopupWindow 显示的形式 底部或者下拉等
            // 在某个位置显示
            mPopupWindow.setAnimationStyle(R.style.pop_window);
            mPopupWindow.showAtLocation(mPopWindow, Gravity.CENTER, 0, 0);
            // 作为下拉视图显示
            // mPopupWindow.showAsDropDown(mPopView, Gravity.CENTER, 200, 300);
        }
    }

    public void initPopWindow() {
        mPopWindow = getLayoutInflater().inflate(R.layout.view_item_location_popwindow, null);
        mPopupWindow = new PopupWindow(mPopWindow, (int) (MyApplication.getScreenWidth() / 1.1), CardView.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
    }

    private void initData() {
        TreeNode<Space> root = new TreeNode<>(new Space("20000", "根目录", "20000", null));
        childNodesLeft.add(root);
        TreeNode<Space> newNode = new TreeNode<>(new Space("20001", "E盘", "20001", "20000"));
        root.addChild(newNode);
        TreeNode<Item> newNode2 = new TreeNode<>(new Item("文件1", "20002", "20001", "20002"));
        newNode.addChild(newNode2);
    }

    private void initData2() {
        TreeNode<Space> root = new TreeNode<>(new Space("20000", "根目录", "20000", null));
        childNodesRight.add(root);
        TreeNode<Space> newNode = new TreeNode<>(new Space("20001", "E盘", "20001", "20000"));
        root.addChild(newNode);
        TreeNode<Item> newNode2 = new TreeNode<>(new Item("文件1", "20002", "20001", "20002"));
        newNode.addChild(newNode2);
    }

    private void initRv1() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeViewAdapter(childNodesLeft, Arrays.asList(new FileNodeBinder(this), new DirectoryNodeBinder()));
        //         adapter.ifCollapseChildWhileCollapseParent(true);whether collapse child nodes when their parent node was close.
////

        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {

            @Override
            public boolean onLongClick(TreeNode node, RecyclerView.ViewHolder holder, int position) {
                focusOnLeft();

                //长按意味着用户要从左边移动到右边了
                tag = 1;
                //记录要移动的节点，该节点的父级
                moveNodeParent = node.getParent();
                moveNode = node;

                if (node.getContent() instanceof Item) {
                    Item item = (Item) node.getContent();

                    //变色
                    if (tvName == null) {
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName.setTextColor(Color.BLACK);
                    tvName = holder.itemView.findViewById(R.id.tv_name);
                    tvName.setTextColor(Color.BLUE);
                    //记录移动节点的Id
                    itemId = item.id;
                    //记录位置
                    deleteOrMoveItemPosition = position;
                } else {
                    Space space = (Space) node.getContent();

                    //变色
                    if (tvName == null) {
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName.setTextColor(Color.BLACK);
                    tvName = holder.itemView.findViewById(R.id.tv_name);
                    tvName.setTextColor(Color.BLUE);

                    itemId = space.itemId;   //记录移动节点的Id
                }

                ShowPopWindow();
                return true;
            }

            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder, int position) {

                moveNodeNewParent1 = node;

                focusOnLeft();      //左边加阴影
                if (node.getContent() instanceof Item) {
                    Item item2 = (Item) node.getContent();
                    Log.e("Main", "file.fileName:" + item2.fileName);
                    if (tvName == null) {
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName.setTextColor(Color.BLACK);
                    tvName = holder.itemView.findViewById(R.id.tv_name);
                    tvName.setTextColor(Color.BLUE);
                } else {
                    if (tvName == null) {
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName.setTextColor(Color.BLACK);
                    tvName = holder.itemView.findViewById(R.id.tv_name);
                    tvName.setTextColor(Color.BLUE);
                    //Update and toggle the node.
                    onToggle(!node.isExpand(), holder);
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int rotateDegree = isExpand ? 90 : -90;
                ivArrow.animate().rotationBy(rotateDegree)
                        .start();
            }
        });
        rv.setAdapter(adapter);
    }

    public void initRv2() {
        rv2.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new TreeViewAdapter(childNodesRight, Arrays.asList(new FileNodeBinder(this), new DirectoryNodeBinder()));
        adapter2.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onLongClick(TreeNode node, RecyclerView.ViewHolder holder, int position) {

                focusOnRight();
                //长按意味着右边移动到左边,或者再右边删除
                tag = 2;

                //记录要移动的节点，该节点的父级
                moveNodeParent = node.getParent();
                moveNode = node;
                if (node.getContent() instanceof Item) {
                    Item item = (Item) node.getContent();

                    //变色
                    if (tvName2 == null) {
                        tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName2.setTextColor(Color.BLACK);
                    tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    tvName2.setTextColor(Color.BLUE);

                    itemId = item.id;   //记录移动节点的Id
                    deleteOrMoveItemPosition = position;  //记录位置
                } else {
                    Space space = (Space) node.getContent();

                    //变色
                    if (tvName2 == null) {
                        tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName2.setTextColor(Color.BLACK);
                    tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    tvName2.setTextColor(Color.BLUE);

                    itemId = space.itemId;   //记录移动节点的Id
                }

                ShowPopWindow();
                return true;
            }

            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder, int position) {
                focusOnRight();

                moveNodeNewParent2 = node;

                if (node.getContent() instanceof Item) {
                    Item item2 = (Item) node.getContent();
                    Log.e("Main", "file.fileName:" + item2.fileName);
                    if (tvName2 == null) {
                        tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName2.setTextColor(Color.BLACK);
                    tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    tvName2.setTextColor(Color.BLUE);
                } else {
                    if (tvName2 == null) {
                        tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName2.setTextColor(Color.BLACK);
                    tvName2 = holder.itemView.findViewById(R.id.tv_name);
                    tvName2.setTextColor(Color.BLUE);
                    //Update and toggle the node.
                    onToggle(!node.isExpand(), holder);
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int rotateDegree = isExpand ? 90 : -90;
                ivArrow.animate().rotationBy(rotateDegree)
                        .start();
            }
        });
        rv2.setAdapter(adapter2);
    }


    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick");
        switch (view.getId()) {
            case R.id.item_location_left_cv:
                focusOnLeft();
                break;
            case R.id.item_location_right_cv:
                focusOnRight();
                break;
            case R.id.item_location_popWindow_delete:
                if (moveNode.getContent() instanceof Item) {
                    //只有Item才可以被删除
                    Item item = (Item) moveNode.getContent();
                    //做删除网络请求 如果成功 调用deleteSuccess();
                    deleteSuccess();
                    Log.e(TAG, "delete,id=" + item.id);
                } else {
                    Space space = (Space) moveNode.getContent();
                    if (moveNode.getChildList() != null) {
                        Toast.makeText(this, "有物品的空间不能删除", Toast.LENGTH_SHORT).show();
                    } else {
                        //拿着space.id做删除网络请求 如果成功 调用deleteSuccess();
                        deleteSuccess();
                    }
                }
                break;
            case R.id.item_location_popWindow_move:
                //首先判空
                if (moveNodeNewParent2 == null || moveNodeNewParent1 == null) {
                    Toast.makeText(this, "要移动到哪里呢？", Toast.LENGTH_SHORT).show();
                    mPopupWindow.dismiss();
                    break;
                }
                if (tag == 1) {
                    //判断移动到的位置是不是空间
                    if (moveNodeNewParent2.getContent() instanceof Item) {
                        Toast.makeText(this, "不能移动到物品下面哦", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        //不是 直接跳出
                        break;
                    }
                    //判断是不是父级移动到子级
                    if (isContainParent(moveNode, moveNodeNewParent2)) {
                        //是 直接跳出
                        Toast.makeText(this, "父级不能移动到子级下面", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        break;
                    } else {
                        moveItem(moveNodeNewParent2);
                        //接下来做网络请求 如果移动成功调用moveItemSuccess();
                        moveItemSuccess();
                    }
                } else {
                    if (moveNodeNewParent1.getContent() instanceof Item) {
                        Toast.makeText(this, "不能移动到物品下面哦", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        break;
                    }
                    if (isContainParent(moveNode, moveNodeNewParent1)) {
                        Toast.makeText(this, "父级不能移动到子级下面", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        break;
                    } else {
                        moveItem(moveNodeNewParent1);
                        //接下来做网络请求 如果移动成功调用moveItemSuccess();
                        moveItemSuccess();
                    }

                }
                break;
//            case R.id.item_detail_tv:
//                Intent intent = new Intent(this, ItemInfoActivity.class);
//                intent.putExtra(FinalDatas.ITEM_ID, itemId);
//                startActivity(intent);
//                mPopupWindow.dismiss();
//                break;
//            case R.id.edit_info_tv:
//                Intent intent2 = new Intent(this, AddItemActivity.class);
//                intent2.putExtra(ACTIVITY_TYPE, EDIT_ITEM);
//                intent2.putExtra(FinalDatas.ITEM_ID, itemId);
//                startActivity(intent2);
//                mPopupWindow.dismiss();
//                break;
        }
    }

    private void onMove(){

    }

    public void focusOnLeft() {
        btnPopMove.setText("移动->");
        cardViewLeft.setCardElevation(40);
        cardViewRight.setCardElevation(3);
    }

    public void focusOnRight() {
        btnPopMove.setText("<-移动");
        cardViewLeft.setCardElevation(3);
        cardViewRight.setCardElevation(40);
    }


    /**
     * 判断是不是父级移到子级
     *
     * @param t1：移动对象
     * @param t2：移动到的位置
     * @return
     */
    public Boolean isContainParent(TreeNode t1, TreeNode t2) {
        List<TreeNode> treeNodes = t1.getChildList();
        if (treeNodes != null) {
            for (int i = 0; i < treeNodes.size(); i++) {
                TreeNode treeNode = treeNodes.get(i);
                if (treeNode.getContent() instanceof Space) {
                    Space space = (Space) treeNode.getContent();
                    Space space2 = (Space) t2.getContent();
                    if (space.id.equals(space2.id)) {
                        return true;
                    } else {
                        //递归
                        if (isContainParent(treeNode, t2)) {
                            return true;
                        }
                    }
                } else {
                    continue;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private void moveItem(TreeNode moveNodeNewParent) {
        Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        //下面两个id在实际开发中用于网络请求
        String fromId = null;
        String toId = null;
        isLocat = false;
        if (moveNode.getContent() instanceof Item) {
            Item item = (Item) moveNode.getContent();
            fromId = item.id;
        } else {
            Space space = (Space) moveNode.getContent();
            fromId = space.itemId;
            isLocat = true;
        }
        if (moveNodeNewParent.getContent() instanceof Item) {
            Toast.makeText(this, "不能移动到物品下", Toast.LENGTH_SHORT).show();
        } else {
            Space space = (Space) moveNodeNewParent.getContent();
            toId = space.id;
        }
        mPopupWindow.dismiss();
    }

    private void moveItemSuccess() {
        if (tag == 1) {
            //从左边移动到右边的话
            //先删掉左边的
            moveNodeParent.getChildList().remove(moveNode);
            adapter.refresh(childNodesLeft);
            //再删右边的
            for (int i = 0; i < childNodesRight.size(); i++) {
                TreeNode tn = childNodesRight.get(i);
                if (moveNode.getContent() instanceof Space) {
                    TreeNode moveNode1 = findNode(tn, moveNode, true);
                    moveNode1.getParent().deleteChild(moveNode1);
                    break;
                } else {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().deleteChild(moveNode1);
                    break;
                }
            }
            adapter2.refresh(childNodesRight);
            //然后在moveNodeNewParent下添加新的node
            //这里一定要新建一个node 不能直接使用MoveNode到右边去 如果两边的RecyclerView共用一个内存地址的node
            //从第二次移动同一个node就会导致一些奇怪的错误
            TreeNode treeNode = null;
            try {
                treeNode = moveNode.clone();
                moveNodeNewParent2.addChild(treeNode);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            //刷新布局
            adapter2.refresh(childNodesRight);

            //还没完 还要在左边的新的parent下添加node
            //先在左边找到parent
            TreeNode parent = null;
            for (int i = 0; i < childNodesLeft.size(); i++) {
                TreeNode tn = childNodesLeft.get(i);
                //移动到的肯定是空间 所以直接填true
                parent = findNode(tn, moveNodeNewParent2, true);
                break;
            }
            //原来的moveNode要弃用 因为不弃用直接放在新的parent下比如 parent.addChild(moveNode)
            //会导致padding不对的问题 所以克隆一份
            try {
                TreeNode treeNode1 = treeNode.clone();
                parent.addChild(treeNode1);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            moveNode = null;
            adapter.refresh(childNodesLeft);
        } else {
            //如果是从右到左
            //先删掉右边的
            moveNodeParent.getChildList().remove(moveNode);
            adapter2.refresh(childNodesRight);
            //再删左边的
            for (int i = 0; i < childNodesLeft.size(); i++) {
                TreeNode tn = childNodesLeft.get(i);
                if (moveNode.getContent() instanceof Space) {
                    TreeNode moveNode1 = findNode(tn, moveNode, true);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter.refresh(childNodesLeft);
                    break;
                } else {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter.refresh(childNodesLeft);
                    break;
                }
            }
            //然后在moveNodeNewParent下添加新的node
            //这里一定要新建一个node 不能直接使用MoveNode到右边去 如果两边的RecyclerView共用一个内存地址的node
            //从第二次移动同一个node就会导致一些奇怪的错误
            TreeNode treeNode = null;
            try {
                treeNode = moveNode.clone();
                moveNodeNewParent1.addChild(treeNode);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            //还没完 还要在左边的新的parent下添加node
            //先在左边找到parent
            TreeNode parent = null;
            for (int i = 0; i < childNodesRight.size(); i++) {
                TreeNode tn = childNodesRight.get(i);
                //一定是空间
                parent = findNode(tn, moveNodeNewParent1, true);
                break;
            }
            try {
                TreeNode treeNode1 = treeNode.clone();
                parent.addChild(treeNode1);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            moveNode = null;
            adapter2.refresh(childNodesRight);
        }
    }

    private void deleteSuccess() {
        mPopupWindow.dismiss();
        //再左边删除的话
        if (tag == 1) {
            //先把右边的删掉
            for (int i = 0; i < childNodesRight.size(); i++) {
                TreeNode tn = childNodesRight.get(i);
                if (moveNode.getContent() instanceof Space) {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter2.refresh(childNodesRight);
                    break;
                } else {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter2.refresh(childNodesRight);
                    break;
                }

            }
            //再把左边的删掉
            moveNodeParent.getChildList().remove(moveNode);
            adapter.refresh(childNodesLeft);
        } else {
            //再右边删除的话
            //先把左边的删掉
            for (int i = 0; i < childNodesLeft.size(); i++) {
                TreeNode tn = childNodesLeft.get(i);
                if (moveNode.getContent() instanceof Space) {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter.refresh(childNodesLeft);
                    break;
                } else {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter.refresh(childNodesLeft);
                    break;
                }

            }
            //再把右边的删掉
            moveNodeParent.getChildList().remove(moveNode);
            adapter2.refresh(childNodesRight);
        }
    }

    /**
     * 查找算法 递归
     *
     * @param resource
     * @param traget
     * @param isLocat
     * @return
     */
    public TreeNode findNode(TreeNode resource, TreeNode traget, Boolean isLocat) {
        if (isLocat && (resource.getContent() instanceof Space)) {
            Space space2 = (Space) traget.getContent();
            Space space = (Space) resource.getContent();
            if (space.id.equals(space2.id)) {
                return resource;
            }
        }
        if (resource.getChildList() != null) {
            List<TreeNode> treeNodes = resource.getChildList();
            Space space2 = null;
            Item item2 = null;
            if (isLocat) {
                space2 = (Space) traget.getContent();
                for (int i = 0; i < treeNodes.size(); i++) {
                    TreeNode treeNode = treeNodes.get(i);
                    if (treeNode.getContent() instanceof Space) {
                        Space space = (Space) treeNode.getContent();
                        if (space.id.equals(space2.id)) {
                            return treeNode;
                        } else {
                            TreeNode result = findNode(treeNode, traget, isLocat);
                            if (result != null) {
                                return result;
                            }
                        }
                    }
                }
            } else {
                item2 = (Item) traget.getContent();
                for (int i = 0; i < treeNodes.size(); i++) {
                    TreeNode treeNode = treeNodes.get(i);
                    if (treeNode.getContent() instanceof Item) {
                        Item item = (Item) treeNode.getContent();
                        if (item.id.equals(item2.id)) {
                            return treeNode;
                        }
                    } else {
                        TreeNode result = findNode(treeNode, traget, isLocat);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }
}
