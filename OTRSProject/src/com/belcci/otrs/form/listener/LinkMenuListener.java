package com.belcci.otrs.form.listener;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import com.belcci.otrs.form.OTRS;
import com.belcci.otrs.form.OTRSProperty;

public class LinkMenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();
		System.out.println(item.getLabel());
        try {
			ProcessBuilder process = new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", 
					      "http://192.168.0.72/otrs/index.pl?Action=Login&Lang=ru&User=" +
					      OTRSProperty.getInstance().getProperty(OTRS.PR_LOGIN) +  
					      "&Password=" +
					      OTRSProperty.getInstance().getProperty(OTRS.PR_PSW));
			process.start();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
};
