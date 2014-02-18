package com.belcci.otrs.form;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OTRSRepository {
	private OTRSChecker checker;
	private SystemTray tray;
	private TrayIcon trayIcon;
	private Display display = new Display();
	private Shell shell;
	private PopupMenu topMenu;
	private static OTRSRepository repository;
	private Image LogonImage;
	private Image LogoffImage;
	private Image ReminderImage;
	private int counter;
    
    public static OTRSRepository getInstance() {
    	if (repository == null) {
    		repository = new OTRSRepository();
    	}
    	return repository;
    }

	public Image getLogoffImage() {
		return LogoffImage;
	}

	public void setLogoffImage(Image logoffImage) {
		LogoffImage = logoffImage;
	}

	public Image getReminderImage() {
		return ReminderImage;
	}

	public void setReminderImage(Image reminderImage) {
		ReminderImage = reminderImage;
	}

	public Image getLogonImage() {
		return LogonImage;
	}

	public void setLogonImage(Image createImage) {
		LogonImage = createImage;
		
	}	

	
	public OTRSChecker getOTRSChecker() {
		if (checker == null) {
			checker = new OTRSChecker();
		}
		return checker;
	}

	public void setOTRSChecker(OTRSChecker checker) {
		this.checker = checker;
	}

	public SystemTray getTray() {
		return tray;
	}

	public void setTray(SystemTray tray) {
		this.tray = tray;
	}

	public TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public void setTrayIcon(TrayIcon trayIcon) {
		this.trayIcon = trayIcon;
	}

	public Display getDisplay() {
		return display ;
	}

	public Shell getShell() {
		if (shell == null) {
    		shell = new Shell(display);
		}
		return shell;
	}

	public PopupMenu getTopMenu() {
	   return topMenu;
	}
	
	public void setTopMenu(PopupMenu topMenu2) {
		this.topMenu = topMenu2;
	}

	public int getCounter() {
		return counter;
	}

	public void decCounter() {
		counter--;
	}

	public void incCounter() {
		counter++;
	}

}
