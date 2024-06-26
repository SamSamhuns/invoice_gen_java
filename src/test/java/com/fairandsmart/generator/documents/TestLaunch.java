package com.ssdgen.generator.documents;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestLaunch {
    static int layout;
    static int number;

    private static void launch() throws Exception {
        switch(layout){
            case 1:
                TestAmazonLayout.test(number);
                break;
            case 2:
                TestBDmobilierLayout.test(number);
                break;
            case 3:
                TestCdiscountLayout.test(number);
                break;
            case 4:
                TestDartyLayout.test(number);
                break;
            case 5:
                TestssdgenLayout.test(number);
                break;
            case 6:
                TestLDLCLayout.test(number);
                break;
            case 7:
                TestLoriaLayout.test(number);
                break;
            case 8:
                TestMACOMPLayout.test(number);
                break;
            case 9:
                TestMaterielnetLayout.test(number);
                break;
            case 10:
                TestNatureDecouvertesLayout.test(number);
                break;
            case 11:
                TestgenericLayout.test(number);
                break;
            case 12:
                TestSSDPayslipLayout.test(number);
                break;
            case 13:
                TestSSDReceiptLayout.test(number);
                break;
            case 14:
                TestSSDInvoiceLayout.test(number);
                break;
            default:
                System.out.println("Invalid data");
        }

    }

    public static void main(String[] args){

        JFrame frame = new JFrame("SSD generation");

        JPanel saisie1 = new JPanel(new GridBagLayout());
        //decoration du champ
        saisie1.setPreferredSize(new Dimension(800,30));
        //texte explicatif
        JLabel texte1 = new JLabel("Which layout do you want to generate ? ",JLabel.CENTER);
        texte1.setFont(new java.awt.Font("Helvetica",Font.PLAIN,15));
        saisie1.add(texte1);

        DefaultListCellRenderer centr = new DefaultListCellRenderer();
        centr.setHorizontalAlignment(JLabel.CENTER);
        JComboBox layoutChoice = new JComboBox(new String[] {" ",
                "Amazon",
                "Bdmobilier",
                "Cdiscount",
                "Darty",
                "ssdgen",
                "Ldlc",
                "Loria",
                "Macomp",
                "Materielnet",
                "Nature&Découvertes",
                "Random",
                "SSD Payslip",
                "SSD Receipt",
                "SSD Invoice"});
        layoutChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layout = layoutChoice.getSelectedIndex();
            }
        });
        layoutChoice.setRenderer(centr);
        saisie1.add(layoutChoice);

        JPanel saisie2 = new JPanel(new GridBagLayout());
        //decoration du champ
        saisie1.setPreferredSize(new Dimension(500,30));
        //texte explicatif
        JLabel texte2 = new JLabel("How many documents do you want ? ",JLabel.CENTER);
        texte2.setFont(new java.awt.Font("Helvetica",Font.PLAIN,15));
        saisie2.add(texte2);
        JSpinner spinner = new JSpinner();
        JSpinner.NumberEditor spinnerEditor = new JSpinner.NumberEditor(spinner,"###,##0");
        spinner.setEditor(spinnerEditor);
        spinnerEditor.getModel().setValue(1);
        spinnerEditor.getModel().setStepSize(1);
        spinnerEditor.getModel().setMinimum(1);
        spinner.setPreferredSize(new Dimension(100,30));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                number = (Integer)spinnerEditor.getModel().getNumber();
            }
        });
        saisie2.add(spinner);

        JPanel saisie3 = new JPanel();
        JButton generate = new JButton("Generate");
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    launch();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        saisie3.add(generate);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(saisie1,BorderLayout.NORTH);
        frame.getContentPane().add(saisie2,BorderLayout.CENTER);
        frame.getContentPane().add(saisie3,BorderLayout.SOUTH);
        frame.setSize(new Dimension(500,700));
        frame.pack();
        frame.setVisible(true);

    }
}
