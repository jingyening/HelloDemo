package com.bruce.jing.hello.demo.widget.emoji;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.widget.emoji.entity.BaseEmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmojiIconEntity;
import com.bruce.jing.hello.demo.widget.emoji.entity.EmoticonsEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEmojiDatas {

    public static final String DELETE_KEY = "em_delete_delete_expression";

    public static final Map<String,Integer> EMOJI_NAME_ICON_MAP = new HashMap<>();

    /**
     * 颜文字初始数据
     */
    private static final String[] emoticons = new String[]{
            "(*^ω^*)",
            "(｡･ω･｡)",
            "(*^▽^*)",
            "(๑^ں^๑)",
            "(๑>ᴗ<๑)",
            "(¬‿¬)",
            "(▰˘◡˘▰)",
            "(﹒︠ᴗ﹒︡)",
            "(・∀・)",
            "(●´∀｀●)",
            "(・◇・)",
            "(°∀°)b",
            "(≧∇≦)/",
            "\\( ‘з’)/",
            "(T＿T)",
            "(ＴДＴ)",
            "(.﹒︣︿﹒︣.)",
            "(ﾉ´Д`)",
            "((´д｀))",
            "(π﹏π)",
            "(-`д´-)",
            "<(｀^´)>",
            "(ｏ`皿′ｏ)",
            "(≧Д≦)ノ",
            "(ー_ー)!!",
            "(;≧皿≦）",
            "(⊙_⊙)凸",
            "щ(ºДºщ)",
            "( ꒪Д꒪)ノ",
            "( •᷄ὤ•᷅)？",
            "(⊙_◎)",
            "(・◇・)？",
            "(￣(エ)￣)",
            "(」ﾟヘﾟ)」",
            "(´･_･`)",
            "(￣工￣lll)",
            "「(°ヘ°)",
            "( ˘ ³˘)♥",
            "づ￣ ³￣)づ",
            "(｡･ω･｡)ﾉ♡",
            "(♥ω♥*)",
            "(๑・ω-)～♥",
            "|(￣3￣)|",
            "(￣ε￣＠)",
            "(╯3╰)",
            "(´(エ)｀)",
            "(*ΦωΦ*)",
            "(ↀДↀ)✧",
            "(=｀ェ´=)",
            "(=ↀωↀ=)",
            "(U・x・U)",
            "／(･ × ･)＼",
            "(*’(OO)’*)",
            "(`･⊝･´)"
    };

    /**
     * emoji表情数据
     */
    private static final String[] EMOJI_NAME_ARRAY = new String[]{
            "[赞]",
            "[哈哈]",
            "[色]",
            "[笑哭]",
            "[惊呆]",
            "[大哭]",
            "[流汗]",
            "[疑问]",
            "[晕]",
            "[奸笑]",
            "[愉快]",
            "[奋斗]",
            "[亲亲]",
            "[酣睡]",
            "[得意]",
            "[狗]",
            "[猪头]",
            "[猫咪]",
            "[爱心]",
            "[心碎]",
            "[恐惧]",
            "[偷笑]",
            "[害羞]",
            "[愤怒]",
            "[惊讶]",
            "[生病]",
            "[鼓掌]",
            "[擦汗]",
            "[可怜]",
            "[闭嘴]",
            "[调皮]",
            "[鄙视]",
            "[皱眉]",
            "[捂脸]",
            "[吃瓜]",
            "[玫瑰]",
            "[晚安]",
            "[抓狂]",
            "[发呆]",
            "[撇嘴]",
            "[尴尬]"
    };


    private static int[] EMOJI_ICON_ARRAY = new int[]{
            R.raw.ulemoji_1,
            R.raw.ulemoji_2,
            R.raw.ulemoji_3,
            R.raw.ulemoji_4,
            R.raw.ulemoji_5,
            R.raw.ulemoji_6,
            R.raw.ulemoji_7,
            R.raw.ulemoji_8,
            R.raw.ulemoji_9,
            R.raw.ulemoji_10,
            R.raw.ulemoji_11,
            R.raw.ulemoji_12,
            R.raw.ulemoji_13,
            R.raw.ulemoji_14,
            R.raw.ulemoji_15,
            R.raw.ulemoji_16,
            R.raw.ulemoji_17,
            R.raw.ulemoji_18,
            R.raw.ulemoji_19,
            R.raw.ulemoji_20,
            R.raw.ulemoji_21,
            R.raw.ulemoji_22,
            R.raw.ulemoji_23,
            R.raw.ulemoji_24,
            R.raw.ulemoji_25,
            R.raw.ulemoji_26,
            R.raw.ulemoji_27,
            R.raw.ulemoji_28,
            R.raw.ulemoji_29,
            R.raw.ulemoji_30,
            R.raw.ulemoji_31,
            R.raw.ulemoji_32,
            R.raw.ulemoji_33,
            R.raw.ulemoji_34,
            R.raw.ulemoji_35,
            R.raw.ulemoji_36,
            R.raw.ulemoji_37,
            R.raw.ulemoji_38,
            R.raw.ulemoji_39,
            R.raw.ulemoji_40,
            R.raw.ulemoji_41
    };

    //初始化表情映射表
    static{
        for (int i = 0; i < EMOJI_NAME_ARRAY.length; i++) {
            EMOJI_NAME_ICON_MAP.put(EMOJI_NAME_ARRAY[i],EMOJI_ICON_ARRAY[i]);
        }
    }

    private static final List<BaseEmojiIconEntity> EMOJI_DATA = createEmojiData();

    private static List<BaseEmojiIconEntity> createEmojiData() {
        List<BaseEmojiIconEntity> datas = new ArrayList<>();
        for (int i = 0; i < EMOJI_ICON_ARRAY.length; i++) {
           EmojiIconEntity entity = new EmojiIconEntity(EMOJI_ICON_ARRAY[i], EMOJI_NAME_ARRAY[i]);
           datas.add(entity);
        }
        return datas;
    }

    public static List<BaseEmojiIconEntity> getEmojiData() {
        return EMOJI_DATA;
    }

    private static final List<BaseEmojiIconEntity> EMOTICIONS_DATA = createEmoticonsData();

    private static List<BaseEmojiIconEntity> createEmoticonsData() {
        List<BaseEmojiIconEntity> datas = new ArrayList<>();
        for (int i = 0; i < emoticons.length; i++){
            EmoticonsEntity entity = new EmoticonsEntity(emoticons[i]);
            datas.add(entity);
        }
        return datas;
    }

    public static List<BaseEmojiIconEntity> getEmoticonData(){
        return EMOTICIONS_DATA;
    }



}