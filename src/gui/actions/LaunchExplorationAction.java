package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utils.Monitoring;
import engine.ModelChecker;
import engine.SequentialGraphSimulator;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

public class LaunchExplorationAction implements ActionListener {

  private JFileChooser functional_file_chooser;
  private JFileChooser proof_file_chooser;
  private JFileChooser FCI_file_chooser;
  private JFrame frame;
  private ModelChecker<GlobalState, StateMachine, State, Transition> model_checker =
      new ModelChecker<GlobalState, StateMachine, State, Transition>();
  private SequentialGraphSimulator simulator;
  private JCheckBox verbose_box;

  public LaunchExplorationAction(JFileChooser functional_file_chooser,
      JFileChooser proof_file_chooser, JFileChooser FCI_file_chooser,
      JFrame frame, JCheckBox verbose_box) {
    this.functional_file_chooser = functional_file_chooser;
    this.proof_file_chooser = proof_file_chooser;
    this.frame = frame;
    this.verbose_box = verbose_box;
    this.FCI_file_chooser = FCI_file_chooser;
  }

  public void actionPerformed(ActionEvent e) {
    Model functional_model = null;
    Model proof_model = null;
    if (functional_file_chooser.getSelectedFile() == null) {
      JOptionPane.showMessageDialog(frame,
          "There is no functional model loaded",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (proof_file_chooser.getSelectedFile() == null) {
      JOptionPane.showMessageDialog(frame,
          "There is no proof model loaded",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
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
    simulator.setVerbose(verbose_box.isSelected());

    long startTime = System.nanoTime();

    model_checker.reset();
    simulator.generateAllInitialStates(model_checker);

    GlobalState result = model_checker.verify(simulator);

    System.out.println("Result : " + result);

    long estimatedTime = System.nanoTime() - startTime;
    Monitoring.printFullPeakMemoryUsage();
    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");

    if (proof_file_chooser.getSelectedFile() == null) {
      JOptionPane.showMessageDialog(frame,
          "Proof failed.",
          "Error",
          JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(frame,
          "Proof success.",
          "Success",
          JOptionPane.PLAIN_MESSAGE);
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
