package com.belcci.otrs.model;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.belcci.otrs.form.OTRS;



public class OTRSReader  {
	
    private static OTRSReader reader;
    
    private OTRSReader() {
    }
    
    public static OTRSReader getInstance() {
    	if (reader == null) {
    		reader = new OTRSReader();
    	}
    	return reader;
    }
    
    public void printTicket(Map<String, String> props) {
		for (String key : props.keySet()) {
			printDebug(key + ": " + props.get(key));
		}
	}

	public SOAPMessage call(String namespace, String strurl, String operation,
			Map<String, String> params) throws Exception {
		return doaction(namespace, strurl, operation, params);
	}

	private SOAPMessage doaction(String namespace, String strurl,
			String operation, Map<String, String> params) throws Exception {

		
		// Create the connection
		SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
		SOAPConnection conn = scf.createConnection();

		// Create message be either DYNAMIC_SOAP_PROTOCOL, DEFAULT_SOAP_PROTOCOL (which is the same as) SOAP_1_1_PROTOCOL, or SOAP_1_2_PROTOCOL.
		MessageFactory mf = MessageFactory.newInstance();
		printDebug("Factory: " + mf.getClass().getName());

		SOAPMessage msg = mf.createMessage();
		SOAPMessage resp = mf.createMessage();
		printDebug("Message request: " + msg.getClass().getName());
		printDebug("Message response: " + resp.getClass().getName());

		// Object for message parts
		SOAPPart sp = msg.getSOAPPart();
		printDebug("SOAPPArt: " + sp.getClass().getName());

		// envelope setting
		SOAPEnvelope env = sp.getEnvelope();
		env.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		env.addNamespaceDeclaration("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		env.addNamespaceDeclaration("soapenc",
				"http://schemas.xmlsoap.org/soap/encoding/");
		env.addNamespaceDeclaration("soap",
				"http://schemas.xmlsoap.org/soap/envelop/"); // SOAP 1.1
		env.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

		// body setting
		SOAPBody body = env.getBody();
		SOAPBodyElement dispatch = body.addBodyElement(new QName(namespace,
				operation));

		// fill in params
		Set<String> keys = params.keySet();
		for (String tag : keys) {
			String value = params.get(tag);
			dispatch.addChildElement(tag).addTextNode(value)
					.setAttribute("xsi:type", "xsd:string");
		}

		System.out.println("URL" + strurl);
		long start = System.currentTimeMillis();
		URL url = new URL(strurl);
		resp = conn.call(msg, url);
		conn.close();
		
		long end = System.currentTimeMillis();
		System.out.println("Duration: " + (end - start));
		return resp;
	}

	private void printDebug(String str) {
		if (OTRS.DEBUG) {
			System.out.println(str);
		}
	}

}
