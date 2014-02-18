package com.belcci.otrs.form.listener;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import com.belcci.otrs.form.OTRSRepository;
import com.belcci.otrs.form.TicketsListForm;


public class TicketMenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();
		Shell shell = OTRSRepository.getInstance().getShell();

		TicketsListForm pform = new TicketsListForm(shell, SWT.OK);
  	    pform.open();
		
	}

};