package com.bruce.jing.hello.demo.widget.opensource.swipeload;

/**
 * Created by Aspsine on 2015/8/17.
 */
public interface SwipeTrigger {
    void onPrepare();

    void onMove(int y, boolean isComplete, boolean automatic);

    void onRelease();

    void onComplete();

    void onReset();
}
