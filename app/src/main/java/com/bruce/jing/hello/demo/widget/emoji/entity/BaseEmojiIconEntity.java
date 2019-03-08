package com.bruce.jing.hello.demo.widget.emoji.entity;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2019/2/21 下午3:45
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class BaseEmojiIconEntity{

    /**
     * 普通or大表情
     */
    protected Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type{
        /**
         * 颜文字
         */
        EMOTICONS,
        /**
         * 普通表情，可以一次输入多个到edittext（emoji）
         */
        EMOJI,
        /**
         * 大表情，点击之后直接发送（sticker）
         */
        STICKER,
        /**
         * 空数据占位符
         */
        PLACEHOLDER,

        /**
         * 删除按钮
         */
        DELETE_BUTTON


//        public String getPath(Type type){
//            switch (type){
//                case EMOJI:
//                    break;
//                case EMOTICONS:
//                    break;
//                case STICKERS:
//            }
//        }

    }

}
