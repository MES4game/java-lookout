package et3.java.lookout;

import et3.java.lookout.entities.*;
import et3.java.lookout.exceptions.ELOBase;
import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.utils.Percentage;
import et3.java.lookout.utils.PercentageType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class Database {
    public static List<String[]> getRows(Path path, int size) {
        try {
            return Files.lines(path)
                .skip(1)
                .map(line -> {
                    String[] parts = line.split("\t", -1);
                    String[] padded = new String[size];
                    for (int i = 0; i < size; i++) padded[i] = i < parts.length ? parts[i] : "";
                    return padded;
                })
                .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void doBuyback(String[] row) throws ELOBase {
        PercentageType type;
        switch (row[2].strip()) {
            case "égal à" -> type = PercentageType.EQUALS;
            case "inférieur à" -> type = PercentageType.LESS_THAN;
            case "supérieur à" -> type = PercentageType.GREATER_THAN;
            default -> { return; }
        }

        Entity source = Entity.getEntity(row[4].strip());
        Entity newOwner = Entity.getEntity(row[1].strip());
        Percentage percentage = new Percentage(Double.parseDouble(row[3].strip().replace("%", "")) / 100.0, type);

        if (source instanceof Purchasable _source && newOwner instanceof Person _newOwner) {
            Percentage newPercentage;

            try {
                newPercentage = _source.getOwner(_newOwner);
            } catch (ELOEntityNotFound e) {
                newPercentage = new Percentage(0, type);
            }

            newPercentage.setPercentage(newPercentage.getPercentage() + percentage.getPercentage());

            if (!_source.getOwnerList().containsKey(_newOwner.id))
                _source.addOwner(_newOwner, newPercentage);
            else
                _source.changeOwner(_newOwner, newPercentage);

            if (_source instanceof Media _media) {
                if (!_newOwner.getOwnedList().containsKey(_media.id))
                    _newOwner.addOwned(_media, newPercentage);
                else
                    _newOwner.changeOwned(_media, newPercentage);
            } else if (_source instanceof PersonLegal _legal) {
                if (!_newOwner.getSubsidiaryList().containsKey(_legal.id))
                    _newOwner.addSubsidiary(_legal, newPercentage);
                else
                    _newOwner.changeSubsidiary(_legal, newPercentage);
            }
        }
    }

    public static void load(String folder) {
        Path path;

        path = Paths.get(folder, "medias.tsv");
        for (String[] row : Database.getRows(path, 6)) {
            MediaType type;
            switch (row[1].strip()) {
                case "Presse (généraliste  politique  économique)" -> type = MediaType.PRESS;
                case "Télévision" -> type = MediaType.TELEVISION;
                case "Radio" -> type = MediaType.RADIO;
                case "Site" -> type = MediaType.SITE;
                default -> type = MediaType.NONE;
            }

            MediaPeriodicity periodicity;
            switch (row[2].strip()) {
                case "Quotidien" -> periodicity = MediaPeriodicity.DAILY;
                case "Hebdomadaire" -> periodicity = MediaPeriodicity.WEEKLY;
                case "Bimestriel" -> periodicity = MediaPeriodicity.FORTNIGHTLY;
                case "Mensuel" -> periodicity = MediaPeriodicity.MONTHLY;
                case "Bimensuel" -> periodicity = MediaPeriodicity.BIMONTHLY;
                default -> periodicity = MediaPeriodicity.NONE;
            }

            new Media(row[0].strip(), "", type, periodicity, row[3].strip(), Objects.equals(row[4].strip(), "Payant"), row[5].strip().equals("checked"));
        }

        path = Paths.get(folder, "organisations.tsv");
        for (String[] row : Database.getRows(path, 2)) new PersonLegal(row[0].strip(), row[1].strip());

        path = Paths.get(folder, "personnes.tsv");
        for (String[] row : Database.getRows(path, 9)) {
            Map<Integer, Integer> challenge = new HashMap<>();
            challenge.put(2024, Integer.parseInt(row[1].isBlank() ? "-1" : row[1]));
            challenge.put(2023, Integer.parseInt(row[3].isBlank() ? "-1" : row[3]));
            challenge.put(2022, Integer.parseInt(row[5].isBlank() ? "-1" : row[5]));
            challenge.put(2021, Integer.parseInt(row[7].isBlank() ? "-1" : row[7]));

            Map<Integer, Integer> forbes = new HashMap<>();
            forbes.put(2024, Integer.parseInt(row[2].isBlank() ? "-1" : row[2]));
            forbes.put(2023, Integer.parseInt(row[4].isBlank() ? "-1" : row[4]));
            forbes.put(2022, Integer.parseInt(row[6].isBlank() ? "-1" : row[6]));
            forbes.put(2021, Integer.parseInt(row[8].isBlank() ? "-1" : row[8]));

            new PersonNatural(row[0].strip(), "", challenge, forbes);
        }

        path = Paths.get(folder, "personne-organisation.tsv");
        for (String[] row : Database.getRows(path, 6))
            try { Database.doBuyback(row); } catch (ELOBase e) { e.printStackTrace(); };

        path = Paths.get(folder, "organisation-organisation.tsv");
        for (String[] row : Database.getRows(path, 6))
            try { Database.doBuyback(row); } catch (ELOBase e) { e.printStackTrace(); };

        path = Paths.get(folder, "personne-media.tsv");
        for (String[] row : Database.getRows(path, 5))
            try { Database.doBuyback(row); } catch (ELOBase e) { e.printStackTrace(); };

        path = Paths.get(folder, "organisation-media.tsv");
        for (String[] row : Database.getRows(path, 5))
            try { Database.doBuyback(row); } catch (ELOBase e) { e.printStackTrace(); };

        // TODO: recompute ADMIN percentage for every Purchasable
        for (Entity e : Entity.getEntities()) {
            if (e instanceof Purchasable _prod) {
                double p = 1;
                for (Map.Entry<Integer, Percentage> o : _prod.getOwnerList().entrySet()) {
                    if (o.getKey() == PersonNatural.ADMIN.id) continue;

                    if (o.getValue().type == PercentageType.GREATER_THAN) {
                        p = 0;
                        break;
                    }

                    p -= o.getValue().getPercentage();
                }

                p = Math.min(1, p);
                p = Math.max(0, p);
                Percentage res = new Percentage(p, PercentageType.EQUALS);

                if (p == 0) {
                    _prod.removeOwner(PersonNatural.ADMIN);
                    if (e instanceof Media _media)
                        PersonNatural.ADMIN.removeOwned(_media);
                    else if (e instanceof PersonLegal _legal)
                        PersonNatural.ADMIN.removeSubsidiary(_legal);
                    continue;
                }

                if (_prod.getOwnerList().containsKey(PersonNatural.ADMIN.id))
                    _prod.changeOwner(PersonNatural.ADMIN, res);
                else
                    _prod.addOwner(PersonNatural.ADMIN, res);

                if (e instanceof Media _media) {
                    if (PersonNatural.ADMIN.getOwnedList().containsKey(e.id))
                        PersonNatural.ADMIN.changeOwned(_media, res);
                    else
                        PersonNatural.ADMIN.addOwned(_media, res);
                }
                else if (e instanceof PersonLegal _legal) {
                    if (PersonNatural.ADMIN.getSubsidiaryList().containsKey(e.id))
                        PersonNatural.ADMIN.changeSubsidiary(_legal, res);
                    else
                        PersonNatural.ADMIN.addSubsidiary(_legal, res);
                }
            }
        }
    }
}
