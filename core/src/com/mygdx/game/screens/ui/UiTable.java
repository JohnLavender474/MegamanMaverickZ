package com.mygdx.game.screens.ui;

import com.badlogic.gdx.math.Rectangle;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UiTable {

    private final Map<Integer, UiTableRow> rows = new HashMap<>();
    private final Rectangle bounds = new Rectangle();
    private int currentRowIndex;

    public static UiTable newTable(float x, float y, float width, float height) {
        UiTable table = new UiTable();
        table.bounds.set(x, y, width, height);
        return table;
    }

    public UiTableRow row(float height) {
        UiTableRow currentTableRow = rows.get(currentRowIndex);
        UiTableRow nextTableRow = new UiTableRow(
                this, bounds.x, currentTableRow != null ? currentTableRow.bounds.y : bounds.y,
                bounds.width, height);
        rows.put(currentRowIndex, nextTableRow);
        currentRowIndex++;
        return nextTableRow;
    }

    public static class UiTableRow {

        private final Map<Integer, UiTableCell> cells = new HashMap<>();
        private final Rectangle bounds = new Rectangle();
        @Getter private final UiTable table;
        @Getter private int currentColIndex;

        private UiTableRow(UiTable table, float x, float y, float width, float height) {
            this.table = table;
            bounds.set(x, y, width, height);
        }

        public UiTableRow col() {
            cells.put(currentColIndex, new UiTableCell(currentColIndex, this));
            currentColIndex++;
            return this;
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UiTableCell {

        private final int colIndex;
        @Getter private final UiTableRow row;
        @Setter private Object userData;

        public Rectangle getCellBounds() {
            float width = row.bounds.width / row.cells.size();
            float height = row.bounds.height;
            float x = row.bounds.x + width * colIndex;
            float y = row.bounds.y;
            return new Rectangle(x, y, width, height);
        }

        public <T> T getUserData(Class<T> clazz) {
            return clazz.cast(userData);
        }

    }

}
