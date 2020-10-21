package ua.findvacancies.mvc.model;

import ua.findvacancies.mvc.model.strategy.*;

public enum Provider {
    DOU("DOU", new DOUStrategy(new DocumentConnect())),
    HEADHUNTER("HeadHunter", new HHStrategy()),
    RABOTAUA("RabotaUA", new RabotaUAStrategy()),
    WORKUA("WorkUA", new WorkUAStrategy());

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

