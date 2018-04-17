package service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ExportModel {
    public static String ExportFaMaModel() {
        boolean relationships = false,
                isConstraints = false,
                fileSaved = false;

        int countRelationship = 0;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        BufferedReader reader;
        try {
            docBuilder = docFactory.newDocumentBuilder();

            // root element
            Document doc = docBuilder.newDocument();
            // <featuremodel></featuremodel>
            Element featureModel = doc.createElement("featureModel");
            doc.appendChild(featureModel);

            // <struct></struct>
            Element struct = doc.createElement("struct");
            featureModel.appendChild(struct);

            Element constraints = doc.createElement("constraints");
            featureModel.appendChild(constraints);

            Element rootFeature = null;

            reader = new BufferedReader(new FileReader("tmp/default.fm"));
            String line = reader.readLine();
            while (line != null) {
                if(line.contains("%Relationships"))
                {
                    relationships = true;
                    isConstraints = false;
                }else if(line.contains("%Constraints"))
                {
                    isConstraints = true;
                    relationships = false;
                }
                else if(!line.isEmpty())
                {
                    if(relationships)
                    {
                        String cleanString = line.replace(":", "")
                                .replace(";", "");
                        String[] lineFeatures = cleanString.split("\\s+");
                        if(countRelationship == 0)
                        {
                            // Root Feature/Product
                            // <and abstract="true" mandatory="true" name="V"></and>
                            rootFeature = doc.createElement("and");
                            rootFeature.setAttribute("abstract", "true");
                            rootFeature.setAttribute("mandatory", "true");
                            rootFeature.setAttribute("name", lineFeatures[0]);

                            for (int i = 1; i < lineFeatures.length; i++) {
                                Element childFeature;
                                if(lineFeatures[i].contains("["))
                                {
                                    String feature = lineFeatures[i].replace("[","").replace("]","");
                                    // <feature name="Y"/>
                                    childFeature = doc.createElement("feature");
                                    childFeature.setAttribute("name", feature);
                                    rootFeature.appendChild(childFeature);
                                }else{
                                    // <feature mandatory="true" name="X"/>
                                    childFeature = doc.createElement("feature");
                                    childFeature.setAttribute("mandatory", "true");
                                    childFeature.setAttribute("name", lineFeatures[i]);
                                    rootFeature.appendChild(childFeature);
                                }
                            }

                            struct.appendChild(rootFeature);
                        }else{
                            System.out.println("On Else");
                            NodeList elements = featureModel.getElementsByTagName("feature");
                            for (int i = 0; i < elements.getLength(); i++) {
                                System.out.println(elements.item(i));
                            }
                        }
                        countRelationship++;
                    }
                    else{
                        String cleanString = line.replace(";", "");
                        String[] lineConstraints = cleanString.split("\\s+");

                        Element rule = doc.createElement("rule");
                        constraints.appendChild(rule);

                        Element imp = doc.createElement("imp");
                        rule.appendChild(imp);

                        if(lineConstraints[1].equals("REQUIRES"))
                        {
                            Element firstFeature = doc.createElement("var");
                            firstFeature.appendChild(doc.createTextNode(lineConstraints[0]));

                            Element secondFeature = doc.createElement("var");
                            secondFeature.appendChild(doc.createTextNode(lineConstraints[2]));

                            imp.appendChild(firstFeature);
                            imp.appendChild(secondFeature);
                        }else if(lineConstraints[1].equals("EXCLUDES"))
                        {
                            Element firstFeature = doc.createElement("var");
                            firstFeature.appendChild(doc.createTextNode(lineConstraints[0]));

                            Element not = doc.createElement("not");

                            Element secondFeature = doc.createElement("var");
                            secondFeature.appendChild(doc.createTextNode(lineConstraints[2]));
                            not.appendChild(secondFeature);

                            imp.appendChild(firstFeature);
                            imp.appendChild(not);
                        }
                    }
                }

                // read next line
                line = reader.readLine();
            }
            reader.close();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("model.xml"));

            transformer.transform(source, result);

            return "File Saved!";

        } catch (IOException | ParserConfigurationException | TransformerException e) {
            return e.getMessage();
        }
    }
}
