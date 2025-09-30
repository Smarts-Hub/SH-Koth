package dev.smartshub.shkoth.storage.cache;

public class PushStackCache {

    // Temp storage for contextual arguments, like player name or koth name in some messages
    private static volatile String Arg1;
    private static volatile String Arg2;
    private static volatile String Arg3;
    private static volatile String Arg4 = "";

    public static void pushArg1(String arg1){
        Arg1 = arg1;
    }

    public static void pushArg2(String arg2){
        Arg2 = arg2;
    }

    public static void pushArg3(String arg3){
        Arg3 = arg3;
    }

    public static void pushArg4(String arg4){
        Arg4 = arg4;
    }

    public static String getArg1(){
        return Arg1;
    }

    public static String getArg2(){
        return Arg2;
    }

    public static String getArg3(){
        return Arg3;
    }

    public static String getArg4(){
        return Arg4;
    }

}
