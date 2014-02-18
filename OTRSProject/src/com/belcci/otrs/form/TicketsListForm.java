package com.belcci.otrs.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import com.belcci.otrs.model.OTRSReader;
import com.belcci.otrs.model.OTRSSOAPMessageParser;
import com.belcci.otrs.model.OTRSTicket;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;


public class TicketsListForm extends Dialog {
	public static final Logger LOG=Logger.getLogger(TicketsListForm.class);
	protected Object result;
	protected Shell shell;
	private Table table;
	private List<OTRSTicket> tickets = new ArrayList<OTRSTicket>();
	private List<String> ticketIDs = new ArrayList<String>();
	private ProgressBar bar;
	private int idindex;

	public TicketsListForm(Shell parent, int style) {
		super(parent, style);
	}

	public Object open() {
		final Display display = getParent().getDisplay();
		createContents();
		shell.open();
		shell.layout();

		makeTicketsTable();
		Thread thread = new Thread() {
			public void run() {
				if (table.isDisposed())
					return;
				
				for (idindex = 0; idindex < ticketIDs.size(); idindex++) {
					
					display.syncExec(new Runnable() {

						public void run() {
							if (table.isDisposed())
								return;

							try {
								OTRSTicket ticket = getTicketByID(ticketIDs
										.get(idindex));
								TableItem item = new TableItem(table, SWT.NONE);
								item.setText(0, ticket.getTicketID());
								item.setText(1, ticket.getCustomerUserID());
								item.setText(2, ticket.getTitle());
								item.setText(3, ticket.getQueue());
								item.setText(4, ticket.getCreated());
								item.setText(5, ticket.getState());
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					});
				}
			}
		};
		thread.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setSize(561, 323);
		shell.setText("Список новых заявок");
		shell.setLayout(new FillLayout());

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		//bar = new ProgressBar(shell, SWT.SMOOTH);
		try {
			ticketIDs = getTicketIDsList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private List<String> getTicketIDsList() throws Exception {
		List<String> ids = new ArrayList<String>();
		OTRSSOAPMessageParser parser = OTRSSOAPMessageParser.getInstance();

		String operation = OTRS.OPR_TICKET_SEARCH;
		Map<String, String> params = new HashMap<String, String>();
		params.put(OTRS.TAG_USERLOGIN,
				OTRSProperty.getInstance().getProperty(OTRS.PR_LOGIN));
		params.put(OTRS.TAG_PASSWORD,
				OTRSProperty.getInstance().getProperty(OTRS.PR_PSW));
		params.put(OTRS.TAG_STATES, OTRS.VL_STATE_NEW);

		SOAPMessage resp = OTRSReader.getInstance().call(
				OTRSProperty.getInstance().getProperty(
						OTRS.PR_WEBSERVICE_NAMESPACE),
				OTRSProperty.getInstance().getProperty(OTRS.PR_OTRS_URL) + 
						OTRSProperty.getInstance().getProperty(OTRS.PR_WEBSERVICE_URL),
				operation, params);

		if (parser.checkOTRSResponseError(resp)) {
			Map<String, String> errs = parser.parseOTRSResponseError(resp);
			throw new Exception(errs.toString());
		} else {
			ids = parser.parseOTRSTicketSearchResponse(resp);
		}
		return ids;
	}

	private void makeTicketsTable() {
		if (table.getItems().length > 0) {
			table.removeAll();
		}

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		String[] titles = { "Номер запроса", "Клиент", "Тема", "Очередь", "Дата создания", "Статус"};
		int[] wd = {4,10,35,30, 15, 4};
		
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth((wd[i] * table.getSize().x)/100);
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
	}

	private OTRSTicket getTicketByID(String id) throws Exception {
		OTRSTicket ticket = null;
		OTRSSOAPMessageParser parser = OTRSSOAPMessageParser.getInstance();

		String operation = OTRS.OPR_TICKET_GET;
		Map<String, String> params = new HashMap<String, String>();
		params.put(OTRS.TAG_USERLOGIN,
				OTRSProperty.getInstance().getProperty(OTRS.PR_LOGIN));
		params.put(OTRS.TAG_PASSWORD,
				OTRSProperty.getInstance().getProperty(OTRS.PR_PSW));
		params.put(OTRS.TAG_TICKETID, id);
		SOAPMessage resp = OTRSReader.getInstance().call(
				OTRSProperty.getInstance().getProperty(
						OTRS.PR_WEBSERVICE_NAMESPACE),
				OTRSProperty.getInstance().getProperty(OTRS.PR_OTRS_URL) + 		
						OTRSProperty.getInstance().getProperty(OTRS.PR_WEBSERVICE_URL),
				operation, params);
		if (parser.checkOTRSResponseError(resp)) {
			Map<String, String> errs = parser.parseOTRSResponseError(resp);
			LOG.error(errs.get("ErrorMessage"));
		} else {
			ticket = parser.parseOTRSTicketGet(resp);
		}
		return ticket;
	}

	public void setTickets(List<OTRSTicket> list) {
		tickets = list;
	}
}
