package com.belcci.otrs.model;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.belcci.otrs.form.OTRS;
import com.belcci.otrs.util.XSDTypeConverter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTRSSOAPMessageParser {
	
private static OTRSSOAPMessageParser parser;
    
    public static OTRSSOAPMessageParser getInstance() {
    	if (parser == null) {
    		parser = new OTRSSOAPMessageParser();
    	}
    	return parser;
    }
    
	public boolean checkOTRSResponseError(SOAPMessage msg)
			throws ParserConfigurationException, SAXException, IOException,
			SOAPException {
		Document doc = convertSOAPMessageToDOM(msg);
		NodeList list = doc.getElementsByTagName(OTRS.TAG_ERROR);
		return list.getLength() > 0;
	}

	public Map<String, String> parseOTRSResponseError(SOAPMessage msg)
			throws ParserConfigurationException, SAXException, IOException,
			SOAPException {
		Map<String, String> errors = new HashMap<String, String>();

		Document doc = convertSOAPMessageToDOM(msg);
		NodeList list = doc.getElementsByTagName(OTRS.TAG_ERROR);

		for (int k = 0; k < list.getLength(); k++) {
			NodeList tags = list.item(k).getChildNodes();

			for (int i = 0; i < tags.getLength(); i++) {
				Node tag = tags.item(i);
				errors.put(tag.getNodeName(), tag.getTextContent().trim());
			}
		}
		return errors;
	}

	public String parseOTRSSessionIDResponse(SOAPMessage msg) throws Exception {
		String sid = null;
		Document doc = convertSOAPMessageToDOM(msg);
		NodeList list = doc.getElementsByTagName(OTRS.TAG_SESSIONCREATE_RESP);

		if (list.getLength() == 0) {

		} else {

			for (int i = 0; i < list.getLength(); i++) {
				Node item = list.item(i);

				if (OTRS.TAG_SESSIONCREATE_RESP.equals(item.getNodeName())) {
					NodeList nl = item.getChildNodes();
					
					for (int j = 0; j < nl.getLength(); j++) {
						if (OTRS.TAG_SESSIONID.equals(nl.item(i).getNodeName())) {
							sid = nl.item(j).getTextContent();
						}
					}
				}
			}
		}
		return sid;
	}

	private Document convertSOAPMessageToDOM(SOAPMessage msg)
			throws ParserConfigurationException, SAXException, IOException,
			SOAPException {
		Document doc;
		try {
			doc = msg.getSOAPPart().getEnvelope().getBody()
					.extractContentAsDocument();
		} catch (Exception ex) {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(convertSOAPMessageToXML(msg)));
			doc = db.parse(is);
		}
		printDebug("DOOM created");
		return doc;
	}

	

	

	public List<String> parseOTRSTicketSearchResponse(SOAPMessage msg)
			throws SOAPException, ParserConfigurationException, SAXException,
			IOException {
		List<String> ids = new ArrayList<String>();
		Document doc = convertSOAPMessageToDOM(msg);
		NodeList list = doc.getElementsByTagName(OTRS.TAG_TICKET_SEARCH_RESP);

		for (int i = 0; i < list.getLength(); i++) {
			NodeList nl = list.item(i).getChildNodes();

			for (int j = 0; j < nl.getLength(); j++) {
				if (OTRS.TAG_TICKETID.equals(nl.item(j).getNodeName())) {
					ids.add(nl.item(j).getTextContent().trim());
				}
			}
		}
		return ids;
	}

	public Map<String, String> parseOTRSTicketGetResponse(SOAPMessage msg)
			throws SOAPException, ParserConfigurationException, SAXException,
			IOException {
		Map<String, String> params = new HashMap<String, String>();

		Document doc = convertSOAPMessageToDOM(msg);
		NodeList list = doc.getElementsByTagName(OTRS.TAG_TICKETGET_RESP);

		for (int k = 0; k < list.getLength(); k++) {
			NodeList tags = list.item(k).getChildNodes();

			for (int i = 0; i < tags.getLength(); i++) {

				if (tags.item(i).hasChildNodes()) {
					NodeList items = tags.item(i).getChildNodes();

					for (int j = 0; j < items.getLength(); j++) {
						params.put(items.item(j).getNodeName(), items.item(j)
								.getTextContent().trim());
					}
				}
			}
		}
		return params;
	}

	public OTRSTicket parseOTRSTicketGet(SOAPMessage msg)
			throws SOAPException, ParserConfigurationException, SAXException,
			IOException {
		OTRSTicket ticket = new OTRSTicket();

		Document doc = convertSOAPMessageToDOM(msg);
		NodeList list = doc.getElementsByTagName(OTRS.TAG_TICKETGET_RESP);

		for (int k = 0; k < list.getLength(); k++) {
			NodeList tags = list.item(k).getChildNodes();
			
			for (int i = 0; i < tags.getLength(); i++) {
				
				if (tags.item(i).hasChildNodes()) {
					NodeList items = tags.item(i).getChildNodes();
							
					for (int j = 0; j < items.getLength(); j++) {
						
						try {
							Method method;
							method = ticket.getClass().getMethod("set" + items.item(j).getNodeName(), String.class);
							method.invoke(ticket, items.item(j).getTextContent().trim());
						} catch (NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (DOMException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return ticket;
	}

	
	
	public String convertSOAPMessageToXML(SOAPMessage msg)
			throws SOAPException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		msg.writeTo(out);
		String retmsg = new String(out.toByteArray());
		printDebug("converted");
		return retmsg;
	}

	private Object nodeToObject(Node node) {
		Node xsdTypeNode = node.getAttributes().getNamedItemNS(
				XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");

		Object value;
		if (xsdTypeNode != null) {
			value = XSDTypeConverter.convertXSDToObject(node.getTextContent()
					.trim(), xsdTypeNode.getTextContent().trim());
		} else {
			value = node.getTextContent().trim();
		}

		return value;
	}

	public Object[] nodesToArray(SOAPMessage msg) throws SOAPException {
		Document doc = msg.getSOAPPart().getEnvelope().getBody()
				.extractContentAsDocument();
		Element el = doc.getDocumentElement();
		NodeList nl = el.getChildNodes();

		Object[] results = new Object[nl.getLength()];

		for (int i = 0; i < nl.getLength(); i++) {
			results[i] = this.nodeToObject(nl.item(i));
		}
		return results;
	}

	public List<?> nodesToList(SOAPMessage msg) throws SOAPException {
		return Arrays.asList(this.nodesToArray(msg));
	}

	public Map<String, Object> nodesToMap(SOAPMessage msg) throws SOAPException {
		Map<String, Object> map = new HashMap<>();

		Document doc = msg.getSOAPPart().getEnvelope().getBody()
				.extractContentAsDocument();
		Element el = doc.getDocumentElement();
		NodeList nl = el.getChildNodes();

		for (int i = 0; i < (nl.getLength() / 2); i++) {
			Node valueNode = nl.item(i * 2 + 1);
			String key = nl.item(i * 2).getTextContent().trim();

			Object value = this.nodeToObject(valueNode);

			map.put(key, value);
		}

		return map;
	}
	
	private void printDebug(String str) {
		if (OTRS.DEBUG) {
			System.out.println(str);
		}
	}
}