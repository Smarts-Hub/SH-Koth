package dev.smartshub.shkoth.storage.cache;

public class PushStackCache {

    // Temp storage for contextual arguments, like player name or koth name in some messages
    private static volatile String Arg1;
    private static volatile String Arg2;
    private static volatile String Arg3;

    public static void pushArgs(String... args) {
        Arg1 = args.length > 0 ? args[0] : null;
        Arg2 = args.length > 1 ? args[1] : null;
        Arg3 = args.length > 2 ? args[2] : null;
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

}
