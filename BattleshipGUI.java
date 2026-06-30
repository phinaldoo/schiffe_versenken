import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BattleshipGUI extends JFrame {
    // Colors
    public static final Color OCEAN_DARK = new Color(15, 32, 56);
    public static final Color OCEAN_MEDIUM = new Color(22, 54, 92);
    public static final Color OCEAN_LIGHT = new Color(32, 78, 135);
    public static final Color WATER_COLOR = new Color(47, 109, 176);
    public static final Color WATER_HOVER = new Color(64, 133, 198);
    public static final Color SHIP_COLOR = new Color(85, 98, 112);
    public static final Color SHIP_HOVER = new Color(105, 118, 132);
    public static final Color HIT_COLOR = new Color(220, 53, 69);
    public static final Color HIT_GLOW = new Color(255, 100, 100);
    public static final Color MISS_COLOR = new Color(108, 117, 125);
    public static final Color SUNK_COLOR = new Color(139, 0, 0);
    public static final Color ACCENT_COLOR = new Color(0, 188, 212);
    public static final Color ACCENT_HOVER = new Color(38, 198, 218);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color TEXT_PRIMARY = new Color(236, 240, 241);
    public static final Color TEXT_SECONDARY = new Color(189, 195, 199);
    public static final Color PANEL_BG = new Color(25, 42, 68);
    public static final Color CARD_BG = new Color(35, 55, 85);
    
    // Fonts
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    public static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 19);
    public static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Screens
    private WelcomePanel welcomePanel;
    private SetupPanel setupPanel;
    private GamePanel gamePanel;
    private ResultPanel resultPanel;
    
    // Game state
    private GAME game;
    private String player1Name;
    private String player2Name;
    private List<ShipType> fleetDefinition;
    private int boardSize = 10;
    
    public BattleshipGUI() {
        setTitle("Schiffe Versenken");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setPreferredSize(new Dimension(1400, 900));
        
        // Setup main card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(OCEAN_DARK);
        
        // Initialize fleet
        fleetDefinition = defaultFleet();
        
        // Create screens
        welcomePanel = new WelcomePanel(this);
        setupPanel = new SetupPanel(this);
        gamePanel = new GamePanel(this);
        resultPanel = new ResultPanel(this);
        
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(setupPanel, "setup");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(resultPanel, "result");
        
        add(mainPanel);
        
        // Center on screen
        pack();
        setLocationRelativeTo(null);
        
        // Start with welcome screen
        showWelcome();
    }
    
    private List<ShipType> defaultFleet() {
        List<ShipType> fleet = new ArrayList<>();
        fleet.add(new ShipType("Schlachtschiff", 5, 1));
        fleet.add(new ShipType("Zerstörer", 4, 1));
        fleet.add(new ShipType("U-Boot", 3, 2));
        fleet.add(new ShipType("Patrouillenboot", 2, 1));
        return fleet;
    }
    
    public void showWelcome() {
        cardLayout.show(mainPanel, "welcome");
    }
    
    public void startGame(String p1Name, String p2Name) {
        String validatedP1Name = validatePlayerName(p1Name, "Spieler 1");
        String validatedP2Name = validatePlayerName(p2Name, "Spieler 2");
        if (validatedP1Name.equalsIgnoreCase(validatedP2Name)) {
            throw new IllegalArgumentException("Player names must be different.");
        }

        this.player1Name = validatedP1Name;
        this.player2Name = validatedP2Name;
        this.game = new GAME(player1Name, player2Name, boardSize, fleetDefinition);
        setupPanel.startSetup(0);
        cardLayout.show(mainPanel, "setup");
    }

    private String validatePlayerName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " name must not be empty.");
        }
        return name.trim();
    }
    
    public void setupComplete(int playerIndex) {
        if (playerIndex == 0) {
            // Show transition, then setup player 2
            showTransitionScreen(player2Name + " ist an der Reihe", "", () -> {
                setupPanel.startSetup(1);
            });
        } else {
            // Both players ready, start the game
            showTransitionScreen("Beide Spieler bereit!", 
                game.getActivePlayerName() + " beginnt das Spiel!", () -> {
                gamePanel.startGame();
                cardLayout.show(mainPanel, "game");
            });
        }
    }
    
    private void showTransitionScreen(String message, String subMessage, Runnable onComplete) {
        JDialog transition = new JDialog(this, true);
        transition.setUndecorated(true);
        transition.setBounds(getBounds());
        transition.setBackground(OCEAN_DARK);
        
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, OCEAN_DARK, 0, getHeight(), OCEAN_MEDIUM);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        overlayPanel.setLayout(new GridBagLayout());
        overlayPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
        
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(TITLE_FONT);
        msgLabel.setForeground(TEXT_PRIMARY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subLabel = new JLabel(subMessage);
        subLabel.setFont(HEADER_FONT);
        subLabel.setForeground(TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ModernButton continueBtn = new ModernButton("Weiter", ACCENT_COLOR);
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.addActionListener(e -> {
            transition.dispose();
            SwingUtilities.invokeLater(onComplete);
        });
        
        JPanel messageCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, CARD_BG, 0, getHeight(), new Color(30, 50, 75));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

                g2d.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 140));
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 28, 28);
                g2d.dispose();
            }
        };
        messageCard.setOpaque(false);
        messageCard.setLayout(new BoxLayout(messageCard, BoxLayout.Y_AXIS));
        messageCard.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        messageCard.setPreferredSize(new Dimension(520, 320));
        messageCard.setMaximumSize(new Dimension(520, 320));

        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageCard.add(Box.createVerticalGlue());
        messageCard.add(msgLabel);
        messageCard.add(Box.createRigidArea(new Dimension(0, 15)));
        messageCard.add(subLabel);
        messageCard.add(Box.createRigidArea(new Dimension(0, 40)));
        messageCard.add(continueBtn);
        messageCard.add(Box.createVerticalGlue());
        
        overlayPanel.add(messageCard);
        transition.add(overlayPanel);
        transition.setVisible(true);
    }
    
    public void endTurn() {
        showTransitionScreen("Zug beendet", 
            game.getActivePlayerName() + " ist am Zug", () -> {
            gamePanel.refreshBoards();
        });
    }
    
    public void showGameOver(String winner) {
        resultPanel.showResult(winner);
        cardLayout.show(mainPanel, "result");
    }
    
    public void newGame() {
        game = null;
        player1Name = null;
        player2Name = null;
        setupPanel.resetState();
        gamePanel.resetState();
        resultPanel.stopCelebration();
        showWelcome();
    }
    
    public void showSettingsDialog() {
        JDialog dialog = createSettingsDialog();
        dialog.setVisible(true);
    }
    
    public void leaveCurrentGame() {
        newGame();
    }
    
    public void closeGame() {
        dispose();
        System.exit(0);
    }
    
    public GAME getGame() {
        return game;
    }
    
    public List<ShipType> getFleetDefinition() {
        return fleetDefinition;
    }
    
    public int getBoardSize() {
        return boardSize;
    }
    
    private JDialog createSettingsDialog() {
        final int dialogWidth = 560;
        final int dialogHeight = 470;
        final int dialogCornerRadius = 28;

        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialogWidth, dialogHeight, dialogCornerRadius, dialogCornerRadius));
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, OCEAN_DARK, getWidth(), getHeight(), OCEAN_MEDIUM);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                
                g2d.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 130));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 28, 28);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 34, 26, 34));
        
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Einstellungen");
        titleLabel.setFont(TITLE_FONT.deriveFont(30f));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton closeButton = createDialogCloseButton();
        closeButton.addActionListener(e -> dialog.dispose());
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);
        
        ModernButton leaveButton = new ModernButton("Spiel verlassen", WARNING_COLOR);
        leaveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaveButton.setPreferredSize(new Dimension(320, 52));
        leaveButton.addActionListener(e -> {
            dialog.dispose();
            leaveCurrentGame();
        });
        
        ModernButton closeGameButton = new ModernButton("Spiel schließen", HIT_COLOR);
        closeGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeGameButton.setPreferredSize(new Dimension(320, 52));
        closeGameButton.addActionListener(e -> {
            dialog.dispose();
            closeGame();
        });
        
        ModernButton resumeButton = new ModernButton("Zurück", ACCENT_COLOR);
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.setPreferredSize(new Dimension(220, 50));
        resumeButton.addActionListener(e -> dialog.dispose());
        
        panel.add(titleBar);
        panel.add(Box.createRigidArea(new Dimension(0, 36)));
        panel.add(leaveButton);
        panel.add(Box.createRigidArea(new Dimension(0, 28)));
        panel.add(closeGameButton);
        panel.add(Box.createRigidArea(new Dimension(0, 28)));
        panel.add(resumeButton);
        panel.add(Box.createVerticalGlue());
        
        dialog.setContentPane(panel);
        dialog.getRootPane().setOpaque(false);
        dialog.getRootPane().setDefaultButton(resumeButton);
        dialog.getRootPane().registerKeyboardAction(
            e -> dialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        return dialog;
    }
    
    private JButton createDialogCloseButton() {
        JButton button = new JButton("x");
        button.setFont(HEADER_FONT);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }

        setGlobalUIFont(BattleshipGUI.LABEL_FONT);
        
        SwingUtilities.invokeLater(() -> {
            BattleshipGUI gui = new BattleshipGUI();
            gui.setVisible(true);
        });
    }

    private static void setGlobalUIFont(Font font) {
        FontUIResource resource = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, resource);
            }
        }
    }
}
