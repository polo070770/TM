/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package test_hsb;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Orquidea
 */
public class Tesela_manager {
    private JFrame frame;
    private JPanel panel;
    private JLabel label;
    private JLabel label2;
    private JLabel label_empty;
    
    
    private ArrayList<BufferedImage> teselas;
    private int row_pixels;
    private int column_pixels;
    
    public Tesela_manager(){
        this.teselas = new ArrayList<BufferedImage>();
        row_pixels = 20;
        column_pixels = 20;
        
        frame = new JFrame("show teselas");
        frame.setVisible(true);
        frame.setSize(500, 500);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        label = new JLabel();
        label2 = new JLabel();
        label_empty = new JLabel(" ");
        panel.add(label);
        panel.add(label_empty);
        panel.add(label2);
        
        
        frame.add(panel);
    }
    
    public ArrayList<BufferedImage> getTeselas(){
        return teselas;
    }
    
    public void buildTeselas(BufferedImage image){
        for (int j=0; j<image.getHeight(); j+=column_pixels){
            for (int i=0; i<image.getWidth(); i+=row_pixels){
                BufferedImage tesImage = image.getSubimage(i, j, row_pixels, column_pixels);
                teselas.add(tesImage);
            }
        }
    }
    
    public void showTesela(int tesela, int lab){
        ImageIcon icon = new ImageIcon(teselas.get(tesela));
        if (lab==1){
            label.setIcon(icon);
        }else{
            label2.setIcon(icon);
        }
    }
    
    public void showTeselas() throws InterruptedException{
        for (BufferedImage tesela : teselas) {
            ImageIcon icon = new ImageIcon(tesela);
            label.setIcon(icon);
            Thread.sleep(500);
        }
    }
    
    public int sdf(int tes1, int tes2){
        BufferedImage image = teselas.get(tes1);
        BufferedImage image2 = teselas.get(tes2);
        
        double sdf_correlation = 0.;
        for (int i=0; i<row_pixels; i++){
            for (int j=0; j<column_pixels; j++){
                /*int pixel = image.getRGB(i, j);
                int pixel2 = image2.getRGB(i, j);
                sdf_correlation+=Math.pow((pixel-pixel2),2);*/
                Color pixel1 = new Color (image.getRGB(i, j));
                Color pixel2 = new Color (image2.getRGB(i, j));
                sdf_correlation += ((Math.abs(pixel1.getRed()-pixel2.getRed())
                        +Math.abs(pixel1.getGreen()-pixel2.getGreen())
                        +Math.abs(pixel1.getBlue()-pixel2.getBlue()))/3.0);
                //System.out.println(sdf_correlation);
            }
        }
        sdf_correlation=sdf_correlation /((double) image.getWidth()*image2.getHeight());
        System.out.println(tes1+" "+tes2+" "+sdf_correlation);
        return 0;
    }
    
    public void sdf(){
        BufferedImage image = teselas.get(0);
        
        
        for (int k=0; k<teselas.size(); k++){
            double sdf_correlation = 0.;
            BufferedImage image2 = teselas.get(k);
            for (int i=0; i<row_pixels; i++){
                for (int j=0; j<column_pixels; j++){
                    Color pixel1 = new Color (image.getRGB(i, j));
                    Color pixel2 = new Color (image2.getRGB(i, j));
                    sdf_correlation += ((Math.abs(pixel1.getRed()-pixel2.getRed())
                            +Math.abs(pixel1.getGreen()-pixel2.getGreen())
                            +Math.abs(pixel1.getBlue()-pixel2.getBlue()))/3.0);
                }
            }
            sdf_correlation=sdf_correlation /(row_pixels*column_pixels);
            System.out.println("correlation "+k+"="+sdf_correlation);
        }
    }
}
