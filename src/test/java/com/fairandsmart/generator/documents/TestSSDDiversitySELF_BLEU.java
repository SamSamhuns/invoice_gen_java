package com.fairandsmart.generator.documents;

/*-
 * #%L
 * FacoGen / A tool for annotated GEDI based invoice generation.
 *
 * Authors:
 *
 * Xavier Lefevre <xavier.lefevre@fairandsmart.com> / FairAndSmart
 * Nicolas Rueff <nicolas.rueff@fairandsmart.com> / FairAndSmart
 * Alan Balbo <alan.balbo@fairandsmart.com> / FairAndSmart
 * Frederic Pierre <frederic.pierre@fairansmart.com> / FairAndSmart
 * Victor Guillaume <victor.guillaume@fairandsmart.com> / FairAndSmart
 * Jérôme Blanchard <jerome.blanchard@fairandsmart.com> / FairAndSmart
 * Aurore Hubert <aurore.hubert@fairandsmart.com> / FairAndSmart
 * Kevin Meszczynski <kevin.meszczynski@fairandsmart.com> / FairAndSmart
 * Djedjiga Belhadj <djedjiga.belhadj@gmail.com> / Loria
 * %%
 * Copyright (C) 2019 - 2020 Fair And Smart
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fairandsmart.generator.evaluation.CompleteInformation;
import com.fairandsmart.generator.evaluation.ElementaryInfo;
import com.fairandsmart.generator.evaluation.InfoMap;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Hashtable;
import java.util.List;


public class TestSSDDiversitySELF_BLEU {

     public static void prepare_classes_content_layout(String path1, String path2){
         String dirPath = path1;
         String dirPath2 = path2;
         File fileName = new File(dirPath);
         File[] fileList = fileName.listFiles();
         for (File file: fileList) {
             int xmin=0,xmax=0,ymin=0,ymax=0;
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
                 if (node_DL_PAGE.getNodeType() == Node.ELEMENT_NODE) {
                     Element eElement = (Element) node_DL_PAGE;
                     NodeList liste = eElement.getElementsByTagName("DL_ZONE");
                     for (int j = 0; j < liste.getLength(); j++) {
                         Node node = liste.item(j);
                         if (node.getNodeType() == Node.ELEMENT_NODE) {
                             Element eElement1 = (Element) node;
                             int x1= Integer.parseInt(eElement1.getAttribute("col"));
                             int y1 = Integer.parseInt(eElement1.getAttribute("row"));
                             int x2 = x1 + Integer.parseInt(eElement1.getAttribute("width"));
                             int y2 = y1 + Integer.parseInt(eElement1.getAttribute("height"));
                             if(xmin == 0 || xmin > x1) xmin =x1;
                             if(xmax == 0 || xmax < x2) xmax =x2;
                             if(ymin == 0 || ymin > y1) ymin =y1;
                             if(ymax == 0 || ymax < y2) ymax =y2;
                             if (!eElement1.getAttribute("correctclass").equals("undefined")) {
                                 ElementaryInfo elInf = new ElementaryInfo(x1, y1, eElement1.getAttribute("contents"));
                                 CompleteInformation info = information.get(eElement1.getAttribute("correctclass"));
                                 if (info != null) {
                                     CompleteInformation inf = information.get(eElement1.getAttribute("correctclass"));
                                     inf.UpdateInformation(elInf,x1,y1,x2,y2);
                                     information.replace(eElement1.getAttribute("correctclass"), inf);
                                 } else {
                                     information.put(eElement1.getAttribute("correctclass"), new CompleteInformation(eElement1.getAttribute("correctclass"),
                                             elInf,x1, y1, x2, y2));
                                 }
                             }
                         }
                     }
                 }
                 InfoMap infoMap = new InfoMap();
                 infoMap.setWidth(xmax-xmin);
                 infoMap.setHeight(ymax-ymin);
                 infoMap.setInformationMap(information);
                 JAXBContext jaxbContext = JAXBContext.newInstance(InfoMap.class);
                 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                 jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                 jaxbMarshaller.marshal(infoMap, new File(dirPath2+file.getName()));
             } catch (ParserConfigurationException | SAXException | IOException | JAXBException e) {
                 e.printStackTrace();
             }
         }
     }

     public static Double score_Self_Bleu_from_xml (String path2)throws JAXBException{
         String dirPath2 = path2;
         File fileName2 = new File(dirPath2);
         File[] fileList2 = fileName2.listFiles();
         List<Double> scoresBLEU = new ArrayList<Double>();
         for (File file: fileList2) {
             JAXBContext jaxbContext = JAXBContext.newInstance(InfoMap.class);
             Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
             InfoMap infMap = (InfoMap) jaxbUnmarshaller.unmarshal(file);
             for(String infId : infMap.getInformationMap().keySet())
             {
                 String className =infMap.getInformationMap().get(infId).getClassName();
                 ArrayList<String> contents = new ArrayList<String>();
                 contents.add(infMap.getInformationMap().get(infId).getContents());
                 for (File file2: fileList2) {
                     InfoMap infMap2 = (InfoMap) jaxbUnmarshaller.unmarshal(file2);
                     for (String infId2 : infMap2.getInformationMap().keySet()) {
                         CompleteInformation info2 = infMap2.getInformationMap().get(infId2);
                         if (infMap2.getInformationMap().get(infId2).getClassName().equals(className)){
                             contents.add(infMap2.getInformationMap().get(infId2).getContents());
                         }
                     }
                 }
                 try {
                     String s = null;
                     String[] cmd = new String[contents.size()+2];
                     cmd[0] ="python3";
                     cmd[1] = "bleu.py";
                     for (int i=0;i<contents.size();i++){
                         cmd[i+2]=contents.get(i);
                     }
                     Thread.sleep(0);
                     Process p = Runtime.getRuntime().exec(cmd);
                     BufferedReader stdInput = new BufferedReader(new
                             InputStreamReader(p.getInputStream()));
                     BufferedReader stdError = new BufferedReader(new
                             InputStreamReader(p.getErrorStream()));
                     // The output from the command
                     System.out.println("Here is the standard output of the command:\n");
                     while ((s = stdInput.readLine()) != null) {
                         System.out.println(s);
                         Double d=Double.parseDouble(s);
                         scoresBLEU.add(d);
                     }
                     System.out.println("There is the standard error of the command :\n");
                     while ((s = stdError.readLine()) != null) {
                         System.out.println(s);
                     }
                 }
                 catch (IOException | InterruptedException e) {
                     System.out.println("exception happened - here's what I know: ");
                     e.printStackTrace();
                 }
             }
         }
         DoubleSummaryStatistics averageOverBLEU = scoresBLEU.stream()
                 .mapToDouble((a) -> a)
                 .summaryStatistics();
         return averageOverBLEU.getAverage();
     }

    @Test
    public static Double test(String path) throws Exception {
        Path path2 = Paths.get(path+"/xml2/");
        if ( !Files.exists(path2) ) {
            Files.createDirectory(path2);
        }
        prepare_classes_content_layout(path+"xml/",path+"/xml2/");
        return score_Self_Bleu_from_xml(path+"/xml2/");
    }

}
