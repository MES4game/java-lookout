package et3.java.lookout;

import et3.java.lookout.entities.*;
import et3.java.lookout.events.Buyback;
import et3.java.lookout.events.Publication;
import et3.java.lookout.events.PublicationType;
import et3.java.lookout.exceptions.*;
import et3.java.lookout.observers.Observer;
import et3.java.lookout.observers.ObserverBuyback;
import et3.java.lookout.observers.ObserverPublication;
import et3.java.lookout.utils.Percentage;
import et3.java.lookout.utils.PercentageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class ConsoleUI {
    public static final Scanner scanner = new Scanner(System.in);

    public static void printInit() {
        System.out.println("Welcome to the Media lookout !");
        System.out.println();
    }

    public static String getResources() {
        System.out.print("Enter path to folder for loading database: ");
        String res = ConsoleUI.scanner.nextLine();
        System.out.println();
        return res;
    }

    public static int getChoice() {
        System.out.println("=".repeat(100));
        System.out.println("Every possible actions:");
        System.out.println("0.  Exit");
        System.out.println("1.  Display every Media");
        System.out.println("2.  Display every Organization");
        System.out.println("3.  Display every Owner");
        System.out.println("4.  Display a Media");
        System.out.println("5.  Display an Organization");
        System.out.println("6.  Display an Owner");
        System.out.println("7.  Make a buyback");
        System.out.println("8.  Make a publication");
        System.out.println("9.  See every observers");
        System.out.println("10. See observers of an entity");
        System.out.println("11. Add an observer of buyback");
        System.out.println("12. Add an observer of publication");
        System.out.println("13. Remove an observer");
        System.out.println("14. Display buyback history for a media");
        System.out.println("15. Display publication history for an owner");
        System.out.println("16. Display publication mentions percent for an entity");
        System.out.println("17. Change Threshold for publication alert");
        System.out.println("18. Display recent alerts");
        System.out.println("19. Display every alerts");
        System.out.print("> ");

        try {
            int res = Integer.parseInt(ConsoleUI.scanner.nextLine());
            System.out.println();
            return res;
        } catch (NumberFormatException e) { return -1; }
    }

    public static Entity getEntity(String type) throws ELOEntityNotFound {
        System.out.print("Enter " + type + " id: ");
        return Entity.getEntity(Integer.parseInt(ConsoleUI.scanner.nextLine()));
    }

    public static Observer getObserver() throws ELOObserverNotFound {
        System.out.print("Enter Observer id: ");
        return Observer.getObserver(Integer.parseInt(ConsoleUI.scanner.nextLine()));
    }

    public static Observer getEntityObserver(String type) throws ELOEntityNotFound, ELOObserverNotFound {
        Entity entity = ConsoleUI.getEntity(type);
        entity.printObservers();
        Observer observer = ConsoleUI.getObserver();
        if (!entity.isRegistered(observer))
            throw new ELOObserverNotFound("Observer not registered");
        return observer;
    }

    public static Percentage getPercentage() throws ELOPercentage {
        System.out.print("Enter percentage (as a floating-point number): ");
        double number = Double.parseDouble(ConsoleUI.scanner.nextLine());

        // It is only used for buyback, so we can set it directly to EQUALS
        // for (PercentageType value : PercentageType.values()) System.out.println(value);
        // System.out.print("> ");
        // PercentageType type = PercentageType.valueOf(ConsoleUI.scanner.nextLine());

        return new Percentage(number, PercentageType.EQUALS);
    }

    public static void printMediaList() {
        System.out.println("=== Media List ===");
        for (Entity e : Entity.getEntities()) if (e instanceof Media) System.out.println(e);
    }

    public static void printLegalList() {
        System.out.println("=== Legal Person List ===");
        for (Entity e : Entity.getEntities()) if (e instanceof PersonLegal) System.out.println(e);
    }

    public static void printNaturalList() {
        System.out.println("=== Natural Person List ===");
        for (Entity e : Entity.getEntities()) if (e instanceof PersonNatural) System.out.println(e);
    }

    public static void printMedia() throws ELOEntity, ELOPercentage {
        Entity entity = ConsoleUI.getEntity("Media");
        if (entity instanceof Media _media) {
            System.out.println("=== Display Media: " + _media + " ===");
            _media.printEveryOwners();
        }
    }

    public static void printLegal() throws ELOEntity, ELOPercentage {
        Entity entity = ConsoleUI.getEntity("Legal Person");
        if (entity instanceof PersonLegal _legal) {
            System.out.println("=== Display Legal Person: " + _legal + " ===");
            _legal.printEveryOwners();
            _legal.printEverySubsidiaries();
            _legal.printEveryOwned();
        }
    }

    public static void printNatural() throws ELOEntity, ELOPercentage {
        Entity entity = ConsoleUI.getEntity("Natural Person");
        if (entity instanceof PersonNatural _natural) {
            System.out.println("=== Display Natural Person: " + _natural + " ===");
            _natural.printChallengeRanks();
            _natural.printForbesRanks();
            _natural.printEverySubsidiaries();
            _natural.printEveryOwned();
        }
    }

    public static Buyback<?> getBuyback() throws ELOEntity, ELOPercentage {
        Entity entity = ConsoleUI.getEntity("Media/Legal Person");
        if (!(entity instanceof Purchasable))
            throw new ELOEntityWrongType("Can't cast to Purchasable");

        Entity oldOwner = ConsoleUI.getEntity("Old Owner");
        if (!(oldOwner instanceof Person))
            throw new ELOEntityWrongType("Can't cast to Person");

        Entity newOwner = ConsoleUI.getEntity("New Owner");
        if (!(newOwner instanceof Person))
            throw new ELOEntityWrongType("Can't cast to Person");

        return new Buyback<>((Entity & Purchasable) entity, (Person) oldOwner, (Person) newOwner, ConsoleUI.getPercentage());
    }

    public static Publication getPublication() throws ELOEntity {
        Entity entity = ConsoleUI.getEntity("Media");
        if (!(entity instanceof Media))
            throw new ELOEntityWrongType("Can't cast to Media");

        for (PublicationType value : PublicationType.values()) System.out.println(value);
        System.out.print("> ");
        PublicationType type = PublicationType.valueOf(ConsoleUI.scanner.nextLine());

        System.out.print("Enter number of mentions: ");
        int number = Integer.parseInt(ConsoleUI.scanner.nextLine());

        List<Entity> mentions = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            System.out.print("Enter " + (i + 1) + "th mentioned entity id: ");
            mentions.add(Entity.getEntity(Integer.parseInt(ConsoleUI.scanner.nextLine())));
        }

        return new Publication((Media) entity, type, mentions);
    }

    public static void printObservers() { Observer.printObservers(); }

    public static void printEntityObservers() throws ELOEntityNotFound {
        ConsoleUI.getEntity("Entity").printObservers();
    }

    public static void removeObserver() throws ELOEntityNotFound, ELOObserverNotFound {
        Entity entity = ConsoleUI.getEntity("Entity");
        entity.printObservers();
        Observer observer = ConsoleUI.getObserver();
        if (!entity.isRegistered(observer))
            throw new ELOObserverNotFound("Observer not registered");
        entity.unregister(observer);
    }

    public static void printBuybackHistory() throws ELOEntityNotFound, ELOObserverNotFound, ELOObserverWrongType {
        try {
            ObserverBuyback observer = (ObserverBuyback) ConsoleUI.getEntityObserver("Media");
            observer.printHistory();
        } catch (ClassCastException e) { throw new ELOObserverWrongType("Can't cast to ObserverBuyback"); }
    }

    public static void printPublicationHistory() throws ELOEntityNotFound, ELOObserverNotFound, ELOObserverWrongType {
        try {
            ObserverPublication observer = (ObserverPublication) ConsoleUI.getEntityObserver("Media/Natural Person");
            observer.printHistory();
        } catch (ClassCastException e) { throw new ELOObserverWrongType("Can't cast to ObserverPublication"); }
    }

    public static void printPublicationMentions() throws ELOEntityNotFound, ELOObserverNotFound, ELOObserverWrongType {
        try {
            ObserverPublication observer = (ObserverPublication) ConsoleUI.getEntityObserver("Media/Natural Person");
            observer.printMentions();
        } catch (ClassCastException e) { throw new ELOObserverWrongType("Can't cast to ObserverPublication"); }
    }

    public static double getThreshold() {
        System.out.println("Old Threshold: " + ObserverPublication.getThreshold());
        System.out.print("Enter new Threshold: ");
        return Double.parseDouble(ConsoleUI.scanner.nextLine());
    }

    public static void printRecentAlerts() { Lookout.printRecentHistory(); }

    public static void printAlerts() { Lookout.printHistory(); }
}
