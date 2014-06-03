package gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

import abstractGraph.conditions.Variable;
import abstractGraph.events.ExternalEvent;
import engine.GraphSimulator;
import graph.GlobalState;
import graph.Model;
import graph.StateMachine;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
  private JList<String> proof_internal_event_FIFO;
  private JList<String> functionnal_internal_event_FIFO;
  private JList<String> proof_external_event_FIFO;
  private JList<String> functionnal_external_event_FIFO;
  private JList<String> commands;
  private JList<String> functional_state_tag_change_FIFO;
  private JList<String> proof_state_tag_change_FIFO;
  private JList<String> state_machines_current_state;
  private JList<String> variables_list;
  private JTable table;
  private GraphSimulator simulator;
  private LinkedList<ExternalEvent> external_events = new LinkedList<ExternalEvent>();

  public MainWindow(GraphSimulator simulator)
      throws HeadlessException {
    this.simulator = simulator;

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("New");
    menuBar.add(mnFile);

    JMenuItem mntmNewSimulation = new JMenuItem("New Simulation");
    mnFile.add(mntmNewSimulation);

    JMenuItem mntmRestartSimulation = new JMenuItem("Restart Simulation");
    mnFile.add(mntmRestartSimulation);

    JPanel fifo_panel = new JPanel();
    JPanel global_state_panel = new JPanel();
    JPanel user_option_panel = new JPanel();
    JPanel transitions_panel = new JPanel();
    proof_internal_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    functionnal_internal_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    proof_external_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    functionnal_external_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    commands = new JList<String>(new DefaultListModel<String>());
    functional_state_tag_change_FIFO = new JList<String>(
        new DefaultListModel<String>());
    proof_state_tag_change_FIFO = new JList<String>(
        new DefaultListModel<String>());
    variables_list = new JList<String>(new DefaultListModel<String>());
    state_machines_current_state = new JList<String>(
        new DefaultListModel<String>());

    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout
        .setHorizontalGroup(
        groupLayout
            .createParallelGroup(Alignment.LEADING)
            .addGroup(
                groupLayout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        groupLayout
                            .createParallelGroup(Alignment.LEADING)
                            .addGroup(
                                groupLayout
                                    .createSequentialGroup()
                                    .addComponent(user_option_panel,
                                        GroupLayout.PREFERRED_SIZE, 184,
                                        GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(global_state_panel,
                                        GroupLayout.DEFAULT_SIZE, 307,
                                        Short.MAX_VALUE)
                                    .addPreferredGap(
                                        ComponentPlacement.UNRELATED)
                                    .addComponent(fifo_panel,
                                        GroupLayout.DEFAULT_SIZE, 313,
                                        Short.MAX_VALUE))
                            .addComponent(transitions_panel,
                                GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE))
                    .addGap(6))
        );
    groupLayout.setVerticalGroup(
        groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
                groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(user_option_panel,
                                GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(global_state_panel,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fifo_panel, GroupLayout.DEFAULT_SIZE,
                                390, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(transitions_panel,
                        GroupLayout.PREFERRED_SIZE, 181,
                        GroupLayout.PREFERRED_SIZE)
                    .addGap(2))
        );

    ButtonGroup button_group = new ButtonGroup();
    final JRadioButton rdbtnCompleteSimulation =
        new JRadioButton("Complete Simulation");
    rdbtnCompleteSimulation.setSelected(true);
    rdbtnCompleteSimulation.setPreferredSize(new Dimension(101, 23));

    JButton btnSimulate = new JButton("next");
    btnSimulate.setPreferredSize(new Dimension(101, 23));
    btnSimulate.setMinimumSize(new Dimension(101, 23));
    btnSimulate.setMaximumSize(new Dimension(101, 23));

    JButton btnUploadExternalEvent = new JButton("Upload External Event");
    btnUploadExternalEvent.setPreferredSize(new Dimension(101, 23));
    btnUploadExternalEvent.setMinimumSize(new Dimension(101, 23));
    btnUploadExternalEvent.setMaximumSize(new Dimension(101, 23));

    JButton btnEatExternalEvents = new JButton("Eat External Events");
    btnEatExternalEvents.setPreferredSize(new Dimension(101, 23));
    btnEatExternalEvents.setMaximumSize(new Dimension(101, 23));
    btnEatExternalEvents.setMinimumSize(new Dimension(101, 23));

    final JRadioButton rdbtnOneExternalEvent =
        new JRadioButton("One External Event Step");

    final JRadioButton rdbtnOneInternalEvent =
        new JRadioButton("One Internal Event Step");

    button_group.add(rdbtnCompleteSimulation);
    button_group.add(rdbtnOneExternalEvent);
    button_group.add(rdbtnOneInternalEvent);

    JScrollPane scrollPane = new JScrollPane();
    GroupLayout gl_user_option_panel = new GroupLayout(user_option_panel);
    gl_user_option_panel.setHorizontalGroup(
        gl_user_option_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                Alignment.TRAILING,
                gl_user_option_panel.createSequentialGroup()
                    .addGroup(
                        gl_user_option_panel.createParallelGroup(
                            Alignment.TRAILING)
                            .addGroup(
                                Alignment.LEADING,
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(2)
                                    .addComponent(btnEatExternalEvents,
                                        GroupLayout.DEFAULT_SIZE, 180,
                                        Short.MAX_VALUE))
                            .addGroup(
                                Alignment.LEADING,
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(2)
                                    .addComponent(scrollPane,
                                        GroupLayout.DEFAULT_SIZE, 180,
                                        Short.MAX_VALUE))
                            .addGroup(
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(btnUploadExternalEvent,
                                        GroupLayout.PREFERRED_SIZE, 176,
                                        GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(rdbtnCompleteSimulation,
                                        GroupLayout.PREFERRED_SIZE, 176,
                                        GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(rdbtnOneExternalEvent,
                                        GroupLayout.PREFERRED_SIZE, 176,
                                        GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(rdbtnOneInternalEvent,
                                        GroupLayout.PREFERRED_SIZE, 176,
                                        GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(btnSimulate,
                                        GroupLayout.PREFERRED_SIZE, 178,
                                        GroupLayout.PREFERRED_SIZE)))
                    .addGap(2))
        );
    gl_user_option_panel.setVerticalGroup(
        gl_user_option_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_user_option_panel.createSequentialGroup()
                    .addGap(11)
                    .addComponent(btnUploadExternalEvent,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addGap(92)
                    .addComponent(rdbtnCompleteSimulation,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbtnOneExternalEvent)
                    .addComponent(rdbtnOneInternalEvent)
                    .addGap(2)
                    .addComponent(btnSimulate, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 79,
                        Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnEatExternalEvents,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addGap(44))
        );

    final JList<String> entry_list = new JList<String>(new SortedListModel());
    scrollPane.setViewportView(entry_list);
    entry_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    SortedListModel listModel = (SortedListModel) entry_list.getModel();
    user_option_panel.setLayout(gl_user_option_panel);
    Iterator<ExternalEvent> data =
        this.simulator.getModel().iteratorExternalEvents();
    while (data.hasNext()) {
      listModel.addElement(data.next().toString());
    }

    table = new JTable();
    GroupLayout gl_transitions_panel = new GroupLayout(transitions_panel);
    gl_transitions_panel.setHorizontalGroup(
        gl_transitions_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_transitions_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(table, GroupLayout.DEFAULT_SIZE, 800,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );
    gl_transitions_panel.setVerticalGroup(
        gl_transitions_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_transitions_panel.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(table, GroupLayout.PREFERRED_SIZE, 159,
                        GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
    transitions_panel.setLayout(gl_transitions_panel);

    JScrollPane state_machine_scroll_panel = new JScrollPane();

    JScrollPane variables_scroll_panel = new JScrollPane();

    GroupLayout gl_global_state_panel = new GroupLayout(global_state_panel);
    gl_global_state_panel.setHorizontalGroup(
        gl_global_state_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_global_state_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(state_machine_scroll_panel,
                        GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(variables_scroll_panel,
                        GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addContainerGap())
        );
    gl_global_state_panel
        .setVerticalGroup(
        gl_global_state_panel
            .createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_global_state_panel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_global_state_panel.createParallelGroup(
                            Alignment.BASELINE)
                            .addComponent(state_machine_scroll_panel,
                                GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                            .addComponent(variables_scroll_panel,
                                GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
                    .addContainerGap())
        );

    variables_scroll_panel.setViewportView(variables_list);

    state_machine_scroll_panel.setViewportView(state_machines_current_state);
    global_state_panel.setLayout(gl_global_state_panel);

    GroupLayout gl_fifo_panel = new GroupLayout(fifo_panel);
    gl_fifo_panel
        .setHorizontalGroup(
        gl_fifo_panel
            .createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_fifo_panel
                    .createSequentialGroup()
                    .addGap(15)
                    .addGroup(
                        gl_fifo_panel
                            .createParallelGroup(Alignment.TRAILING)
                            .addGroup(
                                gl_fifo_panel
                                    .createSequentialGroup()
                                    .addGroup(
                                        gl_fifo_panel
                                            .createParallelGroup(
                                                Alignment.TRAILING)
                                            .addComponent(
                                                functionnal_external_event_FIFO,
                                                GroupLayout.DEFAULT_SIZE, 140,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                functional_state_tag_change_FIFO,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 140,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                functionnal_internal_event_FIFO,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 140,
                                                Short.MAX_VALUE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(
                                        gl_fifo_panel
                                            .createParallelGroup(
                                                Alignment.TRAILING)
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_external_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        142, Short.MAX_VALUE)
                                                    .addContainerGap())
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_internal_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        142, Short.MAX_VALUE)
                                                    .addGap(10))
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_state_tag_change_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        142, Short.MAX_VALUE)
                                                    .addContainerGap())))
                            .addGroup(
                                gl_fifo_panel.createSequentialGroup()
                                    .addComponent(commands,
                                        GroupLayout.DEFAULT_SIZE, 288,
                                        Short.MAX_VALUE)
                                    .addContainerGap())))
        );
    gl_fifo_panel.setVerticalGroup(
        gl_fifo_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_fifo_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(proof_internal_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(functionnal_internal_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE,
                            false)
                            .addComponent(functional_state_tag_change_FIFO,
                                GroupLayout.PREFERRED_SIZE, 86,
                                GroupLayout.PREFERRED_SIZE)
                            .addComponent(proof_state_tag_change_FIFO,
                                GroupLayout.PREFERRED_SIZE, 86,
                                GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.LEADING)
                            .addComponent(proof_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(functionnal_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(commands, GroupLayout.DEFAULT_SIZE, 92,
                        Short.MAX_VALUE)
                    .addGap(15))
        );
    fifo_panel.setLayout(gl_fifo_panel);
    getContentPane().setLayout(groupLayout);
    // TODO Auto-generated constructor stub

    /* fill the lists of variables and current_state */
    fillInCurrentStates(state_machines_current_state, simulator.getModel(),
        simulator.getGlobalState());
    removeAllEllement(variables_list);
    fillInVariables(variables_list, simulator.getModel(), simulator
        .getGlobalState());
    if (this.simulator.getProof() != null) {
      fillInCurrentStates(state_machines_current_state, simulator
          .getProof(),
          simulator.getGlobalState());
      fillInVariables(variables_list, simulator.getProof(), simulator
          .getGlobalState());
    }
    /* listener for the next button */
    btnSimulate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (rdbtnCompleteSimulation.isSelected()) {
          MainWindow.this.simulator.executeAll(external_events);
        } else if (rdbtnOneExternalEvent.isSelected()) {
          /* Finish to execute the functional and proof models */
          MainWindow.this.simulator.execute(null);
          ExternalEvent event = external_events.poll();
          MainWindow.this.simulator.execute(event);
        } else if (rdbtnOneInternalEvent.isSelected()) {
          MainWindow.this.simulator.processSingleEvent(external_events);
        }
        updateLists();
      }
    });

    btnEatExternalEvents.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        external_events.add(new ExternalEvent(entry_list.getSelectedValue()));
        fillInList(functionnal_external_event_FIFO, external_events);
      }
    });
  }

  private void updateLists() {
    fillInList(proof_external_event_FIFO,
        simulator.getExternalProofEventQueue());
    fillInList(functionnal_external_event_FIFO, external_events);
    fillInList(functionnal_internal_event_FIFO,
        simulator.getInternalFunctionalEventQueue());
    fillInList(proof_internal_event_FIFO,
        simulator.getInternalProofEventQueue());
    removeAllEllement(state_machines_current_state);
    fillInCurrentStates(state_machines_current_state, simulator.getModel(),
        simulator.getGlobalState());
    removeAllEllement(variables_list);
    fillInVariables(variables_list, simulator.getModel(), simulator
        .getGlobalState());
    if (simulator.getProof() != null) {
      fillInCurrentStates(state_machines_current_state, simulator
          .getProof(),
          simulator.getGlobalState());
      fillInVariables(variables_list, simulator.getProof(), simulator
          .getGlobalState());
    }
  }

  private <T> void fillInList(JList<String> list, LinkedList<T> data) {
    DefaultListModel<String> listModel = (DefaultListModel<String>) list
        .getModel();
    listModel.removeAllElements();
    for (T one_data : data) {
      listModel.addElement(one_data.toString());
    }
  }

  private void fillInCurrentStates(JList<String> list, Model model,
      GlobalState global_state) {
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    DefaultListModel<String> listModel = (DefaultListModel<String>) list
        .getModel();
    Iterator<StateMachine> state_machine_iterator = model.iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine = state_machine_iterator.next();
      System.out.print(state_machine.getName() + "\n");
      listModel.addElement(state_machine.getName() + " --> "
          + global_state.getState(state_machine).getId());
    }
  }

  private void fillInVariables(JList<String> list, Model model,
      GlobalState global_state) {
    DefaultListModel<String> listModel = (DefaultListModel<String>) list
        .getModel();
    Iterator<Variable> variable_iterator = model.iteratorExistingVariables();
    while (variable_iterator.hasNext()) {
      Variable variable = variable_iterator.next();
      try {
        listModel.addElement(variable + " = "
            + global_state.getVariableValue(variable));
      } catch (NullPointerException e) {
        listModel.addElement(variable + " = "
            + "??");
      }

    }
  }

  private void removeAllEllement(JList<String> list) {
    DefaultListModel<String> listModel = (DefaultListModel<String>) list
        .getModel();
    listModel.removeAllElements();
  }

  public static void main(String[] args) {
    Model model = new Model("test");
    GraphSimulator simulator = new GraphSimulator(model);
    MainWindow main_window = new MainWindow(simulator);
    main_window.pack();
    main_window.setLocationRelativeTo(null);
    main_window.setVisible(true);
  }
}
