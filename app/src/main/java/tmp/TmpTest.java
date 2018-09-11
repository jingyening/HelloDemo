package tmp;

import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.google.gson.Gson;

public class TmpTest {

    public static void test(){
        PhotoSelectLogCenter lc = new PhotoSelectLogCenter();
        lc.setAction("1");
        lc.setUid("12345");
        lc.setTimeStamp(System.currentTimeMillis());
        String[] s = {"20x9","1280x100","111x333"};
        lc.setPicSizes(s);
        Gson gson = new Gson();
        JLogUtil.d("brucetest", gson.toJson(lc));
    }


}
