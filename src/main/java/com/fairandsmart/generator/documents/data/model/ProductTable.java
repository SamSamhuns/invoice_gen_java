package com.fairandsmart.generator.documents.data.model;

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

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;


public class ProductTable {

    public class ColItem {

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

    private List<String> tableHeaders;
    private float[] configRow;
    private Map<String, ColItem> itemMap;

    private final Random rnd = new Random();
    // This list also order determins which fields to display in table
    private static final List<List<String>> candidateTableHeaders = Arrays.asList(
        Arrays.asList("Item",     "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Tax",      "Total"),
        Arrays.asList("SN",       "ItemCode", "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Tax",      "SubTotal"),
        Arrays.asList("Item",     "Qty",      "ItemRate", "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Tax",      "TaxRate",  "Total"),
        Arrays.asList("Item",     "Qty",      "ItemRate", "Tax",      "SubTotal"),
        Arrays.asList("Item",     "Qty",      "Tax",      "Disc",     "SubTotal"),
        Arrays.asList("SN",       "Item",     "Qty",      "ItemRate", "Tax",      "Total"),
        Arrays.asList("SN",       "Item",     "Qty",      "ItemRate", "Tax",      "SubTotal"),
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

    public List<String> getTableHeaders() {
        return tableHeaders;
    }
    public float[] getConfigRow() {
        return configRow;
    }
    public Map<String, ColItem> getItemMap() {
        return itemMap;
    }

    @Override
    public String toString() {
        return "ProductTable{" +
                "tableHeaders=" + tableHeaders +
                "configRow=" + configRow +
                '}';
    }

    public ProductTable(ProductContainer pc, String amtSuffix, float tableWidth) throws Exception {
        // All tableHeaders are considered
        this(pc, amtSuffix,tableWidth, candidateTableHeaders.size());
    }

    public ProductTable(ProductContainer pc, String amtSuffix, float tableWidth, int candTableSize) throws Exception {
        // candTableSize refers to the number of tableHeaders to consider, larger size would mean more proba of longer headers
        int candSize = (int) Math.min(candTableSize, candidateTableHeaders.size());
        List<String> tableHeaders = candidateTableHeaders.get(rnd.nextInt(candSize));

        // Building Header Item labels, table values and footer labels list
        Map<String, ColItem> itemColMap = new HashMap<>();
        //      Hdr Identifier          Width-Weight  Column Label Head         Col Label Footer                     Col Value Footer
        itemColMap.put("SN",       new ColItem(1f,    pc.getsnHead(),           "",                                  ""));
        itemColMap.put("Qty",      new ColItem(1f,    pc.getQtyHead(),          "",                                  ""));
        itemColMap.put("ItemCode", new ColItem(1f,    pc.getCodeHead(),         "",                                  ""));
        itemColMap.put("Item",     new ColItem(3.5f,  pc.getNameHead(),         "",                                  ""));
        itemColMap.put("ItemRate", new ColItem(1.55f, pc.getUPHead(),           pc.getTotalHead(),                   pc.getFmtTotal()+amtSuffix));
        itemColMap.put("Disc",     new ColItem(1.55f, pc.getDiscountHead(),     pc.getDiscountTotalHead(),           pc.getFmtTotalDiscount()+amtSuffix));
        itemColMap.put("DiscRate", new ColItem(1.55f, pc.getDiscountRateHead(), pc.getDiscountRateTotalHead(),       pc.getFmtTotalDiscountRate()));
        itemColMap.put("Tax",      new ColItem(1.55f, pc.getTaxHead(),          pc.getTaxTotalHead(),                pc.getFmtTotalTax()+amtSuffix));
        itemColMap.put("TaxRate",  new ColItem(1.55f, pc.getTaxRateHead(),      pc.getTaxRateTotalHead(),            pc.getFmtTotalTaxRate()));
        itemColMap.put("SubTotal", new ColItem(1.55f, pc.getTotalHead(),        pc.getTotalHead(),                   pc.getFmtTotalWithDiscount()+amtSuffix));
        itemColMap.put("Total",    new ColItem(1.55f, pc.getWithTaxTotalHead(), pc.getWithTaxAndDiscountTotalHead(), pc.getFmtTotalWithTaxAndDiscount()+amtSuffix));

        // find total col weight sums based on tableHeaders
        float weightSum = 0;
        for (String tableHeader: tableHeaders) {
            weightSum += itemColMap.get(tableHeader).getWidthWeight();
        };
        // get base column width based on contents of tableHeaders & total weightSum
        float baseColWidth = tableWidth / weightSum;

        // building item table column width list
        float[] configRowWidths = new float[tableHeaders.size()];
        float curWidth = 0;
        for (int i=0; i<configRowWidths.length; i++) {
            float colWidth = itemColMap.get(tableHeaders.get(i)).getWidthWeight() * baseColWidth;
            curWidth += colWidth;
            configRowWidths[i] = colWidth;
        }

        // Values must add to tableWidth
        assert (curWidth <= tableWidth);
        // if curWidth < tableWidth, increment size of each col till curWidth >= tableWidth
        while (curWidth < tableWidth) {
            for (int i=0; i<configRowWidths.length; i++) {
                configRowWidths[i] += 1;
                curWidth += 1;
                if (curWidth >= tableWidth) {
                    break;
                }
            }
        }
        this.tableHeaders = tableHeaders;
        this.itemMap = itemColMap;
        this.configRow = configRowWidths;
    }
}
