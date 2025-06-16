package org.example.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Source(@NotNull String name, @Nullable String alias) {

}
