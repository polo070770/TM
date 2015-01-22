package com.vista;

import com.controlador.*;
import com.model.Image_Modifiers;
import com.model.Zip_Writer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Frame principal que contiene todos los elementos para la reproducción y para
 * la gestión del menú.
 */
public class Main_Frame {

    private final Controller ctrl;
    private final HSV_changer_Frame hsv_frame;
    private final Filter_changer_Frame filter_frame;

    private final JFrame window;

    //controles del menú
    private final JMenuBar menu_bar;

    private final JMenu file;
    private final JMenuItem open;
    private final JMenuItem save;

    private final JMenu edit;
    private final JMenuItem change_hsv;
    private final JMenuItem apply_filter;

    private final JMenu codec;
    private final JMenuItem encode;
    private final JMenuItem decode;

    //Panel izquierdo (reproductor)
    private final JPanel panel_left;

    private final JPanel panel_img;
    private final JLabel label_img;

    private final JPanel panel_button;
    private final JButton button_backward;
    private final JButton button_forward;
    private final JButton button_play;
    private final JButton button_stop;

    //Panel derecho (debug y códec)
    private final JPanel panel_right;

    private final JPanel panel_debug;
    private final JLabel label_debug;
    public static JTextArea ta_debug;
    private final JScrollPane scroll_ta;

    private final JPanel panel_codec;
    private final JPanel panel_label;
    private final JPanel panel_tf;
    private final JLabel label_nteselas;
    private final JTextField tf_nteselas;
    private final JLabel label_nmov;
    private final JTextField tf_nmov;
    private final JLabel label_gop;
    private final JTextField tf_gop;
    private final JLabel label_qth;
    private final JTextField tf_qth;

    //Variables de control
    private boolean textPause;
    private boolean fileCharged;

    /**
     * Constructor, inicializa todos los atributos de la clase y llama al método
     * de configuración.
     */
    public Main_Frame() {
        hsv_frame = new HSV_changer_Frame();
        filter_frame = new Filter_changer_Frame();

        window = new JFrame("ZIP READER");

        menu_bar = new JMenuBar();
        file = new JMenu("File");
        open = new JMenuItem("Open...");
        save = new JMenuItem("Save as zip...");
        save.setEnabled(false);

        edit = new JMenu("Edit");
        apply_filter = new JMenuItem("Apply filter");
        apply_filter.setEnabled(false);
        change_hsv = new JMenuItem("Change hsv");
        change_hsv.setEnabled(false);

        codec = new JMenu("Codec");
        encode = new JMenuItem("Encode");
        encode.setEnabled(false);
        decode = new JMenuItem("Decode");

        panel_left = new JPanel();
        panel_img = new JPanel();
        label_img = new JLabel();
        ctrl = new Controller(label_img);

        panel_button = new JPanel();
        button_backward = new JButton();
        button_play = new JButton();
        button_stop = new JButton();
        button_forward = new JButton();

        panel_right = new JPanel();
        panel_debug = new JPanel();
        label_debug = new JLabel("Debug console:");
        ta_debug = new JTextArea(10, 25);
        ta_debug.setEditable(false);
        scroll_ta = new JScrollPane(ta_debug);
        scroll_ta.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel_codec = new JPanel();
        panel_label = new JPanel();
        panel_tf = new JPanel();

        label_nteselas = new JLabel("#teselas:");
        tf_nteselas = new JTextField("20", 5);

        label_nmov = new JLabel("#offset:");
        tf_nmov = new JTextField("10", 5);

        label_gop = new JLabel("GOP:");
        tf_gop = new JTextField("10", 5);

        label_qth = new JLabel("quality thresholding:");
        tf_qth = new JTextField("25", 5);

        textPause = false;
        fileCharged = false;
        this.init();
    }

