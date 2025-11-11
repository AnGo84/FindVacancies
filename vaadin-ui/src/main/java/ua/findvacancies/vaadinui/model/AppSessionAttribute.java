package ua.findvacancies.vaadinui.model;

public enum AppSessionAttribute {
    CUSTOM_LOCALE("customLocale");

    private final String label;

    AppSessionAttribute(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
