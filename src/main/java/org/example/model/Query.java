package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Query {
    @NotNull
    private List<String> columns = new ArrayList<>();
    @NotNull
    private List<Source> fromSources = new ArrayList<>();
    @NotNull
    private List<Join> joins = new ArrayList<>();
    @NotNull
    private List<Where> where = new ArrayList<>();
    @NotNull
    private List<String> groupByColumns = new ArrayList<>();
    @NotNull
    private List<Having> having = new ArrayList<>();
    @NotNull
    private List<Sort> sortColumns = new ArrayList<>();
    @Nullable
    private Integer limit;
    @Nullable
    private Integer offset;
}
