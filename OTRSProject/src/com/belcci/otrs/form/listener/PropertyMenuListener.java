package com.belcci.otrs.form.listener;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.belcci.otrs.form.LoginForm;
import com.belcci.otrs.form.OTRS;
import com.belcci.otrs.form.OTRSProperty;
import com.belcci.otrs.form.OTRSRepository;
import com.belcci.otrs.form.PropertiesForm;
import com.belcci.otrs.model.OTRSReader;

public class PropertyMenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		// MenuItem item = (MenuItem) e.getSource();
		
		PropertiesForm pform = new PropertiesForm(OTRSRepository.getInstance().getShell(), SWT.ICON_INFORMATION
				| SWT.OK);
		pform.open();
	}
};
