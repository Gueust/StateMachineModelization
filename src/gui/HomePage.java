package gui;

import graph.verifiers.CoherentVariablesWriting;
import graph.verifiers.DeterminismChecker;
import graph.verifiers.InitializationProperties;
import graph.verifiers.NoUselessVariables;
import graph.verifiers.SingleWritingChecker;
import graph.verifiers.WrittenAtLeastOnceChecker;
import gui.actions.LaunchSimulationAction;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class HomePage extends JFrame {

  public HomePage() throws HeadlessException {
    ToolTipManager.sharedInstance().setInitialDelay(100);
    ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

    this.setTitle("New Simulation");

    JPanel file_upload_panel = new JPanel();
    JPanel verification_panel = new JPanel();
    JButton btnVerifyProperties = new JButton("Verify Properties");
    JPanel checkbox_panel = new JPanel();

    JScrollPane scrollPane = new JScrollPane();
    checkbox_panel.setVisible(true);

    JCheckBox chckbxCheckAll = new JCheckBox("Don't stop at the first error");
    chckbxCheckAll
        .setToolTipText("If not checked, the verification will stop at the first error encountered.");
    chckbxCheckAll.setSelected(true);

    JTextArea txtrVerificationLog = new JTextArea();
    txtrVerificationLog
        .setToolTipText("The file where the logs of the verification are written.");
    txtrVerificationLog.setEditable(false);

    JButton btnChangeLogFile = new JButton("Change log file");

    GroupLayout gl_verification_panel = new GroupLayout(verification_panel);
    gl_verification_panel.setHorizontalGroup(
        gl_verification_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_verification_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_verification_panel.createParallelGroup(
                            Alignment.LEADING)
                            .addComponent(checkbox_panel,
                                GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                            .addGroup(
                                gl_verification_panel.createSequentialGroup()
                                    .addComponent(btnVerifyProperties,
                                        GroupLayout.DEFAULT_SIZE, 194,
                                        Short.MAX_VALUE)
                                    .addGap(28)
                                    .addComponent(chckbxCheckAll)
                                    .addGap(65))
                            .addGroup(
                                gl_verification_panel.createSequentialGroup()
                                    .addComponent(btnChangeLogFile,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addGap(10)
                                    .addComponent(txtrVerificationLog,
                                        GroupLayout.DEFAULT_SIZE, 233,
                                        Short.MAX_VALUE)
                                    .addGap(10)))
                    .addContainerGap())
        );
    gl_verification_panel.setVerticalGroup(
        gl_verification_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_verification_panel.createSequentialGroup()
                    .addComponent(checkbox_panel, GroupLayout.DEFAULT_SIZE,
                        301, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl_verification_panel.createParallelGroup(
                            Alignment.TRAILING)
                            .addComponent(txtrVerificationLog,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnChangeLogFile))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl_verification_panel.createParallelGroup(
                            Alignment.BASELINE)
                            .addComponent(chckbxCheckAll)
                            .addComponent(btnVerifyProperties))
                    .addContainerGap())
        );

    JCheckBox chckbxDeterminism = new JCheckBox("Determinism");
    chckbxDeterminism
        .setToolTipText("For each state, check that all the transitions with a same event and (a different destination state or a different list of actions) are exclusive.");
    chckbxDeterminism.setEnabled(false);
    chckbxDeterminism.setSelected(true);

    JCheckBox chckbxNoConcurrentWritting =
        new JCheckBox("No concurrent writting");
    chckbxNoConcurrentWritting
        .setToolTipText("Check that all variable are written by at most one graph.");
    chckbxNoConcurrentWritting.setEnabled(false);
    chckbxNoConcurrentWritting.setSelected(true);

    JCheckBox chckbxCoherentWritting = new JCheckBox("Coherent writting");
    chckbxCoherentWritting
        .setToolTipText("Check that for each state, a variable will always have the same value when the current state of the graph will be that state.");
    chckbxCoherentWritting.setSelected(true);

    JCheckBox chckbxUselessVariables = new JCheckBox("Useless variables");
    chckbxUselessVariables
        .setToolTipText("Check that all the variables that are written are used at least once.");
    chckbxUselessVariables.setSelected(true);

    JCheckBox chckbxWrittenAtLeast = new JCheckBox("Written at least once");
    chckbxWrittenAtLeast
        .setToolTipText("Check that all the variables that are in the condition or event fields are written at least once (i.e. found in a field Action).");
    chckbxWrittenAtLeast.setSelected(true);

    JCheckBox chckbxGoodInitialization = new JCheckBox("Good initialization");
    chckbxGoodInitialization
        .setToolTipText("Verify that every graph contains a state 0. That those state have just only event \"ACT_Init\". And that for those transition, the only variable in the conditions are CTL.");
    chckbxGoodInitialization.setSelected(true);
    chckbxGoodInitialization.setEnabled(false);

    HashMap<Class<?>, JCheckBox> property_hashmap = new HashMap<Class<?>, JCheckBox>();
    property_hashmap.put(DeterminismChecker.class, chckbxDeterminism);
    property_hashmap
        .put(SingleWritingChecker.class, chckbxNoConcurrentWritting);
    property_hashmap.put(NoUselessVariables.class, chckbxUselessVariables);
    property_hashmap
        .put(CoherentVariablesWriting.class, chckbxCoherentWritting);
    property_hashmap
        .put(WrittenAtLeastOnceChecker.class, chckbxWrittenAtLeast);
    property_hashmap
        .put(InitializationProperties.class, chckbxGoodInitialization);

    GroupLayout gl_checkbox_panel = new GroupLayout(checkbox_panel);
    gl_checkbox_panel.setHorizontalGroup(
        gl_checkbox_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_checkbox_panel.createSequentialGroup()
                    .addGroup(
                        gl_checkbox_panel
                            .createParallelGroup(Alignment.LEADING)
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addGap(223)
                                    .addComponent(scrollPane,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(
                                        gl_checkbox_panel.createParallelGroup(
                                            Alignment.LEADING)
                                            .addComponent(
                                                chckbxUselessVariables,
                                                GroupLayout.DEFAULT_SIZE, 349,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                chckbxCoherentWritting,
                                                GroupLayout.DEFAULT_SIZE, 349,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                chckbxNoConcurrentWritting,
                                                GroupLayout.DEFAULT_SIZE, 349,
                                                Short.MAX_VALUE)
                                            .addComponent(chckbxDeterminism,
                                                GroupLayout.DEFAULT_SIZE, 349,
                                                Short.MAX_VALUE)
                                            .addComponent(chckbxWrittenAtLeast,
                                                GroupLayout.DEFAULT_SIZE, 349,
                                                Short.MAX_VALUE)))
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(chckbxGoodInitialization,
                                        GroupLayout.DEFAULT_SIZE, 349,
                                        Short.MAX_VALUE)))
                    .addContainerGap())
        );
    gl_checkbox_panel.setVerticalGroup(
        gl_checkbox_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_checkbox_panel.createSequentialGroup()
                    .addGap(10)
                    .addComponent(chckbxGoodInitialization)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxDeterminism)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxNoConcurrentWritting)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxCoherentWritting)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxUselessVariables)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxWrittenAtLeast)
                    .addPreferredGap(ComponentPlacement.RELATED, 151,
                        Short.MAX_VALUE)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
    checkbox_panel.setLayout(gl_checkbox_panel);
    verification_panel.setLayout(gl_verification_panel);

    JTextArea txtrFunctionalModel = new JTextArea();
    txtrFunctionalModel.setWrapStyleWord(true);
    txtrFunctionalModel.setEditable(false);
    txtrFunctionalModel.setFocusable(false);
    txtrFunctionalModel.setText("Functional model");

    JButton btnLoadFunctionalModel = new JButton("Load functional model");

    JTextArea txtrProofModel = new JTextArea();
    txtrProofModel.setWrapStyleWord(true);
    txtrProofModel.setEditable(false);
    txtrProofModel.setText("Proof model");

    JButton btnLoadProofModel = new JButton("Load proof model");

    JButton btnSimulation = new JButton("Simulation");

    JButton btnRemoveProofModel = new JButton("Remove proof model");

    GroupLayout gl_file_upload_panel = new GroupLayout(file_upload_panel);
    gl_file_upload_panel.setHorizontalGroup(
        gl_file_upload_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_file_upload_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_file_upload_panel.createParallelGroup(
                            Alignment.TRAILING)
                            .addComponent(btnLoadProofModel, Alignment.LEADING,
                                GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                            .addComponent(btnSimulation, Alignment.LEADING,
                                GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                            .addGroup(
                                Alignment.LEADING,
                                gl_file_upload_panel.createParallelGroup(
                                    Alignment.TRAILING)
                                    .addComponent(txtrFunctionalModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE, 184,
                                        Short.MAX_VALUE)
                                    .addComponent(txtrProofModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE, 184,
                                        Short.MAX_VALUE)
                                    .addComponent(btnLoadFunctionalModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE))
                            .addComponent(btnRemoveProofModel,
                                Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                184, Short.MAX_VALUE))
                    .addContainerGap())
        );
    gl_file_upload_panel.setVerticalGroup(
        gl_file_upload_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_file_upload_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(txtrFunctionalModel,
                        GroupLayout.PREFERRED_SIZE, 41,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnLoadFunctionalModel,
                        GroupLayout.PREFERRED_SIZE, 42,
                        GroupLayout.PREFERRED_SIZE)
                    .addGap(11)
                    .addComponent(txtrProofModel, GroupLayout.PREFERRED_SIZE,
                        42, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnLoadProofModel,
                        GroupLayout.PREFERRED_SIZE, 42,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnRemoveProofModel,
                        GroupLayout.PREFERRED_SIZE, 41,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, 53,
                        Short.MAX_VALUE)
                    .addComponent(btnSimulation, GroupLayout.PREFERRED_SIZE,
                        58, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
    file_upload_panel.setLayout(gl_file_upload_panel);
    // TODO Auto-generated constructor stub

    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(groupLayout
        .createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(file_upload_panel,
                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE)
            .addGap(18)
            .addComponent(verification_panel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
        );
    groupLayout.setVerticalGroup(groupLayout
        .createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(groupLayout
                .createParallelGroup(Alignment.TRAILING)
                .addComponent(verification_panel,
                    Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                    297, Short.MAX_VALUE)
                .addComponent(file_upload_panel, Alignment.LEADING,
                    GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
            .addContainerGap())
        );
    getContentPane().setLayout(groupLayout);

    /* The actions performed by the different buttons */
    FileNameExtensionFilter filter =
        new FileNameExtensionFilter("txt files", "txt");

    JFileChooser functional_file_chooser = new JFileChooser();
    functional_file_chooser.setFileFilter(filter);
    btnLoadFunctionalModel.addActionListener(new LinkFileChoserToTextArea(
        functional_file_chooser,
        txtrFunctionalModel));

    JFileChooser proof_file_chooser = new JFileChooser();
    proof_file_chooser.setSelectedFile(null);
    proof_file_chooser.setFileFilter(filter);
    btnLoadProofModel.addActionListener(
        new LinkFileChoserToTextArea(proof_file_chooser, txtrProofModel));

    JFileChooser log_file_chooser = new JFileChooser();
    String default_log_file_name = "verification_log.txt";
    File default_log = new File(default_log_file_name);
    txtrVerificationLog.setText(default_log_file_name);
    log_file_chooser.setSelectedFile(default_log);
    log_file_chooser.setFileFilter(filter);
    btnChangeLogFile.addActionListener(
        new LinkFileChoserToTextArea(log_file_chooser, txtrVerificationLog));

    btnVerifyProperties.addActionListener(new VerifyPorpertyGui(
        property_hashmap, chckbxCheckAll, log_file_chooser,
        functional_file_chooser, proof_file_chooser, this));
    btnSimulation.addActionListener(new LaunchSimulationAction(
        functional_file_chooser, proof_file_chooser, this));
    btnRemoveProofModel.addActionListener(new RemoveProof(proof_file_chooser,
        txtrProofModel));

    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  public class RemoveProof implements ActionListener {
    JFileChooser proof_file_chooser;
    JTextArea text_area;

    public RemoveProof(JFileChooser proof_file_chooser, JTextArea text_area) {
      this.proof_file_chooser = proof_file_chooser;
      this.text_area = text_area;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      proof_file_chooser.setSelectedFile(null);
      text_area.setText("Proof model");
      text_area.setToolTipText("");

    }

  }

  public static void main(String[] args) {

    HomePage home_page = new HomePage();
    home_page.pack();
    home_page.setLocationRelativeTo(null);
    home_page.setVisible(true);

  }
}
