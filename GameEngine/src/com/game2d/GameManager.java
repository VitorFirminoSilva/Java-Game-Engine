package com.game2d;

import com.audio.SoundClip;
import com.engine2d.AbstractGame;
import com.engine2d.GameContainer;
import com.engine2d.Renderer;
import com.engine2d.gfx.Image;
import com.engine2d.gfx.ImageTile;
import java.awt.event.KeyEvent;

public class GameManager extends AbstractGame{

    private ImageTile image;
    private SoundClip clip;
    
    float temp = 0;
    
    public GameManager(){
      image = new ImageTile("/spriteSheet.png", 32, 21); 
      clip = new SoundClip("/audio/testeSound.wav"); 
      clip.setVolume(-10);
    }
    
    @Override
    public void update(GameContainer gameContainer, float dt) {
       
        if(gameContainer.getInput().isKeyDown(KeyEvent.VK_A)){
            clip.play();
        }
        
        temp += dt * 4;
        
        if(temp > 12){
            temp = 0;
        }
        
    }

    @Override
    public void render(GameContainer gameContainer, Renderer renderer) {
        renderer.drawImageTile(image, gameContainer.getInput().getMouseX(), gameContainer.getInput().getMouseY(), (int)temp, 0);
        renderer.fillRect(10, 10, 16, 16, 0xffffccff);
    }
    
    public static void main(String args[]){
        GameContainer gameContainer = new GameContainer(new GameManager());
        gameContainer.start();
    }
    
}
