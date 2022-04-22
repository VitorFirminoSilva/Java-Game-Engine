package com.engine2d;


import com.engine2d.gfx.Font;
import com.engine2d.gfx.Image;
import com.engine2d.gfx.ImageRequest;
import com.engine2d.gfx.ImageTile;
import com.engine2d.gfx.Light;
import com.engine2d.gfx.LightRequest;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Renderer {
    
    private Font font = Font.STANDARD;
    private ArrayList<ImageRequest> imageRequest = new ArrayList<ImageRequest>();
    private ArrayList<LightRequest> lightRequest = new ArrayList<LightRequest>();
    
    private int pixelW, pixelH;
    private int[] pixels;
    private int[] zBuffer;
    
    private int ambienteColor = 0xff232323;
    private int[] lightMap;
    private int[] lightBlock;
    
    
    private int zDepth = 0;
    
    private boolean processing = false;
    
    
    
    public Renderer(GameContainer gameContainer){
        pixelW = gameContainer.getWIDTH();
        pixelH = gameContainer.getHEIGHT();
        pixels = ((DataBufferInt) gameContainer.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zBuffer = new int[pixels.length];
        
        lightMap = new int[pixels.length];
        lightBlock = new int[pixels.length];
    }
    
    public void clear(){
        for (int i = 0; i < pixels.length; i++) {
           pixels[i] = 0;
           zBuffer[i] = 0;
           lightMap[i] = ambienteColor;
           lightBlock[i] = 0;
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
        
        for (int i = 0; i < imageRequest.size(); i++) {
            ImageRequest imageRQ = imageRequest.get(i);
            setzDepth(imageRQ.getzDepth());
            drawImage(imageRQ.getImage(), imageRQ.getOffX(), imageRQ.getOffY());
        }
        
        for (int i = 0; i < lightRequest.size(); i++) {
            LightRequest lightRQ = lightRequest.get(i);
            drawLightRequest(lightRQ.getLight(), lightRQ.locX, lightRQ.locY);
        }
        
        
        
        for (int i = 0; i < pixels.length; i++) {
            float red = ((lightMap[i] >> 16) & 0xff) / 255f;
            float green = ((lightMap[i] >> 8) & 0xff) / 255f;
            float blue = (lightMap[i]& 0xff) / 255f;
            
            pixels[i] = ((int)(((pixels[i] >> 16) & 0xff) * red) << 16 |
                        (int)(((pixels[i] >> 8) & 0xff) * green) << 8 |
                        (int)((pixels[i] & 0xff) * blue));
        }
        
        imageRequest.clear();
        lightRequest.clear();
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
            pixels[index] = (newRed << 16 | newGreen << 8 | newBlue);
        }
    }
    
    public void setLightMap(int x, int y, int value){
        if(x < 0 || x >= pixelW || y < 0 || y >= pixelH){
           return; 
        }
        int index = x + y * pixelW;
        
        int baseColor = lightMap[index];
        
        int maxRed = Math.max(((baseColor >> 16) & 0xff), ((value >> 16) & 0xff));
        int maxGreen = Math.max(((baseColor >> 8) & 0xff), ((value >> 8) & 0xff));
        int maxBlue = Math.max((baseColor & 0xff), (value & 0xff));
        lightMap[index] = (maxRed << 16 | maxGreen << 8 | maxBlue);
    }
    
    public void setLightBlock(int x, int y, int value){
        if(x < 0 || x >= pixelW || y < 0 || y >= pixelH){
           return; 
        }
        int index = x + y * pixelW;
        
        if(zBuffer[index] > zDepth)
            return;

        lightBlock[index] = value;
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
                setLightBlock(x + offX, y + offY, image.getLightBlock());
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
                setLightBlock(x + offX, y + offY, image.getLightBlock());                
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
    public void drawLight(Light light, int offX, int offY){
        lightRequest.add(new LightRequest(light, offX, offY));
    } 
    
    private void drawLightRequest(Light light, int offX, int offY){
        for (int i = 0; i <= light.getDiameter(); i++) {
            drawLightLine(light, light.getRadius(), light.getRadius(), i, 0, offX, offY);
            drawLightLine(light, light.getRadius(), light.getRadius(), i, light.getDiameter(), offX, offY);
            drawLightLine(light, light.getRadius(), light.getRadius(), 0, i, offX, offY);
            drawLightLine(light, light.getRadius(), light.getRadius(), light.getDiameter(), i, offX, offY);
        }
    }
    
    private void drawLightLine(Light light, int x0, int y0, int x1, int y1, int offX, int offY){
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        
        int err = dx - dy;
        int e2;
        
        while(true){
            
            int screenX = x0 - light.getRadius() + offX;
            int screenY = y0 - light.getRadius() + offY;
            
            int lightColor = light.getLightValue(x0, y0);
            
            if(screenX < 0 || screenX >= pixelW || screenY < 0 || screenY >= pixelH)
                return;
            
            if(lightColor == 0)
                return;
            
            if(lightBlock[screenX + screenY * pixelW] == Light.FULL)
                return;
            
            setLightMap(screenX, screenY, lightColor);
            
            if(x0 == x1 && y0 == y1)
                break;
            
            e2 = 2 * err;
            if(e2 > -1 * dy){
                err -= dy;
                x0 += sx;
            }
            
            if(e2 < dx){
               err += dx;
               y0 += sy;
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
