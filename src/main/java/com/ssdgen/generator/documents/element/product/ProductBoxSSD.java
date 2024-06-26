package com.ssdgen.generator.documents.element.product;

import com.ssdgen.generator.documents.data.model.Product;
import com.ssdgen.generator.documents.data.model.ProductContainer;
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

public class ProductBoxSSD extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());
    private final PDFont font;
    private final PDFont fontB;
    private final float fontSize;
    private Color headBackgroundColor;
    private Color bodyBackgroundColor;
    private final VerticalContainer container;
    private final ProductContainer productContainer;
    private String [] chosenFormatForEval;
    private static final Random rnd = new Random();

    private static final List<String[]> tableFormat = new ArrayList<>();
    {
        tableFormat.add(new String[] {"PD", "QTY", "UP", "TXR", "PTWTX"} );
        tableFormat.add(new String[] {"QTY", "PD", "UP", "TXR", "PTWTX"} );
        tableFormat.add(new String[] {"SNO", "PD", "QTY", "UP", "TXR", "PTWTX"} );
        tableFormat.add(new String[] {"SNO", "QTY", "PD", "UP", "TXR", "PTWTX"} );
    }

    private static final List<float[]> tableConfig = new ArrayList<>();
    {
        tableConfig.add(new float[] {210f, 60f, 80f, 80f, 80f} );
        tableConfig.add(new float[] {60f, 210f, 80f, 80f, 80f} );
        tableConfig.add(new float[] {40f, 190f, 70f, 70f, 70f, 70f} );
        tableConfig.add(new float[] {40f, 70f, 190f, 70f, 70f, 70f} );
    }

    public Random getRandom() {
        return rnd;
    }

    public ProductBoxSSD(float posX, float posY, ProductContainer productContainer, PDFont font, PDFont fontB, float fontSize) throws Exception {
        container = new VerticalContainer(posX, posY, 0);
        this.productContainer = productContainer;
        this.font = font;
        this.fontB = fontB;
        this.fontSize = fontSize;
        this.init();
    }

    private String getProductElement(Product product, String colName){
        String productElement;
        switch (colName){
            case "PD":  productElement = product.getName();
                break;
            case "QTY":  productElement = Integer.toString(product.getQuantity());
                break;
            case "UP":  productElement = product.getFmtPrice();
                break;
            case "TXR":  productElement = product.getTaxRate() * 100 +"%";
                break;
            case "PTWTX":  productElement = product.getFmtTotalPrice();
                break;
//            case "sn":  productElement = product.getFmtTotalPrice();
//                break;

            default: return "Invalid Product Name";
        }
        return  productElement;
    }


    private void init() throws Exception {
        final Map<String, String> headLabels = new HashMap<>();
        {
            headLabels.put("PD", productContainer.getNameHead());
            headLabels.put("QTY", productContainer.getQtyHead());
            headLabels.put("UP", productContainer.getUPHead());
            headLabels.put("TXR", productContainer.getTaxRateHead());
            headLabels.put("PTWTX", productContainer.getLineTotalHead());
            headLabels.put("SNO", productContainer.getsnHead());
        }


        int chosenFormatIndex = getRandom().nextInt(tableFormat.size());
        float[] configRow = tableConfig.get(chosenFormatIndex);
        String [] chosenFormat = tableFormat.get(chosenFormatIndex);
        chosenFormatForEval = chosenFormat;
        TableRowBox head = new TableRowBox(configRow, 0, 0, VAlign.BOTTOM);
        head.setBackgroundColor(Color.BLACK);

        for(String colname: chosenFormat)
        {
            head.addElement(new SimpleTextBox(fontB, fontSize+1, 0, 0, headLabels.get(colname), Color.WHITE, null, HAlign.CENTER), false);
        }
        container.addElement(head);

        int productNum= 0;
        for(Product product : productContainer.getProducts() ) {
            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            HAlign halign;
            productNum++;
            String productElement;
            for(String colname: chosenFormat)
            {
                productElement = getProductElement(product, colname);
                if(colname.equals("SNO"))
                    productElement = Integer.toString(productNum);
                halign = HAlign.CENTER;
                if(colname.equals("PD"))
                    halign = HAlign.LEFT;
                //System.out.println(colname + productElement);
                productLine.addElement(new SimpleTextBox(fontB, fontSize, 0, 0, productElement, Color.BLACK, null, halign), false);//, colname), false);
            }
            container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
            container.addElement(productLine);
            container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        }

        container.addElement(new HorizontalLineBox(0,0, head.getBBox().getWidth()+30, 0));
        container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 15));

       /* TableRowBox totalHT = new TableRowBox(configRow, 0, 0);
        totalHT.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalHT.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalHT.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalHT.addElement(new SimpleTextBox(fontB, fontSize+1, 0, 0, productContainer.getTotalHead(), Color.BLACK, null, HAlign.LEFT), false);
        totalHT.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getFmtTotal(), Color.BLACK, null, HAlign.CENTER , "TWTX"), false);

        container.addElement(totalHT);

        TableRowBox totalTax = new TableRowBox(configRow, 0, 0);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTax.addElement(new SimpleTextBox(fontB, fontSize+1, 0, 0, productContainer.getTaxTotalHead(), Color.BLACK, null, HAlign.LEFT), false);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getFmtTotalTax(), Color.BLACK, null, HAlign.CENTER , "TTX"), false);

        container.addElement(totalTax);

        TableRowBox totalTTC = new TableRowBox(configRow, 0, 0);
        totalTTC.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.RIGHT), false);
        totalTTC.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTTC.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTTC.addElement(new SimpleTextBox(fontB, fontSize+1, 0, 0, productContainer.getWithTaxTotalHead(), Color.BLACK, null, HAlign.LEFT), false);
        totalTTC.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getFmtTotalWithTax(), Color.BLACK, null, HAlign.CENTER,"TA" ), false);

        container.addElement(totalTTC);*/
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
