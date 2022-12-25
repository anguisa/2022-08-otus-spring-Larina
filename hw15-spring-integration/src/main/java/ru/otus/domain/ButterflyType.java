package ru.otus.domain;

public enum ButterflyType {
    PIERIS_BRASSICAE("Капустница"),
    AGLAIS_URTICAE("Крапивница"),
    PAPILIO_MACHAON("Махаон"),
    APATURA_ILIA("Радужница"),
    VANESSA_ATALANTA("Адмирал")
    ;

    private final String title;

    ButterflyType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
