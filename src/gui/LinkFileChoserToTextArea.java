package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

class LinkFileChoserToTextArea implements ActionListener {

  private JFileChooser file_chooser;
  private JFileChooser linked_file_chooser;
  private JTextArea jtext_area;

  public LinkFileChoserToTextArea(JFileChooser file_chooser,
      JTextArea jtext_area, JFileChooser linked_file_chooser) {
    this.file_chooser = file_chooser;
    this.linked_file_chooser = linked_file_chooser;
    this.jtext_area = jtext_area;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    int returnVal = file_chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String file_name = file_chooser.getSelectedFile().getName();
      linked_file_chooser.setCurrentDirectory(file_chooser.getSelectedFile());
      jtext_area.setText(file_name);
      jtext_area.setToolTipText(file_name);
    }
  }
}
