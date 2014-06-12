package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import engine.GraphSimulator;
import engine.ModelChecker;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

public class LaunchExplorationAction implements ActionListener {

  private JFileChooser functional_file_chooser;
  private JFileChooser proof_file_chooser;
  private JFrame frame;
  private ModelChecker<GlobalState, StateMachine, State, Transition> model_checker =
      new ModelChecker<GlobalState, StateMachine, State, Transition>();
  private GraphSimulator simulator;
  private int i = 0;
  private boolean verbose;

  public LaunchExplorationAction(JFileChooser functional_file_chooser,
      JFileChooser proof_file_chooser, JFrame frame, JCheckBox verbose_box) {
    this.functional_file_chooser = functional_file_chooser;
    this.proof_file_chooser = proof_file_chooser;
    this.frame = frame;
    this.verbose = verbose_box.isSelected();
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
    } catch (IOException e1) {
      e1.printStackTrace();
      return;
    }
    try {
      proof_model = loadFile(factory, proof_file_chooser,
          "proof model");

    } catch (IOException e1) {
      e1.printStackTrace();
      return;
    }
    // TODO add the initialization of a global state to add to the file
    simulator = new GraphSimulator(functional_model, proof_model);
    HashMap<String, String> CTL_list = functional_model.regroupCTL();
    System.out.print(verbose);
    simulator.setVerbose(verbose);

    LinkedList<GlobalState> global_states = new LinkedList<GlobalState>();
    long startTime = System.nanoTime();
    verifyWithAllInitialValue(CTL_list.keySet(),
        new HashMap<String, Boolean>(), global_states);
    model_checker.configureInitialGlobalStates(global_states);
    model_checker.configureExternalEvents(simulator
        .getModel()
        .iteratorExternalEvents());

    GlobalState result = model_checker.verify(simulator);
    System.out.println("Result : " + result);
    long estimatedTime = System.nanoTime() - startTime;

    printFullPeakMemoryUsage();

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

  /**
   * 
   * @param set
   *          The list of the positive CTL in the model without a value assigned
   *          to them.
   * @param tmp
   *          An empty HashMap used internally to store the already set CTLs.
   * @return null if the verification finish without meeting
   */
  private void verifyWithAllInitialValue(Set<String> set,
      HashMap<String, Boolean> tmp,
      Collection<GlobalState> result) {
    /* Terminal case */
    if (set.isEmpty()) {
      System.out.println(this.i++);
      simulator.init(tmp);
      result.add(simulator.getGlobalState().clone());
      return;
    }

    /* Recursion */
    Iterator<String> ctl_iterator = set.iterator();
    String ctl_name = ctl_iterator.next();
    ctl_iterator.remove();

    tmp.put(ctl_name, true);
    verifyWithAllInitialValue(new HashSet<String>(set), tmp, result);

    tmp.put(ctl_name, false);
    verifyWithAllInitialValue(new HashSet<String>(set), tmp, result);

  }

  private void printFullPeakMemoryUsage() {
    List<MemoryPoolMXBean> pools = ManagementFactory
        .getMemoryPoolMXBeans();
    long total_used = 0, total_commited = 0;

    for (MemoryPoolMXBean pool : pools) {
      MemoryUsage peak = pool.getPeakUsage();
      total_used += peak.getUsed();
      total_commited += peak.getCommitted();
    }
    System.out.println();
    System.out.printf("Total peak memory used: %,d%n", total_used);
    System.out.printf("Total peak memory reserved: %,d%n", total_commited);
  }
}
