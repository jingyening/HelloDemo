package com.bruce.jing.hello.demo.widget.emoji.widget;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.adapter.OnRecyclerItemClickListener;
import com.bruce.jing.hello.demo.widget.emoji.adapter.EmojiIconAdapter;
import com.bruce.jing.hello.demo.widget.emoji.adapter.EmojiIconPagerAdapter;
import com.bruce.jing.hello.demo.widget.emoji.adapter.EmoticonsGridAdapter;
import com.bruce.jing.hello.demo.widget.emoji.entity.BaseEmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmojiIconGroupEntity;

import java.util.ArrayList;
import java.util.List;

public class EmojiIconPagerView extends ViewPager {


    private static final String TAG = "EmojiIconPagerView";

    /**
     * 颜文字表情行数
     */
    public static final int DEFAULT_EMOTICONS_ROWS = 3;
    /**
     * 颜文字表情列数
     */
    public static final int DEFAULT_EMOTICONS_COLUMNS = 4;

    /**
     * emoji表情行数
     */
    public static final int DEFAULT_EMOJI_ICON_ROWS = 3;
    /**
     * emoji表情列数
     */
    public static final int DEFAULT_EMOJI_ICON_COLUMNS = 8;

    /**
     * 大表情行数
     */
    public static final int DEFAULT_BIG_EMOJI_ICON_ROWS = 2;
    /**
     * 大表情列数
     */
    public static final int DEFAULT_BIG_EMOJI_ICON_COLUMNS = 4;

    private Context context;
    private List<EmojiIconGroupEntity> mGroupEntities;
    private List<BaseEmojiIconEntity> totalEmojiIconList = new ArrayList<>();

    private PagerAdapter pagerAdapter;


    private int mEmoticonColumns = DEFAULT_EMOTICONS_COLUMNS;
    private int mEmoticonRows = DEFAULT_EMOTICONS_ROWS;


    private int mEmojiIconColumns = DEFAULT_EMOJI_ICON_COLUMNS;
    private int mEmojiIconRows = DEFAULT_EMOJI_ICON_ROWS;

    private int mBigEmojiIconColumns = DEFAULT_BIG_EMOJI_ICON_COLUMNS;
    private int mBigEmojiIconRows = DEFAULT_BIG_EMOJI_ICON_ROWS;

    private int firstGroupPageSize;

    private int maxPageCount;
    private int previousPagerPosition;
    private EmojiIconPagerViewListener pagerViewListener;
    private List<View> mPageViews;

    public EmojiIconPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public EmojiIconPagerView(Context context) {
        this(context, null);
    }


    public void init(List<EmojiIconGroupEntity> emojiIconGroupList, int emijiconColumns, int bigEmojiIconColumns) {
        if (emojiIconGroupList == null) {
            throw new RuntimeException("emojiIconGroupList is null");
        }

        this.mGroupEntities = emojiIconGroupList;
        this.mEmojiIconColumns = emijiconColumns;
        this.mBigEmojiIconColumns = bigEmojiIconColumns;

        mPageViews = new ArrayList<>();
        for (int i = 0; i < mGroupEntities.size(); i++) {
            EmojiIconGroupEntity group = mGroupEntities.get(i);
            List<BaseEmojiIconEntity> groupEmojiIcons = group.getEmojiIconList();
            totalEmojiIconList.addAll(groupEmojiIcons);
            List<View> gridViews = getGroupGridViews(group);
            if (i == 0) {
                firstGroupPageSize = gridViews.size();
            }
            maxPageCount = Math.max(gridViews.size(), maxPageCount);
            mPageViews.addAll(gridViews);
        }

        pagerAdapter = new EmojiIconPagerAdapter(mPageViews);
        setAdapter(pagerAdapter);
        addOnPageChangeListener(new EmojiPagerChangeListener());

        if (pagerViewListener != null) {
            pagerViewListener.onPagerViewInited(maxPageCount, firstGroupPageSize);
        }
    }

    public void setPagerViewListener(EmojiIconPagerViewListener pagerViewListener) {
        this.pagerViewListener = pagerViewListener;
    }


    /**
     * 设置当前表情组位置（根据groupPosition换算成pagePosition）
     *
     * @param position
     */
    public void setGroupPostion(int position) {
        if (getAdapter() != null && position >= 0 && position < mGroupEntities.size()) {
            int count = 0;
            for (int i = 0; i < position; i++) {
                count += getPageSize(mGroupEntities.get(i));
            }
            setCurrentItem(count);
        }
    }

