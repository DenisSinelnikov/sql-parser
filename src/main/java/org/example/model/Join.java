package org.example.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Join(@NotNull String type, @NotNull String table, @Nullable String alias, @NotNull String onCondition) {

}
