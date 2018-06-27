package studio.attect.virtuallayouttest;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.ColumnLayoutHelper;
import com.alibaba.android.vlayout.layout.FixLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelperEx;
import com.alibaba.android.vlayout.layout.ScrollFixLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //第一步，正常从布局中找到RecyclerView
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclverView);

        //第二步：初始化VirtualLayoutManager和DelegateAdapter
        //用于代替标准布局管理器和数据适配器
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager, false);

        //为各种类型的布局创建一个List进行储存
        List<DelegateAdapter.Adapter> adapterList = new LinkedList<>();

        //添加各种需要的布局
        adapterList.add(new LinearHelperAdapter());
        adapterList.add(new GridHelperAdapter());
        adapterList.add(new StickLayoutAdapter());
        adapterList.add(new OnePlusNAdapter());
        adapterList.add(new ColumnLayoutAdapter());
        adapterList.add(new ScrollFixLayoutAdapter());
        adapterList.add(new SingleLayoutAdapter());
        adapterList.add(new LinearHelperAdapter());
        adapterList.add(new FixLayoutAdapter());
        adapterList.add(new StaggeredLayoutAdapter());

        //将所有添加的布局交给适配器委托
        delegateAdapter.setAdapters(adapterList);

        //给RecyclerView设置布局管理器和内容适配器
        recyclerView.setLayoutManager(virtualLayoutManager);
        recyclerView.setAdapter(delegateAdapter);
    }


    /**
     * 线性布局
     *
     * 写法和原来的RecyclerView.Adapter很像
     */
    class LinearHelperAdapter extends DelegateAdapter.Adapter<LinearHelperAdapter.LinearViewHolder>{

        @NonNull
        @Override
        public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LinearViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
            holder.textView.setText("Linear视图" + String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        /**
         * 此方法用于真正告诉布局管理器这是个什么布局
         * 此处要返回阿里写好的
         * 比如本处的LinearLayoutHelper
         * 同时也可以在这里进行配置
         * @return LayoutHelper
         */
        @Override
        public LayoutHelper onCreateLayoutHelper() {
            LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
            linearLayoutHelper.setDividerHeight(10); //每行的间隔，单位都是px
            linearLayoutHelper.setMarginBottom(30); //底部外边距，但所有Helper中这个都是有些BUG，padding也是，比起用这个，更推荐用一个单独的SingleLayoutHelper添加一个空白的来撑开
            return  linearLayoutHelper;

        }

        //正常的ViewHolder
        class LinearViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            LinearViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.DKGRAY);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }

    /**
     * 多行定义比例布局
     */
    class GridHelperAdapter extends DelegateAdapter.Adapter<GridHelperAdapter.GridViewHolder>{

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);//一行有多少项
            gridLayoutHelper.setWeights(new float[]{20.0f,60.0f,20.0f});//每行中每项的
            gridLayoutHelper.setGap(10);
            gridLayoutHelper.setPaddingBottom(30);
            return gridLayoutHelper;
        }

        @NonNull
        @Override
        public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
            holder.textView.setText("Grid视图" + String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 6; //如果数量与Weights的数量不成倍数关系，最后一行可能权重会有点错误
        }

        class GridViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            GridViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.RED);
                textView = itemView.findViewById(R.id.textView);
                textView.setTextColor(Color.WHITE);
            }
        }
    }

    /**
     * 一脱N布局（一加手机躺枪）
     * 有两种的，具体看阿里的说明，小布局有不同，最大数量也不同
     * 淘宝首页很多这种布局的效果
     */
    class OnePlusNAdapter extends DelegateAdapter.Adapter<OnePlusNAdapter.OnePlusViewHolder>{

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            //此处还存在OnePlusNLayoutHelperEx，可以使用Ctrl+点击查看阿里的注释看到差异
            OnePlusNLayoutHelper onePlusNLayoutHelper = new OnePlusNLayoutHelper(5);
            onePlusNLayoutHelper.setMarginBottom(30);
            onePlusNLayoutHelper.setAspectRatio(2); //整体布局的宽:高 =》 2:1
            return onePlusNLayoutHelper;
        }

        @NonNull
        @Override
        public OnePlusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OnePlusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull OnePlusViewHolder holder, int position) {
            holder.textView.setText("1拖N:" + String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 5;//注意这个是有上限的
        }

        class OnePlusViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            OnePlusViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.BLUE);
                textView = itemView.findViewById(R.id.textView);
                textView.setTextColor(Color.WHITE);
            }
        }
    }

    /**
     * 吸附布局， 可以配置吸顶或者吸底
     *
     * 这个用起来感觉位置有点bug，特别是前后布局有Padding和Margin时
     */
    class StickLayoutAdapter extends DelegateAdapter.Adapter<StickLayoutAdapter.StickViewHolder>{

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            StickyLayoutHelper stickyLayoutHelper = new StickyLayoutHelper();
            stickyLayoutHelper.setStickyStart(true);//true为顶部，false为底部
            return stickyLayoutHelper;
        }

        @NonNull
        @Override
        public StickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StickViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull StickViewHolder holder, int position) {
            holder.textView.setText("Stick:" + String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class StickViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            StickViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.YELLOW);
                itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
                textView = itemView.findViewById(R.id.textView);
                textView.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * 屏幕定位布局
     *
     * 这个貌似放在List里的顺序无所谓
     * 类似CSS中的position:fixed效果
     */
    class FixLayoutAdapter extends DelegateAdapter.Adapter<FixLayoutAdapter.FixViewHolder>{
        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return new FixLayoutHelper(FixLayoutHelper.BOTTOM_LEFT,20,20);//设置相对屏幕方位以及边缘距离
        }

        @NonNull
        @Override
        public FixViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FixViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FixViewHolder holder, int position) {
            holder.textView.setText("Fix:" + position);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class FixViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            FixViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.GREEN);
                textView = itemView.findViewById(R.id.textView);
                textView.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * 滑动到指定位置后再触发屏幕定位效果的布局
     * 比如滑动到文字部分后显示返回屏幕顶部效果可以用这个
     */
    class ScrollFixLayoutAdapter extends DelegateAdapter.Adapter<ScrollFixLayoutAdapter.ScrollFixViewHolder>{

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            ScrollFixLayoutHelper scrollFixLayoutHelper = new ScrollFixLayoutHelper(ScrollFixLayoutHelper.BOTTOM_RIGHT,20,20);//在屏幕的位置
            scrollFixLayoutHelper.setShowType(ScrollFixLayoutHelper.SHOW_ON_ENTER);//离开还是显示某部分时显示
            return scrollFixLayoutHelper;
        }

        @NonNull
        @Override
        public ScrollFixViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ScrollFixViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ScrollFixViewHolder holder, int position) {
            holder.textView.setText("ScrollFix:" + position);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ScrollFixViewHolder extends RecyclerView.ViewHolder{
            TextView textView ;
            ScrollFixViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.MAGENTA);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }

    /**
     * 差不多就是单行版的GridLayout
     */
    class ColumnLayoutAdapter extends DelegateAdapter.Adapter<ColumnLayoutAdapter.ColumnViewHolder>{

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            ColumnLayoutHelper columnLayoutHelper = new ColumnLayoutHelper();
            columnLayoutHelper.setMarginBottom(30);
            columnLayoutHelper.setWeights(new float[]{10f,20f,30f,40f});
            return columnLayoutHelper;
        }

        @NonNull
        @Override
        public ColumnLayoutAdapter.ColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ColumnViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ColumnLayoutAdapter.ColumnViewHolder holder, int position) {
            holder.textView.setText("Col:" + position);
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        class ColumnViewHolder extends RecyclerView.ViewHolder{
            TextView textView;

            ColumnViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }

    /**
     * 单个View占据一行
     */
    class SingleLayoutAdapter extends DelegateAdapter.Adapter<SingleLayoutAdapter.SingleViewHolder>{
        @Override
        public LayoutHelper onCreateLayoutHelper() {
            SingleLayoutHelper singleLayoutHelper = new SingleLayoutHelper();
            singleLayoutHelper.setAspectRatio(1.5f);
            singleLayoutHelper.setMarginBottom(30);
            return  singleLayoutHelper;
        }

        @NonNull
        @Override
        public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SingleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SingleViewHolder holder, int position) {
            holder.textView.setText("Single View:" + position);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class SingleViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            SingleViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.GRAY);
                textView = itemView.findViewById(R.id.textView);
                textView.setTextColor(Color.WHITE);
            }
        }
    }

    /**
     * 瀑布流布局
     * 和原来StaggeredLayoutManager的效果差不多，但只有垂直方向
     * 效果比如，淘宝下滑不尽的商品推荐
     */
    class StaggeredLayoutAdapter extends DelegateAdapter.Adapter<StaggeredLayoutAdapter.StaggeredViewHolder>{
        private ArrayList<Integer> height = new ArrayList<>();//高度缓存，若不使用这个，每次都更新高度你可以看到RecyclerView自带的高度变动动画效果
        @Override
        public LayoutHelper onCreateLayoutHelper() {
            StaggeredGridLayoutHelper staggeredGridLayoutHelper = new StaggeredGridLayoutHelper();
            staggeredGridLayoutHelper.setGap(5);
            staggeredGridLayoutHelper.setLane(3);
            return staggeredGridLayoutHelper;
        }

        @NonNull
        @Override
        public StaggeredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StaggeredViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull StaggeredViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return 100;//其实不推荐一次加载太多，会导致淘宝同款卡顿
        }

        class StaggeredViewHolder extends RecyclerView.ViewHolder{
            TextView textView;

            StaggeredViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }

            /**
             *
             * @param layoutPosition 在此layout中的位置
             */
            public void setData(int layoutPosition){
                textView.setText(String.valueOf(layoutPosition)); //如果使用getAdapterPosition会得到RecyclerView整体的position，而内部位置则需要使用传参得到
                if(height.size() <= layoutPosition){
                    height.add((int) (Math.random() % 100 * 100)*2 + 100); //随机高度
                }
                itemView.getLayoutParams().height = height.get(layoutPosition);
            }
        }
    }
}
