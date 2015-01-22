/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
//import javax.media.jai.RenderedOp;
//import javax.media.jai.operator.ColorQuantizerDescriptor;

/**
 *
 * @author Sergio
 */
public class Image {
    private BufferedImage imgBuf;
    private BufferedImage imgBufMod;
    private int[][] grayMatrix;
    private String  imgName;
    private int height;
    private int width;
    private Filter imgFilter;
    private float brightness, hue, saturation;
    private Boolean updHSB;
    
    public enum Filter {
        R_CHANNEL, G_CHANNEL, B_CHANNEL, ORIGINAL, BINARY, NEGATIVE, GRAY, MEAN,
        HIGH_PASS, LOW_PASS, X_GRAD, Y_GRAD, TOTAL_GRAD, ISO_GRAD, LAPLACE, 
        COLOR_QUANT
    }
    
    //constructor de la clase, nombre+imagen son los datos a pasar
//    public Image() {
//    }
    
    public Image(BufferedImage imgBuf, String imgN ){
        this.imgBuf         = imgBuf;
        init(imgN);
    }
    
    public Image(File imgFile) throws IOException, NullPointerException {
        this.imgBuf         = ImageIO.read(imgFile);
        init(imgFile.getName());
    }
    
    private void init(String imgN) {
        int pos             = imgN.lastIndexOf(".");
        this.imgName        = imgN.substring(0, pos);
        this.imgBufMod      = copy(imgBuf);
        this.height         = imgBuf.getHeight();
        this.width          = imgBuf.getWidth();    
        this.grayMatrix     = getGrayMatrix();
        this.imgFilter      = Filter.ORIGINAL;
        this.hue            = 0.0f;
        this.brightness     = 0.0f;
        this.saturation     = 0.0f;
        this.updHSB         = false;
    }
    
    //metodo para asignar una imagen
    public void setBufImg(BufferedImage bi) {
        this.imgBuf = bi;
    }
	
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public BufferedImage getImgBuf() {
        return this.imgBuf;
    }
    
    public BufferedImage getImgBufMod() {
        return this.imgBufMod;
    }
    
    public Filter getImgFilter() {
        return this.imgFilter;
    }
    
    //metodo que devuelve el nombre de la imagen
    public String getImgName() {
        return this.imgName;
    }
    
    public void filter(Filter fl) {
        if (this.imgFilter != fl || this.hue != 0.0f || this.saturation != 0.0f
                || this.brightness != 0.0f) {
            this.updHSB     = true;
            this.imgFilter  = fl;
            switch (fl) {
                case R_CHANNEL: 
                    this.filterR();
                    break;
                case G_CHANNEL:
                    this.filterG();
                    break;
                case B_CHANNEL:
                    this.filterB();
                    break;
                case BINARY:
                    this.binaryFilter(width);
                    break;
                case GRAY:
                    this.filtrogris();
                    break;
                case HIGH_PASS:
                    this.filtroPasaaltos();
                    break;
                case LOW_PASS:
                    this.filtroPasabajos();
                    break;
                case MEAN:
                    this.promediodeVecinos();
                    break;
                case NEGATIVE:
                    this.filtroinv();
                    break;
                case ORIGINAL:
                    this.revertToOriginal();
                    break;
                case TOTAL_GRAD:
                    this.filtroG();
                    break;
                case X_GRAD:
                    this.filtroGx();
                    break;
                case Y_GRAD:
                    this.filtroGy();
                    break;
                case ISO_GRAD:
                    this.filtroGiso();
                    break;
                case LAPLACE:
                    this.laplace();
                    break;
                default:
                    System.err.println("ERROR: Undefined filter");
                    break;
            }
        } else
            this.updHSB     = false;
    }
    
    public void filter(Filter fl, int th) {
        //if (this.imgFilter != fl) {
            this.imgFilter = fl;
            switch (fl) {
                case BINARY:
                    this.binaryFilter(th);
                    break;
                case COLOR_QUANT:
//                    this.colorQuant(th);
                    break;
                default:
                    System.err.println("ERROR: Undefined filter");
                    break;
        //    }
        }
    }
      
