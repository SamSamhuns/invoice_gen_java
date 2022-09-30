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
 * Copyright (C) 2019 Fair And Smart
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

import java.util.List;

public class CategoryEmployee {

    private String category_code;
    private String category;
    List<Job> list_job;

    public CategoryEmployee() {
    }

    public CategoryEmployee(String category_code, String category, List<Job> list_job) {
        this.category_code = category_code;
        this.category = category;
        this.list_job = list_job;
    }

    public String getCode_categorie() {
        return category_code;
    }

    public String getCategorie() {
        return category;
    }

    public List<Job> getList_job() {
        return list_job;
    }

    public void setCode_categorie(String category_code) {
        this.category_code = category_code;
    }

    public void setCategorie(String category) {
        this.category = category;
    }

    public void setList_job(List<Job> list_job) {
        this.list_job = list_job;
    }
}
