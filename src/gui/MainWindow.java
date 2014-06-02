package gui;

import java.awt.Dimension;
import java.awt.HeadlessException;

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

import engine.GraphSimulator;
import graph.Model;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
  private JTable table;
  GraphSimulator simulator;
  boolean with_proof;

  public MainWindow(GraphSimulator simulator, boolean with_proof)
      throws HeadlessException {
    this.simulator = simulator;
    this.with_proof = with_proof;

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
                            .addComponent(transitions_panel,
                                GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
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
                                        Short.MAX_VALUE)))
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
                                GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                            .addComponent(global_state_panel,
                                GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                            .addComponent(fifo_panel, GroupLayout.DEFAULT_SIZE,
                                491, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(transitions_panel,
                        GroupLayout.PREFERRED_SIZE, 181,
                        GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );

    JRadioButton rdbtnCompleteSimulation =
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

    JRadioButton rdbtnOneExternalEvent =
        new JRadioButton("One External Event Step");

    JRadioButton rdbtnOneInternalEvent =
        new JRadioButton("One Internal Event Step");
    GroupLayout gl_user_option_panel = new GroupLayout(user_option_panel);
    gl_user_option_panel.setHorizontalGroup(
        gl_user_option_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_user_option_panel.createSequentialGroup()
                    .addGroup(
                        gl_user_option_panel.createParallelGroup(
                            Alignment.LEADING)
                            .addComponent(btnEatExternalEvents,
                                GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                            .addGroup(
                                Alignment.TRAILING,
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(textPane,
                                        GroupLayout.DEFAULT_SIZE, 174,
                                        Short.MAX_VALUE))
                            .addGroup(
                                Alignment.TRAILING,
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(4)
                                    .addGroup(
                                        gl_user_option_panel
                                            .createParallelGroup(
                                                Alignment.TRAILING)
                                            .addComponent(
                                                rdbtnOneInternalEvent,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 174,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                rdbtnOneExternalEvent,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 174,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                rdbtnCompleteSimulation,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 174,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                btnUploadExternalEvent,
                                                Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 174,
                                                Short.MAX_VALUE)))
                            .addGroup(
                                Alignment.TRAILING,
                                gl_user_option_panel.createSequentialGroup()
                                    .addGap(2)
                                    .addComponent(btnSimulate,
                                        GroupLayout.DEFAULT_SIZE, 172,
                                        Short.MAX_VALUE)))
                    .addContainerGap())
        );
    gl_user_option_panel.setVerticalGroup(
        gl_user_option_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_user_option_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnUploadExternalEvent,
                        GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addGap(58)
                    .addComponent(rdbtnCompleteSimulation,
                        GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
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
                        GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addGap(45))
        );
    user_option_panel.setLayout(gl_user_option_panel);

    table = new JTable();
    GroupLayout gl_transitions_panel = new GroupLayout(transitions_panel);
    gl_transitions_panel.setHorizontalGroup(
        gl_transitions_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_transitions_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(table, GroupLayout.DEFAULT_SIZE, 799,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );
    gl_transitions_panel.setVerticalGroup(
        gl_transitions_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_transitions_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(table, GroupLayout.DEFAULT_SIZE, 159,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );
    transitions_panel.setLayout(gl_transitions_panel);

    JList list = new JList();

    JList list_1 = new JList();
    GroupLayout gl_global_state_panel = new GroupLayout(global_state_panel);
    gl_global_state_panel.setHorizontalGroup(
        gl_global_state_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                Alignment.LEADING,
                gl_global_state_panel.createSequentialGroup()
                    .addGap(11)
                    .addComponent(list, GroupLayout.DEFAULT_SIZE, 138,
                        Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(list_1, GroupLayout.DEFAULT_SIZE, 138,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );
    gl_global_state_panel.setVerticalGroup(
        gl_global_state_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_global_state_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_global_state_panel.createParallelGroup(
                            Alignment.BASELINE)
                            .addComponent(list, GroupLayout.DEFAULT_SIZE, 364,
                                Short.MAX_VALUE)
                            .addComponent(list_1, GroupLayout.DEFAULT_SIZE,
                                364, Short.MAX_VALUE))
                    .addContainerGap())
        );
    global_state_panel.setLayout(gl_global_state_panel);

    JList proof_internal_event_FIFO = new JList();

    JList functionnal_internal_event_FIFO = new JList();

    JList functionnal_temporary_event_FIFO = new JList();

    JList proof_temporary_event_FIFO = new JList();

    JList proof_external_event_FIFO = new JList();

    JList functionnal_external_event_FIFO = new JList();

    JList commands = new JList();

    JList functional_state_tag_change_FIFO = new JList();

    JList proof_state_tag_change_FIFOlist_3 = new JList();
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
                            .createParallelGroup(Alignment.LEADING)
                            .addGroup(
                                gl_fifo_panel.createSequentialGroup()
                                    .addComponent(commands,
                                        GroupLayout.DEFAULT_SIZE, 288,
                                        Short.MAX_VALUE)
                                    .addContainerGap())
                            .addGroup(
                                gl_fifo_panel
                                    .createSequentialGroup()
                                    .addComponent(
                                        functionnal_external_event_FIFO,
                                        GroupLayout.DEFAULT_SIZE, 139,
                                        Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(proof_external_event_FIFO,
                                        GroupLayout.DEFAULT_SIZE, 143,
                                        Short.MAX_VALUE)
                                    .addContainerGap())
                            .addGroup(
                                gl_fifo_panel
                                    .createSequentialGroup()
                                    .addGroup(
                                        gl_fifo_panel
                                            .createParallelGroup(
                                                Alignment.TRAILING)
                                            .addGroup(
                                                Alignment.LEADING,
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        functionnal_temporary_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        139, Short.MAX_VALUE)
                                                    .addPreferredGap(
                                                        ComponentPlacement.UNRELATED)
                                                    .addComponent(
                                                        proof_temporary_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        139, Short.MAX_VALUE))
                                            .addGroup(
                                                Alignment.LEADING,
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        functionnal_internal_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        139, Short.MAX_VALUE)
                                                    .addPreferredGap(
                                                        ComponentPlacement.UNRELATED)
                                                    .addComponent(
                                                        proof_internal_event_FIFO,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        139, Short.MAX_VALUE))
                                            .addGroup(
                                                Alignment.LEADING,
                                                gl_fifo_panel
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        functional_state_tag_change_FIFO,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        139,
                                                        GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(
                                                        ComponentPlacement.RELATED,
                                                        10, Short.MAX_VALUE)
                                                    .addComponent(
                                                        proof_state_tag_change_FIFOlist_3,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        139,
                                                        GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(
                                                        ComponentPlacement.RELATED)))
                                    .addGap(10))))
        );
    gl_fifo_panel.setVerticalGroup(
        gl_fifo_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_fifo_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(proof_internal_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(functionnal_internal_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(functionnal_temporary_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(proof_temporary_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(functional_state_tag_change_FIFO,
                                GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addComponent(proof_state_tag_change_FIFOlist_3,
                                GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl_fifo_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(functionnal_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addComponent(proof_external_event_FIFO,
                                GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(commands, GroupLayout.DEFAULT_SIZE, 82,
                        Short.MAX_VALUE)
                    .addGap(12))
        );
    fifo_panel.setLayout(gl_fifo_panel);
    getContentPane().setLayout(groupLayout);
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    Model model = new Model("test");
    GraphSimulator simulator = new GraphSimulator(model);
    boolean with_proof = false;
    MainWindow main_window = new MainWindow(simulator, with_proof);
    main_window.pack();
    main_window.setLocationRelativeTo(null);
    main_window.setVisible(true);
  }
}
