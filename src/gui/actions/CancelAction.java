package gui.actions;

/*
 Copyright 2014 Jean-Baptiste Lespiau jeanbaptiste.lespiau@gmail.com

 This file is part of Fizzim.

 Fizzim is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 Fizzim is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class CancelAction extends AbstractAction {
  Window window;

  public CancelAction(Window w, String name, String short_description) {
    window = w;
    putValue(NAME, name);
    putValue(SHORT_DESCRIPTION, short_description);
  }

  public CancelAction(Window w, String name) {
    window = w;
    putValue(NAME, name);
  }

  public void actionPerformed(ActionEvent e) {
    window.dispose();
  }
}