    public Boolean writeImages(String format, File outDir) {
        File imgOutFile     = new File(outDir + File.separator + this.imgName + "." + format);
        try {
            return ImageIO.write(this.imgBufMod, format, imgOutFile);
        } catch (IOException ex) {
            System.err.println("Error saving image " + imgOutFile.getName() );
            Logger.getLogger(GZip.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void filterR() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Color colors    = new Color(this.imgBuf.getRGB(i,j));
                int newRGB      = new Color(colors.getRed(), 0, 0).getRGB();
                this.imgBufMod.setRGB(i, j, newRGB);
            }
        } 
    }
    
    public void filterG() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Color colors    = new Color(this.imgBuf.getRGB(i,j));
                int newRGB      = new Color(0, colors.getGreen(), 0).getRGB();
                this.imgBufMod.setRGB(i, j, newRGB);
            }
        } 
    }
        
    public void filterB() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Color colors    = new Color(this.imgBuf.getRGB(i,j));
                int newRGB      = new Color(0, 0, colors.getBlue()).getRGB();
                this.imgBufMod.setRGB(i, j, newRGB);
            }
        } 
    }
    
    public void revertToOriginal() {
        this.imgBufMod = copy(this.imgBuf);
    }
 
    public final BufferedImage copy(BufferedImage original) {
        ColorModel cm = original.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = original.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
     
    public void binaryFilter(int x){
        int umbralfiltro = x; //umbral para aplicar el binaryFilter
        int rojo, verde, azul, suma;
        for (int i = 0; i < this.width; i++) {  //bucles para correr todos los pixels
            for (int j = 0; j < this.height; j++) {
                Color colores = new Color(this.imgBuf.getRGB(i, j));
                rojo = colores.getRed();
                verde = colores.getGreen();
                azul = colores.getBlue();
                suma = (rojo + verde + azul) / 3;
                //System.out.println(suma);
                if (suma < umbralfiltro) {
                    //color negro en entero
                    int rgb = new Color(0, 0, 0).getRGB();
                    this.imgBufMod.setRGB(i, j, rgb);
                } else {
                    //color blanco en entero
                    int rgb = new Color(255, 255, 255).getRGB();
                    this.imgBufMod.setRGB(i, j, rgb);
                }
            }
        } 
    }
    
    public void filtroinv(){
        for (int i = 0; i < this.width; i++) {  //bucles para correr todos los pixels
            for (int j = 0; j < this.height; j++) {
                Color colores = new Color(this.imgBuf.getRGB(i, j));
                int rojo = colores.getRed();
                int verde = colores.getGreen();
                int azul = colores.getBlue();
                int rgb = new Color(255-rojo,255-verde,255-azul).getRGB();
                this.imgBufMod.setRGB(i, j, rgb); //imagen en gris
            }
        }
    }
    
    public void filtrogris() {
        int rojo, verde, azul, suma, rgb;
        //solo es hacer la media de las tonalidades
        for (int i = 0; i < this.width; i++) {  //bucles para correr todos los pixels
            for (int j = 0; j < this.height; j++) {
                Color colores = new Color(this.imgBuf.getRGB(i, j));
                rojo = colores.getRed();
                verde = colores.getGreen();
                azul = colores.getBlue();
                suma = (rojo + verde + azul) / 3;
                rgb = new Color(suma, suma, suma).getRGB();
                this.imgBufMod.setRGB(i, j, rgb);
            }
        }
    }
    
    public void promediodeVecinos() {
        int sumaR, sumaG, sumaB, rgb;
        int sumaA, PromA;
        int PromR, PromG, PromB;
        //solo es hacer la media de las tonalidades
        //filtrogris();
        //pasado a gris   
        for (int i = 1; i < this.width - 1; i++) {  //bucles para correr todos los pixels
            for (int j = 1; j < this.height - 1; j++) {
                //suma de la matriz de vecinos
                sumaR = 0;
                sumaG = 0;
                sumaB = 0;
                sumaA = 0;
                for (int f = i - 1; f <= i + 1; f++) {
                    for (int g = j - 1; g <= j + 1; g++) {
                        Color colores = new Color(this.imgBuf.getRGB(f, g));
                        sumaR = colores.getRed() + sumaR;
                        sumaG = colores.getGreen()+ sumaG;
                        sumaB = colores.getBlue()+ sumaB;
                        sumaA = colores.getAlpha() + sumaA;
                    }
                }
                PromR = sumaR / 9;
                PromG = sumaG / 9;
                PromB = sumaB / 9;
                PromA = sumaA / 9;
                rgb = new Color(PromR, PromG, PromB, PromA).getRGB();
                this.imgBufMod.setRGB(i, j, rgb);
            }
        }
    }

    public final int[][] getGrayMatrix() {
        int rojo, verde, azul, suma, rgb;
        int[][] matrizdevaloresgris = new int[this.width][this.height];
        for (int i = 0; i < this.width; i++) {  //bucles para correr todos los pixels
            for (int j = 0; j < this.height; j++) {
                Color colores = new Color(this.imgBuf.getRGB(i, j));
                rojo = colores.getRed();
                verde = colores.getGreen();
                azul = colores.getBlue();
                suma = (rojo + verde + azul) / 3;
                rgb = new Color(suma, suma, suma).getRGB();
                matrizdevaloresgris[i][j] = rgb;
            }
        }
        return matrizdevaloresgris;
    }
    
    public void filter3x3(int[][] filterArray) {
        float[] filter = new float[9];
        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                filter[i*3+j] = filterArray[i][j];
            }
        }
        Kernel kernel = new Kernel(3,3,filter);
        ConvolveOp cop = new ConvolveOp(kernel,
                                        ConvolveOp.EDGE_NO_OP,
                                        null);
        
        cop.filter(this.imgBuf, this.imgBufMod);
        /*
        for (int i = 1; i < this.width - 1; i++) {
            for (int j = 1; j < this.height - 1; j++) {
                int sumaGris = 0;
                int[][] submatrixGris = new int[3][3];
                //matriz gris
                System.arraycopy(this.grayMatrix[i - 1], j - 1, submatrixGris[0], 0, 3);
                System.arraycopy(this.grayMatrix[i], j - 1, submatrixGris[1], 0, 3);
                System.arraycopy(this.grayMatrix[i + 1], j - 1, submatrixGris[2], 0, 3);

                for (int f = 0; f < 3; f++) {
                    for (int g = 0; g < 3; g++) {
                        sumaGris = filterArray[g][f] * submatrixGris[g][f] + sumaGris;
                    }
                }
                this.imgBufMod.setRGB(i, j, sumaGris);
            }
        }
        */
    }
    
    public void filtroPasaaltos() {
        int[][] pasaaltos = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
        filter3x3(pasaaltos);
    }

    public void filtroPasabajos() {
        int[][] pasabajos = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
        filter3x3(pasabajos);
    }
    
    public void filtroGx() {
        int[][] pasabajos = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        filter3x3(pasabajos);
    }

    public void filtroGy() {
        int[][] pasabajos = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
        filter3x3(pasabajos);
    }

    public void filtroG() {
        int[][] gradientex = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gradientey = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

        for (int i = 1; i < this.width - 1; i++) {
            for (int j = 1; j < this.height - 1; j++) {
                int sumaGrisx = 0;
                int sumaGrisy = 0;
                int[][] submatrixGris = new int[3][3];
                //matriz gris
                System.arraycopy(this.grayMatrix[i - 1], j - 1, submatrixGris[0], 0, 3);
                System.arraycopy(this.grayMatrix[i], j - 1, submatrixGris[1], 0, 3);
                System.arraycopy(this.grayMatrix[i + 1], j - 1, submatrixGris[2], 0, 3);

                for (int f = 0; f < 3; f++) {
                    for (int g = 0; g < 3; g++) {
                        sumaGrisx = gradientex[g][f] * submatrixGris[g][f] + sumaGrisx;
                        sumaGrisy = gradientey[g][f] * submatrixGris[g][f] + sumaGrisy;
                    }
                }
                this.imgBufMod.setRGB(i, j, sumaGrisx + sumaGrisy);
            }
        }
    }

    public void filtroGiso() {
        double[][] gradientex = {{-1, 0, 1}, {-Math.sqrt(2), 0, Math.sqrt(2)}, {-1, 0, 1}};
        double[][] gradientey = {{-1, -Math.sqrt(2), -1}, {0, 0, 0}, {1, Math.sqrt(2), 1}};
        //int[][] histograma = new int[this.width][this.height];
        
        for (int i = 1; i < this.width - 1; i++) {
            for (int j = 1; j < this.height - 1; j++) {
                double sumaGrisx = 0;
                double sumaGrisy = 0;
                int[][] submatrixGris = new int[3][3];
                //matriz gris
                System.arraycopy(this.grayMatrix[i - 1], j - 1, submatrixGris[0], 0, 3);
                System.arraycopy(this.grayMatrix[i], j - 1, submatrixGris[1], 0, 3);
                System.arraycopy(this.grayMatrix[i + 1], j - 1, submatrixGris[2], 0, 3);

                for (int f = 0; f < 3; f++) {
                    for (int g = 0; g < 3; g++) {
                        sumaGrisx = gradientex[g][f] * submatrixGris[g][f] + sumaGrisx;
                        sumaGrisy = gradientey[g][f] * submatrixGris[g][f] + sumaGrisy;
                    }
                }
                int suma = (int) Math.floor(sumaGrisx) + (int) Math.floor(sumaGrisy);
                this.imgBufMod.setRGB(i, j, suma);
                //histograma[i][j] = suma;
            }
        }
        /*
        //hacer un binaryFilter al 50% del valor maximo del histogama
        int Mayor = 0;
        for (int i = 0; i < this.width - 2; i++) {
            for (int j = 0; j < this.height - 2; j++) {
                // determinar el mayor
                if (Mayor < histograma[i][j]) {
                    Mayor = histograma[i][j];
                }
            }
        }
        //ya tenemos el umbral
        int umbral = (int) Math.floor(Mayor * (1 - 0.5));
        System.out.println(umbral); */
    }
    
    public void setHSB(float hue, float saturation, float brightness) {
        float[] hsb = new float[3];
        hue         = hue / 100.0f;
        saturation  = saturation / 100.0f;
        brightness  = brightness / 100.0f; 
        
        if(this.hue != hue || this.saturation != saturation || 
                this.brightness != brightness || this.updHSB)
        {
            this.hue        = hue;
            this.saturation = saturation;
            this.brightness = brightness;
            
            for(int i = 0; i < this.width; i++){
                    for(int j = 0; j < this.height; j++){
                            Color c = new Color(this.imgBufMod.getRGB(i, j));
                            Color.RGBtoHSB(c.getRed(),c.getGreen(), c.getBlue(), hsb);

                            hsb[0] = hsb[0] + (this.hue);
                            if(hsb[0] > 1) hsb[0] = 1.0f;
                            if(hsb[0] < 0) hsb[0] = 0.0f;

                            hsb[1] = hsb[1] + (this.saturation);
                            if(hsb[1] > 1) hsb[1] = 1.0f;
                            if(hsb[1] < 0) hsb[1] = 0.0f;

                            hsb[2] = hsb[2] + (this.brightness);
                            if(hsb[2] > 1) hsb[2] = 1.0f;
                            if(hsb[2] < 0) hsb[2] = 0.0f;

                            this.imgBufMod.setRGB(i, j, Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                    }
            }
        }
    }     
    
    public void laplace() {
        int[][] laplace = {{0, -1, 0}, {-1, 4, -1}, {0, -1, 0}};
        filter3x3(laplace);
    }
    /*
    public void colorQuant(int colorNum) {
        //int colorNum    = 16;
        RenderedOp rendOp = ColorQuantizerDescriptor.create(this.imgBuf, 
                ColorQuantizerDescriptor.MEDIANCUT, colorNum, null, null, 
                null, null, null);
        this.imgBufMod = rendOp.getAsBufferedImage();
    }*/
}
