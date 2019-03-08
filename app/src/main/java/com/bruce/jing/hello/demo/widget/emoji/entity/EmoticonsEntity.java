package com.bruce.jing.hello.demo.widget.emoji.entity;



/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2019/2/21 下午3:46
 * -------------------------------------
 * 描述：颜文字实体
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class EmoticonsEntity extends BaseEmojiIconEntity {

    /**
     *
     */
    private Type type;

    private String text;

    public EmoticonsEntity(String text){
        this.text = text;
        this.type = Type.EMOTICONS;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
