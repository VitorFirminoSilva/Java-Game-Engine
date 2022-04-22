package com.game2d;

import com.audio.SoundClip;
import com.engine2d.AbstractGame;
import com.engine2d.GameContainer;
import com.engine2d.Renderer;
import com.engine2d.gfx.Image;
import com.engine2d.gfx.ImageTile;
import com.engine2d.gfx.Light;
import java.awt.event.KeyEvent;

public class GameManager extends AbstractGame{

    private Image image;
    private Image image2;
    private SoundClip clip;
    private Light light;
    
    float temp = 0;
    
    public GameManager(){
      image = new Image("/box.png");
      image.setAlpha(true);
      image.setLightBlock(Light.FULL);
      image2 = new Image("/background.png");
      image2.setAlpha(true);
      clip = new SoundClip("/audio/testeSound.wav"); 
      clip.setVolume(-10);
      
      light = new Light(100, 0xff00ff00);
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
        
        renderer.setzDepth(0);
        renderer.drawImage(image2,0,0);
        renderer.drawImage(image, 100, 100);
        renderer.drawLight(light, gameContainer.getInput().getMouseX(), gameContainer.getInput().getMouseY());

    }
    
    public static void main(String args[]){
        GameContainer gameContainer = new GameContainer(new GameManager());
        gameContainer.start();
    }
    
}
