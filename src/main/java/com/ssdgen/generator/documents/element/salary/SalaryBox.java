package com.ssdgen.generator.documents.element.salary;

import com.ssdgen.generator.documents.data.model.SalaryCotisationTable;
import com.ssdgen.generator.documents.data.model.SalaryLine;
import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.HAlign;
import com.ssdgen.generator.documents.element.VAlign;
import com.ssdgen.generator.documents.element.border.BorderBox;
import com.ssdgen.generator.documents.element.container.VerticalContainer;
import com.ssdgen.generator.documents.element.line.HorizontalLineBox;
import com.ssdgen.generator.documents.element.table.TableRowBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.text.DecimalFormat;

public class SalaryBox extends ElementBox {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());
    private final PDFont font;
    private final PDFont fontB;
    private final float fontSize;
    private Color headBackgroundColor;
    private Color bodyBackgroundColor;
    private final VerticalContainer container;
    private final SalaryCotisationTable salaryContainer;
    private String[] chosenFormatForEval;
    private static final Random rnd = new Random();

    private static final List<String[]> tableFormat = new ArrayList<>();
    {
        tableFormat.clear();
        tableFormat.add(new String[] { "Rubrique", "Base", "TxSalarial", "CtSalarial", "TxPatrnl", "CotPatrnl" });
        tableFormat.add(new String[] { "Rubrique", "Base", "TxSalarial", "CtSalarial", "CotPatrnl" });
        tableFormat.add(
                new String[] { "Element", "Rubrique", "Base", "TxSalarial", "CtSalarial", "TxPatrnl", "CotPatrnl" });
    }

    private static final List<float[]> tableConfig = new ArrayList<>();
    {
        tableConfig.add(new float[] { 190f, 40f, 70f, 70f, 70f, 70f });
        tableConfig.add(new float[] { 190f, 90f, 90f, 70f, 70f });
        tableConfig.add(new float[] { 40f, 190f, 40f, 70f, 70f, 70f, 70f });
    }

    public Random getRandom() {
        return rnd;
    }

    public SalaryBox(float posX, float posY, SalaryCotisationTable salaryContainer, PDFont font, PDFont fontB,
            float fontSize) throws Exception {
        container = new VerticalContainer(posX, posY, 0);
        this.salaryContainer = salaryContainer;
        this.font = font;
        this.fontB = fontB;
        this.fontSize = fontSize;
        this.init();
    }

    private String getProductElement(SalaryLine salaryLine, String colName) {
        String salaryElement;
        switch (colName) {
            case "Element":
                if (salaryLine.getCodeElement() == 0)
                    salaryElement = "";
                else {
                    salaryElement = "" + salaryLine.getCodeElement();
                }
                break;
            case "Rubrique":
                salaryElement = salaryLine.getHeading();
                break;
            case "Base":
                if (salaryLine.getBase() == 0)
                    salaryElement = "";
                else {
                    salaryElement = df.format(salaryLine.getBase());
                }
                break;
            case "TxSalarial":
                if (salaryLine.getSalaryRate() == 0)
                    salaryElement = "";
                else {
                    salaryElement = df.format(salaryLine.getSalaryRate());
                }
                break;
            case "CtSalarial":
                if (salaryLine.getEmployeeContributions() == 0)
                    salaryElement = "";
                else {
                    salaryElement = df.format(salaryLine.getEmployeeContributions());
                }
                break;
            case "TxPatrnl":
                if (salaryLine.getEmployerRate() == 0)
                    salaryElement = "";
                else {
                    salaryElement = df.format(salaryLine.getEmployerRate());
                }
                break;
            case "CotPatrnl":
                if (salaryLine.getEmployerContributions() == 0)
                    salaryElement = "";
                else {
                    salaryElement = df.format(salaryLine.getEmployerContributions());
                }
                break;

            default:
                return "Invalid Product Name";
        }
        return salaryElement;
    }

    private void init() throws Exception {
        final Map<String, String> headLabels = new HashMap<>();
        {
            headLabels.put("Rubrique", salaryContainer.getHeadingHead());
            headLabels.put("Element", salaryContainer.getCodeElementHead());
            headLabels.put("Base", salaryContainer.getBaseHead());
            headLabels.put("TxSalarial", salaryContainer.getSalaryRateHead());
            headLabels.put("CtSalarial", salaryContainer.getEmployeeContrHead());
            headLabels.put("TxPatrnl", salaryContainer.getEmployerRateHead());
            headLabels.put("CotPatrnl", salaryContainer.getEmployerContrHead());
        }

        int chosenFormatIndex = getRandom().nextInt(tableFormat.size());
        float[] configRow = tableConfig.get(chosenFormatIndex);
        String[] chosenFormat = tableFormat.get(chosenFormatIndex);
        chosenFormatForEval = chosenFormat;
        TableRowBox head = new TableRowBox(configRow, 0, 0, VAlign.BOTTOM);
        head.setBackgroundColor(Color.LIGHT_GRAY);

        for (String colname : chosenFormat) {
            head.addElement(new SimpleTextBox(fontB, fontSize + 1, 0, 0, headLabels.get(colname), Color.black, null,
                    HAlign.CENTER), false);

        }
        container.addElement(head);

        int salarylineNum = 0;
        for (SalaryLine salaryLine : salaryContainer.getSalaryTableLines()) {
            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            HAlign halign;
            salarylineNum++;
            String salaryElement;
            for (String colname : chosenFormat) {
                salaryElement = getProductElement(salaryLine, colname);
                halign = HAlign.LEFT;
                // productLine.addElement(new SimpleTextBox(fontB, fontSize, 0, 0,
                // salaryElement, Color.BLACK, null, halign, colname), false);
                productLine.addElement(
                        new SimpleTextBox(fontB, fontSize, 0, 0, salaryElement, Color.BLACK, null, halign), false);

            }
            // container.addElement(new HorizontalLineBox(0,0, head.getBBox().getWidth()+30,
            // 0));
            container.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
            container.addElement(productLine);
            // container.addElement(new BorderBox(Color.BLACK,Color.BLACK, 0,0, 0, 0, 5));
        }

        container.addElement(new HorizontalLineBox(0, 0, head.getBBox().getWidth() + 30, 0));
        container.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));

    }

    @Override
    public BoundingBox getBBox() {
        return container.getBBox();
    }

    @Override
    public void setWidth(float width) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        this.container.translate(offsetX, offsetY);
    }

    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        this.container.build(stream, writer);
    }

    public String[] getChosenFormatForEval() {
        return chosenFormatForEval;
    }
}
