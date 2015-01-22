
package test_hsb;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class HSB_changer {
    private JFrame frame;
    private BufferedImage image_origen;
    private BufferedImage image_copy;
    
    public HSB_changer(JFrame frame){
        this.frame = frame;
    }
    
    public File read_imgfile(){
        File file=null;

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        return file;
    }

    public ImageIcon read_img(File imgfile) throws IOException{
        BufferedImage image = ImageIO.read(imgfile);
        ImageIcon icon = new ImageIcon(image);
        
        this.image_origen = image;
        WritableRaster wr = ((WritableRaster) image_origen.getData());
       
        image_copy = new BufferedImage(image_origen.getColorModel(), wr, true, null);
        return icon;
    }
    
    private void copy_image(){
        int width = image_origen.getWidth();
        int height = image_origen.getHeight();
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                Color c = new Color(image_origen.getRGB(j, i));
                image_copy.setRGB(j, i, c.getRGB());
            }
        }
    }
    
    public BufferedImage getImage(){
        return image_origen;
    }
    
    
    public void rgb2hsb(float hue, float saturation, float value){
        int width, height, red, green, blue, rgb;
        float []hsbvals = new float[3];
        
        width = image_origen.getWidth();
        height = image_origen.getHeight();
        
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                Color c = new Color(image_copy.getRGB(j, i));
                red = c.getRed();
                green = c.getGreen();
                blue = c.getBlue();

                hsbvals = Color.RGBtoHSB(red, green, blue, null);
                    if (hsbvals[0]!=0.f){
                        hsbvals[0] *= hue;
                        if (hsbvals[0]>1){
                            hsbvals[0]=1;
                        }
                }
                if (saturation!=-1.f){
                    hsbvals[1] *= saturation;
                    if (hsbvals[1]>1){
                        hsbvals[1]=1;
                    }
                }
                if (value!=-1.f){
                    hsbvals[2] *= value;
                    if (hsbvals[2]>1){
                        hsbvals[2]=1;
                    }
                }
                
                rgb = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
                image_origen.setRGB(j, i, rgb);
            }
        }
    }
}
