package diarr.caveuberhaul;

import java.util.HashMap;

public class Profiler {
    public static HashMap<String, Long> ellapsedTime = new HashMap<>();
    public static HashMap<String, Long> startTimes = new HashMap<>();
    public static HashMap<String, Integer> methodCalls = new HashMap<>();

    public static float getAverageTime(String id){
        return ((float) ellapsedTime.get(id)) /methodCalls.get(id);
    }
    public static void methodStart(String id){
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
        StringBuilder builder = new StringBuilder("Average Time per function");
        for (String key: ellapsedTime.keySet()) {
            builder.append(key).append("\t").append(getAverageTime(key));
        }
        CaveUberhaul.LOGGER.info(builder.toString());
    }

}
