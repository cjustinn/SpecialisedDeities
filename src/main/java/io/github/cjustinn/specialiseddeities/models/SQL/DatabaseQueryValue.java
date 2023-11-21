package io.github.cjustinn.specialiseddeities.models.SQL;

import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQueryValueType;

public class DatabaseQueryValue<T> {
    public final int position;
    public final T value;
    public final DatabaseQueryValueType type;

    public DatabaseQueryValue(final int pos, final T val, final DatabaseQueryValueType type) {
        this.position = pos;
        this.value = val;
        this.type = type;
    }
}
