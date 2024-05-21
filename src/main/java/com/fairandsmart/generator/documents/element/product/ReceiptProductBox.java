package com.fairandsmart.generator.documents.element.product;

import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.ProductReceiptContainer;
import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.VAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
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

public class ReceiptProductBox extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());
    private final PDFont font;
    private final PDFont fontB;
    private final float fontSize;
    private Color headBackgroundColor;
    private Color bodyBackgroundColor;
    private final VerticalContainer container;
    private final ProductReceiptContainer productContainer;
    private static final Random rnd = new Random();
    private boolean qtyTotalAvailabel;
    private boolean headersAvailable;
    private boolean itemsTotalAvailable;
    private boolean QtyBefItems;
    private String[] chosenFormatHeaders;

    private static final List<String[]> tableFormat = new ArrayList<>();
    {
        tableFormat.add(new String[] {"PD", "QTY", "UP",  "PTWTX"} );
        tableFormat.add(new String[] {"QTY", "PD", "UP",  "PTWTX"} );
        tableFormat.add(new String[] { "QTY", "PD", "PTWTX"} );

    }

    private static final List<float[]> tableConfig = new ArrayList<>();
    {
        tableConfig.add(new float[] {100f, 60f, 70f,  70f} );
        tableConfig.add(new float[] {60f, 100f, 70f,  70f} );
        tableConfig.add(new float[] {40f, 180f, 80f} );
    }

    public Random getRandom() {
        return rnd;
    }

    public ReceiptProductBox(float posX, float posY, ProductReceiptContainer productContainer, PDFont font, PDFont fontB, float fontSize) throws Exception {
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
            case "PTWTX":  productElement = product.getFmtTotalPrice();
                break;

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
            //headLabels.put("TXR", productContainer.getTaxRateHead());
            headLabels.put("PTWTX", productContainer.getLineTotalHead());
            //headLabels.put("SNO", productContainer.getsnHead());
        }


        int chosenFormatIndex = getRandom().nextInt(tableFormat.size());
        float[] configRow = tableConfig.get(chosenFormatIndex);
        String [] chosenFormat = tableFormat.get(chosenFormatIndex);
        this.chosenFormatHeaders =chosenFormat;
        TableRowBox head = new TableRowBox(configRow, 0, 0, VAlign.BOTTOM);
        head.setBackgroundColor(Color.WHITE);

        headersAvailable= getRandom().nextBoolean();

        if (headersAvailable) {
            for (String colname : chosenFormat) {
                head.addElement(new SimpleTextBox(fontB, fontSize + 1, 0, 0, headLabels.get(colname), Color.BLACK, null, HAlign.CENTER), false);
            }
            container.addElement(head);
        }

        int productNum= 0;
        for(Product product : productContainer.getProducts() ) {
            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            HAlign halign;
            productNum++;
            String productElement;
            for(String colname: chosenFormat)
            {
                productElement = getProductElement(product, colname);
                /*if(colname.equals("SNO"))
                    productElement = Integer.toString(productNum);*/
                halign = HAlign.CENTER;
                if(colname.equals("PD"))
                    halign = HAlign.LEFT;
                //System.out.println(colname + productElement);
                productLine.addElement(new SimpleTextBox(font, fontSize, 0, 0, productElement, Color.BLACK, null, halign),false);// colname), false);
            }
            container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
            container.addElement(productLine);
            container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        }

        container.addElement(new HorizontalLineBox(0,0, head.getBBox().getWidth()+30, 0));
        container.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 15));

        qtyTotalAvailabel = rnd.nextBoolean();
        itemsTotalAvailable = rnd.nextBoolean();

        if(qtyTotalAvailabel || itemsTotalAvailable) {
            TableRowBox totalQtyItems = new TableRowBox(configRow, 0, 0);
            if (qtyTotalAvailabel && itemsTotalAvailable){
                QtyBefItems = rnd.nextBoolean();
                if (QtyBefItems)
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getQtyTotalHead()+" : "+productContainer.getTotalQty(), Color.BLACK, null, HAlign.CENTER), false);
                else
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getItemsTotalHead() +" : "+productContainer.getTotalItems(), Color.BLACK, null, HAlign.CENTER), false);
                totalQtyItems.addElement(new SimpleTextBox(fontB, fontSize + 1, 0, 0, " ", Color.BLACK, null, HAlign.LEFT), false);
                 if (configRow.length == 4) {
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
                }
                if (QtyBefItems)
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getItemsTotalHead() +" : "+productContainer.getTotalItems(), Color.BLACK, null, HAlign.CENTER), false);
                else
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getQtyTotalHead()+" : "+productContainer.getTotalQty(), Color.BLACK, null, HAlign.CENTER), false);
            }else {
                if (qtyTotalAvailabel)
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getQtyTotalHead()+" : "+productContainer.getTotalQty(), Color.BLACK, null, HAlign.CENTER), false);
                else
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getItemsTotalHead() +" : "+productContainer.getTotalItems(), Color.BLACK, null, HAlign.CENTER), false);

                totalQtyItems.addElement(new SimpleTextBox(fontB, fontSize + 1, 0, 0, " ", Color.BLACK, null, HAlign.LEFT), false);
                totalQtyItems.addElement(new SimpleTextBox(fontB, fontSize + 1, 0, 0, " ", Color.BLACK, null, HAlign.LEFT), false);
                if (configRow.length == 4) {
                    totalQtyItems.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
                }
            }


            container.addElement(totalQtyItems);
        }

       /* TableRowBox totalTax = new TableRowBox(configRow, 0, 0);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        totalTax.addElement(new SimpleTextBox(fontB, fontSize+1, 0, 0, productContainer.getTaxTotalHead(), Color.BLACK, null, HAlign.LEFT), false);
        totalTax.addElement(new SimpleTextBox(font, fontSize, 0, 0, productContainer.getFmtTotalTax(), Color.BLACK, null, HAlign.CENTER , "TTX"), false);

        container.addElement(totalTax);*/

        /*TableRowBox totalTTC = new TableRowBox(configRow, 0, 0);
        if (configRow.length == 4){
            totalTTC.addElement(new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER), false);
        }
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

    public String[] getChosenFormatHeaders() {
        return chosenFormatHeaders;
    }
}
