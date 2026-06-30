import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SetupPanel extends JPanel {

    private BattleshipGUI parent;
    private BoardPanel boardPanel;
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JLabel shipInfoLabel;
    private JPanel shipListPanel;
    private ModernButton rotateButton;
    private ModernButton randomButton;
    private ModernButton confirmButton;
    private SettingsIconButton settingsButton;

    private int currentPlayerIndex;
    private GRID currentGrid;
    private Orientation currentOrientation = Orientation.HORIZONTAL;
    private List<ShipType> fleet;
    private List<PlacementTarget> placementTargets;
    private int currentPlacementIndex;

    public SetupPanel(BattleshipGUI parent) {
        this.parent = parent;
        this.fleet = parent.getFleetDefinition();
        this.placementTargets = buildPlacementTargets();
        this.currentPlacementIndex = 0;

        setLayout(new BorderLayout(20, 20));
        setBackground(BattleshipGUI.OCEAN_DARK);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        titleLabel = new JLabel("Schiffe platzieren");
        titleLabel.setFont(BattleshipGUI.TITLE_FONT);
        titleLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);

        instructionLabel = new JLabel("Klicke auf das Spielfeld um dein Schiff zu platzieren");
        instructionLabel.setFont(BattleshipGUI.LABEL_FONT);
        instructionLabel.setForeground(BattleshipGUI.TEXT_SECONDARY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        settingsButton = new SettingsIconButton();
        settingsButton.addActionListener(e -> parent.showSettingsDialog());

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(instructionLabel);

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(settingsButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        boardPanel = new BoardPanel(parent.getBoardSize(), false);
        boardPanel.setInteractive(true);
        boardPanel.setClickListener((row, col) -> handleCellClick(row, col));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(boardPanel, gbc);

        JPanel infoPanel = createShipInfoPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(infoPanel, gbc);

        return panel;
    }

    private JPanel createShipInfoPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BattleshipGUI.CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(BattleshipGUI.OCEAN_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        panel.setPreferredSize(new Dimension(280, 400));

        JLabel shipTitle = new JLabel("Aktuelles Schiff");
        shipTitle.setFont(BattleshipGUI.HEADER_FONT);
        shipTitle.setForeground(BattleshipGUI.TEXT_PRIMARY);
        shipTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        shipInfoLabel = new JLabel("Warte...");
        shipInfoLabel.setFont(BattleshipGUI.LABEL_FONT);
        shipInfoLabel.setForeground(BattleshipGUI.ACCENT_COLOR);
        shipInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rotateButton = new ModernButton("Drehen (R)", BattleshipGUI.OCEAN_LIGHT);
        rotateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rotateButton.addActionListener(e -> rotateShip());

        JLabel listTitle = new JLabel("Flotte");
        listTitle.setFont(BattleshipGUI.HEADER_FONT);
        listTitle.setForeground(BattleshipGUI.TEXT_PRIMARY);
        listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        shipListPanel = new JPanel();
        shipListPanel.setLayout(new BoxLayout(shipListPanel, BoxLayout.Y_AXIS));
        shipListPanel.setOpaque(false);
        shipListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(shipTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(shipInfoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(rotateButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(listTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(shipListPanel);
        panel.add(Box.createVerticalGlue());

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "rotate");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('R'), "rotate");
        getActionMap().put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateShip();
            }
        });

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        randomButton = new ModernButton("Zufällig platzieren", BattleshipGUI.WARNING_COLOR);
        randomButton.setPreferredSize(new Dimension(260, 52));
        randomButton.addActionListener(e -> placeRandomly());

        confirmButton = new ModernButton("Bestätigen", BattleshipGUI.SUCCESS_COLOR);
        confirmButton.setPreferredSize(new Dimension(220, 52));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> confirmSetup());

        panel.add(randomButton);
        panel.add(confirmButton);

        return panel;
    }

    public void startSetup(int playerIndex) {
        this.currentPlayerIndex = playerIndex;
        this.currentGrid = parent.getGame().getPlayerGrid(playerIndex);
        this.currentOrientation = Orientation.HORIZONTAL;
        this.placementTargets = buildPlacementTargets();
        this.currentPlacementIndex = 0;

        titleLabel.setText("Schiffe platzieren - " + currentGrid.getUsername());
        boardPanel.setGrid(currentGrid);

        updateShipInfo();
        updateShipList();
    }

    private void updateShipInfo() {
        boolean allPlaced = allShipsPlaced();

        if (!allPlaced && currentPlacementIndex < placementTargets.size()) {
            PlacementTarget target = placementTargets.get(currentPlacementIndex);
            shipInfoLabel.setText(String.format("%s (Länge: %d)", target.getShipName(), target.getLength()));
            boardPanel.setPlacementMode(true, target.getLength(), currentOrientation);
            rotateButton.setEnabled(true);
        } else {
            shipInfoLabel.setText("Alle Schiffe platziert!");
            boardPanel.setPlacementMode(false, 0, currentOrientation);
            rotateButton.setEnabled(false);
        }

        confirmButton.setEnabled(allPlaced);
        updateInstructionLabel();
    }

    private void updateInstructionLabel() {
        if (allShipsPlaced()) {
            instructionLabel.setText("Klicke ein Schiff zum Verschieben oder bestätige");
            return;
        }

        String orientText = currentOrientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertikal";
        instructionLabel.setText("Ausrichtung: " + orientText + " - Klicke zum Platzieren");
    }

    private void updateShipList() {
        shipListPanel.removeAll();

        for (int i = 0; i < placementTargets.size(); i++) {
            PlacementTarget target = placementTargets.get(i);
            boolean isCurrent = (i == currentPlacementIndex && !target.isPlaced());

            JLabel shipLabel = new JLabel(String.format("%s %s (x%d)",
                target.isPlaced() ? "OK" : (isCurrent ? ">" : "o"),
                target.getShipName(),
                target.getLength()));
            shipLabel.setFont(BattleshipGUI.SMALL_FONT);
            shipLabel.setForeground(target.isPlaced() ? BattleshipGUI.SUCCESS_COLOR :
                (isCurrent ? BattleshipGUI.ACCENT_COLOR : BattleshipGUI.TEXT_SECONDARY));
            shipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            shipLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

            shipListPanel.add(shipLabel);
        }

        shipListPanel.revalidate();
        shipListPanel.repaint();
    }

    private void handleCellClick(int row, int col) {
        if (currentGrid == null) {
            return;
        }

        Coordinate coordinate = new Coordinate(row, col);

        if (allShipsPlaced() && currentGrid.hasShipAt(coordinate)) {
            pickUpShipForEditing(coordinate);
            return;
        }

        if (allShipsPlaced() || currentPlacementIndex >= placementTargets.size()) {
            return;
        }

        if (currentGrid.hasShipAt(coordinate)) {
            return;
        }

        placeCurrentShip(coordinate);
    }

    private void pickUpShipForEditing(Coordinate coordinate) {
        SHIP ship = currentGrid.getShipAt(coordinate);
        if (ship == null) {
            return;
        }

        int placementIndex = findPlacementIndexByName(ship.getName());
        if (placementIndex < 0) {
            return;
        }

        if (currentGrid.removeShip(ship.getName())) {
            placementTargets.get(placementIndex).setPlaced(false);
            currentPlacementIndex = placementIndex;
            boardPanel.repaint();
            updateShipInfo();
            updateShipList();
        }
    }

    private void placeCurrentShip(Coordinate start) {
        PlacementTarget target = placementTargets.get(currentPlacementIndex);

        try {
            if (currentGrid.canPlaceShip(start, currentOrientation, target.getLength())) {
                currentGrid.placeShip(target.getShipName(), start, currentOrientation, target.getLength());
                target.setPlaced(true);
                currentPlacementIndex = findNextUnplacedIndex();

                boardPanel.repaint();
                updateShipInfo();
                updateShipList();
            }
        } catch (IllegalArgumentException e) {
            // Invalid placement, ignore.
        }
    }

    private void rotateShip() {
        if (allShipsPlaced()) {
            return;
        }

        currentOrientation = (currentOrientation == Orientation.HORIZONTAL) ?
            Orientation.VERTICAL : Orientation.HORIZONTAL;
        boardPanel.setPlacementOrientation(currentOrientation);
        updateInstructionLabel();
    }

    private void placeRandomly() {
        long seed = System.nanoTime() + currentPlayerIndex * 97L;
        if (currentGrid == null) {
            return;
        }

        if (allShipsPlaced()) {
            return;
        }

        Random random = new Random(seed);
        List<PlacementTarget> addedTargets = new ArrayList<PlacementTarget>();

        for (PlacementTarget target : placementTargets) {
            if (target.isPlaced()) {
                continue;
            }
            boolean placed = currentGrid.placeShipRandomly(target.getShipName(), target.getLength(), random, 10_000);
            if (!placed) {
                rollbackRandomPlacement(addedTargets);
                JOptionPane.showMessageDialog(
                    this,
                    "Die restlichen Schiffe konnten auf Basis der aktuellen Platzierung nicht automatisch ergänzt werden.",
                    "Automatisches Platzieren nicht möglich",
                    JOptionPane.WARNING_MESSAGE
                );
                boardPanel.repaint();
                updateShipInfo();
                updateShipList();
                return;
            }
            target.setPlaced(true);
            addedTargets.add(target);
        }

        currentPlacementIndex = placementTargets.size();

        boardPanel.repaint();
        updateShipInfo();
        updateShipList();
    }

    private void confirmSetup() {
        parent.setupComplete(currentPlayerIndex);
    }

    public void resetState() {
        currentGrid = null;
        currentOrientation = Orientation.HORIZONTAL;
        placementTargets = buildPlacementTargets();
        currentPlacementIndex = 0;
        titleLabel.setText("Schiffe platzieren");
        instructionLabel.setText("Klicke auf das Spielfeld um dein Schiff zu platzieren");
        shipInfoLabel.setText("Warte...");
        boardPanel.setGrid(null);
        boardPanel.setPlacementMode(false, 0, currentOrientation);
        rotateButton.setEnabled(false);
        confirmButton.setEnabled(false);
        shipListPanel.removeAll();
        shipListPanel.revalidate();
        shipListPanel.repaint();
    }

    private List<PlacementTarget> buildPlacementTargets() {
        List<PlacementTarget> targets = new ArrayList<PlacementTarget>();
        for (ShipType type : fleet) {
            for (int shipNumber = 1; shipNumber <= type.getCount(); shipNumber++) {
                targets.add(new PlacementTarget(type, shipNumber));
            }
        }
        return targets;
    }

    private int findNextUnplacedIndex() {
        for (int i = 0; i < placementTargets.size(); i++) {
            if (!placementTargets.get(i).isPlaced()) {
                return i;
            }
        }
        return placementTargets.size();
    }

    private int findPlacementIndexByName(String shipName) {
        for (int i = 0; i < placementTargets.size(); i++) {
            if (placementTargets.get(i).getShipName().equals(shipName)) {
                return i;
            }
        }
        return -1;
    }

    private boolean allShipsPlaced() {
        return findNextUnplacedIndex() >= placementTargets.size();
    }

    private void rollbackRandomPlacement(List<PlacementTarget> addedTargets) {
        for (PlacementTarget target : addedTargets) {
            currentGrid.removeShip(target.getShipName());
            target.setPlaced(false);
        }
        currentPlacementIndex = findNextUnplacedIndex();
    }

    private static final class PlacementTarget {
        private final ShipType shipType;
        private final int shipNumber;
        private boolean placed;

        private PlacementTarget(ShipType shipType, int shipNumber) {
            this.shipType = shipType;
            this.shipNumber = shipNumber;
            this.placed = false;
        }

        private String getShipName() {
            return shipType.getName() + " #" + shipNumber;
        }

        private int getLength() {
            return shipType.getLength();
        }

        private boolean isPlaced() {
            return placed;
        }

        private void setPlaced(boolean placed) {
            this.placed = placed;
        }
    }
}
