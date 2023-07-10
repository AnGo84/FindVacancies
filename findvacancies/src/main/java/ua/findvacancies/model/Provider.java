package ua.findvacancies.model;

import ua.findvacancies.model.strategy.*;

public enum Provider {
    DOU("DOU", new DOUStrategy(new DocumentConnect())),
    //HEADHUNTER("HeadHunter", new HHStrategy(new DocumentConnect())),
    GRC("GRC", new GRCStrategy(new DocumentConnect())),
    //ROBOTAUA("RobotaUA", new RobotaUAStrategy(new DocumentConnect())),
    WORKUA("WorkUA", new WorkUAStrategy(new DocumentConnect()));

    private final String displayName;
    private final Strategy strategy;

    Provider(String displayName, Strategy strategy) {
        this.displayName = displayName;
        this.strategy = strategy;
    }

    public static boolean contains(String s) {
        for (Provider provider : values())
            if (provider.displayName.equalsIgnoreCase(s))
                return true;
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Strategy getStrategy() {
        return strategy;
    }

}

