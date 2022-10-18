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

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.generator.ModelGenerator;

import java.util.logging.Logger;

public class TableColumnItem {

    private static final Logger LOGGER = Logger.getLogger(TableColumnItem.class.getName());
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

    public TableColumnItem(float colWidth, String colLabelHeader, String colLabelFooter, String colValueFooter) {
        this.colWidth = colWidth;
        this.colLabelHeader = colLabelHeader;
        this.colLabelFooter = colLabelFooter;
        this.colValueFooter = colValueFooter;
    }

    @Override
    public String toString() {
        return "TableColumnItem{" +
                "colWidth=" + colWidth +
                ", colLabelHeader=" + colLabelHeader +
                ", colLabelFooter=" + colLabelFooter +
                ", colValueFooter=" + colValueFooter +
                '}';
    }

    /*
    TableColumnItem{
      colWidth=50
      colLabelHeader=VAT Amt
      colLabelFooter=VAT Total Amt
      colValueFooter=100
      }
    */
}
