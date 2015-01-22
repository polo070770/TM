
package test_hsb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Test_HSB {
    private JFrame frame;
    private JPanel img_panel;
    private JPanel hsb_panel;
    private JLabel img_label;
    private HSB_changer hsbc;
    
    private JSlider slider_h;
    private JSlider slider_s;
    private JSlider slider_v;
    
    //debug console widgets
    private JFrame debug;
    private JPanel panel;
    private JTextArea ta;
    private JTextField tf_input;
    private JTextField tf_input2;
    private JButton btn_tesela;
    private JTextField tf_tes1;
    private JTextField tf_tes2;
    private JButton btn_sdf;
    
    Tesela_manager tesMan;
    
    public int actual_value_h;
    public int actual_value_s;
    public int actual_value_v;
    
    public Test_HSB(){
        img_label = new JLabel();
        img_panel = new JPanel();
        img_panel.add(img_label);
        
        slider_h = new JSlider(SwingConstants.VERTICAL, 0, 360, 180);
        slider_h.setName("Hue");
        hsb_panel = new JPanel();
        hsb_panel.add(slider_h);
        
        slider_s = new JSlider(SwingConstants.VERTICAL, 0, 255, 127);
        slider_h.setName("Saturation");
        hsb_panel.add(slider_s);
        
        slider_v = new JSlider(SwingConstants.VERTICAL, 0, 255, 127);
        slider_v.setName("Value");
        hsb_panel.add(slider_v);
        
        frame = new JFrame("HSB changer");
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setResizable(true);
        frame.setVisible(true);
        frame.add(img_panel, BorderLayout.EAST);
        frame.add(hsb_panel, BorderLayout.WEST);
        
        actual_value_h = slider_h.getValue();
        actual_value_s = slider_s.getValue();
        actual_value_v = slider_v.getValue();
        
        slider_h.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actual_value_h = slider_h.getValue();
                float factorHue = actual_value_h/180.f;
                hsbc.rgb2hsb(factorHue,-1.f,-1.f);
                frame.repaint();
            }
        });
        
        slider_s.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actual_value_s = slider_s.getValue();
                float factorSat = actual_value_s/127.f;
                hsbc.rgb2hsb(-1.f, factorSat, -1.f);
                frame.repaint();
            }
        });
        
        slider_v.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actual_value_v = slider_v.getValue();
                float factorVal = actual_value_v/127.f;
                hsbc.rgb2hsb(-1.f, -1.f, factorVal);
                frame.repaint();
            }
        });
        
        
        
        tesMan = new Tesela_manager();
        debug = new JFrame("debug console");
        debug.setLayout(new BorderLayout());
        debug.setSize(500, 500);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ta = new JTextArea(200,200);
        tf_input = new JTextField();
        tf_input2 = new JTextField();
        
        btn_tesela = new JButton("show tesela");
        tf_tes1 = new JTextField();
        tf_tes2 = new JTextField();
        btn_sdf = new JButton("get sdf correlation value");
        
        panel.add(ta);
        panel.add(tf_input);
        panel.add(tf_input2);
        
        panel.add(btn_tesela);
        panel.add(tf_tes1);
        panel.add(tf_tes2);
        panel.add(btn_sdf);
        
        btn_tesela.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tesela = Integer.parseInt(tf_input.getText());
                tesMan.showTesela(tesela, 1);
                
                int tesela2 = Integer.parseInt(tf_input2.getText());
                tesMan.showTesela(tesela2, 2);
            }
        });
        
        btn_sdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*int tes1 = Integer.parseInt(tf_tes1.getText());
                int tes2 = Integer.parseInt(tf_tes2.getText());
                tesMan.sdf(tes1, tes2);*/
                tesMan.sdf();
            }
        });
        
        debug.add(panel);
        debug.setVisible(true);
    }
    
    public static void main(String[] args) {
        Test_HSB test = new Test_HSB();
        test.run();
    }
    
    private void run(){
        hsbc = new HSB_changer(frame);
        File imgfile = hsbc.read_imgfile();
        try {
            ImageIcon icon = hsbc.read_img(imgfile);
            img_label.setIcon(icon);
        } catch (IOException ex) {
            Logger.getLogger(Test_HSB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedImage image = hsbc.getImage();
        int width = image.getWidth();
        int height = image.getHeight();
        ta.setText("width="+width+"\nheight="+height);
        
        tesMan.buildTeselas(image);
        ta.setText(ta.getText()+"\nnúmero de teselas="+tesMan.getTeselas().size());
        /*try {
        tesMan.showTeselas();
        } catch (InterruptedException ex) {
        Logger.getLogger(Test_HSB.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
