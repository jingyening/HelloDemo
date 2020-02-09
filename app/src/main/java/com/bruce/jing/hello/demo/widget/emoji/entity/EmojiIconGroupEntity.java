package com.bruce.jing.hello.demo.widget.emoji.entity;



import java.util.List;

/**
 * 一组表情所对应的实体类
 */
public class EmojiIconGroupEntity {
    /**
     * 表情数据
     */
    private List<BaseEmojiIconEntity> emojiIconList;
    /**
     * 一组表情的分类icon
     */
    private int icon;
    /**
     * 一组表情的分类组名
     */
    private String name;
    /**
     * 表情类型
     */
    private BaseEmojiIconEntity.Type type;

    public EmojiIconGroupEntity() {

    }

    public EmojiIconGroupEntity(String name, int icon, List<BaseEmojiIconEntity> emojiIconList, BaseEmojiIconEntity.Type type) {
        this.name = name;
        this.icon = icon;
        this.emojiIconList = emojiIconList;
        this.type = type;
    }

    public List<BaseEmojiIconEntity> getEmojiIconList() {
        return emojiIconList;
    }

    public void setEmojiIconList(List<BaseEmojiIconEntity> emojiIconList) {
        this.emojiIconList = emojiIconList;
    }


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseEmojiIconEntity.Type getType() {
        return type;
    }

    public void setType(BaseEmojiIconEntity.Type type) {
        this.type = type;
    }


}