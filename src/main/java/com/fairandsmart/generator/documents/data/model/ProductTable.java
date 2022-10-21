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
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


public class ProductTable {

    public static class ColumnItem {

        private float colWidth;
        private String colLabelHeader;
        private String colLabelFooter;
        private String colValueFooter;

        public float getColWidth() {
            return colWidth;
        }
        public String getColLabelHeader() {
            return colLabelHeader;
        }
        public String getColLabelFooter() {
            return colLabelFooter;
        }
        public String getColValueFooter() {
            return colValueFooter;
        }

        public void setColWidth(float colWidth) {
            this.colWidth = colWidth;
        }
        public void setColLabelHeader(String colLabelHeader) {
            this.colLabelHeader = colLabelHeader;
        }
        public void setColLabelFooter(String colLabelFooter) {
            this.colLabelFooter = colLabelFooter;
        }
        public void setColValueFooter(String colValueFooter) {
            this.colValueFooter = colValueFooter;
        }

        public ColumnItem(float colWidth, String colLabelHeader, String colLabelFooter, String colValueFooter) {
            this.colWidth = colWidth;
            this.colLabelHeader = colLabelHeader;
            this.colLabelFooter = colLabelFooter;
            this.colValueFooter = colValueFooter;
        }

        @Override
        public String toString() {
            return "ColumnItem{" +
                    "colWidth=" + colWidth +
                    ", colLabelHeader=" + colLabelHeader +
                    ", colLabelFooter=" + colLabelFooter +
                    ", colValueFooter=" + colValueFooter +
                    '}';
        }

        /*
        ColumnProduct{
          colWidth=50
          colLabelHeader=VAT Amt
          colLabelFooter=VAT Total Amt
          colValueFooter=100
          }
        */
    }

    private List<String> tableHeaders;
    private float[] configRow;
    private Map<String, ColumnItem> itemMap;

    private final Random rnd = new Random();
    // This list also order determins which fields to display in table
    private static final Map<List<String>, String> candidateTableHeaders = new HashMap<>();
    {
        candidateTableHeaders.put(Arrays.asList("SN", "Article", "Taxe", "Taux de taxe", "Total"), "fr");

        candidateTableHeaders.put(Arrays.asList("SN", "Qty", "Item", "ItemRate", "Disc", "TaxRate", "Tax", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Qty", "Item", "ItemRate", "Disc", "Tax", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("ItemCode", "Qty", "Item", "ItemRate", "Disc", "TaxRate", "Tax", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "Qty", "ItemRate", "Disc", "Tax", "TaxRate", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "ItemCode", "Qty", "ItemRate", "Tax", "TaxRate", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "ItemCode", "Item", "Qty", "ItemRate", "Tax", "TaxRate", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "ItemCode", "Item", "Disc", "DiscRate", "Tax", "TaxRate", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "Qty", "ItemRate", "Tax", "TaxRate", "SubTotal", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "Disc", "DiscRate", "Tax", "TaxRate", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Qty", "ItemRate", "Tax", "TaxRate", "SubTotal", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "Qty", "ItemRate", "Tax", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("Item", "Qty", "ItemRate", "Tax", "TaxRate", "SubTotal", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("Item", "Qty", "ItemRate", "Tax", "SubTotal"), "en");
        candidateTableHeaders.put(Arrays.asList("Item", "Qty", "ItemRate", "Tax", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "Tax", "TaxRate", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("SN", "Item", "Tax", "Total"), "en");
        candidateTableHeaders.put(Arrays.asList("Item", "Tax", "Total"), "en");
    }

    public List<String> getTableHeaders() {
        return tableHeaders;
    }
    public float[] getConfigRow() {
        return configRow;
    }
    public Map<String, ColumnItem> getItemMap() {
        return itemMap;
    }

    @Override
    public String toString() {
        return "ProductTable{" +
                "tableHeaders=" + tableHeaders +
                "configRow=" + configRow +
                '}';
    }

    public ProductTable(ProductContainer pc, String lang, String amtSuffix, float tableWidth) {

        List<List<String>> filteredTableHeaders = candidateTableHeaders.entrySet().stream().filter(entry -> entry.getValue().equals(lang)).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> tableHeaders = filteredTableHeaders.get(rnd.nextInt(filteredTableHeaders.size()));
        this.tableHeaders = tableHeaders;

        // Building Header Item labels, table values and footer labels list
        this.itemMap = new LinkedHashMap<>();

        //          Identifier                     Width  Column Label Head         Col Label Footer                     Col Value Footer
        this.itemMap.put("SN",       new ColumnItem(40f,  pc.getsnHead(),           "",                                  ""));
        this.itemMap.put("Qty",      new ColumnItem(40f,  pc.getQtyHead(),          "",                                  ""));
        this.itemMap.put("ItemCode", new ColumnItem(40f,  pc.getCodeHead(),         "",                                  ""));
        this.itemMap.put("Item",     new ColumnItem(140f, pc.getNameHead(),         "",                                  ""));
        this.itemMap.put("ItemRate", new ColumnItem(62f,  pc.getUPHead(),           pc.getTotalHead(),                   pc.getFmtTotal()+amtSuffix));
        this.itemMap.put("Disc",     new ColumnItem(62f,  pc.getDiscountHead(),     pc.getDiscountTotalHead(),           pc.getFmtTotalDiscount()+amtSuffix));
        this.itemMap.put("DiscRate", new ColumnItem(62f,  pc.getDiscountRateHead(), pc.getDiscountRateTotalHead(),       pc.getFmtTotalDiscountRate()));
        this.itemMap.put("Tax",      new ColumnItem(62f,  pc.getTaxHead(),          pc.getTaxTotalHead(),                pc.getFmtTotalTax()+amtSuffix));
        this.itemMap.put("TaxRate",  new ColumnItem(62f,  pc.getTaxRateHead(),      pc.getTaxRateTotalHead(),            pc.getFmtTotalTaxRate()));
        this.itemMap.put("SubTotal", new ColumnItem(62f,  pc.getTotalHead(),        pc.getTotalHead(),                   pc.getFmtTotalWithDiscount()+amtSuffix));
        this.itemMap.put("Total",    new ColumnItem(62f,  pc.getWithTaxTotalHead(), pc.getWithTaxAndDiscountTotalHead(), pc.getFmtTotalWithTaxAndDiscount()+amtSuffix));

        // building item table column width list
        float[] configRowWidths = new float[tableHeaders.size()];
        float curWidth = 0;
        for (int i=0; i<configRowWidths.length; i++) {
            float colWidth =  itemMap.get(tableHeaders.get(i)).getColWidth();
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
        this.configRow = configRowWidths;
    }
}
