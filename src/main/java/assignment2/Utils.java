package assignment2;

public class Utils {

    public static void log(String msg) {
        if (Config.DEBUG) {
            System.out.println("TH:" + Thread.currentThread() + "  " + msg);
        }
    }
}
