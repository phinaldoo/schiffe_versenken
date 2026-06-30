import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class SettingsIconButton extends JButton {
    private boolean hovered = false;
    private boolean pressed = false;

    public SettingsIconButton() {
        setToolTipText("Einstellungen");
        setPreferredSize(new Dimension(40, 40));
        setMinimumSize(new Dimension(40, 40));
        setMaximumSize(new Dimension(40, 40));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int iconSize = 20;
        int x = (getWidth() - iconSize) / 2;
        int y = (getHeight() - iconSize) / 2;

        Color iconColor = BattleshipGUI.TEXT_PRIMARY;
        if (!isEnabled()) {
            iconColor = BattleshipGUI.TEXT_SECONDARY;
        } else if (pressed) {
            iconColor = BattleshipGUI.ACCENT_HOVER;
        } else if (hovered) {
            iconColor = BattleshipGUI.ACCENT_COLOR;
        }

        drawGear(g2d, x, y, iconSize, iconColor);
        g2d.dispose();
    }

    private void drawGear(Graphics2D g2d, int x, int y, int size, Color color) {
        double cx = x + size / 2.0;
        double cy = y + size / 2.0;
        double outerRadius = size * 0.32;
        double innerRadius = size * 0.16;
        double toothWidth = size * 0.10;
        double toothHeight = size * 0.14;

        g2d.setColor(color);

        for (int i = 0; i < 8; i++) {
            AffineTransform oldTransform = g2d.getTransform();
            g2d.rotate(Math.toRadians(i * 45), cx, cy);
            Shape tooth = new RoundRectangle2D.Double(
                cx - toothWidth / 2.0,
                cy - outerRadius - toothHeight + 1,
                toothWidth,
                toothHeight,
                toothWidth,
                toothWidth
            );
            g2d.fill(tooth);
            g2d.setTransform(oldTransform);
        }

        g2d.fill(new Ellipse2D.Double(cx - outerRadius, cy - outerRadius, outerRadius * 2, outerRadius * 2));
        g2d.setColor(BattleshipGUI.OCEAN_DARK);
        g2d.fill(new Ellipse2D.Double(cx - innerRadius, cy - innerRadius, innerRadius * 2, innerRadius * 2));
    }
}
