package com.belcci.otrs.form;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;

import com.belcci.otrs.form.listener.ApplicationListener;
import com.belcci.otrs.form.listener.ExitMenuListener;
import com.belcci.otrs.form.listener.LinkMenuListener;
import com.belcci.otrs.form.listener.LogMenuListener;
import com.belcci.otrs.form.listener.LoginMenuListener;
import com.belcci.otrs.form.listener.PropertyMenuListener;
import com.belcci.otrs.form.listener.ReportMenuListener;
import com.belcci.otrs.form.listener.TicketMenuListener;
import com.belcci.otrs.model.OTRSReader;
import com.belcci.otrs.model.OTRSSOAPMessageParser;
import com.belcci.otrs.persistence.OTRSConfigurator;
import com.belcci.otrs.util.DesEncrypter;

public class OTRSApplicationAWT {
	public static final Logger LOG=Logger.getLogger(TicketsListForm.class);
	private MenuItem loginItem;
	private MenuItem ticketItem;
	private MenuItem logItem;
	private MenuItem reportItem;
	private MenuItem linkItem;
	private MenuItem propertyItem;
	private MenuItem exitItem;

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new OTRSApplicationAWT().createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		if (!SystemTray.isSupported()) {
			LOG.info("SystemTray не поддерживается в сиcтеме.");
			return;
		}

		loadImages();
		final TrayIcon trayIcon = new TrayIcon(OTRSRepository.getInstance()
				.getLogoffImage(), "OTRS icon");
		System.getProperty("user.dir");

		final PopupMenu topMenu = new PopupMenu();
		loginItem = new MenuItem(OTRS.M_LOGIN);
		ticketItem = new MenuItem(OTRS.M_TICKETS);
		// logItem = new MenuItem(OTRS.M_LOG);
		// reportItem = new MenuItem(OTRS.M_REPORT);
		linkItem = new MenuItem(OTRS.M_LINK);
		propertyItem = new MenuItem(OTRS.M_PROPERTY);
		exitItem = new MenuItem(OTRS.M_EXIT);

		// Add components to popup menu
		topMenu.add(loginItem);
		topMenu.addSeparator();
		topMenu.add(ticketItem);
		// topMenu.add(logItem);
		// topMenu.add(reportItem);
		topMenu.add(linkItem);
		topMenu.addSeparator();
		topMenu.add(propertyItem);
		topMenu.addSeparator();
		topMenu.add(exitItem);

		trayIcon.setPopupMenu(topMenu);
		trayIcon.setToolTip(OTRS.APP_TIPS);
		trayIcon.setImageAutoSize(true);

