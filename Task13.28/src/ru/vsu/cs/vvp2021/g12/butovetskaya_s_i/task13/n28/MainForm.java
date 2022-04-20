package ru.vsu.cs.vvp2021.g12.butovetskaya_s_i.task13.n28;

import utils.JTableUtils;
import utils.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JTable tableGameField;
    private JTable tableColor;
    private JPanel panelTop;
    private JLabel labelStep;
    private JPanel panelMaxStep;
    private JLabel labelMaxStep;
    private JButton buttonNewGame;
    private JLabel labelStatus;
    private JPanel panelStep;

    private static final int DEFAULT_COL_COUNT = 12;
    private static final int DEFAULT_ROW_COUNT = 12;
    private static final int DEFAULT_COLOR_COUNT = 6;

    private static final int DEFAULT_GAP = 25;
    private static final int DEFAULT_CELL_SIZE = 30;

    private final Random rnd = new Random();

    private static final Color[] COLORS = {
            new Color(148,0,211),
            new Color(0, 92, 255),
            new Color(72, 234, 234, 255),
            new Color(24, 222, 115),
            new Color(250, 250, 116),
            new Color(225, 20, 148),
            new Color(255,60,80),
            Color.ORANGE,
            Color.BLUE,
            Color.CYAN,
            Color.RED,
            Color.PINK,
            Color.YELLOW,
            Color.GREEN,
    };

    private GameParams params = new GameParams(DEFAULT_ROW_COUNT, DEFAULT_COL_COUNT, DEFAULT_COLOR_COUNT);
    private Game game = new Game();

    private ParamsDialog dialogParams;

    private static final Game.Cell
            NULL_CELL = new Game.Cell(Game.CellState.CONST, 0);


    public MainForm() {
        this.setTitle("ПЕРЕКРАСИТЬ");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        setJMenuBar(createMenuBar());
        this.pack();

        SwingUtils.setShowMessageDefaultErrorHandler();

        panelMain.setPreferredSize(new Dimension(500, 500));

        tableGameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableGameField, DEFAULT_CELL_SIZE, false, false, false, false);
        tableGameField.setIntercellSpacing(new Dimension(0, 0));
        tableGameField.setEnabled(false);

        tableColor.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableColor, DEFAULT_CELL_SIZE, false, false, false, false);
        tableColor.setIntercellSpacing(new Dimension(0, 0));
        tableColor.setEnabled(false);

        tableGameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    paintCell(row, column, g2d, getWidth(), getHeight());
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        tableColor.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    paintColorCell(row, column, g2d, getWidth(), getHeight());
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        newGame();

        updateWindowSize();
        updateView();

        dialogParams = new ParamsDialog(params, tableGameField, (ActionEvent actionEvent) -> newGame());

        buttonNewGame.addActionListener(e -> {
            newGame();
        });
        tableColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = tableColor.columnAtPoint(e.getPoint());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    game.setColor(index);
                    updateView();
                }
            }
        });

