package et3.java.lookout.entities;

import et3.java.lookout.exceptions.ELOEntity;
import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.exceptions.ELOEntityWrongType;
import et3.java.lookout.exceptions.ELOPercentage;
import et3.java.lookout.utils.Percentage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Person extends Entity {
    private final Map<Integer, Percentage> ownedList = new HashMap<>();
    private final Map<Integer, Percentage> subsidiaryList = new HashMap<>();

    public Person(String name, String comment) {
        super(name, comment);
    }

    public Percentage getOwned(Media media) throws ELOEntityNotFound {
        if (!this.ownedList.containsKey(media.id))
            throw new ELOEntityNotFound("Can't find in owned list");
        return this.ownedList.get(media.id).clone();
    }
    public void addOwned(Media media, Percentage percentage) { this.ownedList.put(media.id, percentage); }
    public void changeOwned(Media media, Percentage percentage) throws ELOEntityNotFound {
        if (!this.ownedList.containsKey(media.id))
            throw new ELOEntityNotFound("Can't find in owner list");
        this.ownedList.replace(media.id, percentage);
    }
    public void removeOwned(Media media) { this.ownedList.remove(media.id); }

    public Percentage getSubsidiary(PersonLegal subsidiary) throws ELOEntityNotFound {
        if (!this.subsidiaryList.containsKey(subsidiary.id))
            throw new ELOEntityNotFound("Can't find in subsidiary list");
        return this.subsidiaryList.get(subsidiary.id).clone();
    }
    public void addSubsidiary(PersonLegal subsidiary, Percentage percentage) {
        this.subsidiaryList.put(subsidiary.id, percentage);
    }
    public void changeSubsidiary(PersonLegal subsidiary, Percentage percentage) throws ELOEntityNotFound {
        if (!this.subsidiaryList.containsKey(subsidiary.id))
            throw new ELOEntityNotFound("Can't find in subsidiary list");
        this.subsidiaryList.replace(subsidiary.id, percentage);
    }
    public void removeSubsidiary(PersonLegal subsidiary) { this.subsidiaryList.remove(subsidiary.id); }

    public Map<Integer, Percentage> getOwnedList() throws ELOPercentage {
        Map<Integer, Percentage> result = new HashMap<>();
        for (Map.Entry<Integer, Percentage> o : this.ownedList.entrySet())
            result.put(o.getKey(), o.getValue().clone());
        return result;
    }

    public Map<Integer, Percentage> getEveryOwned() throws ELOEntity, ELOPercentage {
        Map<Integer, Percentage> result = this.getOwnedList();

        Map<Integer, Percentage> everySubsidiaries = this.getEverySubsidiaries();

        for (Map.Entry<Integer, Percentage> s : everySubsidiaries.entrySet()) {
            try {
                for (Map.Entry<Integer, Percentage> o : ((Person) Entity.getEntity(s.getKey())).getOwnedList().entrySet()) {
                    Percentage p = o.getValue().clone();
                    p.factor(s.getValue());
                    if (result.containsKey(o.getKey())) result.get(o.getKey()).add(p);
                    else result.put(o.getKey(), p);
                }
            } catch (ClassCastException e) { throw new ELOEntityWrongType("Can't cast to Person"); }
        }

        return result;
    }

    public Map<Integer, Percentage> getSubsidiaryList() throws ELOPercentage {
        Map<Integer, Percentage> result = new HashMap<>();
        for (Map.Entry<Integer, Percentage> s : this.subsidiaryList.entrySet())
            result.put(s.getKey(), s.getValue().clone());
        return result;
    }

    public Map<Integer, Percentage> getEverySubsidiaries() throws ELOEntity, ELOPercentage {
        return this.getEverySubsidiaries(this.id);
    }
    public Map<Integer, Percentage> getEverySubsidiaries(int start) throws ELOEntity, ELOPercentage {
        Map<Integer, Percentage> result = this.getSubsidiaryList();

        for (Map.Entry<Integer, Percentage> s : this.getSubsidiaryList().entrySet()) {
            if (Objects.equals(s.getKey(), start)) continue;

            try {
                Map<Integer, Percentage> subRes = ((Person) Entity.getEntity(s.getKey())).getEverySubsidiaries(start);

                for (Map.Entry<Integer, Percentage> ss : subRes.entrySet()) {
                    Percentage p = ss.getValue().clone();
                    p.factor(s.getValue());
                    if (result.containsKey(ss.getKey())) result.get(ss.getKey()).add(p);
                    else result.put(ss.getKey(), p);
                }
            } catch (ClassCastException e) { throw new ELOEntityWrongType("Can't cast to Person"); }
        }

        result.remove(start);

        return result;
    }

    public void printEveryOwned() throws ELOEntity, ELOPercentage {
        System.out.println("Owned:");
        for (Map.Entry<Integer, Percentage> o : this.getEveryOwned().entrySet()) {
            try {
                Media owned = (Media) Entity.getEntity(o.getKey());
                String type;
                try {
                    Percentage percentage = this.getOwned(owned);
                    type = percentage.equals(o.getValue()) ? "Direct" : "Direct/Indirect";
                } catch (ELOEntityNotFound e) { type = "Indirect"; }
                System.out.println("\t" + owned + "(" + type + "): " + o.getValue());
            } catch (ClassCastException e) { throw new ELOEntityWrongType("Can't cast to Media"); }
        }
    }

    public void printEverySubsidiaries() throws ELOEntity, ELOPercentage {
        System.out.println("Subsidiaries:");
        for (Map.Entry<Integer, Percentage> s : this.getEverySubsidiaries().entrySet()) {
            try {
                PersonLegal subsidiary = (PersonLegal) Entity.getEntity(s.getKey());
                String type;
                try {
                    Percentage percentage = this.getSubsidiary(subsidiary);
                    type = percentage.equals(s.getValue()) ? "Direct" : "Direct/Indirect";
                } catch (ELOEntityNotFound e) { type = "Indirect"; }
                System.out.println("\t" + subsidiary + "(" + type + "): " + s.getValue());
            } catch (ClassCastException e) { throw new ELOEntityWrongType("Can't cast to PersonLegal"); }
        }
    }
}
