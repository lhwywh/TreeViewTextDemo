@[TOC](n级折叠树的实现 两个树图联动 基于RecyclerView)
# 特别说明
本例子使用的树形功能是由 recyclertreeview-lib 改编而来的 他的只能一个树图 我这个改了一下能联动的 而且每个节点能存更多的信息
recyclertreeview-lib的作者是TellH GitHub：[https://github.com/TellH/RecyclerTreeView](https://github.com/TellH/RecyclerTreeView)

# 效果
有一个小问题就是判断文件夹下有没有物品有点bug 但是后面代码有说怎么改
![有一点小bug大家发现了吗](https://img-blog.csdnimg.cn/20190930170359868.gif)


## 坑
两边的所有的数据是一样的 但是却是不同的变量 占用不同的内存，也就是说，两个树占用两份空间
在移动时，不能像改接一样直接移动，必须克隆一份新的，否则再对这个移动了的节点展开/折叠时就会有问题。

## 准备
不要按照 [https://github.com/TellH/RecyclerTreeView](https://github.com/TellH/RecyclerTreeView)中的教程导入树图的包，因为我改过他的了。
这个我改过的会放在文章末尾
导入supportDesign包 CardView包和树图的包

```java
    //cardView
    implementation 'com.android.support:cardview-v7:28.0.0'
    //supportDesign
    implementation 'com.android.support:design:28.0.0'
    implementation project(path: ':recyclertreeview-lib')
```
然后写MyApplication 不要忘记在功能清单application标签里加上 

```java
android:name=".MyApplication"
```

```java
public class MyApplication extends Application {

    public static int screenHeight;
    private static int screenWidth;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        mContext = getApplicationContext();
    }

    public static int getScreenWidth() {
        return screenWidth;
    }
    public static Context getContext() {
        return mContext;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

}
```
有两个bean 分别对应文件和文件夹
文件

```java
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
```
文件夹

```java
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
```
两个bean里的布局
R.layout.view_location_item

```css
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"

    android:layout_width="match_parent"

    android:layout_height="30dp"
    android:orientation="horizontal"

    xmlns:tools="http://schemas.android.com/tools">

    <ImageView
        android:layout_marginLeft="18dp"
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:layout_gravity="center_vertical"
        android:src="@mipmap/add_items" />


    <TextView
        android:textColor="#000000"

        android:id="@+id/tv_name"
        android:drawablePadding="10dp"

        android:gravity="center_vertical"

        tools:text="@string/app_name"

        android:textSize="13sp"

        android:layout_width="match_parent"

        android:layout_height="match_parent" />

</LinearLayout>
```
R.layout.view_location_space_item

```css
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"

    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"

    android:layout_height="30dp"

    android:orientation="horizontal">


    <ImageView

        android:id="@+id/iv_arrow"

        android:layout_width="18dp"

        android:layout_height="18dp"

        android:layout_gravity="center_vertical"

        android:src="@drawable/ic_keyboard_arrow_right_black_18dp" />

    <ImageView
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:layout_gravity="center_vertical"
        android:src="@mipmap/add_space" />

    <TextView

        android:id="@+id/tv_name"

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:layout_marginLeft="5dp"


        android:drawablePadding="10dp"

        android:gravity="center_vertical"

        android:textSize="13sp"
        android:textColor="#000000"

        tools:text="@string/app_name" />

</LinearLayout>
```
还有动画文件 style等我就不发了 看github代码吧
## 实现
activity_main.xml

```css
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal">

    <android.support.v7.widget.CardView
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_marginBottom="3dp"

        android:id="@+id/item_location_left_cv"
        android:layout_weight="1"
        app:cardElevation="20dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <android.support.v7.widget.RecyclerView
                android:id="@+id/rv"
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1">

            </android.support.v7.widget.RecyclerView>
        </LinearLayout>


    </android.support.v7.widget.CardView>


    <android.support.v7.widget.CardView
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:id="@+id/item_location_right_cv"
        android:layout_marginBottom="3dp"
        app:cardElevation="3dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <android.support.v7.widget.RecyclerView
                android:id="@+id/rv2"

                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1">

            </android.support.v7.widget.RecyclerView>
        </LinearLayout>


    </android.support.v7.widget.CardView>


</LinearLayout>
```
popWindow

```css
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/location_pop_window_view_group"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:translationZ="6dp"
    app:cardCornerRadius="2dp"
    app:cardElevation="10dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginLeft="10dp"
        android:layout_marginTop="20dp"
        android:layout_marginRight="10dp"
        android:orientation="vertical">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <TextView
                android:id="@+id/item_location_popWindow_move"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="移动"
                android:textSize="18sp" />

            <TextView
                android:id="@+id/item_location_popWindow_delete"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="删除"
                android:textSize="18sp" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:layout_marginBottom="20dp">

            <TextView
                android:id="@+id/item_detail_tv"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="查看物品详情"
                android:textSize="18sp" />

            <TextView
                android:id="@+id/edit_info_tv"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="编辑物品信息"
                android:textSize="18sp" />
        </LinearLayout>
    </LinearLayout>
</android.support.v7.widget.CardView>

```

好了 接下来就是activity的代码了 为了写的快 我没有用mvp 导致我activity贼长 长达680行 下面分块讲解
唉看到后面就知道了代码不优雅
先给出activity的变量

```java
//有2结尾代表右边 没有的代表左边
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
    private int tag = 0;        //tag=2,右边移动到左边,tag=1,左边移动到右边
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
```
整个activity启动时要做这些事

```java
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
```
我们先来看看核心的initData()和initRv1() 都是左边的
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190930174043787.png)
由于需求 space要有两个id看起来有点奇怪 问题不大 读者拿回去改下bean就好了 参数对应上图
```java
    private void initData() {
        TreeNode<Space> root = new TreeNode<>(new Space("20000", "根目录", "20000", null));
        childNodesLeft.add(root);//childNodeLeft是List<TreeNode> childNodesLeft = new ArrayList<>();
        TreeNode<Space> newNode = new TreeNode<>(new Space("20001", "E盘", "20001", "20000"));
        root.addChild(newNode);
        TreeNode<Item> newNode2 = new TreeNode<>(new Item("文件1", "20002", "20001", "20002"));
        newNode.addChild(newNode2);
    }
```
可以看到 这是一个通过parentId形成的一个链式存储结构（可以这么说吧）
再来看看initRv1() 90多行

```java
private void initRv1() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeViewAdapter(childNodesLeft, Arrays.asList(new FileNodeBinder(this), new DirectoryNodeBinder()));
        //         adapter.ifCollapseChildWhileCollapseParent(true);whether collapse child nodes when their parent node was close.
////

        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {

            @Override
            public boolean onLongClick(TreeNode node, RecyclerView.ViewHolder holder, int position) {
            	//左边的cardView加阴影
                focusOnLeft();
                //tag是判断哪边移动到哪边的标记 左边长按意味着用户要从左边移动到右边了
                tag = 1;
                //记录要移动的节点，该节点的父级
                moveNodeParent = node.getParent();
                moveNode = node;
				//多态 如果是文件的话
                if (node.getContent() instanceof Item) {
                    Item item = (Item) node.getContent();
                  
                    if (tvName == null) {
                    	//找到点击的文件的TextView
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                     //变色
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
				//弹出菜单要用户选择操作
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
                    //变色
                    if (tvName == null) {
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName.setTextColor(Color.BLACK);
                    tvName = holder.itemView.findViewById(R.id.tv_name);
                    tvName.setTextColor(Color.BLUE);
                } else {
                	//变色
                    if (tvName == null) {
                        tvName = holder.itemView.findViewById(R.id.tv_name);
                    }
                    tvName.setTextColor(Color.BLACK);
                    tvName = holder.itemView.findViewById(R.id.tv_name);
                    tvName.setTextColor(Color.BLUE);
                    //小三角形动画
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
```
然后initData2()和initRv2()我就不展示了基本 一摸一样的

我们再来看看移动的代码 写在了onclick的覆写方法里

```java
case R.id.item_location_popWindow_move:
                //首先判空
                if (moveNodeNewParent2 == null || moveNodeNewParent1 == null) {
                    Toast.makeText(this, "要移动到哪里呢？", Toast.LENGTH_SHORT).show();
                    mPopupWindow.dismiss();
                    break;
                }
                //如果是tag=1,左边移动到右边
                if (tag == 1) {
                    //判断移动到的位置是不是空间
                    if (moveNodeNewParent2.getContent() instanceof Item) {
                        Toast.makeText(this, "不能移动到物品下面哦", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        //不是 直接跳出
                        break;
                    }
                    //判断是不是父级移动到子级
                    //这又是一个关键算法
                    if (isContainParent(moveNode, moveNodeNewParent2)) {
                        //是 直接跳出
                        Toast.makeText(this, "父级不能移动到子级下面", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        break;
                    } else {
                    	//这又是一个关键
                        moveItem(moveNodeNewParent2);
                        //接下来做网络请求 如果移动成功调用moveItemSuccess();
                        //这又是一个关键算法
                        moveItemSuccess();
                    }
                } else {
                	//如果是tag=2,右边移动到左边
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
```
3个关键 判断是不是父级移动到子级 isContainParent() 
需要这个方法的肯定是文件夹才需要 文件不需要 思路就是递归+比较

```java
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
```
然后是moveItem()这个做demo演示是不必要的 貌似前面长按的时候也会记录2个id from和to  做网络请求是必要的 因为它可以得到两个id 看下面 但是这个方法好像是必要的这里有坑 因为前面的记录会有问题，什么问题我忘了，所以这里要再记录一遍。

```java
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
```
接下来是移动成功后 网络请求response说成功了！的数据处理和布局刷新 到这里才是把树图的数据改了
```java
private void moveItemSuccess() {
        if (tag == 1) {
            //从左边移动到右边的话
            //先删掉左边的节点
            moveNodeParent.getChildList().remove(moveNode);
            adapter.refresh(childNodesLeft);
            //再删右边的
            for (int i = 0; i < childNodesRight.size(); i++) {
                TreeNode tn = childNodesRight.get(i);
                if (moveNode.getContent() instanceof Space) {
                	//下面的findNode又是一个关键的查找算法
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
            //从第二次移动同一个node就会导致一些奇怪的错误 比如点击移动好的node会折叠异常
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
```
查找算法 findNode() 递归 这个查找是根据id相同来判断就是我找的 而不是name

```java
 /**
     * 查找算法 递归
     *
     * @param resource 源
     * @param traget 查找目标
     * @param isLocat 这个节点是文件还是文件夹
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
```
好移动讲完了 接下来是删除 这个就简单了 删除按钮点击事件

```java
case R.id.item_location_popWindow_delete:
                if (moveNode.getContent() instanceof Item) {
                    Item item = (Item) moveNode.getContent();
                    //做删除网络请求 如果成功 调用deleteSuccess();
                    deleteSuccess();
                    Log.e(TAG, "delete,id=" + item.id);
                } else {
                    Space space = (Space) moveNode.getContent();
                    //这里应该改成 if (moveNode.getChildList().size != 0) 就可以解决演示图中没有子项还是说有物品的空间不能删除的bug
                    if (moveNode.getChildList() != null) {
                        Toast.makeText(this, "有物品的空间不能删除", Toast.LENGTH_SHORT).show();
                    } else {
                        //拿着space.id做删除网络请求 如果成功 调用deleteSuccess();
                        deleteSuccess();
                    }
                }
                break;
```
deleteSuccess()方法 更新数据 刷新页面

```java
private void deleteSuccess() {
        mPopupWindow.dismiss();
        //在左边删除的话
        if (tag == 1) {
            //先把右边的删掉 要用递归找到跟左边一模一样的节点
            for (int i = 0; i < childNodesRight.size(); i++) {
                TreeNode tn = childNodesRight.get(i);
                if (moveNode.getContent() instanceof Space) {
                	//moveNode1 是另一颗树的对应的相同的节点
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    adapter2.refresh(childNodesRight);
                    break;
                } else {
                    TreeNode moveNode1 = findNode(tn, moveNode, false);
                    moveNode1.getParent().getChildList().remove(moveNode1);
                    //刷新数据
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
                    //刷新数据
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
```
修改信息这些比较简单的我就不写了 大家自己思考一下吧