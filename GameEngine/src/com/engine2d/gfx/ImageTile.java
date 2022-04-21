package com.engine2d.gfx;

public class ImageTile extends Image{
    
    private int tileWidth, tileHeight;
    
    public ImageTile(String path, int tileWidth, int tileHeight){
        super(path);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }
    
    public Image getTileImage(int tileX, int tileY){
        Image res;
        int[] pixels = new int[tileWidth * tileHeight];
        
        for(int y = 0; y < tileHeight; y++){
            for(int x = 0; x < tileWidth; x++){
                pixels[x + y * tileWidth] = this.getPixels()[(x + tileX * tileWidth) + (y + tileY * tileHeight) * this.getWidth()];
            }
        }
 
        return new Image(pixels, tileWidth, tileHeight);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }  
}
