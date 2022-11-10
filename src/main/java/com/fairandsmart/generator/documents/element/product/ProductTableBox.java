package com.fairandsmart.generator.documents.element.product;

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

import com.fairandsmart.generator.documents.data.helper.HelperCommon;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.line.VerticalLineBox;

import com.mifmif.common.regex.Generex;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.Collection;


public class ProductTableBox extends ElementBox {

    private class ColItem {

        private float widthWeight;  // higher value means col will be wider based on total avai width
        private String labelHeader;
        private String labelFooter;
        private String ValueFooter;

        public float getWidthWeight() {
            return widthWeight;
        }
        public String getLabelHeader() {
            return labelHeader;
        }
        public String getLabelFooter() {
            return labelFooter;
        }
        public String getValueFooter() {
            return ValueFooter;
        }

        public void setWidthWeight(float widthWeight) {
            this.widthWeight = widthWeight;
        }
        public void setLabelHeader(String labelHeader) {
            this.labelHeader = labelHeader;
        }
        public void setLabelFooter(String labelFooter) {
            this.labelFooter = labelFooter;
        }
        public void setValueFooter(String ValueFooter) {
            this.ValueFooter = ValueFooter;
        }

        public ColItem(float widthWeight, String labelHeader, String labelFooter, String ValueFooter) {
            this.widthWeight = widthWeight;
            this.labelHeader = labelHeader;
            this.labelFooter = labelFooter;
            this.ValueFooter = ValueFooter;
        }

        @Override
        public String toString() {
            return "ColItem{" +
                    ", widthWeight=" + widthWeight +
                    ", labelHeader=" + labelHeader +
                    ", labelFooter=" + labelFooter +
                    ", ValueFooter=" + ValueFooter +
                    '}';
        }

        /*
        ColumnProduct{
          widthWeight=1.5
          labelHeader=VAT Amt
          labelFooter=VAT Total Amt
          ValueFooter=100
          }
        */
    }

    private final int candSize;
    private final PDFont fontN;
    private final PDFont fontB;
    private final PDFont fontI;
    private final float fontSizeSmall;
    private final float fontSizeBig;
    private final float width;
    private final Color lineStrokeColor;
    private final float tableTopPosX;
    private final float tableTopPosY;
    private final float tableWidth;

    private final InvoiceModel model;
    private final InvoiceAnnotModel annot;
    private final Map<String, Boolean> proba;

    private VerticalContainer vContainer;
    private final List<HorizontalLineBox> hLines = new ArrayList<HorizontalLineBox>();
    private final List<VerticalLineBox> vLines = new ArrayList<VerticalLineBox>();

    private String tableTopInfo;
    private List<String> tableHeaders;
    private float[] configRow;
    private Map<String, ColItem> itemMap;

