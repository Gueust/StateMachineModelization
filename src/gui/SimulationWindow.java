package gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;

import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.events.ExternalEvent;
import engine.SequentialGraphSimulator;
import graph.GlobalState;
import graph.Model;
import graph.State;
import graph.StateMachine;
import gui.variousModels.SortedListModel;
import gui.variousModels.TextAreaRenderer;
import gui.variousModels.TransitionModel;

@SuppressWarnings("serial")
public class SimulationWindow extends JFrame {
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
  private SequentialGraphSimulator simulator;
  private LinkedList<ExternalEvent> external_events = new LinkedList<ExternalEvent>();
  private HashMap<String, Boolean> initial_CTLs;
  private GlobalState global_state = new GlobalState();
  private JMenuItem mntmRestartSimulation;

  JButton btnSimulate;
  JButton btnEatExternalEvents;

  public SimulationWindow(final SequentialGraphSimulator simulator)
      throws HeadlessException {
    this.simulator = simulator;

    HashSet<String> initialization_events = new HashSet<String>();
    HashMap<String, String> pairs_of_ctl = simulator.getModel().regroupCTL();
    for (Entry<String, String> pair : pairs_of_ctl.entrySet()) {
      initialization_events.add(pair.getKey());
    }

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
        SimulationWindow.this.dispose();

      }
    });

    mntmRestartSimulation = new JMenuItem("Restart Simulation");
    mntmRestartSimulation.setEnabled(false);
    mntmRestartSimulation
        .setToolTipText("Restart the simulation with the initialization chosen in the beginning. It's enabled after the initialization is done.");
    mnFile.add(mntmRestartSimulation);
    mntmRestartSimulation.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        global_state = simulator.init(initial_CTLs);
        SimulationWindow main_window = new SimulationWindow(simulator);
        main_window.global_state = global_state;
        main_window.pack();
        main_window.setLocationRelativeTo(null);
        main_window.setVisible(true);
        main_window.enableExecutionButton();
        main_window.updateLists();
        main_window.enableRestart();

        SimulationWindow.this.dispose();

      }
    });

    JMenuItem mntmInitializeSimulation = new JMenuItem("Initialize simulation");
    mnFile.add(mntmInitializeSimulation);

    JPanel fifo_panel = new JPanel();
    JPanel global_state_panel = new JPanel();
    JPanel user_option_panel = new JPanel();
    JPanel transitions_panel = new JPanel();

    /* Panel for the internal events of the functionnal model */
    functionnal_internal_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    JScrollPane functionnal_internal_event_FIFO_scroll_panel =
        new JScrollPane(functionnal_internal_event_FIFO);
    functionnal_internal_event_FIFO_scroll_panel.setBorder(new TitledBorder(
        null,
        "functionnal internal event", TitledBorder.LEADING, TitledBorder.TOP,
        null, null));

    /* Panels for the external events */
    functionnal_external_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    JScrollPane functionnal_external_event_FIFO_scroll_panel =
        new JScrollPane(functionnal_external_event_FIFO);
    functionnal_external_event_FIFO_scroll_panel.setBorder(new TitledBorder(
        null,
        "functionnal external events", TitledBorder.LEADING, TitledBorder.TOP,
        null, null));

    /* Panels for the command */
    commands = new JList<String>(new DefaultListModel<String>());
    JScrollPane commands_scroll_panel =
        new JScrollPane(commands);
    commands_scroll_panel.setBorder(new TitledBorder(null, "commands",
        TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    /* Panels for the modifications of the states */
    functional_state_tag_change_FIFO =
        new JList<String>(new DefaultListModel<String>());
    JScrollPane functional_state_tag_change_FIFO_scroll_panel =
        new JScrollPane(functional_state_tag_change_FIFO);
    functional_state_tag_change_FIFO_scroll_panel.setBorder(new TitledBorder(
        UIManager
            .getBorder("TitledBorder.border"),
        "functionnal transitions pull by event", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    /* Panel for the modification of states of the proof model */
    proof_state_tag_change_FIFO = new JList<String>(
        new DefaultListModel<String>());
    JScrollPane proof_state_tag_change_FIFO_scroll_panel =
        new JScrollPane(proof_state_tag_change_FIFO);
    proof_state_tag_change_FIFO_scroll_panel.setBorder(new TitledBorder(null,
        "proof transitions pull by event", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    /* Panel for internal event of the proof model */
    proof_internal_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    JScrollPane proof_internal_event_FIFO_scroll_panel =
        new JScrollPane(proof_internal_event_FIFO);
    proof_internal_event_FIFO_scroll_panel.setBorder(new TitledBorder(null,
        "proof internal event", TitledBorder.LEADING, TitledBorder.TOP, null,
        null));

    /* Panel for the external events of the proof model */
    proof_external_event_FIFO = new JList<String>(
        new DefaultListModel<String>());
    JScrollPane proof_external_event_FIFO_scroll_panel =
        new JScrollPane(proof_external_event_FIFO);
    proof_external_event_FIFO_scroll_panel.setBorder(new TitledBorder(null,
        "proof external events", TitledBorder.LEADING, TitledBorder.TOP, null,
        null));

    /* Panel for the variables */
    variables_list = new JList<String>(new SortedListModel());
    state_machines_current_state = new JList<String>(new SortedListModel());

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

    btnSimulate = new JButton("next");
    btnSimulate
        .setToolTipText("To enable this button, you must initialize the simulation. You can do it by clicking on Simulation then initialize simulation.");
    btnSimulate.setEnabled(false);
    btnSimulate.setPreferredSize(new Dimension(101, 23));
    btnSimulate.setMinimumSize(new Dimension(101, 23));
    btnSimulate.setMaximumSize(new Dimension(101, 23));

    JButton btnUploadExternalEvent = new JButton("Upload External Event");

    btnUploadExternalEvent
        .setToolTipText("Allow the user to uplaod a list of external event from a chosen file. The file must contain each external event on a line. ");
    btnUploadExternalEvent.setPreferredSize(new Dimension(101, 23));
    btnUploadExternalEvent.setMinimumSize(new Dimension(101, 23));
    btnUploadExternalEvent.setMaximumSize(new Dimension(101, 23));

    btnEatExternalEvents = new JButton("Eat External Events");
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
    entry_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
                                                functionnal_external_event_FIFO_scroll_panel,
                                                GroupLayout.DEFAULT_SIZE, 227,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                functional_state_tag_change_FIFO_scroll_panel,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 227,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                functionnal_internal_event_FIFO_scroll_panel,
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
                                                        proof_state_tag_change_FIFO_scroll_panel,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        229, Short.MAX_VALUE)
                                                    .addContainerGap())
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_internal_event_FIFO_scroll_panel,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        229, Short.MAX_VALUE)
                                                    .addGap(10))
                                            .addGroup(
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        proof_external_event_FIFO_scroll_panel,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        229, Short.MAX_VALUE)
                                                    .addContainerGap())))
                            .addGroup(
                                gl_fifo_panel.createSequentialGroup()
                                    .addComponent(commands_scroll_panel,
                                        GroupLayout.DEFAULT_SIZE, 462,
                                        Short.MAX_VALUE)
                                    .addContainerGap())))
        );
    gl_fifo_panel.setVerticalGroup(gl_fifo_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(gl_fifo_panel
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.BASELINE)
                .addComponent(
                    proof_internal_event_FIFO_scroll_panel,
                    GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addComponent(
                    functionnal_internal_event_FIFO_scroll_panel,
                    GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.BASELINE, false)
                .addComponent(
                    functional_state_tag_change_FIFO_scroll_panel,
                    GroupLayout.PREFERRED_SIZE, 81,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(
                    proof_state_tag_change_FIFO_scroll_panel,
                    GroupLayout.PREFERRED_SIZE, 81,
                    GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.BASELINE)
                .addComponent(
                    functionnal_external_event_FIFO_scroll_panel,
                    GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addComponent(
                    proof_external_event_FIFO_scroll_panel,
                    GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(commands_scroll_panel,
                GroupLayout.DEFAULT_SIZE, 84,
                Short.MAX_VALUE)
            .addGap(15))
        );
    fifo_panel.setLayout(gl_fifo_panel);
    getContentPane().setLayout(groupLayout);
    // TODO Auto-generated constructor stub

    btnUploadExternalEvent.addActionListener(new ActionListener() {
      private JFileChooser external_event_file_chooser = new JFileChooser();

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int returnVal = external_event_file_chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          String file_name = external_event_file_chooser
              .getSelectedFile()
              .getAbsolutePath();
          external_event_file_chooser
              .setCurrentDirectory(external_event_file_chooser
                  .getSelectedFile());
          try {
            LinkedList<ExternalEvent> list_external_event = simulator
                .getModel()
                .loadScenario(file_name);
            external_events.addAll(list_external_event);
            updateLists();

          } catch (IOException e) {
            JOptionPane.showMessageDialog(SimulationWindow.this,
                "Error with the selected File",
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });

    /* fill the lists of variables and current_state */
    fillInCurrentStates(state_machines_current_state, simulator.getModel(),
        global_state);
    removeAllEllement(variables_list);
    fillInVariables(variables_list, simulator.getModel(), global_state);
    if (this.simulator.getProof() != null) {
      fillInCurrentStates(state_machines_current_state, simulator
          .getProof(), global_state);
      fillInVariables(variables_list, simulator.getProof(), global_state);
    }
    /* listener for the next button */
    btnSimulate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (rdbtnCompleteSimulation.isSelected()) {
          SimulationWindow.this.global_state =
              SimulationWindow.this.simulator.executeAll(global_state,
                  external_events);
        } else if (rdbtnOneExternalEvent.isSelected()) {
          /* Finish to execute the functional and proof models */
          SimulationWindow.this.global_state =
              SimulationWindow.this.simulator.execute(global_state, null);
          ExternalEvent event = external_events.poll();
          SimulationWindow.this.global_state =
              SimulationWindow.this.simulator.executeSimulator(global_state,
                  event);
        } else if (rdbtnOneInternalEvent.isSelected()) {
          SimulationWindow.this.simulator.processSmallestStep(global_state,
              external_events);
        }
        updateLists();
      }
    });

    btnEatExternalEvents.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        for (String event_name : entry_list.getSelectedValuesList()) {
          external_events.add(new ExternalEvent(event_name));
        }
        fillInList(functionnal_external_event_FIFO, external_events);
      }
    });

    mntmInitializeSimulation
        .addActionListener(new InitializeSimulation(
            btnSimulate, btnEatExternalEvents, mntmRestartSimulation));

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setPreferredSize(new Dimension(1200, 630));
    pack();
  }

  private class InitializeSimulation implements ActionListener {

    private JButton next;
    private JButton eat;
    private JMenuItem mntmRestartSimulation;

    public InitializeSimulation(JButton next, JButton eat,
        JMenuItem mntmRestartSimulation) {
      this.eat = eat;
      this.next = next;
      this.mntmRestartSimulation = mntmRestartSimulation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
      InitializationSelector window =
          new InitializationSelector(SimulationWindow.this,
              simulator.getModel().regroupCTL());

      HashMap<String, Boolean> CTLs = window.showDialog();
      System.out.print(CTLs);
      initial_CTLs = (HashMap<String, Boolean>) CTLs.clone();

      global_state = simulator.init(CTLs);
      updateLists();

      eat.setEnabled(true);
      next.setEnabled(true);
      mntmRestartSimulation.setEnabled(true);
    }
  }

  private void updateLists() {
    fillInList(proof_external_event_FIFO,
        simulator.getExternalProofEventQueue());
    LinkedList<ExternalEvent> tmp_list = (LinkedList<ExternalEvent>) simulator
        .getACTFCIList();
    while (!tmp_list.isEmpty()) {
      external_events.addFirst(tmp_list.removeLast());
    }
    fillInList(functionnal_external_event_FIFO, external_events);
    fillInList(functionnal_internal_event_FIFO,
        simulator.getInternalFunctionalEventQueue());
    fillInList(proof_internal_event_FIFO,
        simulator.getInternalProofEventQueue());
    fillInList(commands, simulator.getCommandsQueue());
    removeAllEllement(state_machines_current_state);
    fillInCurrentStates(state_machines_current_state, simulator.getModel(),
        global_state);
    removeAllEllement(variables_list);
    fillInVariables(variables_list, simulator.getModel(), global_state);
    fillInTransitionPull(functional_state_tag_change_FIFO, simulator
        .getFunctionnalTransitionsPullList());
    if (simulator.getProof() != null) {
      fillInCurrentStates(state_machines_current_state, simulator
          .getProof(),
          global_state);
      fillInVariables(variables_list, simulator.getProof(), global_state);
      fillInTransitionPull(proof_state_tag_change_FIFO, simulator
          .getProofTransitionsPullList());
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
    SortedListModel listModel =
        (SortedListModel) list.getModel();

    for (StateMachine state_machine : model) {
      State state = global_state.getState(state_machine);
      listModel.addElement(state_machine.getName() + " --> "
          + (state == null ? "??" : state.getId()));
    }
  }

  private void fillInVariables(JList<String> list, Model model,
      GlobalState global_state) {
    SortedListModel listModel =
        (SortedListModel) list.getModel();
    Iterator<EnumeratedVariable> variable_iterator = model
        .iteratorExistingVariables();
    while (variable_iterator.hasNext()) {
      EnumeratedVariable variable = variable_iterator.next();
      if (variable.getVarname().startsWith("CTL_")) {
        continue;
      }
      try {
        listModel.addElement(variable + " = "
            + global_state.getStringValue(variable));
      } catch (NoSuchElementException e) {
        listModel.addElement(variable + " = " + "??");
      }

    }

    /* We also set the P5, P6 and P7 values */
    listModel.addElement("is_safe(P5)" + " = " + global_state.isSafe());
    listModel.addElement("is_legal(P6)" + " = " + global_state.isLegal());
    listModel.addElement("is_functional(P7)" + " = " + global_state.isNotP7());

  }

  private void fillInTransitionPull(JList<String> list,
      LinkedHashMap<StateMachine, State> current_state_change_list) {
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    DefaultListModel<String> listModel =
        (DefaultListModel<String>) list.getModel();
    listModel.removeAllElements();
    Iterator<Entry<StateMachine, State>> transitions_iterator = current_state_change_list
        .entrySet()
        .iterator();
    while (transitions_iterator.hasNext()) {
      Entry<StateMachine, State> transition = transitions_iterator.next();
      listModel.addElement(transition.getKey().getName() + " --> "
          + transition.getValue().getId());
    }
  }

  private void removeAllEllement(JList<String> list) {
    SortedListModel listModel = (SortedListModel) list
        .getModel();
    listModel.removeAllElements();
  }

  public static void main(String[] args) {
    Model model = new Model("test");
    SequentialGraphSimulator simulator = new SequentialGraphSimulator(model);
    SimulationWindow main_window = new SimulationWindow(simulator);
    main_window.pack();
    main_window.setLocationRelativeTo(null);
    main_window.setVisible(true);
  }

  public void enableExecutionButton() {
    btnEatExternalEvents.setEnabled(true);
    btnSimulate.setEnabled(true);
  }

  private void enableRestart() {
    mntmRestartSimulation.setEnabled(true);
  }
}
