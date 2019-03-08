package com.bruce.jing.hello.demo.widget.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2019/2/28 下午7:26
 * -------------------------------------
 * 描述：表情转换工具
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class EmojiConvertUtil {


    /**
     * 表情文本解析的正则表达式
     */
    public static final String EMOJI_REGULAR_EXPRESSION = "\\[[^\\[\\]]+\\]";

    public static CharSequence convertTextToEmoji(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }


        SpannableString spannableString = new SpannableString(text);
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊

        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(EMOJI_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;


    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     *
     * @param spannableString
     * @param patten
     * @param start
     * @throws Exception
     */
    private static void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start)
            throws Exception {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            Integer resId = DefaultEmojiDatas.EMOJI_NAME_ICON_MAP.get(key);
            // 通过上面匹配得到的字符串来生成图片资源id
            // Field field=R.drawable.class.getDeclaredField(value);
            // int resId=Integer.parseInt(field.get(null).toString());
            if (resId != null && resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(
                        context.getResources(), resId);
//                bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                ImageSpan imageSpan = new ImageSpan(bitmap);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();
                // 将该图片替换字符串中规定的位置中
                spannableString.setSpan(imageSpan, matcher.start(), end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    // 如果整个字符串还未验证完，则继续。。
                    dealExpression(context, spannableString, patten, end);
                }
                break;
            }
        }
    }



}
