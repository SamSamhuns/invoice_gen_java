package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.evaluation.CompleteInformation;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class TestSSDDiversitySCR {

    public static double SCR_ratio_layouts(String path1){
        String dirPath = path1;
        File fileName = new File(dirPath);
        File[] fileList = fileName.listFiles();
        ArrayList<Map<Integer, String>> listSSdComponent = new ArrayList<>();
        for (File file: fileList) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
                System.out.println("------");
                NodeList list = doc.getElementsByTagName("DL_PAGE");
                Node node_DL_PAGE = list.item(0);
                Hashtable<String, CompleteInformation> information = new Hashtable<String, CompleteInformation>();
                Map<Integer, String> SSDComponents = new HashMap<>();
                if (node_DL_PAGE.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node_DL_PAGE;
                    NodeList liste = eElement.getElementsByTagName("DL_ZONE");
                    for (int j = 0; j < liste.getLength(); j++) {
                        Node node = liste.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement1 = (Element) node;
                            SSDComponents.put(Integer.parseInt(eElement1.getAttribute("orderpos")),eElement1.getAttribute("optionalclass"));
                        }
                    }
                }
                if(listSSdComponent.isEmpty() || !listSSdComponent.contains(SSDComponents)){
                    listSSdComponent.add(SSDComponents);
                }

            } catch (ParserConfigurationException | SAXException | IOException  e) {
                e.printStackTrace();
            }
        }
        double SCR = Double.valueOf(listSSdComponent.size())/ Double.valueOf(fileList.length);
        return SCR;
    }

    @Test
    public static Double test(String path) throws Exception {
        Double SCR_score = SCR_ratio_layouts(path);
        return SCR_score ;
    }
}
