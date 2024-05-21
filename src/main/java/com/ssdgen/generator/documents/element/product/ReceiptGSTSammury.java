package com.ssdgen.generator.documents.element.product;

import com.ssdgen.generator.documents.data.model.ProductReceiptContainer;
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

public class ReceiptGSTSammury extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());
    private final PDFont font;
    private final PDFont fontB;
    private final float fontSize;
    private Color headBackgroundColor;
    private Color bodyBackgroundColor;
    private final VerticalContainer container;
    private final ProductReceiptContainer productContainer;
    private static final Random rnd = new Random();
    private String[] chosenFormatHeaders;

    private static final List<String[]> tableFormat = new ArrayList<>();
    {
        tableFormat.add(new String[] {"CODE", "TAUX", "TTC", "VAT", "HT"} );
        tableFormat.add(new String[] {"CODE", "TAUX", "HT", "VAT", "TTC"} );
        tableFormat.add(new String[] {"CODE", "TAUX", "HT", "VAT"} );
        tableFormat.add(new String[] {"CODE", "HT", "VAT"} );

    }

    private static final List<float[]> tableConfig = new ArrayList<>();
    {
        tableConfig.add(new float[] {70f,70f, 70f, 70f,  70f} );
        tableConfig.add(new float[] {50f,60f, 80f, 80f,  80f} );
        tableConfig.add(new float[] {120f, 60f, 80f,  80f} );
        tableConfig.add(new float[] {140f, 90f, 90f} );

    }

    public Random getRandom() {
        return rnd;
    }

    public ReceiptGSTSammury(float posX, float posY, ProductReceiptContainer productContainer, PDFont font, PDFont fontB, float fontSize) throws Exception {
        container = new VerticalContainer(posX, posY, 0);
        this.productContainer = productContainer;
        this.font = font;
        this.fontB = fontB;
        this.fontSize = fontSize;
        this.init();
    }


    private String getTableElement(String colName){
        String tableElement;

        switch (colName){
            case "CODE":  tableElement = "(1)";
                break;
            case "TAUX":  tableElement = String.format("%.2f",productContainer.getTotalTaxRate() * 100)+"%";
                break;
            case "TTC":  tableElement = productContainer.getFmtTotalWithTax();
                break;
            case "VAT":  tableElement = productContainer.getFmtTotalTax();
                break;
            case "HT":  tableElement = productContainer.getFmtTotal();
                break;
            default: return "Invalid element name Name";
        }
        return  tableElement;
    }

    private void init() throws Exception {
        final Map<String, String> headLabels = new HashMap<>();
        {
            headLabels.put("CODE", productContainer.getGSTHead());
            headLabels.put("TAUX", productContainer.getTaxRateHead());
            headLabels.put("TTC", productContainer.getWithTaxTotalHead());
            headLabels.put("VAT", productContainer.getTaxTotalHead());
            headLabels.put("HT", productContainer.getTotalHead());
        }


        int chosenFormatIndex = getRandom().nextInt(tableFormat.size());
        float[] configRow = tableConfig.get(chosenFormatIndex);
        String [] chosenFormat = tableFormat.get(chosenFormatIndex);
        this.chosenFormatHeaders =chosenFormat;
        TableRowBox head = new TableRowBox(configRow, 0, 0, VAlign.BOTTOM);
        head.setBackgroundColor(Color.WHITE);

        for(String colname: chosenFormat)
        {
            head.addElement(new SimpleTextBox(fontB, fontSize+1, 0, 0, headLabels.get(colname), Color.BLACK, null, HAlign.CENTER), false);
        }
        container.addElement(head);

        TableRowBox productLine = new TableRowBox(configRow, 0, 0);
        HAlign halign;
        String tabelElement;

        for(String colname: chosenFormat)
        {
            tabelElement = getTableElement(colname);
            halign = HAlign.CENTER;
            if(colname.equals("CODE"))
                halign = HAlign.LEFT;
            productLine.addElement(new SimpleTextBox(font, fontSize, 0, 0, tabelElement, Color.BLACK, null, halign),false);// colname), false);
        }
        container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        container.addElement(productLine);
        container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));

        container.addElement(new HorizontalLineBox(0,0, head.getBBox().getWidth()+30, 0));
        container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 15));

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

    public String[] getChosenFormatHeaderss() {
        return chosenFormatHeaders;
    }
}
