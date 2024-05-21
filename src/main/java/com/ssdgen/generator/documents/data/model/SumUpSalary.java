package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;

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
