package et3.java.lookout.events;

import et3.java.lookout.entities.*;
import et3.java.lookout.exceptions.*;
import et3.java.lookout.utils.Percentage;
import et3.java.lookout.utils.PercentageType;

public class Buyback<T extends Entity & Purchasable> implements Event {
    public final T source;
    public final Person oldOwner;
    public final Person newOwner;
    public final Percentage percentage;

    public Buyback(T source, Person oldOwner, Person newOwner, Percentage percentage) {
        this.source = source;
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
        this.percentage = percentage;
    }

    @Override
    public void compute() throws ELOEvent, ELOEntity, ELOPercentage, ELOObserver {
        if (!this.source.getOwnerList().containsKey(this.oldOwner.id))
            throw new ELOBuyback("Invalid old owner: not in list of owners");

        Percentage oldPercentage = this.source.getOwner(this.oldOwner);
        Percentage newPercentage = this.source.getOwner(this.newOwner);

        oldPercentage.sub(this.percentage);
        newPercentage.add(this.percentage);

        if (oldPercentage.getPercentage() < 0.00001)
            this.source.removeOwner(this.oldOwner);
        else
            this.source.changeOwner(this.oldOwner, oldPercentage);

        if (!this.source.getOwnerList().containsKey(this.newOwner.id))
            this.source.addOwner(this.newOwner, new Percentage(0, PercentageType.EQUALS));

        this.source.changeOwner(this.newOwner, newPercentage);

        if (this.source instanceof Media _media) {
            if (oldPercentage.getPercentage() < 0.00001)
                this.oldOwner.removeOwned(_media);
            else
                this.oldOwner.changeOwned(_media, oldPercentage);

            if (!this.newOwner.getOwnedList().containsKey(_media.id))
                this.newOwner.addOwned(_media, new Percentage(0, PercentageType.EQUALS));

            this.newOwner.changeOwned(_media, newPercentage);
        }

        else if (this.source instanceof PersonLegal _legal) {
            if (oldPercentage.getPercentage() < 0.00001)
                this.oldOwner.removeSubsidiary(_legal);
            else
                this.oldOwner.changeSubsidiary(_legal, oldPercentage);

            if (!this.newOwner.getSubsidiaryList().containsKey(_legal.id))
                this.newOwner.addSubsidiary(_legal, new Percentage(0, PercentageType.EQUALS));

            this.newOwner.changeSubsidiary(_legal, newPercentage);
        }

        this.source.notify(this);
        this.oldOwner.notify(this);
        this.newOwner.notify(this);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[media=" + this.source + ", oldOwner=" + this.oldOwner +
                ", newOwner=" + this.newOwner + ", percentage=" + this.percentage + "]";
    }
}
