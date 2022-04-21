package com.engine2d;

public class GameContainer implements Runnable {
    
    private Thread thread;
    private Window window;
    private Renderer renderer;
    private Input input;
    private AbstractGame game;
    
    private boolean running = false;
    private final double UPDATE_CAP = 1.0/60.0;
    
    private int WIDTH = 320, HEIGHT = 240;
    private float SCALE = 2f;
    private String title = "2DGame Window";
    
    public GameContainer(AbstractGame game){
        this.game = game;
    }

    public void start(){
        
        window = new Window(this);
        renderer = new Renderer(this);
        input  = new Input(this);
        thread = new Thread(this);
        thread.run();
    }
    
    public void stop(){
        
    }
    
    @Override
    public void run(){
       running = true;
       
       boolean render  = false;
       double firstTime = 0;
       double lastTime = System.nanoTime() / 1000000000.0;
       double passedTime = 0;
       double unprocessedTime = 0;
       
       double frameTime = 0;
       int frames = 0;
       int fps = 0;
       
       while(running){
           
           render = false;
           
           firstTime = System.nanoTime() / 1000000000.0;
           passedTime = firstTime - lastTime;
           lastTime = firstTime;
           
           unprocessedTime += passedTime;
           frameTime += passedTime;
           
           while(unprocessedTime >= UPDATE_CAP){
               unprocessedTime -= UPDATE_CAP;
               render = true;
               
               game.update(this, (float)UPDATE_CAP);
               input.update();
               
               if(frameTime >= 1.0){
                   frameTime = 0;
                   fps = frames;
                   frames = 0;
               }
               
               //TODO: Update Game
           }
           
           if(render){
               
               renderer.clear();
               game.render(this, renderer);
               renderer.process();
               renderer.drawText("FPS: " + fps, 0, 0, 0xff00ffff);
               window.update();
               frames++;
               
               //TODO: Render Game
           }else{
               try {
                   Thread.sleep(1);
               } catch (InterruptedException ex) {
                   ex.printStackTrace();
               }
           }
       }
       
       dispose();
    }
    
    public void dispose(){
        
    }
    
     public int getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(int WIDTH) {
        this.WIDTH = WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(int HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public float getSCALE() {
        return SCALE;
    }

    public void setSCALE(float SCALE) {
        this.SCALE = SCALE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }  

    public Input getInput() {
        return input;
    }
 
}
