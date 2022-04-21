package com.engine2d;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Window {
  
    private JFrame frame; 
    private BufferedImage image;
    private Canvas canvas; 
    private BufferStrategy bufferStrategy;
    private Graphics graphic; 
    
    
    public Window(GameContainer gameContainer){
        image  = new BufferedImage(gameContainer.getWIDTH(), gameContainer.getHEIGHT(), BufferedImage.TYPE_INT_RGB);
        canvas = new Canvas();
        Dimension dimension = new Dimension((int) (gameContainer.getWIDTH() * gameContainer.getSCALE()),(int) (gameContainer.getHEIGHT() * gameContainer.getSCALE()));
        canvas.setPreferredSize(dimension);
        canvas.setMaximumSize(dimension);
        canvas.setMinimumSize(dimension);
        
        frame = new JFrame(gameContainer.getTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        canvas.createBufferStrategy(3);
        bufferStrategy = canvas.getBufferStrategy();
        graphic = bufferStrategy.getDrawGraphics();  
    }
    
    public void update(){
        graphic.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bufferStrategy.show();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }

}