    /**
     * Configura todos los widgets de esta clase y la relación entre ellos.
     */
    private void init() {
        window.setLayout(new BorderLayout());
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                ctrl.onCloseWindow();
            }
        });
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file;

                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Opening");
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    ta_debug.append("\nOpening Zip...");
                    file = fc.getSelectedFile();
                    ctrl.readZip(file);
                    hsv_frame.resetValues();

                    ctrl.stop();
                    ctrl.play();

                    textPause = true;
                    if (!fileCharged) {
                        fileCharged = true;

                        change_hsv.setEnabled(true);
                        apply_filter.setEnabled(true);
                        save.setEnabled(true);
                        encode.setEnabled(true);

                        Image_Modifiers.setOption(Image_Modifiers.filtering_options.NO_FILTER);
                        hsv_frame.resetValues();
                    }

                    ta_debug.append("\nDone!!");
                    BufferedImage image = ctrl.getImage(1);
                    ta_debug.append("\nimage width="+image.getWidth()+"   image height="+image.getHeight());
                }
            }
        });

        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Saving");
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fc.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {

                    ta_debug.append("\nSaving JPEG Zip...");

                    try {
                        String path = fc.getSelectedFile().getParent();
                        String filename = fc.getSelectedFile().getName();

                        Zip_Writer zw = new Zip_Writer(ctrl, path, filename);

                        Thread th = new Thread(zw);
                        th.start();
                        th.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main_Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ta_debug.append("\nDone!");

                }

            }
        });

        change_hsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hsv_frame.getFrame().setVisible(true);
            }
        });

        apply_filter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filter_frame.getFrame().setVisible(true);
            }
        });

        encode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Encoding...");
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fc.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {

                    String path = fc.getSelectedFile().getParent() + File.separator + fc.getSelectedFile().getName();

                    System.out.println(path);
                    int nteselas, nmov, gop, qth;

                    nteselas = Integer.parseInt(tf_nteselas.getText());
                    nmov = Integer.parseInt(tf_nmov.getText());
                    gop = Integer.parseInt(tf_gop.getText());
                    qth = Integer.parseInt(tf_qth.getText());

                    ctrl.encode(nteselas, nmov, gop, qth, path);

                }

            }
        });

        decode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean approved_file = false, approved_file_tesela = false;

                File file = null;

                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("select jpeg zip...");
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                    approved_file = true;
                }

                File file_tesela = null;

                JFileChooser fc_tesela = new JFileChooser();
                fc_tesela.setDialogTitle("select tesela gzip...");
                fc_tesela.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc_tesela.setFileSelectionMode(JFileChooser.FILES_ONLY);

                result = fc_tesela.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    file_tesela = fc_tesela.getSelectedFile();
                    approved_file_tesela = true;
                }

                if (approved_file && approved_file_tesela) {
                    ctrl.decode(file, file_tesela);
                }
            }
        });

        menu_bar.add(file);
        menu_bar.add(edit);
        menu_bar.add(codec);
        file.add(open);
        file.add(save);
        edit.add(change_hsv);
        edit.add(apply_filter);
        codec.add(encode);
        codec.add(decode);

        button_backward.setIcon(
                new javax.swing.ImageIcon(
                        getClass().getResource("/toolbarButtonGraphics/media/Rewind24.gif")));

        button_play.setIcon(
                new javax.swing.ImageIcon(
                        getClass().getResource("/toolbarButtonGraphics/media/Play24.gif")));

        button_stop.setIcon(
                new javax.swing.ImageIcon(
                        getClass().getResource("/toolbarButtonGraphics/media/Stop24.gif")));

        button_forward.setIcon(
                new javax.swing.ImageIcon(
                        getClass().getResource("/toolbarButtonGraphics/media/FastForward24.gif")));

        button_play.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        if (fileCharged) {
                            if (!textPause) {
                                ctrl.play();
                                textPause = true;
                                button_play.setIcon(new javax.swing.ImageIcon(
                                                getClass().getResource("/toolbarButtonGraphics/media/Pause24.gif")));
                                ta_debug.append("\nPLAYING...");
                            } else {
                                ctrl.pause();
                                textPause = false;
                                button_play.setIcon(new javax.swing.ImageIcon(
                                                getClass().getResource("/toolbarButtonGraphics/media/Play24.gif")));
                                ta_debug.append("\nPAUSING...");
                            }
                        }
                    }
                }
        );

        button_stop.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        if (fileCharged) {
                            textPause = false;
                            ctrl.stop();
                            button_play.setIcon(new javax.swing.ImageIcon(
                                            getClass().getResource("/toolbarButtonGraphics/media/Play24.gif")));
                            ta_debug.append("\nSTOPPING");
                        }
                    }
                }
        );

        button_forward.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        if (fileCharged) {
                            ctrl.goForward(5);
                            ta_debug.append("\nFPS:" + ctrl.getFPS());
                        }
                    }
                }
        );

        button_backward.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        if (fileCharged) {
                            ctrl.goBack(5);
                            ta_debug.append("\nFPS:" + ctrl.getFPS());
                        }
                    }
                }
        );

        panel_img.add(label_img);

        panel_button.add(button_backward);

        panel_button.add(button_play);

        panel_button.add(button_stop);

        panel_button.add(button_forward);

        panel_left.setLayout(
                new BorderLayout());
        panel_left.add(panel_img, BorderLayout.CENTER);

        panel_left.add(panel_button, BorderLayout.SOUTH);

        panel_debug.setLayout(
                new BorderLayout());
        panel_debug.add(label_debug, BorderLayout.PAGE_START);

        panel_debug.add(scroll_ta, BorderLayout.LINE_END);

        panel_codec.setLayout(
                new FlowLayout(FlowLayout.LEFT));
        panel_label.setLayout(
                new BoxLayout(panel_label, BoxLayout.Y_AXIS));
        panel_tf.setLayout(
                new BoxLayout(panel_tf, BoxLayout.Y_AXIS));

        panel_label.add(label_nteselas);
        panel_label.add(label_nmov);
        panel_label.add(label_gop);
        panel_label.add(label_qth);
        panel_tf.add(tf_nteselas);
        panel_tf.add(tf_nmov);
        panel_tf.add(tf_gop);
        panel_tf.add(tf_qth);
        panel_codec.add(panel_label);
        panel_codec.add(panel_tf);

        panel_right.setLayout(new BorderLayout());
        panel_right.add(panel_debug, BorderLayout.CENTER);
        panel_right.add(panel_codec, BorderLayout.SOUTH);

        window.setJMenuBar(menu_bar);
        window.add(panel_left, BorderLayout.WEST);
        window.add(panel_right, BorderLayout.EAST);

        window.setSize(650, 400);
        window.setResizable(false);
        window.setVisible(true);
    }
}
