package gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;

import abstractGraph.conditions.Variable;
import abstractGraph.events.ExternalEvent;
import engine.GraphSimulator;
import graph.GlobalState;
import graph.Model;
import graph.State;
import graph.StateMachine;
import gui.variousModels.SortedListModel;
import gui.variousModels.TextAreaRenderer;
import gui.variousModels.TransitionModel;

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
  private JXTable table;
  private JScrollPane table_scroll_pane;
  private GraphSimulator simulator;
  private LinkedList<ExternalEvent> external_events = new LinkedList<ExternalEvent>();

  public MainWindow(final GraphSimulator simulator)
      throws HeadlessException {
    this.simulator = simulator;

    HashSet<String> initialization_events = new HashSet<String>();
    HashMap<String, String> pairs_of_ctl = simulator.getModel().regroupCTL();
    for (Entry<String, String> pair : pairs_of_ctl.entrySet()) {
      initialization_events.add(pair.getKey());
    }

    // this.simulator.init(initialization_events);

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("Simulation");
    menuBar.add(mnFile);

    JMenuItem mntmNewSimulation = new JMenuItem("New Simulation");
    mnFile.add(mntmNewSimulation);
    mntmNewSimulation.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        HomePage home_page = new HomePage();
        home_page.pack();
        home_page.setLocationRelativeTo(null);
        home_page.setVisible(true);
        MainWindow.this.dispose();

      }
    });

    JMenuItem mntmRestartSimulation = new JMenuItem("Restart Simulation");
    mnFile.add(mntmRestartSimulation);
    mntmRestartSimulation.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        MainWindow main_window = new MainWindow(simulator);
        main_window.pack();
        main_window.setLocationRelativeTo(null);
        main_window.setVisible(true);
        MainWindow.this.dispose();
      }
    });

    JMenuItem mntmInitializeSimulation = new JMenuItem("Initialize simulation");
    mnFile.add(mntmInitializeSimulation);

    JPanel fifo_panel = new JPanel();
    JPanel global_state_panel = new JPanel();
    JPanel user_option_panel = new JPanel();
    JPanel transitions_panel = new JPanel();
    proof_internal_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    proof_internal_event_FIFO.setBorder(new TitledBorder(null,
        "proof internal event", TitledBorder.LEADING, TitledBorder.TOP, null,
        null));
    functionnal_internal_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    functionnal_internal_event_FIFO.setBorder(new TitledBorder(null,
        "functionnal internal event", TitledBorder.LEADING, TitledBorder.TOP,
        null, null));
    proof_external_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    proof_external_event_FIFO.setBorder(new TitledBorder(null,
        "proof external events", TitledBorder.LEADING, TitledBorder.TOP, null,
        null));
    functionnal_external_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    functionnal_external_event_FIFO.setBorder(new TitledBorder(null,
        "functionnal external events", TitledBorder.LEADING, TitledBorder.TOP,
        null, null));
    commands = new JList<String>(new DefaultListModel<String>());
    commands.setBorder(new TitledBorder(null, "commands", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));
    functional_state_tag_change_FIFO = new JList<String>(
        new DefaultListModel<String>());
    functional_state_tag_change_FIFO.setBorder(new TitledBorder(UIManager
        .getBorder("TitledBorder.border"),
        "functionnal transitions pull by event", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));
    proof_state_tag_change_FIFO = new JList<String>(
        new DefaultListModel<String>());
    proof_state_tag_change_FIFO.setBorder(new TitledBorder(null,
        "proof transitions pull by event", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));
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
                                        GroupLayout.DEFAULT_SIZE, 481,
                                        Short.MAX_VALUE)
                                    .addPreferredGap(
                                        ComponentPlacement.UNRELATED)
                                    .addComponent(fifo_panel,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE))
                            .addComponent(transitions_panel,
                                GroupLayout.DEFAULT_SIZE, 1168, Short.MAX_VALUE))
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
                                GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                            .addComponent(fifo_panel, GroupLayout.DEFAULT_SIZE,
                                371, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(transitions_panel, GroupLayout.DEFAULT_SIZE,
                        181, Short.MAX_VALUE)
                    .addGap(2))
        );

    ButtonGroup button_group = new ButtonGroup();
    final JRadioButton rdbtnCompleteSimulation =
        new JRadioButton("Complete Simulation");
    rdbtnCompleteSimulation.setSelected(true);
    rdbtnCompleteSimulation.setPreferredSize(new Dimension(101, 23));

    JButton btnSimulate = new JButton("next");
    btnSimulate
        .setToolTipText("To enable this button, you must initialize the simulation. You can do it by clicking on Simulation then initialize simulation.");
    btnSimulate.setEnabled(false);
    btnSimulate.setPreferredSize(new Dimension(101, 23));
    btnSimulate.setMinimumSize(new Dimension(101, 23));
    btnSimulate.setMaximumSize(new Dimension(101, 23));

    JButton btnUploadExternalEvent = new JButton("Upload External Event");
    btnUploadExternalEvent.setPreferredSize(new Dimension(101, 23));
    btnUploadExternalEvent.setMinimumSize(new Dimension(101, 23));
    btnUploadExternalEvent.setMaximumSize(new Dimension(101, 23));

    JButton btnEatExternalEvents = new JButton("Eat External Events");
    btnEatExternalEvents
        .setToolTipText("To enable this button, you must initialize the simulation. You can do it by clicking on Simulation then initialize simulation.");
    btnEatExternalEvents.setEnabled(false);
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

    TransitionModel transitions_model = new TransitionModel();
    transitions_model.addModel(simulator.getModel());
    transitions_model.addModel(simulator.getProof());

    table = new JXTable(transitions_model);
    table.packColumn(1, 1);
    table_scroll_pane = new JScrollPane(table);

    table.getColumnModel().getColumn(1).setMaxWidth(50);
    table.getColumnModel().getColumn(2).setMaxWidth(50);

    TableColumnModel cmodel = table.getColumnModel();
    TextAreaRenderer textAreaRenderer = new TextAreaRenderer();

    for (int i = 0; i < table.getColumnCount(); i++) {
      cmodel.getColumn(i).setCellRenderer(textAreaRenderer);
    }

    GroupLayout gl_transitions_panel = new GroupLayout(transitions_panel);
    gl_transitions_panel.setHorizontalGroup(
        gl_transitions_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_transitions_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(table_scroll_pane, GroupLayout.DEFAULT_SIZE,
                        1148, Short.MAX_VALUE)
                    .addContainerGap())
        );
    gl_transitions_panel.setVerticalGroup(
        gl_transitions_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_transitions_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(table_scroll_pane, GroupLayout.DEFAULT_SIZE,
                        159, Short.MAX_VALUE)
                    .addContainerGap())
        );
    transitions_panel.setLayout(gl_transitions_panel);

    JScrollPane state_machine_scroll_panel = new JScrollPane();
    state_machine_scroll_panel
        .setBorder(BorderFactory
            .createTitledBorder("Automaton > Current State"));

    JScrollPane variables_scroll_panel = new JScrollPane();
    variables_scroll_panel
        .setBorder(BorderFactory.createTitledBorder("Variable > Value"));

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
                                                GroupLayout.DEFAULT_SIZE, 227,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                functional_state_tag_change_FIFO,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 227,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                functionnal_internal_event_FIFO,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 227,
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
                                                        proof_state_tag_change_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        229, Short.MAX_VALUE)
                                                    .addContainerGap())
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_internal_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        229, Short.MAX_VALUE)
                                                    .addGap(10))
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_external_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        229, Short.MAX_VALUE)
                                                    .addContainerGap())))
                            .addGroup(
                                gl_fifo_panel.createSequentialGroup()
                                    .addComponent(commands,
                                        GroupLayout.DEFAULT_SIZE, 462,
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
                                GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(functionnal_internal_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE,
                            false)
                            .addComponent(functional_state_tag_change_FIFO,
                                GroupLayout.PREFERRED_SIZE, 81,
                                GroupLayout.PREFERRED_SIZE)
                            .addComponent(proof_state_tag_change_FIFO,
                                GroupLayout.PREFERRED_SIZE, 81,
                                GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(functionnal_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addComponent(proof_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(commands, GroupLayout.DEFAULT_SIZE, 84,
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

    mntmInitializeSimulation.addActionListener(new InitializeSimulation(
        btnSimulate, btnEatExternalEvents));

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setPreferredSize(new Dimension(1200, 630));
    pack();
  }

  private class InitializeSimulation implements ActionListener {

    private JButton next;
    private JButton eat;

    public InitializeSimulation(JButton next, JButton eat) {
      this.eat = eat;
      this.next = next;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      InitializationSelector window =
          new InitializationSelector(MainWindow.this,
              simulator.getModel().regroupCTL());

      HashSet<String> CTLs = window.showDialog();
      simulator.init(CTLs);
      eat.setEnabled(true);
      next.setEnabled(true);
    }
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
    DefaultListModel<String> listModel =
        (DefaultListModel<String>) list.getModel();

    for (StateMachine state_machine : model) {
      State state = global_state.getState(state_machine);
      listModel.addElement(state_machine.getName() + " --> "
          + (state == null ? "??" : state.getId()));
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
