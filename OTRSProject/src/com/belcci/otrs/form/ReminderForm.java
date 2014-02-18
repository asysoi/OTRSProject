package com.belcci.otrs.form;

import java.awt.Toolkit;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.custom.CLabel;

public class ReminderForm extends Dialog {

	private  Object result;
	private  Shell shell;
	private String message = "";

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ReminderForm(Shell parent, int style) {
		super(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		Display display = getParent().getDisplay();
		shell.open();
		shell.layout();
		
		shell.setLocation(display.getClientArea().width - shell.getSize().x, display.getClientArea().height - 1);
		int targetY = shell.getSize().y;
		
		for (int i = 1; i < targetY ; i++) {
			 shell.setLocation(display.getClientArea().width - shell.getSize().x, display.getClientArea().height - i);
			 shell.setSize(shell.getSize().x, i);
			 
			 try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 1; i < 100; i++) {
			 Toolkit.getDefaultToolkit().beep();
		}
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.BORDER | SWT.ON_TOP);
		shell.setTouchEnabled(true);
		shell.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent arg0) {
				shell.setAlpha(255);
			}
			public void mouseExit(MouseEvent arg0) {
				shell.setAlpha(100);
			}
		});
		shell.setAlpha(100);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		shell.setSize(285, 110);
		shell.setText(getText());
		
		Label lblNewTicket = new Label(shell, SWT.NONE);
		lblNewTicket.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent arg0) {
				shell.setAlpha(255);
			}

			public void mouseExit(MouseEvent arg0) {
				shell.setAlpha(100);
			}
		});
		
		lblNewTicket.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				shell.close();
			}
		});
		lblNewTicket.setFont(SWTResourceManager.getFont("Arial", 8, SWT.NORMAL));
		lblNewTicket.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblNewTicket.setBounds(0, 21, 279, 83);
		lblNewTicket.setText(message);
		lblNewTicket.setAlignment(SWT.CENTER);
		
		CLabel lblNewLabel = new CLabel(shell, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		lblNewLabel.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		lblNewLabel.setBounds(0, 0, 279, 21);
		lblNewLabel.setText("\u0423\u0432\u0435\u0434\u043E\u043C\u043B\u0435\u043D\u0438\u0435 \u043E \u043D\u043E\u0432\u043E\u0439 \u0437\u0430\u044F\u0432\u043A\u0435");
		
		lblNewLabel.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent arg0) {
				shell.setAlpha(255);
			}
			public void mouseExit(MouseEvent arg0) {
				shell.setAlpha(100);
			}
		});

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
