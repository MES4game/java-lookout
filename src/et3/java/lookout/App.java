package et3.java.lookout;

import et3.java.lookout.exceptions.ELOBase;
import et3.java.lookout.observers.ObserverBuyback;
import et3.java.lookout.observers.ObserverPublication;

public final class App {
    public static void init() {
        ConsoleUI.printInit();
        Database.load(ConsoleUI.getResources());
        System.out.println();
    }

    public static void run() {
        while (true) {
            try {
                switch (ConsoleUI.getChoice()) {
                    case 1 -> ConsoleUI.printMediaList();
                    case 2 -> ConsoleUI.printLegalList();
                    case 3 -> ConsoleUI.printNaturalList();
                    case 4 -> ConsoleUI.printMedia();
                    case 5 -> ConsoleUI.printLegal();
                    case 6 -> ConsoleUI.printNatural();
                    case 7 -> ConsoleUI.getBuyback().compute();
                    case 8 -> ConsoleUI.getPublication().compute();
                    case 9 -> ConsoleUI.printObservers();
                    case 10 -> ConsoleUI.printEntityObservers();
                    case 11 -> new ObserverBuyback(ConsoleUI.getEntity("Entity"));
                    case 12 -> new ObserverPublication(ConsoleUI.getEntity("Entity"));
                    case 13 -> ConsoleUI.removeObserver();
                    case 14 -> ConsoleUI.printBuybackHistory();
                    case 15 -> ConsoleUI.printPublicationHistory();
                    case 16 -> ConsoleUI.printPublicationMentions();
                    case 17 -> ObserverPublication.setThreshold(ConsoleUI.getThreshold());
                    case 18 -> ConsoleUI.printRecentAlerts();
                    case 19 -> ConsoleUI.printAlerts();
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice");
                }
            } catch (ELOBase e) { e.printStackTrace(); }

            System.out.println();
            System.out.println();
            System.out.println();
        }
    }
}
