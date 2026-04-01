package inputs;

import main.GamePanel;
import main.GameState;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInputs implements MouseListener, MouseMotionListener {
    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        System.out.println("Mouse clicked at: " + mouseX + ", " + mouseY);

        switch (gamePanel.getGameState()) {
            case START:
                gamePanel.getStartScreen().mouseClicked();
                break;
            case MENU:
                gamePanel.getMenuScreen().mouseClicked(mouseX, mouseY);
                break;
            case CHARACTER_SELECT:
                gamePanel.getCharacterSelectScreen().mouseClicked(mouseX, mouseY);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        switch (gamePanel.getGameState()) {
            case MENU:
                gamePanel.getMenuScreen().mouseMoved(mouseX, mouseY);
                break;
            case CHARACTER_SELECT:
                gamePanel.getCharacterSelectScreen().mouseMoved(mouseX, mouseY);
                break;
            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
}