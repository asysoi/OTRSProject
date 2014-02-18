package com.belcci.otrs.form;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;


public class OTRSApplicationSWT {

public static void main(String[] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	
	Image image = new Image (display, 16, 16);
	Image image2 = new Image (display, 16, 16);
	
	GC gc = new GC(image2);
	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	gc.fillRectangle(image2.getBounds());
	gc.dispose();
	
	final Tray tray = display.getSystemTray ();
	
	if (tray == null) {
		System.out.println ("The system tray is not available");
	} else {
		
		final TrayItem item = new TrayItem (tray, SWT.NONE);
		
		item.setToolTipText("SWT TrayItem");
	
		item.addListener (SWT.Show, new Listener () {
			@Override
			public void handleEvent (Event event) {
				System.out.println("show");
			}
		});
		item.addListener (SWT.Hide, new Listener () {
			@Override
			public void handleEvent (Event event) {
				System.out.println("hide");
			}
		});
		item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event event) {
				System.out.println("selection");
			}
		});
		
		item.addListener (SWT.DefaultSelection, new Listener () {
			@Override
			public void handleEvent (Event event) {
				System.out.println("default selection");
			}
		});
		
		final Menu menu = new Menu (shell, SWT.POP_UP);
		for (int i = 0; i < 8; i++) {
			MenuItem mi = new MenuItem (menu, SWT.PUSH);
			mi.setText ("Item" + i);
			mi.addListener (SWT.Selection, new Listener () {
				@Override
				public void handleEvent (Event event) {
					System.out.println("selection " + event.widget);
				}
			});
			
			if (i == 0) menu.setDefaultItem(mi);
		}
		MenuItem mex = new MenuItem (menu, SWT.PUSH);
		mex.setText ("Exit");
		mex.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				System.out.println("Good Bye");
				item.dispose();
				System.exit(0);
			}
		});
	
		item.addListener (SWT.MenuDetect, new Listener () {
			@Override
			public void handleEvent (Event event) {
				menu.setVisible (true);
			}
		});
		item.setImage (image2);
		item.setHighlightImage (image);
		
		//shell.setBounds(50, 50, 300, 200);
		//shell.open ();
			
		while (! item.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		image.dispose ();
		image2.dispose ();
		display.dispose ();
	}
	
}
}