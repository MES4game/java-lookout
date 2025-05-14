package et3.java.lookout;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

public final class Lookout {
    private static final List<String> history = new ArrayList<>();
    private static int lastIndex = 0;

    public static void addHistory(String s) { Lookout.history.add("[alert] [" + LocalTime.now() + "] " + s); }

    public static void printRecentHistory() {
        for (int i = Lookout.lastIndex; i < Lookout.history.size(); i++) System.out.println(Lookout.history.get(i));
        Lookout.lastIndex = Lookout.history.size();
    }

    public static void printHistory() { for (String s : Lookout.history) System.out.println(s); }
}
