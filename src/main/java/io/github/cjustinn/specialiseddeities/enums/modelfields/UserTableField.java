package io.github.cjustinn.specialiseddeities.enums.modelfields;

public enum UserTableField implements BaseTableField {
    UUID("uuid"),
    Patron("patron"),
    Leader("is_leader"),
    Demigod("is_demigod"),
    God("is_god"),
    Pledged("pledged");

    private final String columnName;
    UserTableField(final String column) {
        this.columnName = column;
    }

    @Override
    public String getColumn() {
        return this.columnName;
    }
}
