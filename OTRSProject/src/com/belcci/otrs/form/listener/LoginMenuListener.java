package com.belcci.otrs.form.listener;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.belcci.otrs.form.LoginForm;
import com.belcci.otrs.form.OTRS;
import com.belcci.otrs.form.OTRSChecker;
import com.belcci.otrs.form.OTRSProperty;
import com.belcci.otrs.form.OTRSRepository;

public class LoginMenuListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();

		if (OTRS.M_LOGOFF.equals(item.getLabel())) {
			item.setLabel(OTRS.M_LOGIN);
			OTRSRepository.getInstance().getOTRSChecker().setFinished(true);
			OTRSRepository.getInstance().setOTRSChecker(null);
			OTRSRepository.getInstance().getTrayIcon().setImage(OTRSRepository.getInstance().getLogoffImage());
			setMenuEnabled(false);
		} else {
			Shell shell = OTRSRepository.getInstance().getShell();
			OTRSProperty props = OTRSProperty.getInstance();

			LoginForm pform = new LoginForm(shell, SWT.ICON_INFORMATION
					| SWT.OK);
			pform.setLogin(props.getProperty(OTRS.PR_LOGIN));
			pform.setPassword(props.getProperty(OTRS.PR_PSW));
			pform.open();

			if (pform.isLogged()) {
				item.setLabel(OTRS.M_LOGOFF);
				setMenuEnabled(true);
				OTRSChecker checker = OTRSRepository.getInstance()
						.getOTRSChecker();
				props.setProperty(OTRS.PR_PSW, pform.getPassword());
				props.setProperty(OTRS.PR_LOGIN, pform.getLogin());
				
				checker.setLogin(pform.getLogin());
				checker.setPassword(pform.getPassword());
				checker.setNamespace((String) props
						.getProperty(OTRS.PR_WEBSERVICE_NAMESPACE));
				checker.setUrl((String)  props.getProperty(OTRS.PR_OTRS_URL) + props.getProperty(OTRS.PR_WEBSERVICE_URL));
				checker.setTimeout(Integer.parseInt((String) OTRSProperty
						.getInstance().getProperty(OTRS.PR_CHECK_TIME)) * 1000 * 60);
				checker.start();
				OTRSRepository.getInstance().getTrayIcon().setImage(OTRSRepository.getInstance().getLogonImage());
			}
		}
	}

	private void setMenuEnabled(boolean enabled) {
		PopupMenu menu = OTRSRepository.getInstance().getTopMenu();

		for (int i = 0; i < menu.getItemCount(); i++) {
			if (menu.getItem(i).getLabel().equals(OTRS.M_TICKETS)
					|| menu.getItem(i).getLabel().equals(OTRS.M_REPORT)) {
				menu.getItem(i).setEnabled(enabled);
			}
		}
	}
}
