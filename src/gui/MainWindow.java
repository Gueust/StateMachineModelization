package gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;
import engine.GraphSimulator;
import graph.Model;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
  JList<SingleEvent> proof_internal_event_FIFO;

  JList<SingleEvent> functionnal_internal_event_FIFO;
  JList<SingleEvent> proof_external_event_FIFO;
  JList<ExternalEvent> functionnal_external_event_FIFO;
  JList<CommandEvent> commands;
  JList functional_state_tag_change_FIFO;
  JList proof_state_tag_change_FIFO;

  private JTable table;
  GraphSimulator simulator;
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

    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(groupLayout
        .createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(groupLayout
                .createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout
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
    groupLayout.setVerticalGroup(groupLayout
        .createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(groupLayout
                .createParallelGroup(Alignment.LEADING, false)
                .addComponent(user_option_panel, 0, 0,
                    Short.MAX_VALUE)
                .addComponent(global_state_panel,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fifo_panel, GroupLayout.DEFAULT_SIZE,
                    390, Short.MAX_VALUE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(transitions_panel,
                GroupLayout.PREFERRED_SIZE, 181,
                GroupLayout.PREFERRED_SIZE)
            .addContainerGap(117, Short.MAX_VALUE))
        );

    ButtonGroup button_group = new ButtonGroup();
    final JRadioButton rdbtnCompleteSimulation =
        new JRadioButton("Complete Simulation");
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

    JTextPane textPane = new JTextPane();

    final JRadioButton rdbtnOneExternalEvent =
        new JRadioButton("One External Event Step");

    final JRadioButton rdbtnOneInternalEvent =
        new JRadioButton("One Internal Event Step");

    button_group.add(rdbtnCompleteSimulation);
    button_group.add(rdbtnOneExternalEvent);
    button_group.add(rdbtnOneInternalEvent);

    GroupLayout gl_user_option_panel = new GroupLayout(user_option_panel);
    gl_user_option_panel.setHorizontalGroup(gl_user_option_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING,
            gl_user_option_panel.createSequentialGroup()
                .addGroup(gl_user_option_panel
                    .createParallelGroup(Alignment.TRAILING)
                    .addComponent(btnEatExternalEvents,
                        GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .addGroup(gl_user_option_panel.createSequentialGroup()
                        .addGap(4)
                        .addComponent(textPane,
                            GroupLayout.DEFAULT_SIZE, 170,
                            Short.MAX_VALUE))
                    .addGroup(gl_user_option_panel.createSequentialGroup()
                        .addGap(2)
                        .addComponent(btnSimulate,
                            GroupLayout.DEFAULT_SIZE, 172,
                            Short.MAX_VALUE))
                    .addGroup(Alignment.LEADING,
                        gl_user_option_panel.createSequentialGroup()
                            .addGap(4)
                            .addGroup(gl_user_option_panel
                                .createParallelGroup(Alignment.LEADING)
                                .addComponent(
                                    rdbtnCompleteSimulation,
                                    Alignment.TRAILING,
                                    GroupLayout.DEFAULT_SIZE, 170,
                                    Short.MAX_VALUE)
                                .addComponent(
                                    rdbtnOneInternalEvent,
                                    GroupLayout.DEFAULT_SIZE, 170,
                                    Short.MAX_VALUE)
                                .addComponent(
                                    rdbtnOneExternalEvent,
                                    GroupLayout.DEFAULT_SIZE, 170,
                                    Short.MAX_VALUE)
                                .addComponent(
                                    btnUploadExternalEvent,
                                    GroupLayout.DEFAULT_SIZE, 170,
                                    Short.MAX_VALUE))))
                .addContainerGap())
        );
    gl_user_option_panel.setVerticalGroup(gl_user_option_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(gl_user_option_panel.createSequentialGroup()
            .addContainerGap()
            .addComponent(btnUploadExternalEvent,
                GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
            .addGap(92)
            .addComponent(rdbtnCompleteSimulation,
                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(rdbtnOneExternalEvent)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(rdbtnOneInternalEvent)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(btnSimulate, GroupLayout.PREFERRED_SIZE,
                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(46)
            .addComponent(textPane, GroupLayout.PREFERRED_SIZE, 70,
                GroupLayout.PREFERRED_SIZE)
            .addGap(18)
            .addComponent(btnEatExternalEvents,
                GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
            .addGap(45))
        );
    user_option_panel.setLayout(gl_user_option_panel);

    table = new JTable();
    GroupLayout gl_transitions_panel = new GroupLayout(transitions_panel);
    gl_transitions_panel.setHorizontalGroup(gl_transitions_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(gl_transitions_panel.createSequentialGroup()
            .addContainerGap()
            .addComponent(table, GroupLayout.DEFAULT_SIZE, 799,
                Short.MAX_VALUE)
            .addContainerGap())
        );
    gl_transitions_panel.setVerticalGroup(gl_transitions_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(gl_transitions_panel.createSequentialGroup()
            .addContainerGap()
            .addComponent(table, GroupLayout.DEFAULT_SIZE, 159,
                Short.MAX_VALUE)
            .addContainerGap())
        );
    transitions_panel.setLayout(gl_transitions_panel);

    JList state_machines_current_state = new JList();

    JList variables = new JList();

    GroupLayout gl_global_state_panel = new GroupLayout(global_state_panel);
    gl_global_state_panel.setHorizontalGroup(
        gl_global_state_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(Alignment.LEADING,
                gl_global_state_panel.createSequentialGroup()
                    .addGap(11)
                    .addComponent(state_machines_current_state,
                        GroupLayout.DEFAULT_SIZE, 138,
                        Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(variables, GroupLayout.DEFAULT_SIZE, 138,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );
    gl_global_state_panel.setVerticalGroup(
        gl_global_state_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_global_state_panel
                .createSequentialGroup()
                .addContainerGap()
                .addGroup(gl_global_state_panel
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(state_machines_current_state,
                        GroupLayout.DEFAULT_SIZE, 364,
                        Short.MAX_VALUE)
                    .addComponent(variables, GroupLayout.DEFAULT_SIZE,
                        364, Short.MAX_VALUE))
                .addContainerGap())
        );
    global_state_panel.setLayout(gl_global_state_panel);

    proof_internal_event_FIFO = new JList<SingleEvent>();

    functionnal_internal_event_FIFO = new JList<SingleEvent>();

    proof_external_event_FIFO = new JList<SingleEvent>();

    functionnal_external_event_FIFO = new JList<ExternalEvent>();

    commands = new JList<CommandEvent>();

    functional_state_tag_change_FIFO = new JList();

    proof_state_tag_change_FIFO = new JList();

    GroupLayout gl_fifo_panel = new GroupLayout(fifo_panel);

    gl_fifo_panel.setHorizontalGroup(gl_fifo_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(gl_fifo_panel
            .createSequentialGroup()
            .addGap(15)
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_fifo_panel
                    .createSequentialGroup()
                    .addGroup(gl_fifo_panel
                        .createParallelGroup(Alignment.TRAILING)
                        .addComponent(
                            functionnal_external_event_FIFO,
                            Alignment.LEADING,
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
                    .addGroup(gl_fifo_panel
                        .createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_fifo_panel
                            .createSequentialGroup()
                            .addComponent(
                                proof_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE,
                                142, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(gl_fifo_panel
                            .createSequentialGroup()
                            .addComponent(
                                proof_internal_event_FIFO,
                                GroupLayout.DEFAULT_SIZE,
                                142, Short.MAX_VALUE)
                            .addGap(10))
                        .addGroup(gl_fifo_panel
                            .createSequentialGroup()
                            .addComponent(
                                proof_state_tag_change_FIFO,
                                GroupLayout.DEFAULT_SIZE,
                                142, Short.MAX_VALUE)
                            .addContainerGap())))
                .addGroup(gl_fifo_panel.createSequentialGroup()
                    .addComponent(commands,
                        GroupLayout.DEFAULT_SIZE, 288,
                        Short.MAX_VALUE)
                    .addContainerGap())))
        );
    gl_fifo_panel.setVerticalGroup(gl_fifo_panel
        .createParallelGroup(Alignment.LEADING)
        .addGroup(gl_fifo_panel.createSequentialGroup()
            .addContainerGap()
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.BASELINE)
                .addComponent(proof_internal_event_FIFO,
                    GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addComponent(functionnal_internal_event_FIFO,
                    GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.BASELINE, false)
                .addComponent(functional_state_tag_change_FIFO,
                    GroupLayout.PREFERRED_SIZE, 86,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(proof_state_tag_change_FIFO,
                    GroupLayout.PREFERRED_SIZE, 86,
                    GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(gl_fifo_panel
                .createParallelGroup(Alignment.BASELINE)
                .addComponent(functionnal_external_event_FIFO,
                    GroupLayout.PREFERRED_SIZE, 86,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(proof_external_event_FIFO,
                    GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(commands, GroupLayout.DEFAULT_SIZE, 90,
                Short.MAX_VALUE)
            .addGap(15))
        );
    fifo_panel.setLayout(gl_fifo_panel);
    getContentPane().setLayout(groupLayout);
    // TODO Auto-generated constructor stub

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
  }

  private void updateLists() {
    fillInList(proof_external_event_FIFO,
        simulator.getExternalProofEventQueue());
    fillInList(functionnal_external_event_FIFO, external_events);
    fillInList(functionnal_internal_event_FIFO,
        simulator.getInternalFunctionalEventQueue());
    fillInList(proof_internal_event_FIFO,
        simulator.getInternalProofEventQueue());

  }

  private <T> void fillInList(JList<T> list, LinkedList<T> data) {
    DefaultListModel<T> listModel = (DefaultListModel<T>) list.getModel();
    listModel.removeAllElements();
    for (T one_data : data) {
      listModel.addElement(one_data);
    }
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
