package ru.vsu.cs.vvp2021.g12.butovetskaya_s_i.task13.n28;

//import java.awt.*;
//import java.net.PortUnreachableException;
import java.util.Random;

public class Game {

    public enum GameState {
        NOT_STARTED,
        PLAYING,
        WIN,
        FAIL
    }

    public enum CellState {
        CHANGE,
        CONST,
        BUTTON
    }

    public static class Cell {
        private CellState state;
        private int color;

        public Cell(CellState state, int color) {
            this.state = state;
            this.color = color;
        }

        public CellState getState() {
            return state;
        }

        public int getColor() { return color; }
    }

    private final Random rnd = new Random();

    GameState state = GameState.NOT_STARTED;

    private Cell[][] field = null;

    private int colorCount = 0;

    private Cell[] color = null;

    private int step;

    private int maxStep;

    public Game() {
    }

    public void newGame(int rowCount, int colCount, int colorCount) {
        step = 0;
        this.colorCount = colorCount;
        maxStep = (int) (rowCount * colCount - 2 * colorCount) / colorCount <2 ? 3 : (int) (rowCount * colCount - 2 * colorCount) / colorCount;

        field = new Cell[rowCount][colCount];
        for (int r = 0; r < field.length; r++) {
            for (int c = 0; c < field[r].length; c++) {
                field[r][c] = new Cell(CellState.CONST, rnd.nextInt(colorCount) + 1);
            }
        }

        field[0][0].state = CellState.CHANGE;

        color = new Cell[colorCount];
        for (int i = 0; i < color.length; i++) {
            color[i] = new Cell(CellState.BUTTON, i + 1);
        }

        createChange(false, 0);
        state = GameState.PLAYING;
    }

    private void createChange(boolean changeColor, int color) {
        int count = 1;
        while (count>0) {
            count = 0;
            for (int r = 0; r < getRowCount(); r++) {
                for (int c = 0; c < getColCount(); c++) {
                    if (field[r][c].getState() == CellState.CHANGE) {
                        field[r][c].color = (changeColor) ? color : field[r][c].getColor();
                        count += openAround(r, c);
                    }
                }
            }
        }
    }

    private int openAround(int row, int col) {

        int openedCount = 0;

        if (row != 0 && field[row - 1][col].getColor() == field[row][col].getColor() && field[row - 1][col].state == CellState.CONST) {
            field[row - 1][col].state = CellState.CHANGE;
            openedCount++;
        }
        if (col != 0 && field[row][col - 1].getColor() == field[row][col].getColor() && field[row][col - 1].state == CellState.CONST) {
            field[row][col - 1].state = CellState.CHANGE;
            openedCount++;
        }
        if (row != field.length - 1 && field[row + 1][col].getColor() == field[row][col].getColor() && field[row + 1][col].state == CellState.CONST) {
            field[row + 1][col].state = CellState.CHANGE;
            openedCount++;
        }
        if (col != field[0].length - 1 && field[row][col + 1].getColor() == field[row][col].getColor() && field[row][col + 1].state == CellState.CONST) {
            field[row][col + 1].state = CellState.CHANGE;
            openedCount++;
        }

        return openedCount;
    }


    public void setColor(int index) {
        if (index < 0 || index >= getColorCount() || state != GameState.PLAYING
                || getCellColor(index).getColor() == getCellField(0, 0).getColor()) {
            return;
        }

        step++;

        createChange(true, color[index].getColor());

        calcState();
    }

    private void calcState() {
        int rowCount = getRowCount(), colCount = getColCount();

        if (step == maxStep) {
            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < colCount; c++) {
                    if (field[r][c].state == CellState.CONST) {
                        state = GameState.FAIL;
                        return;
                    }
                }
            }
            state = GameState.WIN;

        } else if (step < maxStep) {
            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < colCount; c++) {
                    if (field[r][c].state == CellState.CONST) {
                        state = GameState.PLAYING;
                        return;
                    }
                }
            }
            state = GameState.WIN;
        }
    }

    public int getRowCount() {
        return field == null ? 0 : field.length;
    }

    public int getColCount() {
        return field == null ? 0 : field[0].length;
    }

    public int getColorCount() {
        return colorCount;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public int getStep() {
        return step;
    }

    public GameState getState() {
        return state;
    }

    public Cell getCellField(int row, int col) {
        int rowCount = getRowCount(), colCount = getColCount();
        if (row < 0 || row >= rowCount || col < 0 || col >= colCount) {
            return null;
        }

        return field[row][col];
    }

    public Cell getCellColor(int index) {
        if (index < 0 || index >= colorCount) {
            return null;
        }
        return color[index];
    }

}
