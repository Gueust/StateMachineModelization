package gui;

import graph.conditions.aefdParser.GenerateFormulaAEFD;
import gui.actions.CancelAction;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class InitializationSelector extends JDialog {

  private LinkedList<JToggleButton> buttons =
      new LinkedList<JToggleButton>();

  private boolean has_been_validated = false;

  /**
   * 
   * @param parent
   *          The parent component, used to center the Frame. If null is given,
   *          the frame will be placed at the middle of the screen.
   * @param pairs
   */
  public InitializationSelector(Frame parent, HashMap<String, String> pairs) {
    super(parent);

    JScrollPane scrollPane = new JScrollPane();

    JPanel panel = new JPanel();
    scrollPane.setViewportView(panel);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    getContentPane()
        .setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    JPanel upper_panel = new JPanel();
    upper_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    JButton button_OK = new JButton("Ok");
    button_OK.addActionListener(new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        InitializationSelector.this.dispose();
        has_been_validated = true;
      }
    });

    upper_panel.add(button_OK);

    JButton button_cancel =
        new JButton(new CancelAction(this, "Cancel"));
    upper_panel.add(button_cancel);
    getContentPane().add(upper_panel);
    getContentPane().add(scrollPane);

    for (Entry<String, String> pair : pairs.entrySet()) {
      JPanel one_line = new JPanel();
      one_line.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
      JToggleButton button_1 =
          new JToggleButton(pair.getKey());
      button_1.setSelected(true);
      one_line.add(button_1);

      JToggleButton button_2 =
          new JToggleButton(pair.getValue());
      one_line.add(button_2);

      LinkExlusiveButtons exlusivity =
          new LinkExlusiveButtons(button_1, button_2);
      button_1.addActionListener(exlusivity);
      button_2.addActionListener(exlusivity);

      buttons.add(button_1);
      buttons.add(button_2);

      panel.add(one_line);
    }

    pack();
    setLocationRelativeTo(parent);
    setTitle("Initialization of the model");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setModalityType(ModalityType.DOCUMENT_MODAL);
  }

  public static final int ERROR_OPTION = -1;

  /**
   * Make the Dialog visible. It does not return until the Dialog has been
   * closed.
   * 
   * @return The HashSet of the selected buttons. null of no selection has been
   *         done (Dialog closed without validating).
   */
  public HashMap<String, Boolean> showDialog() {
    setVisible(true);

    if (has_been_validated) {
      return getSelected();
    } else {
      return null;
    }
  }

  /**
   * @return A hashSet containing the selected options.
   */
  public HashMap<String, Boolean> getSelected() {
    HashMap<String, Boolean> result = new HashMap<String, Boolean>();
    for (JToggleButton button : buttons) {
      if (button.isSelected()) {
        String CTL_name = button.getText();
        if (GenerateFormulaAEFD.isPositive(CTL_name)) {
          result.put(CTL_name, true);
        } else {
          result.put(GenerateFormulaAEFD.getOppositeName(CTL_name), false);
        }
      }
    }
    return result;
  }

  /* Used as a minimanistic test */
  public static void main(String[] args) {
    HashMap<String, String> CTLs = new HashMap<String, String>();
    CTLs.put("A1", "A2");
    CTLs.put("B1", "B2");

    InitializationSelector main_window =
        new InitializationSelector(null, CTLs);

    main_window.setVisible(true);
  }
}

class LinkExlusiveButtons implements ActionListener {

  JToggleButton b1, b2;

  public LinkExlusiveButtons(JToggleButton b1, JToggleButton b2) {
    this.b1 = b1;
    this.b2 = b2;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    JToggleButton source = (JToggleButton) event.getSource();
    JToggleButton target;
    if (source == b1) {
      target = b2;
    } else {
      target = b1;
    }

    target.setSelected(!source.isSelected());
  }
}
