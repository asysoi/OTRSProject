package com.belcci.otrs.form.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.belcci.otrs.form.OTRSRepository;

public class ExitMenuListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {

		OTRSRepository.getInstance().getTray()
				.remove(OTRSRepository.getInstance().getTrayIcon());
		OTRSRepository.getInstance().getOTRSChecker().setFinished(true);
		OTRSRepository.getInstance().getShell().dispose();
		OTRSRepository.getInstance().getDisplay().dispose();
		System.exit(0);
	}

}
