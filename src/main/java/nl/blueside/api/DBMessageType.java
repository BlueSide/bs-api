package nl.blueside.api;

public enum DBMessageType
{
    UPDATE("update"),
    SESSION_CREATED("session_created"),
    ERROR("error");

    private final String text;

    private DBMessageType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
