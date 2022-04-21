package com.engine2d;


import com.engine2d.gfx.Font;
import com.engine2d.gfx.Image;
import com.engine2d.gfx.ImageRequest;
import com.engine2d.gfx.ImageTile;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Renderer {
    
    private Font font = Font.STANDARD;
    private ArrayList<ImageRequest> imageRequest = new ArrayList<ImageRequest>();
    
    private int pixelW, pixelH;
    private int[] pixels;
    private int[] zBuffer;
    
    private int zDepth = 0;
    
    private boolean processing = false;
    
    
    
    public Renderer(GameContainer gameContainer){
        pixelW = gameContainer.getWIDTH();
        pixelH = gameContainer.getHEIGHT();
        pixels = ((DataBufferInt) gameContainer.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zBuffer = new int[pixels.length];
    }
    
    public void clear(){
        for (int i = 0; i < pixels.length; i++) {
           pixels[i] = 0;
           zBuffer[i] = 0;
        }
    }
    
    public void process(){
        processing = true;
        
        Collections.sort(imageRequest, new Comparator<ImageRequest>(){
            @Override
            public int compare(ImageRequest i0, ImageRequest i1) {

                if(i0.getzDepth() < i1.getzDepth())
                    return -1;
                if(i0.getzDepth() > i1.getzDepth())
                    return 1;
                return 0;
            }
            
        });
        
        for (ImageRequest imageRQ : imageRequest) {
            setzDepth(imageRQ.getzDepth());
            drawImage(imageRQ.getImage(), imageRQ.getOffX(), imageRQ.getOffY());
        }
        imageRequest.clear();
        processing = false;
    }
    
    public void setPixel(int x, int y, int value){
        
        int alpha = ((value >> 24) & 0xff);
        
        if(x < 0 || x >= pixelW || y < 0 || y >= pixelH || alpha == 0){
            
           return; 
        }
        int index = x + y * pixelW;
        
        if(zBuffer[index] > zDepth)
            return;
        
        zBuffer[index] = zDepth;
        
        if(alpha == 255){
            pixels[index] = value;
        }else{
            int color = 0;
            int pixelColor = pixels[index];
            int newRed = ((pixelColor >> 16) & 0xff) - (int)(((pixelColor >> 16) & 0xff - (value >> 16) & 0xff) * (alpha / 255f));
            int newGreen = ((pixelColor >> 8) & 0xff) - (int)(((pixelColor >> 8) & 0xff - (value >> 8) & 0xff) * (alpha / 255f));
            int newBlue = ((pixelColor) & 0xff) - (int)(((pixelColor) & 0xff - (value) & 0xff) * (alpha / 255f));
            pixels[index] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
        }
    }
    
    public void drawText(String text, int offX, int offY, int color){
        
        Image fontImage = font.getFontImage();
        
        text = text.toUpperCase();
        int offset = 0;
        
        for (int i = 0; i < text.length(); i++) {
            int unicode = text.codePointAt(i) - 32;
            
            for (int j = 0; j < fontImage.getHeight(); j++) {
                for (int k = 0; k < font.getWidths()[unicode]; k++) {
                    if(font.getFontImage().getPixels()[(k + font.getOffsets()[unicode]) + j * font.getFontImage().getWidth()] == 0xffffffff){
                        setPixel(k + offX + offset, j + offY, color);
                    }
                }  
            }
            
            offset += font.getWidths()[unicode];
        }
    }
    
    public void drawImage(Image image, int offX, int offY){
        
        if(image.isAlpha() && !processing){
            imageRequest.add(new ImageRequest(image, zDepth, offX, offY));
            return;
        }
        
        if(offX < -image.getWidth()) return;
        if(offY < -image.getHeight()) return;
        if(offX >= pixelW) return;
        if(offY >= pixelH) return;
        
        int newX = 0;
        int newY = 0;
        int newWidth = image.getWidth();
        int newHeight = image.getHeight();

        if(offX < 0){newX -= offX;}
        if(offY < 0){newY -= offY;}
        
        if(newWidth + offX > pixelW){
            newWidth -= newWidth + offX - pixelW;
        }
        
        if(newHeight + offY > pixelH){
            newHeight -= newHeight + offY - pixelH;
        }
        
        for (int y = newY; y < newHeight; y++) {
            for (int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, image.getPixels()[x + y * image.getWidth()]);
            }
        }
    }
    
    public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY){
        
        if(image.isAlpha() && !processing){
            imageRequest.add(new ImageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY));
            return;
        }
        
        
        if(offX < -image.getTileWidth()) return;
        if(offY < -image.getTileHeight()) return;
        if(offX >= pixelW) return;
        if(offY >= pixelH) return;
        
        int newX = 0;
        int newY = 0;
        int newWidth = image.getTileWidth();
        int newHeight = image.getTileHeight();

        if(offX < 0){newX -= offX;}
        if(offY < 0){newY -= offY;}
        
        if(newWidth + offX > pixelW){
            newWidth -= newWidth + offX - pixelW;
        }
        
        if(newHeight + offY > pixelH){
            newHeight -= newHeight + offY - pixelH;
        }
        
        for (int y = newY; y < newHeight; y++) {
            for (int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, image.getPixels()[(x + tileX * image.getTileWidth()) + (y + tileY * image.getTileHeight()) * image.getWidth()]);
            }
        }
    }
    
    public void drawRect(int offX, int offY, int width, int height, int color){

        for (int y = 0; y < height; y++) {
            setPixel(offX, y + offY, color);
            setPixel(offX + width, y + offY, color);
        }
        
        for (int x = 0; x < width; x++) {
            setPixel(x + offX, offY, color);
            setPixel(x + offX, offY + height, color);
        }  
    }
    
    public void fillRect(int offX, int offY, int width, int height, int color){
        if(offX < -width) return;
        if(offY < -height) return;
        if(offX >= pixelW) return;
        if(offY >= pixelH) return;
        
        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if(offX < 0){newX -= offX;}
        if(offY < 0){newY -= offY;}
        
        if(newWidth + offX > pixelW){
            newWidth -= newWidth + offX - pixelW;
        }
        
        if(newHeight + offY > pixelH){
            newHeight -= newHeight + offY - pixelH;
        }

        for (int y = newY; y < newHeight; y++) {
            for (int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, color);
            } 
        } 
    }

    public int getzDepth() {
        return zDepth;
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }  
}