    /**
     * 获取表情组的gridview list
     *
     * @param groupEntity
     * @return
     */
    public List<View> getGroupGridViews(EmojiIconGroupEntity groupEntity) {

        EmojiIconEntity.Type emojiType = groupEntity.getType();
        switch (emojiType) {
            case EMOTICONS:
                return getEmoticonsGroupGridView(groupEntity);
            case EMOJI:
                return getEmojiIconGridView(groupEntity);
            case STICKER:
                return getStickerGroupGridView(groupEntity);
            default:
                return new ArrayList<>();
        }

    }

    /**
     * 获取颜文字view
     *
     * @param groupEntity
     * @return
     */
    private List<View> getEmoticonsGroupGridView(EmojiIconGroupEntity groupEntity) {
        List<BaseEmojiIconEntity> emojiIconList = groupEntity.getEmojiIconList();
        int totalSize = emojiIconList.size();
        //一页的表情数量(去掉删除按钮)
        int itemSize = mEmoticonColumns * mEmoticonRows;
        int pageSize = (totalSize % itemSize) == 0 ? totalSize / itemSize : totalSize / itemSize + 1;

        List<View> views = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            View view = View.inflate(context, R.layout.layout_emoticons_grid_view, null);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_emoticons_grid);

            RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), mEmoticonColumns);
            recyclerView.setLayoutManager(lm);
            List<BaseEmojiIconEntity> list = new ArrayList<>();
            if (i != pageSize - 1) {
                list.addAll(emojiIconList.subList(i * itemSize, (i + 1) * itemSize));
            } else {
                list.addAll(emojiIconList.subList(i * itemSize, totalSize));
            }

            final EmoticonsGridAdapter gridAdapter = new EmoticonsGridAdapter();
            gridAdapter.addAll(list);
            recyclerView.setAdapter(gridAdapter);

            gridAdapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    List<BaseEmojiIconEntity> list = gridAdapter.getDataList();
                    BaseEmojiIconEntity entity = list.get(position);
                    if (pagerViewListener != null) {
                        pagerViewListener.onExpressionClicked(entity);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });

            views.add(view);
        }
        return views;
    }

    /**
     * 获取emoji表情view
     *
     * @param groupEntity
     * @return
     */
    private List<View> getEmojiIconGridView(EmojiIconGroupEntity groupEntity) {
        List<BaseEmojiIconEntity> emojiIconList = groupEntity.getEmojiIconList();

        List<View> views = new ArrayList<>();
        int totalSize = emojiIconList.size();
        //一页的表情数量(去掉删除按钮)
        int itemSize = mEmojiIconColumns * mEmojiIconRows - 1;

        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;

        for (int i = 0; i < pageSize; i++) {
            View view = View.inflate(context, R.layout.layout_emoji_grid_view, null);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_emoji_grid);
            RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), mEmojiIconColumns);
            recyclerView.setLayoutManager(lm);

            List<BaseEmojiIconEntity> list = new ArrayList<>();
            if (i != pageSize - 1) {
                list.addAll(emojiIconList.subList(i * itemSize, (i + 1) * itemSize));
            } else {
                list.addAll(emojiIconList.subList(i * itemSize, totalSize));
            }

            int currentSize = list.size();
            int fullPageSize = mEmojiIconColumns * mEmojiIconRows;
            if (currentSize < fullPageSize) {
                int size = list.size();
                for (int j = size; j < mEmoticonRows * mEmojiIconColumns; j++) {
                    if (j == fullPageSize - 1) {
                        EmojiIconEntity entity = new EmojiIconEntity();
                        entity.setType(BaseEmojiIconEntity.Type.DELETE_BUTTON);
                        list.add(entity);
                    } else {
                        EmojiIconEntity entity = new EmojiIconEntity();
                        entity.setType(BaseEmojiIconEntity.Type.PLACEHOLDER);
                        list.add(entity);
                    }
                }
            }


            final EmojiIconAdapter gridAdapter = new EmojiIconAdapter();
            recyclerView.setAdapter(gridAdapter);
            gridAdapter.addAll(list);

            gridAdapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    List<BaseEmojiIconEntity> list = gridAdapter.getDataList();
                    BaseEmojiIconEntity entity = list.get(position);
                    BaseEmojiIconEntity.Type type = entity.getType();
                    if (type == BaseEmojiIconEntity.Type.EMOJI) {
                        if (pagerViewListener != null) {
                            pagerViewListener.onExpressionClicked(entity);
                        }
                    } else if (type == BaseEmojiIconEntity.Type.DELETE_BUTTON) {
                        if (pagerViewListener != null) {
                            pagerViewListener.onDeleteImageClicked();
                        }
                    }

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });

            views.add(view);
        }
        return views;
    }


    private List<View> getStickerGroupGridView(EmojiIconGroupEntity groupEntity) {
        //do nothing
        return new ArrayList<>();
    }

    /**
     * 添加表情组
     *
     * @param groupEntity
     */
    public void addEmojiIconGroup(EmojiIconGroupEntity groupEntity, boolean notifyDataChange) {
        int pageSize = getPageSize(groupEntity);
        if (pageSize > maxPageCount) {
            maxPageCount = pageSize;
            if (pagerViewListener != null && pagerAdapter != null) {
                pagerViewListener.onGroupMaxPageSizeChanged(maxPageCount);
            }
        }
        mPageViews.addAll(getGroupGridViews(groupEntity));
        if (pagerAdapter != null && notifyDataChange) {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 移除表情组
     *
     * @param position
     */
    public void removeEmojiIconGroup(int position) {
        if (position > mGroupEntities.size() - 1) {
            return;
        }
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取某一组表情的pager数量
     *
     * @param groupEntity
     * @return
     */
    private int getPageSize(EmojiIconGroupEntity groupEntity) {
        BaseEmojiIconEntity.Type type = groupEntity.getType();
        List<BaseEmojiIconEntity> emojiIconList = groupEntity.getEmojiIconList();
        int totalSize = emojiIconList.size();
        int pageSize = 0;
        switch (type) {
            case EMOTICONS: {
                int itemSize = mEmoticonColumns * mEmoticonRows;
                pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
            }
            break;
            case EMOJI: {
                int itemSize = mEmojiIconColumns * mEmojiIconRows - 1;
                pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
            }

            break;
            case STICKER: {
                int itemSize = mBigEmojiIconColumns * mBigEmojiIconRows;
                pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
            }
            break;
        }
        return pageSize;

    }

    class EmojiPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            int endSize = 0;
            int groupPosition = 0;
            for (EmojiIconGroupEntity groupEntity : mGroupEntities) {
                int groupPageSize = getPageSize(groupEntity);
                //选中的position在当前遍历的group里
                if (endSize + groupPageSize > position) {
                    //前面的group切换过来的
                    if (previousPagerPosition - endSize < 0) {
                        if (pagerViewListener != null) {
                            pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
                            pagerViewListener.onGroupPagePositionChangedTo(0);
                        }
                        break;
                    }
                    //后面的group切换过来的
                    if (previousPagerPosition - endSize >= groupPageSize) {
                        if (pagerViewListener != null) {
                            pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
                            pagerViewListener.onGroupPagePositionChangedTo(position - endSize);
                        }
                        break;
                    }

                    //当前group的pager切换
                    if (pagerViewListener != null) {
                        pagerViewListener.onGroupInnerPagePositionChanged(previousPagerPosition - endSize, position - endSize);
                    }
                    break;

                }
                groupPosition++;
                endSize += groupPageSize;
            }

            previousPagerPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }


    public interface EmojiIconPagerViewListener {
        /**
         * pagerview初始化完毕
         *
         * @param groupMaxPageSize   最大表情组的page大小
         * @param firstGroupPageSize 第一组的page大小
         */
        void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize);

        /**
         * 表情组位置变动(从一组表情组移动另一组)
         *
         * @param groupPosition    表情组位置
         * @param pagerSizeOfGroup 表情组里的pager的size
         */
        void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup);

        /**
         * 表情组内的page位置变动
         *
         * @param oldPosition
         * @param newPosition
         */
        void onGroupInnerPagePositionChanged(int oldPosition, int newPosition);

        /**
         * 从别的表情组切过来的page位置变动
         *
         * @param position
         */
        void onGroupPagePositionChangedTo(int position);

        /**
         * 表情组最大pager数变化
         *
         * @param maxCount
         */
        void onGroupMaxPageSizeChanged(int maxCount);

        void onDeleteImageClicked();

        void onExpressionClicked(BaseEmojiIconEntity emojiEntity);

    }


}