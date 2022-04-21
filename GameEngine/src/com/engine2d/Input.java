package com.engine2d;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener,MouseListener, MouseMotionListener, MouseWheelListener{
    
    private GameContainer gameContainer;
    
    private final int NUM_KYES = 256;
    private boolean[] keys = new boolean[NUM_KYES];
    private boolean[] keysLast = new boolean[NUM_KYES];
    
    private final int NUM_BUTTONS = 5;
    private boolean[] buttons = new boolean[NUM_BUTTONS];
    private boolean[] buttonsLast = new boolean[NUM_BUTTONS];
    
    private int mouseX, mouseY;
    private int scroll;
    
    public Input(GameContainer gameContainer){
        this.gameContainer = gameContainer;
        mouseX = 0;
        mouseY = 0;
        scroll = 0;
        
        gameContainer.getWindow().getCanvas().addKeyListener(this);
        gameContainer.getWindow().getCanvas().addMouseListener(this);
        gameContainer.getWindow().getCanvas().addMouseMotionListener(this);
        gameContainer.getWindow().getCanvas().addMouseWheelListener(this);
     
    }
    
    public void update(){
        scroll = 0;
        
        for (int i = 0; i < NUM_KYES; i++) {
            keysLast[i] = keys[i];
        }
        
        for (int i = 0; i < NUM_BUTTONS; i++) {
            buttonsLast[i] = buttons[i];
        }
    }
    
    public boolean isKey(int keyCode){
        return keys[keyCode];
    }
    
    public boolean isKeyUp(int keyCode){
        return !keys[keyCode] && keysLast[keyCode];
    }
    
    public boolean isKeyDown(int keyCode){
        return keys[keyCode] && keysLast[keyCode];
    }
    
    public boolean isButton(int buttonCode){
        return buttons[buttonCode];
    }
    
    public boolean isButtonUp(int buttonCode){
        return !buttons[buttonCode] && buttonsLast[buttonCode];
    }
    
    public boolean isButtonDown(int buttonCode){
        return buttons[buttonCode] && buttonsLast[buttonCode];
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        buttons[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        buttons[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = (int) (e.getX() / gameContainer.getSCALE());
        mouseY = (int) (e.getY() / gameContainer.getSCALE());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = (int) (e.getX() / gameContainer.getSCALE());
        mouseY = (int) (e.getY() / gameContainer.getSCALE());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scroll = e.getWheelRotation();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getScroll() {
        return scroll;
    }

}
