package gui;

import graph.GraphFactoryAEFD;
import graph.Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import abstractGraph.verifiers.AbstractVerificationUnit;
import abstractGraph.verifiers.Verifier;
import utils.TeeOutputStream;

public class VerifyPorpertyGui implements ActionListener {
  private HashMap<Class<?>, JCheckBox> properties;
  private JCheckBox check_all;
  private JFileChooser log_file_chooser;
  private JFileChooser functional_file_chooser;
  private JFileChooser proof_file_chooser;
  private JFrame frame;

  public VerifyPorpertyGui(HashMap<Class<?>, JCheckBox> properties,
      JCheckBox check_all, JFileChooser log_file_chooser,
      JFileChooser functional_file_chooser,
      JFileChooser proof_file_chooser, JFrame frame) {
    this.properties = properties;
    this.check_all = check_all;
    this.log_file_chooser = log_file_chooser;
    this.functional_file_chooser = functional_file_chooser;
    this.proof_file_chooser = proof_file_chooser;
    this.frame = frame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    /* Change the output stream of the system to the log file */
    FileOutputStream fos = null;
    if (log_file_chooser.getSelectedFile() != null) {
      try {
        fos = new FileOutputStream(log_file_chooser
            .getSelectedFile()
            .getAbsoluteFile());
      } catch (FileNotFoundException e2) {
        e2.printStackTrace();
      }

      TeeOutputStream myOut = new TeeOutputStream(System.out, fos);
      PrintStream ps = new PrintStream(myOut);
      System.setOut(ps);
      TeeOutputStream myErr = new TeeOutputStream(System.err, fos);
      PrintStream ps_err = new PrintStream(myErr);
      System.setErr(ps_err);
    }

    GraphFactoryAEFD factory = new GraphFactoryAEFD(null);
    Model model, proof;

    if (functional_file_chooser.getSelectedFile() == null) {
      JOptionPane.showMessageDialog(frame,
          "There is no functional model loaded",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    /* We build the verifier according to the checkboxes */
    Verifier verifier = new Verifier();

    Iterator<Entry<Class<?>, JCheckBox>> check_box_iterator = properties
        .entrySet()
        .iterator();
    while (check_box_iterator.hasNext()) {
      Entry<Class<?>, JCheckBox> check_box = check_box_iterator.next();
      AbstractVerificationUnit verication_unit = null;
      try {
        verication_unit = (AbstractVerificationUnit) check_box
            .getKey()
            .newInstance();
      } catch (InstantiationException e1) {
        e1.printStackTrace();
        System.exit(-1);
      } catch (IllegalAccessException e1) {
        e1.printStackTrace();
        System.exit(-1);
      }
      if (check_box.getValue().isSelected()) {
        verifier.addVerification(verication_unit);
      }
    }

    /* We verify that file has been chosen for uploading the model */
    try {
      /* We build the functional model by parsing the file */
      model = factory.buildModel(functional_file_chooser
          .getSelectedFile()
          .getAbsolutePath()
          .toString(), "functional model");
    } catch (IOException e1) {
      System.err.println(e1.toString());
      return;
    }

    System.out.print("The verification of the functional model in the file "
        + functional_file_chooser.getSelectedFile().getName() + " : \n\n");
    if (check_all.isSelected()) {

      if (verifier.checkAll(model, true)) {
        JOptionPane.showMessageDialog(frame,
            "All the properties are respected by the functionnal model.",
            "Success",
            JOptionPane.PLAIN_MESSAGE);
      } else {
        JOptionPane
            .showMessageDialog(
                frame,
                "Some properties aren't respected by the functionnal model. Look in the log file for more details",
                "Error",
                JOptionPane.ERROR_MESSAGE);
      }
    } else {
      if (verifier.check(model, true)) {
        JOptionPane.showMessageDialog(frame,
            "All the properties are respected by the functionnal model.",
            "Success",
            JOptionPane.PLAIN_MESSAGE);
      } else {
        JOptionPane
            .showMessageDialog(
                frame,
                "Some properties aren't respected by the functionnal model. Look in the log file for more details",
                "Error",
                JOptionPane.ERROR_MESSAGE);
      }
    }

    if (proof_file_chooser.getSelectedFile() != null) {
      try {
        proof = factory.buildModel(proof_file_chooser
            .getSelectedFile()
            .getAbsolutePath()
            .toString(), "proof model");
      } catch (IOException e1) {
        System.err.println(e1.toString());
        return;
      }

      System.out.print("The verification of the proof model in the file "
          + functional_file_chooser.getSelectedFile().getName() + " : \n\n");

      if (check_all.isSelected()) {
        if (verifier.checkAll(proof, true)) {
          JOptionPane.showMessageDialog(frame,
              "All the properties are respected by the proof model.",
              "Success",
              JOptionPane.PLAIN_MESSAGE);
        } else {
          JOptionPane
              .showMessageDialog(
                  frame,
                  "Some properties aren't respected by the proof model. Look in the log file for more details",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
        }
      } else {
        if (verifier.check(proof, true)) {
          JOptionPane.showMessageDialog(frame,
              "All the properties are respected by the functionnal model.",
              "Success",
              JOptionPane.PLAIN_MESSAGE);
        } else {
          JOptionPane
              .showMessageDialog(
                  frame,
                  "Some properties aren't respected by the functionnal model. Look in the log file for more details",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
}
