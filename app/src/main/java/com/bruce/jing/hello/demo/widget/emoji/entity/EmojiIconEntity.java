package com.bruce.jing.hello.demo.widget.emoji.entity;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 表情实体
 */
public class EmojiIconEntity extends BaseEmojiIconEntity implements Serializable {

    public EmojiIconEntity() {
    }

    public EmojiIconEntity(int icon, String emojiText) {
        this.icon = icon;
        this.emojiText = emojiText;
        this.type = Type.EMOJI;
    }

    @SerializedName(value = "identityCode", alternate = {"id"})
    /**
     * 唯一识别号
     */
    private String identityCode;


    /**
     * 表情emoji文本内容,大表情此项内容可以为null
     */
    @SerializedName(value = "emojiText", alternate = {"name"})
    private String emojiText;


    /**
     *
     */
    private int icon;

    private String nickName;


    /**
     * 获取静态图片(小图片)资源id
     *
     * @return
     */
    public int getIcon() {
        return icon;
    }


    /**
     * 设置静态图片资源id
     *
     * @param icon
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }


    /**
     * 获取emoji文本内容
     *
     * @return
     */
    public String getEmojiText() {
        return emojiText;
    }


    /**
     * 设置emoji文本内容
     *
     * @param emojiText
     */
    public void setEmojiText(String emojiText) {
        this.emojiText = emojiText;
    }


    /**
     * 获取表情类型
     *
     * @return
     */
    public Type getType() {
        return type;
    }


    /**
     * 设置表情类型
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }


    /**
     * 获取识别码
     *
     * @return
     */
    public String getIdentityCode() {
        return identityCode;
    }

    /**
     * 设置识别码
     *
     * @param identityCode
     */
    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}