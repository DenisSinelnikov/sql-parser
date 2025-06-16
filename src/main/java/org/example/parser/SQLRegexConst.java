package org.example.parser;

public final class SQLRegexConst {
    public static final String SPACE = "\\s+";

    public static final String SELECT_COLUMNS_PATTERN =
        "(?i)" + Keyword.SELECT + " (.*?) " + Keyword.FROM;

    public static final String FROM_PATTERN =
        "(?i)" + Keyword.FROM + " (.*?)( " + Keyword.WHERE
            + " | " + Keyword.GROUP_BY
            + " | " + Keyword.HAVING
            + " | " + Keyword.ORDER_BY
            + " | " + Keyword.LIMIT
            + " | " + Keyword.OFFSET
            + " |$)";

    public static final String WHERE_PATTERN =
        "(?i)" + Keyword.WHERE + " (.*?)( " + Keyword.GROUP_BY
            + " | " + Keyword.HAVING
            + " | " + Keyword.ORDER_BY
            + " | " + Keyword.LIMIT
            + " | " + Keyword.OFFSET
            + " |$)";

    public static final String GROUP_BY_PATTERN =
        "(?i)" + Keyword.GROUP_BY + " (.*?)( " + Keyword.HAVING
            + " | " + Keyword.ORDER_BY
            + " | " + Keyword.LIMIT
            + " | " + Keyword.OFFSET
            + " |$)";

    public static final String HAVING_PATTERN =
        "(?i)" + Keyword.HAVING + " (.*?)( " + Keyword.ORDER_BY
            + " | " + Keyword.LIMIT
            + " | " + Keyword.OFFSET
            + " |$)";

    public static final String ORDER_BY_PATTERN =
        "(?i)" + Keyword.ORDER_BY + " (.*?)( " + Keyword.LIMIT
            + " | " + Keyword.OFFSET
            + " |$)";

    public static final String LIMIT_PATTERN =
        "(?i)" + Keyword.LIMIT + " (\\d+)";

    public static final String OFFSET_PATTERN =
        "(?i)" + Keyword.OFFSET + " (\\d+)";

    public static final String JOIN_PATTERN =
        "(?i)(" + Keyword.INNER_JOIN
            + "|" + Keyword.LEFT_JOIN
            + "|" + Keyword.RIGHT_JOIN
            + "|" + Keyword.FULL_JOIN + ")\\s+(.*?)\\s+"
            + Keyword.ON + "\\s+(.*?)(?=("
            + " " + Keyword.INNER_JOIN
            + " | " + Keyword.LEFT_JOIN
            + " | " + Keyword.RIGHT_JOIN
            + " | " + Keyword.FULL_JOIN
            + " | " + Keyword.WHERE
            + " | " + Keyword.GROUP_BY
            + " | " + Keyword.HAVING
            + " | " + Keyword.ORDER_BY
            + " | " + Keyword.LIMIT
            + " | " + Keyword.OFFSET
            + " |$))";

    private SQLRegexConst() {}
}
