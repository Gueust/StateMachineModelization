package gui.actions;

import engine.SequentialGraphSimulator;
import graph.GraphFactoryAEFD;
import graph.Model;
import gui.MainWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LaunchSimulationAction implements ActionListener {
  private JFileChooser functional_file_chooser;
  private JFileChooser proof_file_chooser;
  private JFileChooser FCI_file_chooser;
  private JFrame frame;

  public LaunchSimulationAction(JFileChooser functional_file_chooser,
      JFileChooser proof_file_chooser, JFileChooser FCI_file_chooser,
      JFrame frame) {
    this.functional_file_chooser = functional_file_chooser;
    this.proof_file_chooser = proof_file_chooser;
    this.FCI_file_chooser = FCI_file_chooser;
    this.frame = frame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Model functional_model = null;
    Model proof_model = null;
    SequentialGraphSimulator simulator;
    if (functional_file_chooser.getSelectedFile() != null) {
      GraphFactoryAEFD factory = new GraphFactoryAEFD();
      try {
        functional_model = loadFile(factory, functional_file_chooser,
            "functional model");
        functional_model.build();
        if (FCI_file_chooser.getSelectedFile() != null) {
          functional_model.loadFCI(FCI_file_chooser
              .getSelectedFile()
              .getAbsolutePath());
        }
      } catch (IOException e1) {
        e1.printStackTrace();
        return;
      }
      if (proof_file_chooser.getSelectedFile() != null) {
        try {
          proof_model = loadFile(factory, proof_file_chooser,
              "proof model");
          proof_model.build();
        } catch (IOException e1) {
          e1.printStackTrace();
          return;
        }
        // TODO add the initialization of a global state to add to the file
        simulator = new SequentialGraphSimulator(functional_model, proof_model);
      } else {
        simulator = new SequentialGraphSimulator(functional_model);
      }
      frame.dispose();
      MainWindow main_window = new MainWindow(simulator);
      main_window.setLocationRelativeTo(frame);
      main_window.setVisible(true);
    } else {
      JOptionPane.showMessageDialog(frame,
          "There is no functional model loaded",
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }

  }

  private Model loadFile(GraphFactoryAEFD factory,
      JFileChooser model_file_chooser, String name) throws IOException {
    return factory.buildModel(model_file_chooser
        .getSelectedFile()
        .getAbsolutePath()
        .toString(), name);
  }

}
