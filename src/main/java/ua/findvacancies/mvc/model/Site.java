package ua.findvacancies.mvc.model;

/**
 * Created by AnGo on 24.08.2017.
 */
public enum Site {
    DOU("DOU", new DOUStrategy()),
    HEADHUNTER("HeadHunter", new HHStrategy()),
    RABOTAUA("RabotaUA", new RabotaUAStrategy()),
    WORKUA("WorkUA", new WorkUAStrategy());


    private final String displayName;
    private final Strategy strategy;

    Site(String displayName, Strategy strategy) {
        this.displayName = displayName;
        this.strategy = strategy;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Strategy getStrategy() {
        return strategy;
    }


}

