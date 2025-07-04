package org.example.parser;

import org.example.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.parser.SQLRegexConst.*;

public class SqlParser {

    private static final String CONDITION_SPLIT_REGEX = "(?i) " + Keyword.AND + " ";

    public Query parse(String sql) {
        QueryBuilder builder = new QueryBuilder();
        String cleanedSql = sql.replaceAll(SQLRegexConst.SPACE, " ").strip();

        builder.columns(parseColumns(extractSection(cleanedSql, SQLRegexConst.SELECT_COLUMNS_PATTERN)));
        builder.fromSources(parseFromSources(cleanedSql));
        builder.joins(parseJoins(cleanedSql));
        builder.where(parseWhere(cleanedSql));
        builder.groupByColumns(parseGroupBy(cleanedSql));
        builder.having(parseHaving(cleanedSql));
        builder.sortColumns(parseOrderBy(cleanedSql));
        builder.limit(parseLimit(cleanedSql));
        builder.offset(parseOffset(cleanedSql));

        return builder.build();
    }

    private String extractSection(String sql, String regex) {
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sql);
        if (matcher.find()) {
            return matcher.group(1).strip();
        }
        return "";
    }

    private List<String> parseColumns(String columnsPart) {
        return columnsPart.isEmpty() ? List.of() :
            Arrays.stream(columnsPart.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private List<Source> parseFromSources(String sql) {
        String fromPart = extractFromPartManually(sql);

        if (fromPart.isBlank()) {
            return List.of();
        }

        List<Source> sources = new ArrayList<>();

        for (String sourceStr : fromPart.split(",")) {
            String source = sourceStr.trim();

            if (source.startsWith("(")) {
                int parenCount = 0;
                int endIndex = -1;

                for (int i = 0; i < source.length(); i++) {
                    char c = source.charAt(i);
                    if (c == '(') {
                        parenCount++;
                    } else if (c == ')') {
                        parenCount--;
                        if (parenCount == 0) {
                            endIndex = i;
                            break;
                        }
                    }
                }

                if (endIndex != -1 && endIndex < source.length() - 1) {
                    String subquery = source.substring(0, endIndex + 1);
                    String remaining = source.substring(endIndex + 1).trim();
                    String alias = remaining.isEmpty() ? null : remaining;
                    sources.add(new Source(subquery, alias));
                } else if (endIndex != -1) {
                    String subquery = source.substring(0, endIndex + 1);
                    sources.add(new Source(subquery, null));
                }
                continue;
            }

            String[] sourceTokens = source.split("\\s+");
            String name = sourceTokens[0];
            String alias = sourceTokens.length > 1 ? sourceTokens[1] : null;
            sources.add(new Source(name, alias));
        }

        return sources;
    }

    private String extractFromPartManually(String sql) {
        Pattern pattern = Pattern.compile(JOIN_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        pattern = Pattern.compile(WHERE_REGEX, Pattern.DOTALL);
        matcher = pattern.matcher(sql);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    private List<Join> parseJoins(String sql) {
        List<Join> joins = new ArrayList<>();
        Matcher joinMatcher = Pattern.compile(
            SQLRegexConst.JOIN_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        ).matcher(sql);
        while (joinMatcher.find()) {
            String joinType = joinMatcher.group(1).strip();
            String tablePart = joinMatcher.group(2).strip();
            String onCondition = joinMatcher.group(3).strip();

            String[] tableTokens = tablePart.split("\\s+");
            String table = tableTokens[0];
            String alias = tableTokens.length > 1 ? tableTokens[1] : null;

            joins.add(new Join(joinType, table, alias, onCondition));
        }
        return joins;
    }

    private List<Where> parseWhere(String sql) {
        String wherePart = extractWherePartManually(sql);
        if (wherePart.isEmpty()) {
            return List.of();
        }
        String[] conditions = wherePart.split(CONDITION_SPLIT_REGEX);
        return Arrays.stream(conditions)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Where::new)
            .toList();
    }

    private String extractWherePartManually(String sql) {
        int whereIndex = -1;
        String lowerSql = sql.toLowerCase();

        int searchFrom = 0;
        while (true) {
            int foundIndex = lowerSql.indexOf("where", searchFrom);
            if (foundIndex == -1) break;

            boolean isWordBoundary = (foundIndex == 0 || !Character.isLetterOrDigit(lowerSql.charAt(foundIndex - 1))) &&
                (foundIndex + 5 >= lowerSql.length() || !Character.isLetterOrDigit(lowerSql.charAt(foundIndex + 5)));

            if (isWordBoundary) {
                whereIndex = foundIndex;
            }
            searchFrom = foundIndex + 1;
        }

        if (whereIndex == -1) {
            return "";
        }

        String fromWhere = sql.substring(whereIndex + 5).trim();

        Pattern pattern = Pattern.compile(FROM_WHERE_REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fromWhere);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return fromWhere;
    }

    private List<String> parseGroupBy(String sql) {
        String groupByPart = extractSection(sql, SQLRegexConst.GROUP_BY_PATTERN);
        if (groupByPart.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(groupByPart.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }

    private List<Having> parseHaving(String sql) {
        String havingPart = extractSection(sql, SQLRegexConst.HAVING_PATTERN);
        if (havingPart.isEmpty()) {
            return List.of();
        }
        String[] conditions = havingPart.split(CONDITION_SPLIT_REGEX);
        return Arrays.stream(conditions)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Having::new)
            .toList();
    }

    private List<Sort> parseOrderBy(String sql) {
        String orderByPart = extractSection(sql, SQLRegexConst.ORDER_BY_PATTERN);
        if (orderByPart.isEmpty()) {
            return List.of();
        }
        String[] parts = orderByPart.split(",");
        List<Sort> sorts = new ArrayList<>();
        for (String part : parts) {
            String[] tokens = part.trim().split("\\s+");
            String column = tokens[0];
            String direction = tokens.length > 1 ? tokens[1].toUpperCase() : "ASC";
            sorts.add(new Sort(column, direction));
        }
        return sorts;
    }

    private Integer parseLimit(String sql) {
        Matcher limitMatcher = Pattern.compile(SQLRegexConst.LIMIT_PATTERN, Pattern.CASE_INSENSITIVE).matcher(sql);
        if (limitMatcher.find()) {
            return Integer.parseInt(limitMatcher.group(1));
        }
        return null;
    }

    private Integer parseOffset(String sql) {
        Matcher offsetMatcher = Pattern.compile(SQLRegexConst.OFFSET_PATTERN, Pattern.CASE_INSENSITIVE).matcher(sql);
        if (offsetMatcher.find()) {
            return Integer.parseInt(offsetMatcher.group(1));
        }
        return null;
    }
}