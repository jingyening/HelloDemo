package tmp;

public class PhotoSelectLogCenter {

    private String uid;

    /**
     * 1：用户点击生成视频按钮
     * 2：用户没有点击生成视频按钮
     */
    private String action;

    /**
     *时间
     */
    private long timeStamp;

    /**
     *图片size集合 eg. ["20x9", "1280x100", "111x333"]
     */
    private String[] picSizes;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String[] getPicSizes() {
        return picSizes;
    }

    public void setPicSizes(String[] picSizes) {
        this.picSizes = picSizes;
    }
}
