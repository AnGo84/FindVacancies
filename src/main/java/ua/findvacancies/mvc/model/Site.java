package ua.findvacancies.mvc.model;

/**
 * Created by AnGo on 24.08.2017.
 */
public enum Site {
    HEADHUNTER("HH"),
    RABOTAUA("RabotaUA"),
    WORKUA("WorkUA");

    private final String displayName;

    Site(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

