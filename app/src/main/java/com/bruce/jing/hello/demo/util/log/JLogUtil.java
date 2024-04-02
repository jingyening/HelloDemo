package com.bruce.jing.hello.demo.util.log;

import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JLogUtil {

  private static String LOG_PREFIX = "com.bruce.jing.hello.demo";
  private static final int MAX_LOG_TAG_LENGTH = 23;
  private static boolean isDebugMode = false;
  private static final HashMap<String, LogInfo> statisticsMap = new HashMap<>();
  private static long timeStamp = 0L;

  public static void init(boolean isDebugMode) {
    JLogUtil.isDebugMode = isDebugMode;
    timeStamp = SystemClock.elapsedRealtime();

  }

  public static String getLogPrefix() {
    return LOG_PREFIX;
  }

  public static String makeLogTag(String str) {
    if (str.length() > MAX_LOG_TAG_LENGTH) {
      return str.substring(0, MAX_LOG_TAG_LENGTH - 1);
    }
    return str;
  }

  /**
   * Don't use this when obfuscating class names!
   */
  public static String makeLogTag(Class cls) {
    return makeLogTag(cls.getSimpleName());
  }

  public static void log(int priority, String tag, Object... messages) {
    //        if (BuildConfig.DEBUG) {
    log(getLogPrefix(), priority, null, tag, messages);
    //        }
  }

  public static void v(String tag, Object... messages) {
    //        if (BuildConfig.DEBUG) {
    log(getLogPrefix(), android.util.Log.VERBOSE, null, tag, messages);
    //        }
  }

  public static void d(String tag, Object... messages) {
    //        if (BuildConfig.DEBUG) {
    log(getLogPrefix(), android.util.Log.INFO, null, tag, messages);
    //        }
  }
  public static void d(Class clazz, String message){
    Log.d(clazz.getSimpleName(),message);
  }
  public static void e(Class clazz, String message){
    Log.e(clazz.getSimpleName(),message);
  }
  /**
   * the log will not shown in release version
   */
  public static void debug(String tag, Object... messages) {
    log(getLogPrefix(), android.util.Log.DEBUG, null, tag, messages);
  }

  public static void i(String tag, Object... messages) {
    log(getLogPrefix(), android.util.Log.INFO, null, tag, messages);
  }

  public static void w(String tag, Object... messages) {
    log(getLogPrefix(), android.util.Log.WARN, null, tag, messages);
  }

  public static void w(String tag, Throwable t, Object... messages) {
    log(getLogPrefix(), android.util.Log.WARN, t, tag, messages);
  }

  public static void e(String tag, Object... messages) {
    log(getLogPrefix(), android.util.Log.ERROR, null, tag, messages);
  }

  public static void e(String tag, Throwable t, Object... messages) {
    log(getLogPrefix(), android.util.Log.ERROR, t, tag, messages);
  }

  private static void log(String tag, int level, Throwable t, String subTag, Object... messages) {
    String tagTrimmed = makeLogTag(tag);
    if (android.util.Log.isLoggable(tagTrimmed, level)) {
      String message;
      if (t == null && messages != null && messages.length == 1) {
        // handle this common case without the extra cost of creating a stringbuffer:
        message = messages[0].toString();
      } else {
        StringBuilder sb = new StringBuilder();
        if (messages != null) {
          for (Object m : messages) {
            sb.append(m);
          }
        }
        if (t != null) {
          sb.append("\n").append(android.util.Log.getStackTraceString(t));
        }
        message = sb.toString();
      }
      android.util.Log.println(level, tagTrimmed, "[" + subTag + "]:" + message);
      if (isDebugMode) {
        statisticsLog(subTag, message);
      }
    }
  }

  private static void statisticsLog(String tag, String msg) {
    if (tag != null) {
      LogInfo logInfo = statisticsMap.get(tag);
      if (logInfo == null) {
        logInfo = new LogInfo();
        statisticsMap.put(tag, logInfo);
      }
      logInfo.logCount++;
      if (msg != null) {
        logInfo.logSize += msg.length();
      }
    }
  }

  public static void dumpLogStatistic() {
    dumpLogStatistic(statisticsMap);
  }

  public static void dumpLogStatisticSortByLines() {
    Map<String, LogInfo> logInfoMap = sortMapByValue(true);
    dumpLogStatistic(logInfoMap);
  }

  public static void dumpLogStatisticSortBySize() {
    Map<String, LogInfo> logInfoMap = sortMapByValue(false);
    dumpLogStatistic(logInfoMap);
  }

  private static void dumpLogStatistic(Map<String, LogInfo> dumpMap) {
    int myPid = Process.myPid();
    if (!isDebugMode) {
      JLogUtil.d("WT_MULTIMEDIA_LOG",
          "========== dumpLogStatistic only support in debug mode==========");
    }
    long duration = SystemClock.elapsedRealtime() - timeStamp;
    JLogUtil.d("WT_MULTIMEDIA_LOG",
        "========== start dump log pid=" + myPid + " in " + duration + " ms ==========");
    long totalLines = 0L;
    long totalSize = 0L;
    Set<Map.Entry<String, LogInfo>> entries = dumpMap.entrySet();
    Iterator<Map.Entry<String, LogInfo>> iterator = entries.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, LogInfo> next = iterator.next();
      String key = next.getKey();
      LogInfo value = next.getValue();
      JLogUtil.d("WT_MULTIMEDIA_LOG", "print tag = "
          + key
          + " ,"
          + value.logCount
          + " lines and length size is "
          + value.logSize);
      totalLines += value.logCount;
      totalSize += value.logSize;
    }
    JLogUtil.d("WT_MULTIMEDIA_LOG", "print total lines = " + totalLines + " , totalSize = " + totalSize);
    JLogUtil.d("WT_MULTIMEDIA_LOG", "========== end dump pid:" + myPid + "==========");
  }

  private static Map<String, LogInfo> sortMapByValue(final boolean byLines) {
    Map<String, LogInfo> sortedMap = new LinkedHashMap();
    if (!statisticsMap.isEmpty()) {
      List<Map.Entry<String, LogInfo>> entryList = new ArrayList(statisticsMap.entrySet());
      Collections.sort(entryList,
          new Comparator<Map.Entry<String, LogInfo>>() {
            public int compare(Map.Entry<String, LogInfo> entry1,
                Map.Entry<String, LogInfo> entry2) {
              long value1 = 0, value2 = 0;
              if (byLines) {
                value1 = entry1.getValue().logCount;
                value2 = entry2.getValue().logCount;
              } else {
                value1 = entry1.getValue().logSize;
                value2 = entry2.getValue().logSize;
              }
              return (int) (value2 - value1);
            }
          });
      Iterator<Map.Entry<String, LogInfo>> iterator = entryList.iterator();
      Map.Entry<String, LogInfo> tmpEntry;
      while (iterator.hasNext()) {
        tmpEntry = iterator.next();
        sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
      }
    }
    return sortedMap;
  }

  public static void clearDump() {
    try {
      statisticsMap.clear();
      timeStamp = SystemClock.elapsedRealtime();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void dumpList(String tag, String key, List<?> list) {
    d(tag, key + " dumpList begin...");
    try {
      for (Object object : list) {
        d(tag, key + ": " + object.toString());
      }
    } catch (Exception e) {
      e(tag, "dumpList exception:" + e.getLocalizedMessage());
    }
    d(tag, key + " dumpList end...");
  }

  public static void dumpMap(String tag, String key, Map<?, ?> map) {
    d(tag, key + " dumpMap begin...");
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      d(tag, key + ": key=" + entry.getKey() + ", value=" + entry.getValue());
    }
    d(tag, key + " dumpMap end...");
  }


  public static void logStatckTrace(String tag, Throwable ex) {
    StackTraceElement[] stackElements = ex.getStackTrace();
    printStackTrace(tag, stackElements);

  }

  public static void logStatckTrace(String tag) {
    d(tag, android.util.Log.getStackTraceString(new Throwable()));
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


  static class LogInfo{
    public volatile long logCount = 0L;

    public volatile long logSize = 0L;

  }



}