    private final Random rnd = new Random();
    private static final Collection<String> numericalHdrs = Arrays.asList("ItemRate", "Disc", "Tax", "SubTotal", "Total");
    // This list also order determins which fields to display in table
    // candTableSize allows to further filter the ppssible values, lower candTableSize means table with fewer cols
    private static final List<List<String>> candidateTableHeaders = Arrays.asList(
        Arrays.asList("SN",       "Tax",      "Total"),
        Arrays.asList("Item",     "Tax",      "Total"),
        Arrays.asList("Item",     "TaxRate",  "Total"),
        Arrays.asList("SN",       "Item",     "Tax",      "Total"),
        Arrays.asList("Item",     "ItemCode", "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Disc",     "Total"),
        Arrays.asList("Item",     "Qty",      "ItemRate", "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Tax",      "TaxRate",  "Total"),
        Arrays.asList("Item",     "Qty",      "ItemRate", "SubTotal", "Total"),
        Arrays.asList("Item",     "Qty",      "Tax",      "SubTotal", "Total"),
        Arrays.asList("SN",       "Item",     "Qty",      "ItemRate", "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Qty",      "ItemRate", "SubTotal", "Total"),
        Arrays.asList("SN",       "Qty",      "Item",     "ItemRate", "Disc",     "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Disc",     "DiscRate", "Tax",      "TaxRate",  "Total"),
        Arrays.asList("SN",       "Qty",      "ItemRate", "Tax",      "TaxRate",  "SubTotal", "Total"),
        Arrays.asList("Item",     "Qty",      "ItemRate", "Tax",      "TaxRate",  "SubTotal", "Total"),
        Arrays.asList("SN",       "Qty",      "Item",     "ItemRate", "Disc",     "TaxRate",  "Tax",      "Total"),
        Arrays.asList("ItemCode", "Qty",      "Item",     "ItemRate", "Disc",     "TaxRate",  "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Qty",      "ItemRate", "Disc",     "Tax",      "TaxRate",  "Total"),
        Arrays.asList("SN",       "Item",     "ItemCode", "Qty",      "ItemRate", "Tax",      "TaxRate",  "Total"),
        Arrays.asList("SN",       "ItemCode", "Item",     "Qty",      "ItemRate", "Tax",      "TaxRate",  "Total"),
        Arrays.asList("SN",       "ItemCode", "Item",     "Disc",     "DiscRate", "Tax",      "TaxRate",  "Total"),
        Arrays.asList("SN",       "Item",     "Qty",      "ItemRate", "Tax",      "TaxRate",  "SubTotal", "Total")
    );
    // item table list top info
    private static final Map<String, List<String>> candidateTableTopInfo = new HashMap<>();
    {
        candidateTableTopInfo.put("fr", Arrays.asList(
            "(Transaction|Nature de la Transaction|Type de Transaction) : (Achat|Vente)",
            "(Votre commande|Commande)",
            ""
        ));
        candidateTableTopInfo.put("en", Arrays.asList(
            "(Transaction|Nature of Transaction|Transaction Type): (Purchase|Sale)",
            "(Purchase|Sale) Order information",
            "(Your Order|Order|Order Details|Order Summary)",
            "Please pay upon receipt",
            "All prices are in",
            "Original",
            ""
        ));
    }

    public String getTableTopInfo() {
        return tableTopInfo;
    }
    public List<String> getTableHeaders() {
        return tableHeaders;
    }
    public float[] getConfigRow() {
        return configRow;
    }
    public Map<String, ColItem> getItemMap() {
        return itemMap;
    }
    public Collection<String> getNumericalHdrs() {
        return numericalHdrs;
    }

    @Override
    public String toString() {
        return "ProductTable{" +
                "tableTopInfo=" + tableTopInfo +
                "tableHeaders=" + tableHeaders +
                "configRow=" + configRow +
                '}';
    }

    public ProductTableBox(PDFont fontN, PDFont fontB, PDFont fontI,
                           float fontSizeSmall, float fontSizeBig,
                           float width, Color lineStrokeColor,
                           float tableTopPosX, float tableTopPosY, float tableWidth,
                           InvoiceModel model, InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        // All tableHeaders are considered
        this(candidateTableHeaders.size(), fontN, fontB, fontI, fontSizeSmall, fontSizeBig,
             width, lineStrokeColor, tableTopPosX, tableTopPosY, tableWidth, model, annot, proba);
    }

    public ProductTableBox(int candTableSize,
                           PDFont fontN, PDFont fontB, PDFont fontI,
                           float fontSizeSmall, float fontSizeBig,
                           float width, Color lineStrokeColor,
                           float tableTopPosX, float tableTopPosY, float tableWidth,
                           InvoiceModel model, InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        // candTableSize refers to the number of tableHeaders to consider, larger size would mean more proba of longer headers
        this.candSize = Math.min(candTableSize, candidateTableHeaders.size());

        this.fontN = fontN;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSizeSmall = fontSizeSmall;
        this.fontSizeBig = fontSizeBig;
        this.width = width;
        this.lineStrokeColor = lineStrokeColor;
        this.tableTopPosX = tableTopPosX;
        this.tableTopPosY = tableTopPosY;
        this.tableWidth = tableWidth;

        this.model = model;
        this.annot = annot;
        this.proba = proba;

        this.init();
    }

