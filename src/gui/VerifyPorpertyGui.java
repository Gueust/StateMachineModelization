package gui;

import graph.GraphFactoryAEFD;
import graph.Model;
import graph.verifiers.AbstractVerificationUnit;
import graph.verifiers.Verifier;

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
        // TODO Auto-generated catch block
        e2.printStackTrace();
      }
    } else {
      try {
        fos = new FileOutputStream("verification_log.txt");
      } catch (FileNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
    TeeOutputStream myOut = new TeeOutputStream(System.out, fos);
    PrintStream ps = new PrintStream(myOut);
    System.setOut(ps);
    TeeOutputStream myErr = new TeeOutputStream(System.err, fos);
    PrintStream ps_err = new PrintStream(myErr);
    System.setErr(ps_err);
    GraphFactoryAEFD factory = new GraphFactoryAEFD();
    Model model = null, proof = null;
    /* We verify that file has been chosen for uploading the model */
    if (functional_file_chooser.getSelectedFile() != null) {
      try {
        /* We build the functional model by parsing the file */
        model = factory.buildModel(functional_file_chooser
            .getSelectedFile()
            .getAbsolutePath()
            .toString(), "functional model");
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      /* we walk through properties and take the HashMap containing the checkbox */
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
          // TODO
          e1.printStackTrace();
        } catch (IllegalAccessException e1) {
          // TODO
          e1.printStackTrace();
        }
        if (check_box.getValue().isSelected()) {
          verifier.addVerification(verication_unit);
        }
      }
      System.out.print("The verification of the functional model in the file "
          + functional_file_chooser.getSelectedFile().getName() + " : \n\n");
      if (check_all.isSelected()) {

        verifier.checkAll(model, true);
      } else {
        verifier.check(model, true);
      }
      if (proof_file_chooser.getSelectedFile() != null) {
        try {
          model = factory.buildModel(proof_file_chooser
              .getSelectedFile()
              .getAbsolutePath()
              .toString(), "proof model");
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        System.out.print("The verification of the proof model in the file "
            + functional_file_chooser.getSelectedFile().getName() + " : \n\n");
        if (check_all.isSelected()) {
          verifier.checkAll(proof, true);
        } else {
          verifier.check(proof, true);
        }
      }

    } else {
      JOptionPane.showMessageDialog(frame,
          "There is no functional model loaded",
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
