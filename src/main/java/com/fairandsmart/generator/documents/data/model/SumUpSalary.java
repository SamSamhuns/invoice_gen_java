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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class SumUpSalary {

    private static final Logger LOGGER = Logger.getLogger(SumUpSalary.class.getName());
    private final Random random= new Random();
    private double netApayer;
    private double netImposable;
    private double brut;


    public SumUpSalary(double netApayer, double netImposable) {
        this.netApayer = netApayer;
        this.netImposable = netImposable;
    }

    public double getNetApayer() {
        return netApayer;
    }

    public double getNetImposable() {
        return netImposable;
    }

    public void setNetApayer(double netApayer) {
        this.netApayer = netApayer;
    }

    public void setNetImposable(double netImposable) {
        this.netImposable = netImposable;
    }

    public double getBrut() {
        return brut;
    }

    public void setBrut(double brut) {
        this.brut = brut;
    }

    public String getNetImposabletLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Net imposable : "));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getNetAvantImpotLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Net à payer avant impôt sur le revenue :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getNetApayerLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Net à payer :","Salaire Net à payer"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    @Override
    public String toString() {
        return "SumUpSalary{" +
                "netApayer=" + netApayer +
                ", netImposable=" + netImposable +
                '}';
    }

    public static class Generator implements ModelGenerator<SumUpSalary> {

        @Override
        public SumUpSalary generate(GenerationContext ctx) {
            Random rand =new Random();

            double netApayer = rand.nextDouble();
            double netImpo = rand.nextDouble();

            return new SumUpSalary(netApayer, netImpo);
        }
    }
}
