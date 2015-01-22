package com.vista;

import com.model.Image_Modifiers;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Filter_changer_Frame {

    private final JFrame frame_filter;

    private final JPanel matrix_panel;
    private final JPanel matrix_cont;
    private final JPanel button_panel;
    private final JPanel btnGroup_panel;

    private ArrayList<JTextField> matrix;

    private JTextField m_00;
    private JTextField m_01;
    private JTextField m_02;

    private JTextField m_10;
    private JTextField m_11;
    private JTextField m_12;

    private JTextField m_20;
    private JTextField m_21;
    private JTextField m_22;

    private final JButton btn_reset;
    private final JButton btn_average;
    private final JButton btn_apply;

    private final ButtonGroup btnGroup;
    private final JRadioButton rBtn_negative;
    private final JRadioButton rBtn_binarization;
    private final JRadioButton rBtn_sovelX;
    private final JRadioButton rBtn_sovelY;
    private final JRadioButton rBtn_laplacian;

    public static final int rows = 3;
    public static final int columns = 3;
    public static float[] filter = {1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f
    };

    public Filter_changer_Frame() {

        frame_filter = new JFrame("Setting MATRIX-FILTER");
        frame_filter.setLayout(new BorderLayout());

        matrix_cont = new JPanel();
        matrix_panel = new JPanel();

        matrix_cont.add(matrix_panel);

        matrix_panel.setLayout(new GridLayout(3, 3));
        matrix_panel.setPreferredSize(new Dimension(200, 200));

        button_panel = new JPanel();
        button_panel.setLayout(new BorderLayout());

        matrix = new ArrayList<JTextField>();

        initializeTextFields();

        btn_reset = new JButton("RESET");
        btn_reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (JTextField t : matrix) {
                    t.setText("");
                }

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.NO_FILTER);
                btnGroup.clearSelection();
            }

        });

        btn_apply = new JButton("APPLY FILTER");
        btn_apply.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int idx = 0;
                for (JTextField tf : matrix) {
                    filter[idx] = Float.valueOf(tf.getText());
                    idx++;
                }

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.FILTERING);
                btnGroup.clearSelection();

            }
        });

        btn_average = new JButton("AVERAGE FILTER");
        btn_average.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                for (JTextField tf : matrix) {
                    tf.setText(" 1 / 9");
                }

                filter = new float[]{
                    1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
                    1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
                    1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f

                };

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.FILTERING);

            }
        });

        btnGroup_panel = new JPanel();
        btnGroup_panel.setLayout(new BoxLayout(btnGroup_panel, BoxLayout.Y_AXIS));

        btnGroup = new ButtonGroup();

        rBtn_negative = new JRadioButton("Negative");
        rBtn_negative.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.NEGATIVE);

            }
        });

        rBtn_binarization = new JRadioButton("Binarization");
        rBtn_binarization.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.BINARIZATION);

            }
        });

        rBtn_laplacian = new JRadioButton("Laplacian");
        rBtn_laplacian.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                m_00.setText("0.0");
                m_01.setText("-1.0");
                m_02.setText("0.0");

                m_10.setText("-1.0");
                m_11.setText("4.0");
                m_12.setText("-1.0");

                m_20.setText("0.0");
                m_21.setText("-1.0");
                m_22.setText("0.0");

                filter = new float[]{
                    0.0f, -1.0f, 0.0f,
                    -1.0f, 4.0f, -1.0f,
                    0.0f, -1.0f, 0.0f

                };

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.FILTERING);
            }
        });

        rBtn_sovelX = new JRadioButton("Sobel x");
        rBtn_sovelX.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                m_00.setText("-1.0");
                m_01.setText("0.0");
                m_02.setText("1.0");

                m_10.setText("-2.0");
                m_11.setText("0.0");
                m_12.setText("2.0");

                m_20.setText("-1.0");
                m_21.setText("0.0");
                m_22.setText("1.0");

                filter = new float[]{
                    -1.0f, 0.0f, 1.0f,
                    -2.0f, 0.0f, 2.0f,
                    -1.0f, 0.0f, 1.0f
                };

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.FILTERING);

            }
        });

        rBtn_sovelY = new JRadioButton("Sobel y");
        rBtn_sovelY.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                m_00.setText("1.0");
                m_01.setText("2.0");
                m_02.setText("1.0");

                m_10.setText("0.0");
                m_11.setText("0.0");
                m_12.setText("0.0");

                m_20.setText("-1.0");
                m_21.setText("-2.0");
                m_22.setText("-1.0");

                filter = new float[]{
                    1.0f, 2.0f, 1.0f,
                    0.0f, 0.0f, 0.0f,
                    -1.0f, -2.0f, -1.0f
                };

                Image_Modifiers.setOption(Image_Modifiers.filtering_options.FILTERING);

            }
        });

        btnGroup.add(rBtn_negative);
        btnGroup.add(rBtn_binarization);
        btnGroup.add(rBtn_laplacian);
        btnGroup.add(rBtn_sovelX);
        btnGroup.add(rBtn_sovelY);

        btnGroup_panel.add(rBtn_negative);
        btnGroup_panel.add(rBtn_binarization);
        btnGroup_panel.add(rBtn_laplacian);
        btnGroup_panel.add(rBtn_sovelX);
        btnGroup_panel.add(rBtn_sovelY);

        button_panel.add(btn_reset, BorderLayout.WEST);
        button_panel.add(btn_apply, BorderLayout.CENTER);
        button_panel.add(btn_average, BorderLayout.EAST);

        frame_filter.add(matrix_cont, BorderLayout.CENTER);
        frame_filter.add(button_panel, BorderLayout.SOUTH);
        frame_filter.add(btnGroup_panel, BorderLayout.EAST);

        frame_filter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame_filter.setSize(400, 300);
        frame_filter.setResizable(false);

    }

    /**
     * Inicializa los texts fields para la matriz de filtro, kernel
     */
    private void initializeTextFields() {

        m_00 = new JTextField(5);
        m_00.setPreferredSize(new Dimension(50, 50));
        m_00.setHorizontalAlignment(JTextField.CENTER);

        m_01 = new JTextField(5);
        m_01.setPreferredSize(new Dimension(50, 50));
        m_01.setHorizontalAlignment(JTextField.CENTER);

        m_02 = new JTextField(5);
        m_02.setPreferredSize(new Dimension(50, 50));
        m_02.setHorizontalAlignment(JTextField.CENTER);

        m_10 = new JTextField(5);
        m_10.setPreferredSize(new Dimension(50, 50));
        m_10.setHorizontalAlignment(JTextField.CENTER);

        m_11 = new JTextField(5);
        m_11.setPreferredSize(new Dimension(50, 50));
        m_11.setHorizontalAlignment(JTextField.CENTER);

        m_12 = new JTextField(5);
        m_12.setPreferredSize(new Dimension(50, 50));
        m_12.setHorizontalAlignment(JTextField.CENTER);

        m_20 = new JTextField(5);
        m_20.setPreferredSize(new Dimension(50, 50));
        m_20.setHorizontalAlignment(JTextField.CENTER);

        m_21 = new JTextField(5);
        m_21.setPreferredSize(new Dimension(50, 50));
        m_21.setHorizontalAlignment(JTextField.CENTER);

        m_22 = new JTextField(5);
        m_22.setPreferredSize(new Dimension(50, 50));
        m_22.setHorizontalAlignment(JTextField.CENTER);

        matrix.add(m_00);
        matrix.add(m_01);
        matrix.add(m_02);
        matrix.add(m_10);
        matrix.add(m_11);
        matrix.add(m_12);
        matrix.add(m_20);
        matrix.add(m_21);
        matrix.add(m_22);

        for (JTextField tf : matrix) {
            matrix_panel.add(tf);
        }

    }

    /**
     * Funcion que retorna el frame
     *
     * @return retorna el frame
     */
    public JFrame getFrame() {
        return frame_filter;
    }

}
