package ru.vsu.cs.vvp2021.g12.butovetskaya_s_i.task13.n28;

public class GameParams {
    private int rowCount;
    private int colCount;
    private int colorCount;

    public GameParams(int rowCount, int colCount, int colorCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.colorCount = colorCount;
    }

    public GameParams() {
        this(12, 12, 6);
    }

    public int getColCount() {
        return colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColorCount() {
        return colorCount;
    }

    public void setColorCount(int colorCount) {
        this.colorCount = colorCount;
    }

}
