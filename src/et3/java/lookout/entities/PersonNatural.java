package et3.java.lookout.entities;

import java.util.HashMap;
import java.util.Map;

public class PersonNatural extends Person {
    public static final PersonNatural ADMIN = new PersonNatural("admin", "only for system", new HashMap<>(), new HashMap<>());

    private final Map<Integer, Integer> challengeRanks = new HashMap<>();
    private final Map<Integer, Integer> forbesRanks = new HashMap<>();

    public PersonNatural(String name, String comment, Map<Integer, Integer> challengeRanks, Map<Integer, Integer> forbesRanks) {
        super(name, comment);
        this.challengeRanks.putAll(challengeRanks);
        this.forbesRanks.putAll(forbesRanks);
    }

    public Integer getChallengeRank(Integer year) { return this.challengeRanks.get(year); }
    public Integer getForbesRank(Integer year) { return this.forbesRanks.get(year); }

    public void printChallengeRanks() {
        System.out.println("Challenge Ranks:");
        this.challengeRanks.forEach((year, rank) -> System.out.println("\t" + year + ": " + rank));
    }

    public void printForbesRanks() {
        System.out.println("Challenge Ranks:");
        this.forbesRanks.forEach((year, rank) -> System.out.println("\t" + year + ": " + rank));
    }
}