//        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
//            @Override
//            public boolean dispatchKeyEvent(KeyEvent e) {
//                game.leftMouseClick(e.getKeyChar()-1);
//                updateView();
//                if (e.getID() == KeyEvent.KEY_PRESSED) {
//                    System.out.printf("globalKeyPressed: %s, %s, %s%n",
//                            e.getKeyChar(), e.getKeyCode(), e.getExtendedKeyCode());
//                }
//
//                return false;
//            }
//        });
    }

    private JMenuItem createMenuItem(String text, String shortcut, Character mnemonic, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        if (shortcut != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace('+', ' ')));
        }
        if (mnemonic != null) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBarMain = new JMenuBar();

        JMenu menuGame = new JMenu("Игра");
        menuBarMain.add(menuGame);
        menuGame.add(createMenuItem("Новая", "ctrl+N", null, e -> {
            newGame();
        }));
        menuGame.add(createMenuItem("Параметры", "ctrl+P", null, e -> {
            dialogParams.updateView();
            dialogParams.setVisible(true);
        }));
        menuGame.addSeparator();
        menuGame.add(createMenuItem("Выход", "ctrl+X", null, e -> {
            System.exit(0);
        }));

        JMenu menuView = new JMenu("Вид");
        menuBarMain.add(menuView);
        menuView.add(createMenuItem("Подогнать размер окна", null, null, e -> {
            updateWindowSize();
        }));
        menuView.addSeparator();
        SwingUtils.initLookAndFeelMenu(menuView);

        JMenu menuHelp = new JMenu("Справка");
        menuBarMain.add(menuHelp);
        menuHelp.add(createMenuItem("Правила", "ctrl+R", null, e -> {
            SwingUtils.showInfoMessageBox("Эта головоломка-пазл состоит из 144-х маленьких цветных квадратиков, \nупакованных в один большой квадрат со стороной 12. \n Задача игрока — за минимальное число шагов (ходов) перекрасить большой квадрат \nв любой из шести доступных цветов. Набор этих цветов, \nкаждый из которых является кнопкой, находится внизу под квадратом. \nПерекраска начинается с левого верхнего угла, то есть с самого первого маленького квадратика.\n" +
                    "\n" +
                    "Начнем сначала. Итак, перед Вами тот самый большой квадрат, \nсостоящий из множества мелких шестицветной цветовой палитры.\n" +
                    "\n" +
                    "Вне зависимости от цвета левого верхнего квадратика, изучите его окружение. \nПод окружением следует понимать соседние квадратики, касающиеся \nбазового любой из сторон (касание углом — не в счет!). Если Вы обнаружите, \nчто в окружении превалирует, скажем, красный цвет, то \nжмите внизу в палитре на красный цвет. Исходный квадратик присоединит \nк себе все красные из окружения, и сам станет красным. Таким образом, \nВы получите уже область, состоящую из нескольких квадратиков.\n" +
                    "\n" +
                    "Исследуя по аналогии дальнейшее окружение (только теперь уже красной области), \nвыбирайте следующий цвет для присоединения. И так далее. \nПостарайтесь перекрасить все за меньшее число ходов.\n" +
                    "\n" +
                    "К сожалению, в этой головоломке всего один уровень, \nно к ней можно возвращаться снова и снова, стараясь побить магический рекорд 13, \nи перекрасить квадрат, например за 12 шагов.", "Правила");
        }));
        menuHelp.add(createMenuItem("О программе", "ctrl+A", null, e -> {
            SwingUtils.showInfoMessageBox(
                    "Реализация игры \"ПЕРЕКРАСИТЬ\"" +
                            "\n\nАвтор: Бутовецкая С.И." +
                            "\nE-mail: s.butovetskaya@gmail.com",
                    "О программе"
            );
        }));

        return menuBarMain;
    }

    private void updateWindowSize() {
        int menuSize = this.getJMenuBar() != null ? this.getJMenuBar().getHeight() : 0;
        SwingUtils.setFixedSize(
                this,
                tableGameField.getWidth() + 2 * DEFAULT_GAP ,
                tableGameField.getHeight() + tableColor.getHeight() + labelStatus.getHeight() +
                        menuSize + 2 * DEFAULT_GAP + (int)(DEFAULT_CELL_SIZE * 3.7)
        );
        this.setMaximumSize(null);
        this.setMinimumSize(null);
    }

    private void updateView() {
        labelMaxStep.setText(""+game.getMaxStep());
        tableGameField.repaint();
        labelStep.setText(""+game.getStep());
        if (game.getState() == Game.GameState.PLAYING) {
            labelStatus.setForeground(Color.BLACK);
            labelStatus.setText("Игра идет");
        } else {
            labelStatus.setText("");
            if (game.getState() == Game.GameState.WIN) {
                labelStatus.setText("Победа :-)");
                SwingUtils.showInfoMessageBox("Ваш счёт: "+game.getStep()+" из "+game.getMaxStep()+"\nВы выиграли. Поздравляем!");
                labelStatus.setForeground(Color.RED);
            } else if (game.getState() == Game.GameState.FAIL) {
                labelStatus.setText("Поражение :-(");
                SwingUtils.showInfoMessageBox("К сожалению, вы проиграли. Попробуйте снова! ");
                labelStatus.setForeground(Color.RED);
            }
        }
    }

    private void paintColorCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        Color color = COLORS[game.getCellColor(column).getColor()-1];

        int size = Math.min(cellWidth, cellHeight);
        int bound = (int) Math.round(size * 0.01);

        g2d.setColor(color);
        g2d.fillRect(bound, bound, size, size);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(bound, bound, size, size);
    }

    private void paintCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        Game.Cell cell = game.getCellField(row, column);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cell == null) {
            cell = NULL_CELL;
        }
        int size = Math.min(cellWidth, cellHeight);

        Color backColor = COLORS[cell.getColor()-1];

        g2d.setColor(backColor);
        g2d.fillRect(0, 0, size, size);
    }

    private void newGame() {
        game.newGame(params.getRowCount(), params.getColCount(), params.getColorCount());
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );

        JTableUtils.resizeJTable(tableColor,
                1, DEFAULT_COLOR_COUNT,
                40, 40
        );

        updateView();
    }

}