    private void init() throws Exception {
        boolean upperCap = rnd.nextBoolean();  // table header items case
        Color black = Color.BLACK;
        Color white = Color.WHITE;
        Color lgray = new Color(239,239,239);
        PDFont fontNB = rnd.nextBoolean() ? fontN : fontB;
        Company company = model.getCompany();
        ProductContainer pc = model.getProductContainer();

        // check if cur should be included in table amt items
        String amtSuffix = "";
        String cur = pc.getCurrency();
        if (proba.get("currency_in_table_items")) {
          amtSuffix = " "+cur;
          annot.getTotal().setCurrency(cur);
        }

        float tableRightPosX = tableTopPosX+tableWidth;
        // table main vContainer
        vContainer = new VerticalContainer(tableTopPosX,tableTopPosY,width);

        // Building Header Item labels, table values and footer labels list
        Map<String, ColItem> itemMap = new HashMap<>();
        //      Hdr Identifier          Width-weight  Column Label Head         Col Label Footer                     Col Value Footer
        itemMap.put("SN",       new ColItem(1.00f, pc.getsnHead(),           "",                                  ""));
        itemMap.put("Qty",      new ColItem(1.00f, pc.getQtyHead(),          "",                                  ""));
        itemMap.put("ItemCode", new ColItem(1.09f, pc.getCodeHead(),         "",                                  ""));
        itemMap.put("Item",     new ColItem(3.50f, pc.getNameHead(),         "",                                  ""));
        itemMap.put("ItemRate", new ColItem(1.55f, pc.getUPHead(),           pc.getTotalHead(),                   pc.getFmtTotal()+amtSuffix));
        itemMap.put("Disc",     new ColItem(1.55f, pc.getDiscountHead(),     pc.getDiscountTotalHead(),           pc.getFmtTotalDiscount()+amtSuffix));
        itemMap.put("DiscRate", new ColItem(1.50f, pc.getDiscountRateHead(), pc.getDiscountRateTotalHead(),       pc.getFmtTotalDiscountRate()));
        itemMap.put("Tax",      new ColItem(1.55f, pc.getTaxHead(),          pc.getTaxTotalHead(),                pc.getFmtTotalTax()+amtSuffix));
        itemMap.put("TaxRate",  new ColItem(1.50f, pc.getTaxRateHead(),      pc.getTaxRateTotalHead(),            pc.getFmtTotalTaxRate()));
        itemMap.put("SubTotal", new ColItem(1.55f, pc.getTotalHead(),        pc.getTotalHead(),                   pc.getFmtTotalWithDiscount()+amtSuffix));
        itemMap.put("Total",    new ColItem(1.55f, pc.getWithTaxTotalHead(), pc.getWithTaxAndDiscountTotalHead(), pc.getFmtTotalWithTaxAndDiscount()+amtSuffix));

        // find total col weight sums based on randomly selected tableHeaders
        List<String> tableHeaders = candidateTableHeaders.get(rnd.nextInt(candSize));
        float weightSum = 0;
        for (String tableHeader: tableHeaders) {
            weightSum += itemMap.get(tableHeader).getWidthWeight();
        }
        // get base column width based on contents of tableHeaders & total weightSum
        float baseColWidth = (float) Math.floor(tableWidth / weightSum);

        // building item table column width list
        float[] configRow = new float[tableHeaders.size()];
        float curWidth = 0;
        for (int i=0; i<configRow.length; i++) {
            float colWidth = itemMap.get(tableHeaders.get(i)).getWidthWeight() * baseColWidth;
            curWidth += colWidth;
            configRow[i] = colWidth;
        }

        // if curWidth < tableWidth, increment size of each col till curWidth >= tableWidth
        if (curWidth < tableWidth) {
            float diff = tableWidth - curWidth;
            float addEach = diff/configRow.length;
            for (int i=0; i<configRow.length; i++) {
                configRow[i] += addEach;
                curWidth += addEach;
                if (curWidth >= tableWidth) {
                    configRow[i] -= addEach;
                    curWidth -= addEach;
                    break;
                }
            }
        }
        assert curWidth <= tableWidth;

        HAlign tableHdrAlign = proba.get("table_center_align_items") ? HAlign.CENTER : HAlign.LEFT;
        // table header text colors
        Color hdrTextColor = proba.get("table_hdr_black_text") ? black: white; // hdrTextColor black (predominantly) or white
        Color hdrBgColor = (hdrTextColor == white) ? black: Arrays.asList(Color.GRAY, lgray, white).get(rnd.nextInt(3)); // hdrBgColor should be contrasting to hdrTextColor

        // table top info, randomly select tableTopInfo based on lang
        List<String> tableTopTexts = candidateTableTopInfo.get(model.getLang());
        String tableTopText = new Generex(tableTopTexts.get(rnd.nextInt(tableTopTexts.size()))).random();
        tableTopText = tableTopText.equals("All prices are in")? tableTopText+" "+cur: tableTopText;
        tableTopText = (hdrBgColor == white) ? "" : tableTopText;

        vContainer.addElement(new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB),9,0,0, tableTopText));
        vContainer.addElement(new BorderBox(white, white, 0,0,0,0,1));

        float tableTopTextHeight = vContainer.getBBox().getHeight();

        // table top horizontal line
        hLines.add(new HorizontalLineBox(tableTopPosX, tableTopPosY-tableTopTextHeight, tableRightPosX, tableTopPosY-tableTopTextHeight, lineStrokeColor));

        // table item list head
        TableRowBox row1 = new TableRowBox(configRow, 0, 0);
        for (String tableHeader: tableHeaders) {
            String hdrLabel = itemMap.get(tableHeader).getLabelHeader();
            String tableHdrLabel = upperCap ? hdrLabel.toUpperCase() : hdrLabel;
            // if numerical header used, check if cur needs to appended at the end
            if (proba.get("currency_in_table_headers") && !proba.get("currency_in_table_items") && numericalHdrs.contains(tableHeader)) {
                tableHdrLabel += " ("+cur+")";
            }
            row1.addElement(new SimpleTextBox(fontNB,fontSizeBig,0,0, tableHdrLabel, hdrTextColor, hdrBgColor, tableHdrAlign, hdrLabel+"HeaderLabel"), false);
        }
        row1.setBackgroundColor(hdrBgColor);

        vContainer.addElement(row1);
        vContainer.addElement(new HorizontalLineBox(0, 0, tableRightPosX, 0, lineStrokeColor));
        vContainer.addElement(new BorderBox(white, white, 0,0,0,0,5));

        // table item list body
        String quantity; String snNum;
        String qtySuffix = rnd.nextBoolean() ? " "+pc.getQtySuffix() : "" ;
        Color cellTextColor; Color cellBgColor;
        for(int w=0; w<pc.getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);
            cellTextColor = black;
            cellBgColor = randomProduct.getName().equalsIgnoreCase("shipping") ? lgray: white;
            quantity = randomProduct.getName().equalsIgnoreCase("shipping") ? "": randomProduct.getQuantity()+qtySuffix;
            snNum = randomProduct.getName().equalsIgnoreCase("shipping") ? "": Integer.toString(w + 1);

            InvoiceAnnotModel.Item randomItem = new InvoiceAnnotModel.Item();
            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String cellText = "";
                PDFont cellFont = fontN;
                HAlign cellAlign = tableHdrAlign;
                switch (tableHeader) {
                    case "SN":
                        cellText = snNum;
                        randomItem.setSerialNumber(cellText); break;
                    case "Qty":
                        cellText = quantity;
                        randomItem.setQuantity(cellText); break;
                    case "ItemCode":
                        cellText = randomProduct.getCode();
                        randomItem.setItemCode(cellText); break;
                    case "Item":
                        cellFont = fontNB;
                        cellText = randomProduct.getName();
                        randomItem.setDescription(cellText); break;
                    case "ItemRate":
                        cellText = randomProduct.getFmtPrice()+amtSuffix;
                        randomItem.setUnitPrice(cellText); break;
                    case "Disc":
                        cellText = randomProduct.getFmtTotalDiscount()+amtSuffix;
                        randomItem.setDiscount(cellText); break;
                    case "DiscRate":
                        cellText = randomProduct.getFmtDiscountRate();
                        randomItem.setDiscountRate(cellText); break;
                    case "Tax":
                        cellText = randomProduct.getFmtTotalTax()+amtSuffix;
                        randomItem.setTax(cellText); break;
                    case "TaxRate":
                        cellText = randomProduct.getFmtTaxRate();
                        randomItem.setTaxRate(cellText); break;
                    case "SubTotal":
                        cellText = randomProduct.getFmtTotalPriceWithDiscount()+amtSuffix;
                        randomItem.setSubTotal(cellText); break;
                    case "Total":
                        cellText = randomProduct.getFmtTotalPriceWithTaxAndDiscount()+amtSuffix;
                        randomItem.setTotal(cellText); break;
                }
                cellBgColor = proba.get("alternate_table_items_bg_color") && w % 2 == 0 ? lgray: cellBgColor;
                SimpleTextBox rowBox = new SimpleTextBox(cellFont, fontSizeSmall, 0, 0, cellText, cellTextColor, cellBgColor, cellAlign, tableHeader+"Item");
                productLine.addElement(rowBox, false);
            }
            annot.getItems().add(randomItem);

            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
            productLine.setBackgroundColor(cellBgColor);
            vContainer.addElement(productLine);
            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
        }

        vContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
        vContainer.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
        float tableItemsHeight = vContainer.getBBox().getHeight() - tableTopTextHeight;

        vContainer.addElement(new HorizontalLineBox(0,0, tableRightPosX, 0, lineStrokeColor));
        vContainer.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
        vContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));

        if (proba.get("table_footer_multi_row")) {
            float[] configFooterRow = {450f, 80f}; // Adds up to 530 which is pageW - leftM - rightM
            for (int i=0; i<tableHeaders.size(); i++ ) {
                TableRowBox footerInvoice = new TableRowBox(configFooterRow, 0, 25);
                String tableHeader = tableHeaders.get(i);
                String hdrLabel = itemMap.get(tableHeader).getLabelFooter();
                String hdrValue = itemMap.get(tableHeader).getValueFooter();
                footerInvoice.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), HAlign.RIGHT, tableHeader+"FooterLabel"), false);
                footerInvoice.addElement(new SimpleTextBox(fontN ,fontSizeSmall,0,0, (upperCap ? hdrValue.toUpperCase() : hdrValue), HAlign.RIGHT, tableHeader+"FooterValue"), false);
                vContainer.addElement(footerInvoice);

                switch (tableHeader) {
                    case "Tax": annot.getTotal().setTaxPrice(hdrValue); break;
                    case "TaxRate": annot.getTotal().setTaxRate(hdrValue); break;
                    case "Disc": annot.getTotal().setDiscountPrice(hdrValue); break;
                    case "DiscRate": annot.getTotal().setDiscountRate(hdrValue); break;
                    case "ItemRate": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "SubTotal": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "Total": annot.getTotal().setTotalPrice(hdrValue); break;
                }
            }
        }
        else if (proba.get("table_footer_single_row"))  {
            // Footer Labels for final total amount, tax and discount
            TableRowBox titleTotalInvoice = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrLabel = itemMap.get(tableHeader).getLabelFooter();
                titleTotalInvoice.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), tableHdrAlign, tableHeader+"FooterLabel"), false);
            }
            vContainer.addElement(titleTotalInvoice);

            vContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
            vContainer.addElement(new HorizontalLineBox(0,0, tableRightPosX, 0, lineStrokeColor));
            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));

            // Footer Numerical formatted values for final total amount, tax and discount
            TableRowBox totalInvoice1 = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrValue = itemMap.get(tableHeader).getValueFooter();
                totalInvoice1.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, (upperCap ? hdrValue.toUpperCase() : hdrValue), tableHdrAlign, tableHeader+"FooterValue"), false);
                switch (tableHeader) {
                    case "Tax": annot.getTotal().setTaxPrice(hdrValue); break;
                    case "TaxRate": annot.getTotal().setTaxRate(hdrValue); break;
                    case "Disc": annot.getTotal().setDiscountPrice(hdrValue); break;
                    case "DiscRate": annot.getTotal().setDiscountRate(hdrValue); break;
                    case "ItemRate": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "SubTotal": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "Total": annot.getTotal().setTotalPrice(hdrValue); break;
                }
            }
            vContainer.addElement(totalInvoice1);

            vContainer.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
            vContainer.addElement(new HorizontalLineBox(0, 0, tableRightPosX, 0, lineStrokeColor));
            vContainer.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
        }

        // Add registered address information
        if (proba.get("registered_address_info")) {
            vContainer.addElement(new HorizontalLineBox(0, 0, tableRightPosX, 0, lineStrokeColor));
            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));

            String addressFooterText = String.format("Registered Address for %s, %s, %s, %s, %s, %s",
                                                     company.getName(),
                                                     company.getAddress().getLine1(),
                                                     company.getAddress().getLine2(),
                                                     company.getAddress().getZip(),
                                                     company.getAddress().getCity(),
                                                     company.getAddress().getCountry());
            SimpleTextBox addressFooter = new SimpleTextBox(fontN, fontSizeBig, 0, 0, addressFooterText);
            addressFooter.setWidth(500);
            vContainer.addElement(addressFooter);
            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
            vContainer.addElement(new HorizontalLineBox(0, 0, tableRightPosX, 0, lineStrokeColor));

            annot.getVendor().setVendorName(company.getName());
            annot.getVendor().setVendorAddr(company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity()+" "+company.getAddress().getCountry());
            annot.getVendor().setVendorPOBox(company.getAddress().getZip());
        }
        else if (proba.get("total_in_words")) {
            vContainer.addElement(new HorizontalLineBox(0, 0, tableRightPosX, 0, lineStrokeColor));
            vContainer.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));

            String totalInWordsText = HelperCommon.spellout_number(
                    pc.getTotalWithTax(),
                    new Locale(model.getLocale()));
            totalInWordsText = "Total in Words: " + totalInWordsText+" "+cur;
            totalInWordsText = (rnd.nextInt(100) < 50) ? totalInWordsText.toUpperCase() : totalInWordsText;

            SimpleTextBox totalInWordsFooter = new SimpleTextBox(fontN, fontSizeBig+1, 0, 0, totalInWordsText);
            totalInWordsFooter.setWidth(500);
            vContainer.addElement(totalInWordsFooter);
            vContainer.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
            vContainer.addElement(new HorizontalLineBox(0, 0, tableRightPosX, 0, lineStrokeColor));
            annot.getTotal().setCurrency(cur);
        }

        // Add vertical borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = tableTopPosX;
            float yPos = tableTopPosY - tableTopTextHeight;
            vLines.add(new VerticalLineBox(xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor));
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                vLines.add(new VerticalLineBox(xPos-2, yPos, xPos-2, yPos - tableItemsHeight, lineStrokeColor));
                xPos += configRow[i];
            }
            vLines.add(new VerticalLineBox(tableRightPosX, yPos, tableRightPosX, yPos - tableItemsHeight, lineStrokeColor));
        }
    }

    @Override
    public BoundingBox getBBox() {
        return vContainer.getBBox();
    }

    @Override
    public void setWidth(float width) throws Exception {
        vContainer.getBBox().setWidth(width);
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        vContainer.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        vContainer.build(stream,writer);
        for (VerticalLineBox line: vLines) line.build(stream,writer);
        for (HorizontalLineBox line: hLines) line.build(stream,writer);
    }
}
