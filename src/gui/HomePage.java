package gui;

import graph.GraphFactoryAEFD;
import graph.verifiers.CoherentVariablesWriting;
import graph.verifiers.DeterminismChecker;
import graph.verifiers.NoUselessVariables;
import graph.verifiers.SingleWritingChecker;
import graph.verifiers.WrittenAtLeastOnceChecker;

import java.awt.HeadlessException;
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

    this.setTitle("New Simulation");

    JPanel file_upload_panel = new JPanel();
    JPanel verification_panel = new JPanel();
    JButton btnVerifyProperties = new JButton("Verify Properties");
    JPanel checkbox_panel = new JPanel();

    JScrollPane scrollPane = new JScrollPane();
    checkbox_panel.setVisible(true);

    JCheckBox chckbxCheckAll = new JCheckBox("Check All");
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
    chckbxDeterminism.setEnabled(false);
    chckbxDeterminism.setSelected(true);

    JCheckBox chckbxNoConcurrentWritting = new JCheckBox(
        "No concurrent writting");
    chckbxNoConcurrentWritting.setEnabled(false);
    chckbxNoConcurrentWritting.setSelected(true);

    JCheckBox chckbxCoherentWritting = new JCheckBox("Coherent writting");
    chckbxCoherentWritting.setSelected(true);

    JCheckBox chckbxUselessVariables = new JCheckBox("Useless variables");
    chckbxUselessVariables.setSelected(true);

    JCheckBox chckbxWrittenAtLeast = new JCheckBox("Written at least once");
    chckbxWrittenAtLeast.setSelected(true);

    HashMap<Class<?>, JCheckBox> property_hashmap = new HashMap<Class<?>, JCheckBox>();
    property_hashmap.put(DeterminismChecker.class, chckbxDeterminism);
    property_hashmap
        .put(SingleWritingChecker.class, chckbxNoConcurrentWritting);
    property_hashmap.put(NoUselessVariables.class, chckbxUselessVariables);
    property_hashmap
        .put(CoherentVariablesWriting.class, chckbxCoherentWritting);
    property_hashmap
        .put(WrittenAtLeastOnceChecker.class, chckbxWrittenAtLeast);

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
                                    .addComponent(chckbxUselessVariables,
                                        GroupLayout.DEFAULT_SIZE, 344,
                                        Short.MAX_VALUE))
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(chckbxCoherentWritting,
                                        GroupLayout.DEFAULT_SIZE, 344,
                                        Short.MAX_VALUE))
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(chckbxNoConcurrentWritting,
                                        GroupLayout.DEFAULT_SIZE, 344,
                                        Short.MAX_VALUE))
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(chckbxDeterminism,
                                        GroupLayout.DEFAULT_SIZE, 344,
                                        Short.MAX_VALUE))
                            .addGroup(
                                gl_checkbox_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(chckbxWrittenAtLeast,
                                        GroupLayout.DEFAULT_SIZE, 344,
                                        Short.MAX_VALUE)))
                    .addContainerGap())
        );
    gl_checkbox_panel.setVerticalGroup(
        gl_checkbox_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(
                gl_checkbox_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(chckbxDeterminism)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxNoConcurrentWritting)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxCoherentWritting)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxUselessVariables)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxWrittenAtLeast)
                    .addPreferredGap(ComponentPlacement.RELATED, 177,
                        Short.MAX_VALUE)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
    checkbox_panel.setLayout(gl_checkbox_panel);
    verification_panel.setLayout(gl_verification_panel);

    JTextArea txtrFunctionalModel = new JTextArea();
    txtrFunctionalModel.setEditable(false);
    txtrFunctionalModel.setFocusable(false);
    txtrFunctionalModel.setText("Functional model");

    JButton btnLoadFunctionalModel = new JButton("Load functional model");

    JTextArea txtrProofModel = new JTextArea();
    txtrProofModel.setEditable(false);
    txtrProofModel.setText("Proof model");

    JButton btnLoadProofModel = new JButton("Load proof model");

    JButton btnSimulation = new JButton("Simulation");

    GroupLayout gl_file_upload_panel = new GroupLayout(file_upload_panel);
    gl_file_upload_panel.setHorizontalGroup(
        gl_file_upload_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl_file_upload_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        gl_file_upload_panel.createParallelGroup(
                            Alignment.TRAILING)
                            .addComponent(btnSimulation, Alignment.LEADING,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(
                                Alignment.LEADING,
                                gl_file_upload_panel.createParallelGroup(
                                    Alignment.TRAILING, false)
                                    .addComponent(btnLoadFunctionalModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE, 184,
                                        Short.MAX_VALUE)
                                    .addComponent(txtrFunctionalModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE, 184,
                                        Short.MAX_VALUE)
                                    .addComponent(btnLoadProofModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE, 184,
                                        Short.MAX_VALUE)
                                    .addComponent(txtrProofModel,
                                        Alignment.LEADING,
                                        GroupLayout.DEFAULT_SIZE, 184,
                                        Short.MAX_VALUE)))
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
                    .addPreferredGap(ComponentPlacement.RELATED, 100,
                        Short.MAX_VALUE)
                    .addComponent(btnSimulation, GroupLayout.PREFERRED_SIZE,
                        58, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
    file_upload_panel.setLayout(gl_file_upload_panel);
    // TODO Auto-generated constructor stub

    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
        groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
                groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(file_upload_panel,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(verification_panel, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
    groupLayout
        .setVerticalGroup(
        groupLayout
            .createParallelGroup(Alignment.TRAILING)
            .addGroup(
                groupLayout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        groupLayout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(verification_panel,
                                Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                297, Short.MAX_VALUE)
                            .addComponent(file_upload_panel, Alignment.LEADING,
                                GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                    .addContainerGap())
        );
    getContentPane().setLayout(groupLayout);

    /* The actions performed by the different buttons */
    GraphFactoryAEFD graph_factory;
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
    btnLoadProofModel.addActionListener(new LinkFileChoserToTextArea(
        proof_file_chooser,
        txtrProofModel));

    JFileChooser log_file_chooser = new JFileChooser();
    String default_log_file_name = "verification_log.txt";
    File default_log = new File(default_log_file_name);
    txtrVerificationLog.setText(default_log_file_name);
    log_file_chooser.setSelectedFile(default_log);
    log_file_chooser.setFileFilter(filter);
    btnChangeLogFile.addActionListener(new LinkFileChoserToTextArea(
        log_file_chooser,
        txtrVerificationLog));

    btnVerifyProperties.addActionListener(new VerifyPorpertyGui(
        property_hashmap, chckbxCheckAll, log_file_chooser,
        functional_file_chooser, proof_file_chooser, this));

  }

  public static void main(String[] args) {
    ToolTipManager.sharedInstance().setInitialDelay(100);
    HomePage home_page = new HomePage();
    home_page.pack();
    home_page.setLocationRelativeTo(null);
    home_page.setVisible(true);

  }
}
