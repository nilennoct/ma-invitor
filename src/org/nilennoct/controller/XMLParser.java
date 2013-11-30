package org.nilennoct.controller;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.lang.Integer.parseInt;

/**
 * Created with IntelliJ IDEA.
 * User: Neo
 * Date: 13-9-5
 * Time: 下午1:40
 */
class XMLParser {

	public static Document parseXML(InputStream in) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputStream xmlStream = new ByteArrayInputStream(AES.decrypt(AES.getBytes(in), NetworkController.baseKey));
//		System.out.println(xmlStream.available());
		if (xmlStream.available() > 0) {
			Document doc = db.parse(xmlStream);
			doc.normalize();
			return doc;
		}
		return null;
	}

	public static String getNodeValue(Document doc, String tagName) {
		NodeList nodes = doc.getElementsByTagName(tagName);
		return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
	}

	public static int getErrorCode(Document doc) {
		return parseInt(doc.getElementsByTagName("code").item(0).getTextContent());
	}
}
