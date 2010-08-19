/****

    activequant - activestocks.eu

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

	
	contact  : contact@activestocks.eu
    homepage : http://www.activestocks.eu

****/
package org.activequant.util.tools;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * helper class for xml parsing.<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.03.2007] Created (Ulrich Staudinger)<br>
 *  - [01.06.2007] Cleanup (Erik Nijkamp)<br>
 *  - [18.11.2007] Added javadocs and double method (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class XMLParser {

    /**
     * Returns XML Document for a given text.
     * 
     * @param text
     * @return the document in DOM Tree representation.
     * @throws Exception
     */
    public static Document parse(String text) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(text)));
        document.normalize();
        return document;
    }
    
    /**
     * checks if an attribute exists in a given node. 
     * @param n
     * @param name
     * @return
     */
	public static boolean hasAttribute(Node n, String name) {
		return n.getAttributes().getNamedItem(name) != null;
	}

	/**
	 * checks whether this parentNode has a specific child node. 
	 * @param parentNode
	 * @param name
	 * @return
	 */
	public static boolean hasNode(Node parentNode, String name) {
		NodeList nl = parentNode.getChildNodes();
		for(int i=0;i<nl.getLength();i++){
			if(nl.item(i).getNodeName().equals(name)){
				return true;
			}
		}
		return false;
	}

	/**
	 * fetches the value of an attribute in a given node as int 
	 * @param n
	 * @param name
	 * @return
	 */
	public static int getAttributeInt(Node n, String name) {
		assert(hasAttribute(n, name));
		int val = Integer.parseInt(n.getAttributes().getNamedItem(name).getNodeValue());
		return val;
	}

	/**
	 * fetches the value of an attribute in a given node as string
	 * @param n
	 * @param name
	 * @return
	 */
	public static String getAttributeString(Node n, String name) {
		assert(hasAttribute(n, name));
		String val = (n.getAttributes().getNamedItem(name).getNodeValue());
		return val;
	}
	
	/**
	 * fetches the value of an attribute in a given node as double
	 * @param n
	 * @param name
	 * @return
	 */
	public static Double getAttributeDouble(Node n, String name){
		assert(hasAttribute(n, name));
		double val = Double.parseDouble((n.getAttributes().getNamedItem(name).getNodeValue()));
		return val;
	}
	
	/**
	 * returns the first node with a given name. 
	 * @param parentNode
	 * @param name
	 * @return
	 */
	public static Node getFirstNodeWithName(Node parentNode, String name) {
		NodeList nl = parentNode.getChildNodes();
		Node ret = null;
		for(int i=0;i<nl.getLength();i++){
			if(nl.item(i).getNodeName().equals(name)){
				ret = nl.item(i);
				break;
			}
		}
		return ret; 
	}
	
	/**
	 * use this method to print an xml node to a string representation. 
	 * Node in, String out. 
	 * @param n
	 * @return
	 */
	public static String toXml(Node n){
		StringBuffer sb = new StringBuffer();
		
		
		
		if(!n.getNodeName().startsWith("#"))sb.append("<" + n.getNodeName());
		// dump out all attributes.
		NamedNodeMap attr = n.getAttributes();
		if(attr!=null){
			for(int i=0;i<attr.getLength();i++){
				Node attrNode = attr.item(i);
				sb.append(" ");
				sb.append(attrNode.getNodeName()).append("=\"");
				sb.append(attrNode.getNodeValue());
				sb.append("\"");
			}
		}
		if(!n.getNodeName().startsWith("#"))sb.append(">");
		
		if(n.getNodeValue()!=null)sb.append(n.getNodeValue());
		
		for(int i=0;i<n.getChildNodes().getLength();i++){
			Node childNode = n.getChildNodes().item(i);
			sb.append(toXml(childNode));
		}
		
		
		if(!n.getNodeName().startsWith("#"))sb.append("</"+n.getNodeName()+">");
		return sb.toString();
	}
	
}