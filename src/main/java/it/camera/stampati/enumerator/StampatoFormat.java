package it.camera.stampati.enumerator;

public enum StampatoFormat {
    PDF(1), XHTML(2), HTML(3), EPUB(4);

    private final int value;

    StampatoFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static StampatoFormat fromValue(int value) {
        for (StampatoFormat format : values()) {
            if (format.value == value) {
				return format;
			}
        }
        throw new IllegalArgumentException("Invalid format value: " + value);
    }

    public static StampatoFormat fromString(String name) {
        for (StampatoFormat format : values()) {
            if (format.name().equalsIgnoreCase(name)) {
				return format;
			}
        }
        throw new IllegalArgumentException("Invalid format name: " + name);
    }

    public String getExtension() {
        return "." + this.name().toLowerCase();
    }
}
