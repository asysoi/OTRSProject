package com.belcci.otrs.form.listener;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogMenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();
		System.out.println(item.getLabel());
		// do
	}
};

