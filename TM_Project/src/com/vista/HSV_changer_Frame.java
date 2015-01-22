package com.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Clase que implementa un frame para modificar los factores de hsv (sirve de
 * alpha) para ser multiplicado posteriormente, con el fin de poder cambiar los
 * valores hsv de los píxeles de la imagen. La modificación se lleva a cabo
 * mediante tres sliders.
 */
public class HSV_changer_Frame {

    private final JFrame frame_hsv;
    private final JPanel panel_hsv;
    private final JPanel panel_btn;

    private final JPanel panel_labels;
    private final JPanel panel_sliders;

    private final JSlider slider_h;
    private final JSlider slider_s;
    private final JSlider slider_v;

    private final JLabel label_h;
    private final JLabel label_s;
    private final JLabel label_v;

    private final JLabel empty;
    private final JLabel empty1;

    private final JLabel value_h;
    private final JLabel value_s;
    private final JLabel value_v;

    //Valores predeterminados de los sliders.
    private static final int DEFAULT_H = 180;
    private static final int DEFAULT_S = 127;
    private static final int DEFAULT_V = 127;

    private static final int DEFAULT_FRAME_WIDTH = 400;
    private static final int DEFAULT_FRAME_HEIGHT = 200;

    private static final int SLIDER_ORIENTATION = SwingConstants.HORIZONTAL;

    private int actual_h;
    private int actual_s;
    private int actual_v;

    public static float factor_h;
    public static float factor_s;
    public static float factor_v;

    private final JButton btn_reset; //Permite resetear los valores predeterminados y los factores hsv
    private final JButton btn_cancel;

    /**
     * Constructor, inicializa todos los atributos de la clase y llama al método
     * de configuración.
     */
    public HSV_changer_Frame() {
        frame_hsv = new JFrame("Set HUE-SATURATION-VALUE");
        panel_hsv = new JPanel();
        panel_btn = new JPanel();

        panel_sliders = new JPanel();
        panel_labels = new JPanel();

        slider_h = new JSlider(SLIDER_ORIENTATION, 0, 360, DEFAULT_H);
        slider_s = new JSlider(SLIDER_ORIENTATION, 0, 255, DEFAULT_S);
        slider_v = new JSlider(SLIDER_ORIENTATION, 0, 255, DEFAULT_V);

        label_h = new JLabel("Hue");
        label_s = new JLabel("Saturation");
        label_v = new JLabel("Brightness");

        empty = new JLabel(" ");
        empty1 = new JLabel(" ");

        //factor = 1 implica que no habrá cambio respecto al canal asociado, ya que 1 no afecta al resultado de una multiplicación.
        factor_h = 1;
        factor_s = 1;
        factor_v = 1;

        value_h = new JLabel(String.valueOf(factor_h));
        value_s = new JLabel(String.valueOf(factor_s));
        value_v = new JLabel(String.valueOf(factor_v));

        btn_reset = new JButton("Reset values");
        btn_cancel = new JButton("Cancel");
        this.init();
    }

    /**
     * Configura todos los widgets de esta clase y la relación entre ellos.
     */
    private void init() {
        frame_hsv.setLayout(new BorderLayout());
        //frame_hsv.getContentPane().setSize(new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));
        frame_hsv.setSize(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        frame_hsv.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame_hsv.setResizable(false);

        //Rectangle r = new Rectangle(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        //frame_hsv.setMaximizedBounds(r);
        slider_h.setName("Hue");
        slider_h.setName("Saturation");
        slider_v.setName("Value");

        slider_h.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actual_h = slider_h.getValue();
                factor_h = actual_h / 180.f;
                value_h.setText(String.valueOf(factor_h));
            }
        });

        slider_s.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actual_s = slider_s.getValue();
                factor_s = actual_s / 127.f;
                value_s.setText(String.valueOf(factor_s));
            }
        });

        slider_v.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actual_v = slider_v.getValue();
                factor_v = actual_v / 127.f;
                value_v.setText(String.valueOf(factor_v));
            }
        });

        btn_reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetValues();
                value_h.setText("1");
                value_s.setText("1");
                value_v.setText("1");
            }
        });

        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame_hsv.dispose();
            }
        });

        panel_hsv.setLayout(new FlowLayout());
        panel_sliders.setLayout(new BoxLayout(panel_sliders, BoxLayout.Y_AXIS));
        panel_labels.setLayout(new BoxLayout(panel_labels, BoxLayout.Y_AXIS));

        panel_sliders.add(slider_h);
        panel_sliders.add(value_h);
        panel_sliders.add(slider_s);
        panel_sliders.add(value_s);
        panel_sliders.add(slider_v);
        panel_sliders.add(value_v);

        panel_labels.add(label_h);
        panel_labels.add(empty);
        panel_labels.add(label_s);
        panel_labels.add(empty1);
        panel_labels.add(label_v);

        panel_hsv.add(panel_labels);
        panel_hsv.add(panel_sliders);

        panel_btn.setLayout(new FlowLayout());
        panel_btn.add(btn_reset);
        panel_btn.add(btn_cancel);

        frame_hsv.add(panel_hsv, BorderLayout.NORTH);
        frame_hsv.add(panel_btn, BorderLayout.SOUTH);
        //frame_hsv.pack();
    }

    /**
     * Devuelve la frame de la clase
     *
     * @return JFrame : la frame hsv de la clase
     */
    public JFrame getFrame() {
        return frame_hsv;
    }

    /**
     * Resetear los valores de sliders y los factores hsv.
     */
    public void resetValues() {
        slider_h.setValue(DEFAULT_H);
        slider_s.setValue(DEFAULT_S);
        slider_v.setValue(DEFAULT_V);

        factor_h = 1;
        factor_s = 1;
        factor_v = 1;
    }
}
