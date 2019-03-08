package com.bruce.jing.hello.demo.util.log;

import android.util.Log;

public final class JLogUtil{

    public static final boolean DEBUG_ENABLE = true;

    public static void d(String tag, String message){
      Log.d(tag,message);
    }
    public static void e(String tag, String message){
      Log.e(tag,message);
    }

    public static void d(Class clazz, String message){
        Log.d(clazz.getSimpleName(),message);
    }
    public static void e(Class clazz, String message){
        Log.e(clazz.getSimpleName(),message);
    }

    public static void logStatckTrace(String tag, Throwable ex) {
        StackTraceElement[] stackElements = ex.getStackTrace();
        printStackTrace(tag, stackElements);

    }

    public static void logStatckTrace(String tag) {
        d(tag,Log.getStackTraceString(new Throwable()));
    }


    public static void logCurrentThreadStackTrace(String tag) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printStackTrace(tag, elements);
    }

    public static void printStackTrace(String tag, StackTraceElement[] stackElements) {
        if (stackElements != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stackElements.length; i++) {
                sb.append(stackElements[i].getClassName() + "-->");
//                sb.append(stackElements[i].getFileName() + "\t");
                sb.append(stackElements[i].getMethodName()+"\t");
                sb.append("lineNumber:");
                sb.append(stackElements[i].getLineNumber());
                sb.append("\n");
            }
            sb.append("-----------------------------------");
            d(tag, sb.toString());
        }
    }


}
