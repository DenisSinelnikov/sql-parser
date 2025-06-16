package org.example.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QueryBuilder {
    private final Query query;

    public QueryBuilder() {
        this.query = new Query();
    }

    public void columns(@NotNull List<String> columns) {
        query.setColumns(columns);
    }

    public void fromSources(@NotNull List<Source> sources) {
        query.setFromSources(sources);
    }

    public void joins(@NotNull List<Join> joins) {
        query.setJoins(joins);
    }

    public void where(@NotNull List<Where> whereClauses) {
        query.setWhere(whereClauses);
    }

    public void groupByColumns(@NotNull List<String> groupByColumns) {
        query.setGroupByColumns(groupByColumns);
    }

    public void having(@NotNull List<Having> havingClauses) {
        query.setHaving(havingClauses);
    }

    public void sortColumns(@NotNull List<Sort> sortColumns) {
        query.setSortColumns(sortColumns);
    }

    public void limit(@Nullable Integer limit) {
        query.setLimit(limit);
    }

    public void offset(@Nullable Integer offset) {
        query.setOffset(offset);
    }

    public Query build() {
        return query;
    }
}