		final SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out
					.println("Приложение не может быть размещено в системной области.");
			return;
		}
		OTRSRepository.getInstance().setTray(tray);
		OTRSRepository.getInstance().setTrayIcon(trayIcon);

		trayIcon.addActionListener(new ApplicationListener());
		loginItem.addActionListener(new LoginMenuListener());
		ticketItem.addActionListener(new TicketMenuListener());
		// logItem.addActionListener(new LogMenuListener());
		// reportItem.addActionListener(new ReportMenuListener());
		linkItem.addActionListener(new LinkMenuListener());
		propertyItem.addActionListener(new PropertyMenuListener());
		exitItem.addActionListener(new ExitMenuListener());

		ticketItem.setEnabled(false);
		// reportItem.setEnabled(false);
		OTRSRepository.getInstance().setTopMenu(topMenu);

		try {
			OTRSProperty props = OTRSProperty.getInstance();
			props.setProperties(OTRSConfigurator.getInstance().readConfig());
			DesEncrypter encrypter = new DesEncrypter();
			props.setProperty(OTRS.PR_PSW,
					encrypter.decrypt(props.getProperty(OTRS.PR_PSW)));
			
			// Chrck autologin setting
			if (Boolean.getBoolean(props.getProperty(OTRS.PR_AUTOLOGIN))) {
			   autologin();
			}
		} catch (FileNotFoundException ex) {
			LOG.info("Default properties loading ...");

			OTRSProperty.getInstance().setProperty(OTRS.PR_CHECK_TIME, "1");
			OTRSProperty.getInstance().setProperty(OTRS.PR_OTRS_URL,
					"http://localhost");
			OTRSProperty.getInstance().setProperty(OTRS.PR_WEBSERVICE_URL,
							"/otrs/nph-genericinterface.pl/Webservice/WebOTRS");
			OTRSProperty.getInstance().setProperty(
					OTRS.PR_WEBSERVICE_NAMESPACE,
					"urn:otrs-com:soap:functions");
//					"http://www.otrs.com/GenericInterface/actions");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Image createImage(String path, String description) {
		URL imageURL = OTRSApplicationAWT.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Ресурс не найден: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	private void loadImages() {
		OTRSRepository.getInstance().setLogonImage(
				createImage("images/l_g.gif", ""));
		OTRSRepository.getInstance().setLogoffImage(
				createImage("images/l_r.gif", ""));
		OTRSRepository.getInstance().setReminderImage(
				createImage("images/l_a.gif", ""));
	}

	private void autologin() {
		if (OTRSProperty.getInstance().getProperty(OTRS.PR_AUTOLOGIN) != null
				&& OTRSProperty.getInstance().getProperty(OTRS.PR_AUTOLOGIN)
						.equals("true")) {
			LOG.info("AutoLogin ... " );
			String operation = OTRS.OPR_SESSION_CREATE;
			Map<String, String> params = new HashMap<String, String>();
			params.put(OTRS.TAG_USERLOGIN, OTRSProperty.getInstance()
					.getProperty(OTRS.PR_LOGIN));
			params.put(OTRS.TAG_PASSWORD, OTRSProperty.getInstance()
					.getProperty(OTRS.PR_PSW));

			try {
				SOAPMessage resp = OTRSReader.getInstance().call(
						(String) OTRSProperty.getInstance().getProperty(
								OTRS.PR_WEBSERVICE_NAMESPACE),
						OTRSProperty.getInstance().getProperty(OTRS.PR_OTRS_URL) + 		
						OTRSProperty.getInstance().getProperty(
								OTRS.PR_WEBSERVICE_URL), operation, params);
				OTRSSOAPMessageParser parser = OTRSSOAPMessageParser
						.getInstance();

				if (parser.checkOTRSResponseError(resp)) {
					Map<String, String> errs = parser
							.parseOTRSResponseError(resp);
					// lblError.setText(errs.get("ErrorMessage"));
				} else {
					String sessionID = parser.parseOTRSSessionIDResponse(resp);
					LOG.info("Session ID: " + sessionID);
					loginItem.setLabel(OTRS.M_LOGOFF);
					setMenuEnabled(true);
					OTRSChecker checker = OTRSRepository.getInstance()
							.getOTRSChecker();
					checker.setLogin(OTRSProperty.getInstance().getProperty(
							OTRS.PR_LOGIN));
					checker.setPassword(OTRSProperty.getInstance().getProperty(
							OTRS.PR_PSW));
					checker.setNamespace((String) OTRSProperty.getInstance()
							.getProperty(OTRS.PR_WEBSERVICE_NAMESPACE));
					checker.setUrl((String) OTRSProperty.getInstance().getProperty(OTRS.PR_OTRS_URL) + 
							OTRSProperty.getInstance().getProperty(OTRS.PR_WEBSERVICE_URL));
					checker.setTimeout(Integer.parseInt((String) OTRSProperty
							.getInstance().getProperty(OTRS.PR_CHECK_TIME)) * 1000 * 60);
					checker.start();
					OTRSRepository
							.getInstance()
							.getTrayIcon()
							.setImage(
									OTRSRepository.getInstance()
											.getLogonImage());

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			LOG.info("No AutoLogin ... " );
			LOG.info(OTRSProperty.getInstance().getProperty(OTRS.PR_AUTOLOGIN));
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