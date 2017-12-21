package nl.blueside.api;

public enum DataSourceType
{
    SHAREPOINT("sp"),
    POKEMON("pkmn");

    private final String text;

    private DataSourceType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
