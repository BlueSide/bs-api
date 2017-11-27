package nl.blueside.api;

public enum SPFieldType
{
    INVALID(0),
    INTEGER(1),
    TEXT(2),
    NOTE(3),
    DATETIME(4),
    COUNTER(5),
    CHOICE(6),
    LOOKUP(7),
    BOOLEAN(8),
    NUMBER(9),
    CURRENCY(10),
    URL(11),
    COMPUTED(12),
    THREADING(13),
    GUID(14),
    MULTICHOICE(15),
    GRIDCHOICE(16),
    CALCULATED(17),
    FILE(18),
    ATTACHMENTS(19),
    USER(20),
    RECURRENCE(21),
    CROSSPROJECTLINK(22),
    MODSTAT(23),
    ERROR(24),
    CONTENTTYPEID(25),
    PAGESEPARATOR(26),
    THREADINDEX(27),
    WORKFLOWSTATUS(28),
    ALLDAYEVENT(29),
    WORKFLOWEVENTTYPE(30),
    GEOLOCATION(31),
    OUTCOMECHOICE(32),
    MAXITEMS(33);

    private final int ordinal;

    SPFieldType(int ordinal)
    {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
