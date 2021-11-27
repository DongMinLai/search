package enums;

/**
 * 可用条件类型
 */
public enum ConditionType {
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    LIKE_LEFT("LIKE LEFT"),
    LIKE_RIGHT("LIKE RIGHT"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    EXISTS("EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN");

    private final String keyword;

    ConditionType(final String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword()
    {
        return this.keyword;
    }
}
