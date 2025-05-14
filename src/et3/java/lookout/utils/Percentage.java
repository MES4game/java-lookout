package et3.java.lookout.utils;

import et3.java.lookout.exceptions.ELOPercentage;

public class Percentage {
    private double percentage;
    public final PercentageType type;

    public Percentage(double percentage, PercentageType type) throws ELOPercentage {
        if (percentage < 0 || percentage > 1)
            throw new ELOPercentage("Percentage must be between 0 and 1");
        this.percentage = percentage;
        this.type = type;
    }

    public double getPercentage() { return this.percentage; }
    public void setPercentage(double percentage) throws ELOPercentage {
        if (percentage < 0 || percentage > 1)
            throw new ELOPercentage("Percentage must be between 0 and 1");
        this.percentage = percentage;
    }

    public void add(Percentage percentage) throws ELOPercentage {
        if (this.type != PercentageType.EQUALS && this.type != PercentageType.LESS_THAN)
            throw new ELOPercentage("Cannot add to percentage of type " + percentage.type);

        if (percentage.type != PercentageType.EQUALS)
            throw new ELOPercentage("Cannot add with percentage of type " + percentage.type);

        if (this.percentage + percentage.getPercentage() > 1.00001)
            throw new ELOPercentage("Cannot exceed 100%");

        this.percentage += percentage.getPercentage();
    }

    public void sub(Percentage percentage) throws ELOPercentage {
        if (this.type != PercentageType.EQUALS && this.type != PercentageType.GREATER_THAN)
            throw new ELOPercentage("Cannot sub to percentage of type " + percentage.type);

        if (percentage.type != PercentageType.EQUALS)
            throw new ELOPercentage("Cannot sub with percentage of type " + percentage.type);

        if (this.percentage - percentage.getPercentage() < -0.00001)
            throw new ELOPercentage("Cannot lower under 0%");

        this.percentage -= percentage.getPercentage();
    }

    public void factor(Percentage percentage) throws ELOPercentage {
        if (this.type != PercentageType.EQUALS)
            throw new ELOPercentage("Cannot factor to percentage of type " + percentage.type);

        if (percentage.type != PercentageType.EQUALS)
            throw new ELOPercentage("Cannot factor with percentage of type " + percentage.type);

        this.percentage *= percentage.getPercentage();
        this.percentage = Math.max(0, this.percentage);
        this.percentage = Math.min(1, this.percentage);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Percentage _o && _o.percentage == this.percentage && _o.type == this.type);
    }

    @Override
    public Percentage clone() throws ELOPercentage { return new Percentage(this.percentage, this.type); }

    @Override
    public String toString() {
        if (this.type == PercentageType.NONE) return "";

        return (this.type == PercentageType.EQUALS ? "=" : (this.type == PercentageType.LESS_THAN ? "<" : ">")) +
                (this.percentage * 100) + "%";
    }
}
