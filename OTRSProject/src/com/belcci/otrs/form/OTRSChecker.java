package com.belcci.otrs.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.belcci.otrs.model.OTRSReader;
import com.belcci.otrs.model.OTRSSOAPMessageParser;
import com.belcci.otrs.model.OTRSTicket;
import com.belcci.otrs.persistence.OTRSConfigurator;
import com.belcci.otrs.util.DesEncrypter;

public class OTRSChecker extends Thread {
	
	private boolean finished = false;
	private String lstr;
	private String pstr;
	private String strurl;
	private String namespace;
	private long timeout;
	private int counter = 0;
	
	public void run() {
		finished = false;

		while (!finished) {
			try {
				System.out.println("Check Tickets");
				checkTickets();
				Thread.sleep(timeout);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void checkTickets() throws Exception {

		OTRSSOAPMessageParser parser = OTRSSOAPMessageParser.getInstance();

		String operation = OTRS.OPR_TICKET_SEARCH;
		Map<String, String> params = new HashMap<String, String>();
		params.put(OTRS.TAG_USERLOGIN, lstr);
		params.put(OTRS.TAG_PASSWORD, pstr);

		SOAPMessage resp = OTRSReader.getInstance().call(namespace, strurl,
				operation, params);

		if (parser.checkOTRSResponseError(resp)) {
			Map<String, String> errs = parser.parseOTRSResponseError(resp);
			System.out.println(errs);
		} else {
			List<String> ids = parser.parseOTRSTicketSearchResponse(resp);
			printList(ids);
			
			String savelast = (String) OTRSProperty.getInstance().getProperty(
					OTRS.PR_LAST_TICKETID) != null ? (String) OTRSProperty
					.getInstance().getProperty(OTRS.PR_LAST_TICKETID) : "";
					
			int index = 0;		
			String comelast = ids.get(index);
			
			if (!comelast.equals(savelast)) {
				operation = "TicketGet";
				
				while (index < ids.size() &&  !savelast.equals(ids.get(index))) {
   	   			   params.put(OTRS.TAG_TICKETID, ids.get(index++));
   	   			   resp = OTRSReader.getInstance().call(namespace, strurl, operation, params);
                   String message = "";
                   
  	   			   if (parser.checkOTRSResponseError(resp)) { 
  	   				     Map<String, String> errs = parser.parseOTRSResponseError(resp); 
  	   				     System.out.println(errs); 
  	   			   } else {
 	   			         OTRSTicket ticket = parser.parseOTRSTicketGet(resp);
 	   			         if ("new".equals(ticket.getState())) {
 	   			        	 message =  '\n' + "Новый запрос от " + ticket.getCustomerUserID() 
 	   			        		       + " к " + ticket.getQueue()
 	   			        		       + '\n' + "Тема: " +  ticket.getTitle()
 	   			        		       + '\n' + "Дата создания: " + ticket.getCreated();; 	   			         

 	   			        	 startReminder(message);
 	   			        	 OTRSRepository.getInstance().getTrayIcon().setToolTip("Электронная заявка. В вашей очереди имеются новые заявки.");
 	   			        	 OTRSRepository.getInstance().getTrayIcon().setImage(OTRSRepository.getInstance().getReminderImage());
 	   			         }

 	   			         
 	   			         
 	   			         /* Map<String, String> props = parser.parseOTRSTicketGetResponse(resp);
 	   			     
 	   			         if (((String) props.get("State")).equals("new")) {
 	   			        	 message =  '\n' + "Новый запрос от " + (String) props.get("CustomerUserID") 
 	   			        		       + " к " + (String) props.get("Queue") 
 	   			        		       + '\n' + "Тема: " +  (String) props.get("Title")
 	   			        		       + '\n' + "Дата создания: " + (String) props.get("Created"); 	   			         

 	   			        	 startReminder(message);
 	   			        	 OTRSRepository.getInstance().getTrayIcon().setToolTip("Электронная заявка. В вашей очереди имеются новые заявки.");
 	   			        	 OTRSRepository.getInstance().getTrayIcon().setImage(OTRSRepository.getInstance().getReminderImage());
 	   			         }
 	   			         */
 	   			         
  	   			   }
 	            } 
 	            
				OTRSProperty.getInstance().setProperty(OTRS.PR_LAST_TICKETID, comelast);
				try {
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
		}
	}

	private void startReminder(String message) {
		final String msg = message;
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				Display display = new Display();
				Shell shell = new Shell(display);
				ReminderForm pform = new ReminderForm(shell,
						SWT.ICON_INFORMATION | SWT.ON_TOP);
				pform.setMessage(msg);
				pform.open();
				counter--;				
				shell.close();
				display.close();
				
				if (counter == 0) {
				   OTRSRepository.getInstance().getTrayIcon().setImage(OTRSRepository.getInstance().getLogonImage());
				}
			}
		};
		
		counter++;
		thread.start();
	}
	
	
	
	

	private void printList(List<String> ids) {
		for (String id : ids) {
			System.out.print("; " + id);
		}
		System.out.println("");
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getPassword() {
		return pstr;
	}

	public void setPassword(String pstr) {
		this.pstr = pstr;
	}

	public String getLogin() {
		return lstr;
	}

	public void setLogin(String strlogin) {
		this.lstr = strlogin;
	}

	public String getUrl() {
		return strurl;
	}

	public void setUrl(String strurl) {
		this.strurl = strurl;
	}
	
	protected void finalize() throws Throwable {
		System.out.println("OTRSChacker object closed...");
	 }
}
