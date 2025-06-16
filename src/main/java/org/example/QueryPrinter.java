package org.example;

import org.example.model.*;

public final class QueryPrinter {

    private QueryPrinter() {}

    public static String print(Query query) {
        StringBuilder sb = new StringBuilder();

        sb.append("Query:\n");

        sb.append("  Columns: ").append(query.getColumns()).append("\n");

        sb.append("  From Sources:\n");
        for (Source source : query.getFromSources()) {
            sb.append("    - name: ").append(source.name());
            if (source.alias() != null) {
                sb.append(" alias: ").append(source.alias());
            }
            sb.append("\n");
        }

        sb.append("  Joins:\n");
        for (Join join : query.getJoins()) {
            sb.append("    - type: ").append(join.type())
                .append(" table: ").append(join.table());
            if (join.alias() != null) {
                sb.append(" alias: ").append(join.alias());
            }
            sb.append(" ON ").append(join.onCondition()).append("\n");
        }

        sb.append("  Where Clauses:\n");
        for (Where where : query.getWhere()) {
            sb.append("    - ").append(where.condition()).append("\n");
        }

        sb.append("  Group By Columns: ").append(query.getGroupByColumns()).append("\n");

        sb.append("  Having Clauses:\n");
        for (Having having : query.getHaving()) {
            sb.append("    - ").append(having.condition()).append("\n");
        }

        sb.append("  Sort Columns:\n");
        for (Sort sort : query.getSortColumns()) {
            sb.append("    - ").append(sort.column())
                .append(" ").append(sort.direction()).append("\n");
        }

        sb.append("  Limit: ").append(query.getLimit()).append("\n");
        sb.append("  Offset: ").append(query.getOffset()).append("\n");

        return sb.toString();
    }
}
