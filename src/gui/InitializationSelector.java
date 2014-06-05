package gui;

import gui.actions.CancelAction;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

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

  // Faire comme ExportChooser.showSaveDialog() qui retourne ue valeur
  public HashSet<String> showDialog() {
    setVisible(true);

    return null;
  }

  /**
   * @return A hashSet containing the selected options.
   */
  public HashSet<String> getSelected() {
    HashSet<String> result = new HashSet<String>();
    for (JToggleButton button : buttons) {
      if (button.isSelected()) {
        result.add(button.getText());
      }
    }
    return result;
  }

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
