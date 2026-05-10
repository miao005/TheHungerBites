package inputs;

import main.GamePanel;
import main.GameState;
import ui.NavButtons;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInputs implements MouseListener, MouseMotionListener {
    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        // Key listener for name entry on game over screen
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (gamePanel.getGameState() == GameState.GAME_OVER)
                    gamePanel.getGameOverScreen().keyTyped(e.getKeyChar());
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        System.out.println("Mouse clicked at: " + mx + ", " + my);

        // Always request focus so key events work
        gamePanel.requestFocusInWindow();

        switch (gamePanel.getGameState()) {
            case START:            gamePanel.getStartScreen().mouseClicked(); break;
            case MENU:             gamePanel.getMenuScreen().mouseClicked(mx, my); break;
            case CHARACTER_SELECT: gamePanel.getCharacterSelectScreen().mouseClicked(mx, my); break;
            case BATTLE:           gamePanel.getBattleScreen().mouseClicked(mx, my); break;
            case ARCADE_REWARD:    gamePanel.getArcadeRewardScreen().mouseClicked(mx, my); break;
            case SETTINGS:         gamePanel.getSettingsScreen().mouseClicked(mx, my); break;
            case LEADERBOARD:      gamePanel.getLeaderboardScreen().mouseClicked(mx, my); break;
            case CREDITS:          gamePanel.getCreditsScreen().mouseClicked(mx, my); break;
            case GAME_OVER:        gamePanel.getGameOverScreen().mouseClicked(mx, my); break;
            default: break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();

        // Nav buttons hover — always check regardless of screen
        boolean navChanged = NavButtons.handleHover(mx, my);
        if (navChanged) gamePanel.repaint();

        switch (gamePanel.getGameState()) {
            case MENU:             gamePanel.getMenuScreen().mouseMoved(mx, my); break;
            case CHARACTER_SELECT: gamePanel.getCharacterSelectScreen().mouseMoved(mx, my); break;
            case BATTLE:           gamePanel.getBattleScreen().mouseMoved(mx, my); break;
            case ARCADE_REWARD:    gamePanel.getArcadeRewardScreen().mouseMoved(mx, my); break;
            case SETTINGS:         gamePanel.getSettingsScreen().mouseMoved(mx, my); break;
            case LEADERBOARD:      gamePanel.getLeaderboardScreen().mouseMoved(mx, my); break;
            case GAME_OVER:        gamePanel.getGameOverScreen().mouseMoved(mx, my); break;
            default: break;
        }
    }

    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}
    @Override public void mouseDragged(MouseEvent e)  {}
}