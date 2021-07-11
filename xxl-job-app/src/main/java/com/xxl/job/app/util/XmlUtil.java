package com.xxl.job.app.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * xml操作工具类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/6/11
 *
 */
public class XmlUtil {
	private static XmlMapper xmlMapper = new XmlMapper();
	/**
	 * 根据Element获取这个对应的元素的xml。
	 * @param element
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public static String getXML(Element element) {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.setOutputProperty("indent", "yes");

			DOMSource source = new DOMSource();
			source.setNode(element);
			StreamResult result = new StreamResult();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			result.setOutputStream(baos);
			transformer.transform(source, result);
			return baos.toString("UTF-8");



		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 在当前节点下，获取指定名称的子元素。
	 * 
	 * @param ruleNode
	 * @param name
	 * @return Element
	 * @exception
	 * @since 1.0.0
	 */
	public static Element getChildNodeByName(Element ruleNode, String name) {
		NodeList nodeList = ruleNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {			
			Element node = getElement(nodeList.item(i));				
			if (node!=null && node.getTagName()!=null && node.getTagName().equalsIgnoreCase(name)) {
				return node;
			}			
		}
		return null;
	}

	private static Element getElement(Object object){
		if(object!=null && object instanceof Element){
			Element node = (Element) object;
			return node;
		}
		return null;
	}

	public static Document getDocument(String xmlFilePath){
		Document doc = null;
		try {			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();		
			doc = db.parse(xmlFilePath);					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static NodeList selectNodes(String express, Object source) {// 查找节点，返回符合条件的节点集。
		NodeList result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (NodeList) xpath.evaluate(express, source,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static Element selectFirstElement(String express,Object source){
		NodeList list = selectNodes(express, source);
		for(int i=0;i<list.getLength();i++){
			Object obj = list.item(i);
			if(obj instanceof Element){
				return (Element)obj;
			}
		}
		return null;
	}

	/**
	 * 验证格式是否正确
	 * @param root
	 * @param firstName 第一个节点名
	 * @param nextName 第二个节点名
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void checkXmlFormat(org.dom4j.Element root,String firstName,String nextName) throws Exception {
		String msg = "导入文件格式不对";
		if (!root.getName().equals(firstName)) {
			throw new Exception(msg);
		}
		List<org.dom4j.Element> itemLists = root.elements();
		for (org.dom4j.Element elm : itemLists) {
			if (!elm.getName().equals(nextName)) {
				throw new Exception(msg);
			}
		}

	}
	
	/**
	 * Xml格式转为Json格式
	 * @param xml	xml格式字符串
	 * @return		json格式字符串
	 */
	public static String toJson(String xml) {
		StringWriter w = new StringWriter();
		try (JsonParser jp = xmlMapper.getFactory().createParser(xml);JsonGenerator jg = JsonUtil.getMapper().getFactory().createGenerator(w);){
			
			while (jp.nextToken() != null) {
				jg.copyCurrentEvent(jp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return w.toString();
	}
}
