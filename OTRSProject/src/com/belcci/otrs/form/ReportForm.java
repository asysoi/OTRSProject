package com.belcci.otrs.form;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;

public class ReportForm extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ReportForm(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(326, 200);
		shell.setText(getText());
		
		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Arial", 10, SWT.NORMAL));
		label.setBounds(82, 28, 113, 16);
		label.setText("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u0437\u0430\u044F\u0432\u043E\u043A");
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u043D\u043E\u0432\u044B\u0445 \u0437\u0430\u044F\u0432\u043E\u043A");
		label_1.setFont(SWTResourceManager.getFont("Arial", 10, SWT.NORMAL));
		label_1.setBounds(46, 60, 154, 16);
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u043E\u0442\u043A\u0440\u044B\u0442\u044B\u0445 \u0437\u0430\u044F\u0432\u043E\u043A");
		label_2.setFont(SWTResourceManager.getFont("Arial", 10, SWT.NORMAL));
		label_2.setBounds(28, 92, 172, 16);
		
		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setText("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u0437\u0430\u043A\u0440\u044B\u0442\u044B\u0445  \u0437\u0430\u044F\u0432\u043E\u043A");
		label_3.setFont(SWTResourceManager.getFont("Arial", 10, SWT.NORMAL));
		label_3.setBounds(23, 125, 177, 16);

	}

}
