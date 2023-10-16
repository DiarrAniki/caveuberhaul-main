package diarr.caveuberhaul;

import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class Profiler {
    protected static HashMap<String, Long> ellapsedTime = new HashMap<>();
    protected static HashMap<String, Long> startTimes = new HashMap<>();
    protected static HashMap<String, Integer> methodCalls = new HashMap<>();
    private static int longestKey = 0;

    public static float getAverageTime(String id){
        return ((float) ellapsedTime.get(id)) /methodCalls.get(id);
    }
    public static void methodStart(String id){
        if (id.length() > longestKey){
            longestKey = id.length();
        }
        startTimes.put(id, System.currentTimeMillis());
        ellapsedTime.putIfAbsent(id, 0L);
        if (methodCalls.get(id) == null){
            methodCalls.put(id, 1);
        } else {
            int times = methodCalls.get(id);
            methodCalls.put(id, times + 1);
        }
    }
    public static void methodEnd(String id){
        float deltaTime = System.currentTimeMillis() - startTimes.get(id);
        ellapsedTime.put(id, (long) (ellapsedTime.get(id) + deltaTime));
    }

    public static void printTimes(){
        StringBuilder builder = new StringBuilder("Average Time per function\n");
        for (String key: ellapsedTime.keySet()) {
            builder.append(key).append("\t\t\t").append(getAverageTime(key)).append("\t").append(ellapsedTime.get(key)).append("\t").append(methodCalls.get(key)).append("\n");
        }
        CaveUberhaul.LOGGER.info(builder.toString());
    }
    public static void printTimesInRespectToID(String id){
        if (ellapsedTime.size() == 0) {return;}
        long totalKeyTime = ellapsedTime.get(id);
        StringBuilder builder = new StringBuilder("Function Times\n").append(StringUtils.rightPad("ID", longestKey)).append(" | ").append(StringUtils.rightPad("Percentage", 11)).append(" | ").append(StringUtils.rightPad("Average", 9)).append(" | ").append(StringUtils.rightPad("Ellapsed", 12)).append(" | ").append("Times called\n");
        for (String key: ellapsedTime.keySet()) {
            double percentage = (ellapsedTime.get(key) * 100d) /totalKeyTime;
            builder.append(StringUtils.rightPad(key, longestKey)).append(" | ").append(StringUtils.rightPad(String.format("%3.3f", percentage), 11)).append(" | ").append(StringUtils.rightPad(String.format("%.3f", getAverageTime(key)), 9)).append(" | ").append(StringUtils.rightPad(String.valueOf(ellapsedTime.get(key)), 12)).append(" | ").append(methodCalls.get(key)).append("\n");
        }
        CaveUberhaul.LOGGER.info(builder.toString());
    }
    public static void clearTimes(){
        ellapsedTime.clear();
        startTimes.clear();
        methodCalls.clear();
        longestKey = 0;
    }

}
