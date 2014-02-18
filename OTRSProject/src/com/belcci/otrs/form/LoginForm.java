package com.belcci.otrs.form;

import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPMessage;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import com.belcci.otrs.model.OTRSReader;
import com.belcci.otrs.model.OTRSSOAPMessageParser;
import com.belcci.otrs.persistence.OTRSConfigurator;
import com.belcci.otrs.util.DesEncrypter;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Control;

public class LoginForm extends Dialog {

	private Object result;
	private Shell shell;
	private Text psw;
	private Text login;
	private String sessionID;
	private String strpsw;
	private String strlogin;
	private Label lblError;
	private boolean logged = false;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public LoginForm(Shell parent, int style) {
		super(parent, style);
		setText("OTRS Login");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		Display display = getParent().getDisplay();
		shell.setLocation((display.getClientArea().width - 350) / 2,
				(display.getClientArea().height - 150) / 2);
		shell.open();
		shell.layout();
		shell.setFocus();

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
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setSize(350, 155);

		shell.setText("\u0414\u043E\u0431\u0440\u043E \u043F\u043E\u0436\u0430\u043B\u043E\u0432\u0430\u0442\u044C \u0432 OTRS");
		Button button = new Button(shell, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		button.setText("\u0412\u043E\u0439\u0442\u0438");
		button.setBounds(111, 95, 75, 25);

		Button button_1 = new Button(shell, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		button_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		button_1.setText("\u041E\u0442\u043C\u0435\u043D\u0430");
		button_1.setBounds(192, 95, 75, 25);

		psw = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		psw.setFont(SWTResourceManager.getFont("Tahoma", 9, SWT.NORMAL));
		psw.setBounds(73, 48, 248, 21);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		label.setText("\u041F\u0430\u0440\u043E\u043B\u044C");
		label.setAlignment(SWT.RIGHT);
		label.setBounds(10, 51, 53, 15);

		login = new Text(shell, SWT.BORDER);
		login.setFont(SWTResourceManager.getFont("Tahoma", 9, SWT.NORMAL));
		login.setBounds(73, 23, 248, 21);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Arial", 9, SWT.BOLD));
		label_1.setText("\u041B\u043E\u0433\u0438\u043D");
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(10, 26, 53, 15);

		lblError = new Label(shell, SWT.NONE);
		lblError.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		lblError.setBounds(73, 75, 248, 15);

		login.setText(strlogin != null ? strlogin : "");
		psw.setText(strpsw != null ? strpsw : "");
		shell.setTabList(new Control[] { login, psw, button, button_1 });

		button.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent arg0) {
				lblError.setText("");
				String operation = OTRS.OPR_SESSION_CREATE;
				Map<String, String> params = new HashMap<String, String>();
				params.put(OTRS.TAG_USERLOGIN, login.getText());
				params.put(OTRS.TAG_PASSWORD, psw.getText());

				
				
				try {
					SOAPMessage resp = OTRSReader.getInstance().call(
							(String) OTRSProperty.getInstance().getProperty(
									OTRS.PR_WEBSERVICE_NAMESPACE), 
									OTRSProperty.getInstance().getProperty(OTRS.PR_OTRS_URL) +
									OTRSProperty.getInstance().getProperty(OTRS.PR_WEBSERVICE_URL), 
									operation, params);
					OTRSSOAPMessageParser parser = OTRSSOAPMessageParser
							.getInstance();

					if (parser.checkOTRSResponseError(resp)) {
						Map<String, String> errs = parser
								.parseOTRSResponseError(resp);
						lblError.setText(errs.get("ErrorMessage"));
					} else {
						sessionID = parser.parseOTRSSessionIDResponse(resp);
						System.out.println("Session ID: " + sessionID);
						logged = true;
						strlogin = login.getText();
						strpsw = psw.getText();
						if (! ((String) params.get(OTRS.TAG_PASSWORD)).equals(OTRSProperty.getInstance().getProperty(OTRS.PR_PSW)) ) {
							savePassword(params.get(OTRS.TAG_PASSWORD));
						}
						shell.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
	}

	protected void savePassword(String newPsw) {
		try {
			    OTRSProperty.getInstance().setProperty(OTRS.PR_PSW, newPsw); 
			    
				DesEncrypter encrypter = new DesEncrypter();
				OTRSProperty.getInstance().setProperty(OTRS.PR_PSW,
							encrypter.encrypt(OTRSProperty.getInstance().getProperty(OTRS.PR_PSW)));
				
				OTRSConfigurator.getInstance().storeConfig(
						OTRSProperty.getInstance().getProperties());
				
				OTRSProperty.getInstance().setProperty(OTRS.PR_PSW,
							encrypter.decrypt(OTRSProperty.getInstance().getProperty(OTRS.PR_PSW)));
		} catch (Exception ex) {
				ex.printStackTrace();
		}
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public String getPassword() {
		return strpsw;
	}

	public void setPassword(String strpsw) {
		this.strpsw = strpsw;
	}

	public String getLogin() {
		return strlogin;
	}

	public void setLogin(String strlogin) {
		this.strlogin = strlogin;
	}
}
