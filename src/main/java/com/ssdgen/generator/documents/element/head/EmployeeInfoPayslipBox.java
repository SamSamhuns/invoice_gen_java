package com.ssdgen.generator.documents.element.head;

import com.ssdgen.generator.documents.data.model.IDNumbers;
import com.ssdgen.generator.documents.data.model.PayslipModel;
import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.HAlign;
import com.ssdgen.generator.documents.element.container.HorizontalContainer;
import com.ssdgen.generator.documents.element.container.VerticalContainer;
import com.ssdgen.generator.documents.element.line.HorizontalLineBox;
import com.ssdgen.generator.documents.element.line.VerticalLineBox;
import com.ssdgen.generator.documents.element.table.TableRowBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmployeeInfoPayslipBox extends ElementBox {

    private PDFont font;
    private PDFont fontB;
    private float fontSize;
    private PayslipModel model;
    private VerticalContainer container;
    private HorizontalContainer hcontainer;
    private PDDocument document;
    private IDNumbers idnumObj;
    private String[] idNames;
    private List<String> listOptClasses;
    private static final List<String[]> idNumbersOrder = new ArrayList<>();
    {
       // idNumbersOrder.add(new String[] {"Siret", "Toa", "Vat"} );

        idNumbersOrder.add(new String[] {"Toa","Cid", "Siret", "Vat"} );
        idNumbersOrder.add(new String[] { "Siret","Toa","Vat"} );
        idNumbersOrder.add(new String[] {"Toa","Rcs", "Siret","Vat"} );
        idNumbersOrder.add(new String[] {"Toa","Rcs", "Siret", "Vat"} );
        idNumbersOrder.add(new String[] {"Toa", "Siret", "Vat"} );
        idNumbersOrder.add(new String[] { "Siret","Toa", "Cid", "Vat"} );
        idNumbersOrder.add(new String[] {"Toa", "Rcs", "Siret", "Vat"} );
    }

    public EmployeeInfoPayslipBox(HorizontalContainer hcontainer){this.hcontainer = hcontainer;}
    public EmployeeInfoPayslipBox(VerticalContainer container){this.container = container;}

    public EmployeeInfoPayslipBox(PDFont font, PDFont fontB, float fontSize, PayslipModel model, PDDocument document) throws Exception {
        this.font = font;
        this.fontB = fontB;
        this.fontSize = fontSize;
        this.model = model;
        this.document = document;
        this.container = new VerticalContainer(0, 0, 0);
        this.hcontainer = new HorizontalContainer(0, 0);
        this.idnumObj = model.getCompany().getIdNumbers();

        this.idNames = idNumbersOrder.get(model.getRandom().nextInt(idNumbersOrder.size()));
        this.init();
    }

    private void init() throws Exception
    {
        container.addElement(concatContainersVertically(new ElementBox[] {
                getEmployeeSucurityNumberBlock(), getEmployeeRegNumberBlock(), getEmployeeCodeBlock()} ) );
    }


    public List<String> getListOptClasses() {
        return listOptClasses;
    }

    public VerticalContainer getEmployeeCodeBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEmployeCodeLabel());
        //Label.setEntityName("ECH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEmployeCode());
        Value.setEntityName("EC");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getEmployeeRegNumberBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getRegistartionNumberLabel());
        //Label.setEntityName("ERNH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getRegistrationNumber());
        Value.setEntityName("ERN");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getEmployeeSucurityNumberBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getSecurityNumberLabel());
        //Label.setEntityName("SSNH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getSocialSecurityNumber());
        Value.setEntityName("SSN");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getEmployeeDateStartBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getDateStartLabel());
        //Label.setEntityName("DSH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getArrivalDate());
        Value.setEntityName("DS");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getJobBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEmploymentLabel());
        //Label.setEntityName("EEMH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEmployment());
        Value.setEntityName("EEM");
        Value.setWidth(100-Label.getBBox().getWidth());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getQualifBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getQualifLabel());
        //Label.setEntityName("QEH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getAssignment());
        Value.setEntityName("QE");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getReleaseDateBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getReleaseDateLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getReleaseDate());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getClassificationBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getClassificationLabel());
        //Label.setEntityName("CLEH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getClassification());
        Value.setEntityName("CLE");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getCoeffBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getCoeffLabel());
        //Label.setEntityName("COEH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getCoef());
        Value.setEntityName("COE");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getSocialSecurityCeilingBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getSocialSecurityCeilingLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getSocialSecurityCeiling());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getHourlyRateBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getHourlyRateLabel());

        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getHourlyRate());
        //Value.setEntityName("HR");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getDateSeniorityBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getDateSeniorityLabel());
        //Label.setEntityName("SDH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getDateSeniority());
        Value.setEntityName("SD");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getMonthlyPayRefBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMonthlyRefPayLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMonthlyPayRef());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getMonthlyPayBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);
        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMonthlyPayLabel());
        //Label.setEntityName("BSH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMonthlyPay());
        Value.setEntityName("BS");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);
        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getTimeTableBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getTimeTableLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getTimetable());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getEchelonBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEchelonLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEchelon());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getContratBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getContratLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getContratType());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getAssignementBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getAssignementLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getAssignment());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getCoefMinBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMinCoeffLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMincoef());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getPeriodBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getPaymentPeriodLabel());
        //Label.setEntityName("PPH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        int rand= model.getRandom().nextInt(2);
        String pattern ="";
        switch (rand){
            case 0:
                pattern =  "MMM YYYY";
                break;
            case 1:
                pattern =  "MM/YYYY";
                break;
        }
        DateFormat df = new SimpleDateFormat(pattern);
        String dateAsString = df.format(model.getEmployeeInformation().getPeriode());
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, dateAsString);
        Value.setEntityName("PP");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getPaymentDateBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getPaymentDateLabel());
        //Label.setEntityName("PDH");
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getPaymentDate());
        Value.setEntityName("PD");
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }
    public VerticalContainer getPaymentPeriodeDatesBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer0 = new HorizontalContainer(0, 0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getPaymentPeriodDatesLabel());
        Label.setPadding(0, 0, 2, 0);
        companyIDContainer0.addElement(Label);
        idContainer.addElement(companyIDContainer0);

        Calendar cal = Calendar.getInstance();
        cal.setTime(model.getEmployeeInformation().getPeriode());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        String pattern = "d MMM YYYY";
        DateFormat df = new SimpleDateFormat(pattern);
        String date1AsString = df.format(cal.getTime());

        SimpleTextBox Label1 = new SimpleTextBox(font, fontSize, 0, 0, "Du" );
        Label1.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label1);
        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0, date1AsString);
        // Value.setEntityName("S" + idName.toUpperCase());
        Value.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value);

        SimpleTextBox Label2 = new SimpleTextBox(font, fontSize, 0, 0, "au" );
        Label2.setPadding(0, 0, 2, 0);
        companyIDContainer.addElement(Label2);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        String date2AsString = df.format(cal.getTime());
        SimpleTextBox Value1 = new SimpleTextBox(font, fontSize, 0, 0, date2AsString);
        // Value.setEntityName("S" + idName.toUpperCase());
        Value1.setPadding(0, 0, 3, 0);
        companyIDContainer.addElement(Value1);

        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getConvCollectBlock() throws Exception
    {
        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer companyIDContainer = new HorizontalContainer(0, 0);

        SimpleTextBox Label = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getConv());
        //Label.setEntityName("CCNH");
        Label.setPadding(0, 0, 2, 0);
        Label.setEntityName("CCN");
        Label.setWidth(300);
        companyIDContainer.addElement(Label);
        idContainer.addElement(companyIDContainer);

        return idContainer;
    }

    public VerticalContainer getEmployeeInformationTable1() throws  Exception
    {
        float[] configRow = {70f, 70f,70f};
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer ligne1 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne2 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne3 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne4 = new HorizontalContainer(0, 0);
        listOptClasses = new ArrayList<>();
        ligne1.addElement(getEmployeeCodeBlock());
        listOptClasses.add("Code");
        emptyBox.setWidth(configRow[0]);
        ligne1.addElement(emptyBox);
        ligne1.addElement(getSocialSecurityCeilingBlock());
        listOptClasses.add("SSCN");
        idContainer.addElement(ligne1);

        ligne2.addElement(getJobBlock());
        listOptClasses.add("Job");
        emptyBox.setWidth(configRow[0]);
        ligne2.addElement(emptyBox);
        ligne2.addElement(getTimeTableBlock());
        listOptClasses.add("TimeTable");
        idContainer.addElement(ligne2);

        ligne3.addElement(getQualifBlock());
        listOptClasses.add("Qualif");
        emptyBox.setWidth(configRow[0]);
        ligne3.addElement(emptyBox);
        ligne3.addElement(getHourlyRateBlock());
        listOptClasses.add("HourRate");
        idContainer.addElement(ligne3);

        ligne4.addElement(getEchelonBlock());
        emptyBox.setWidth(configRow[0]);
        ligne4.addElement(emptyBox);
        listOptClasses.add("Echelon");
        ligne4.addElement(getCoeffBlock());
        listOptClasses.add("Coeff");
        idContainer.addElement(ligne4);

        return idContainer;
    }

    public VerticalContainer getEmployeeInformationTable2() throws  Exception
    {
        float[] configRow = {70f };
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer ligne1 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne2 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne3 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne4 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne5 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne6 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne7 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne8 = new HorizontalContainer(0, 0);

        listOptClasses = new ArrayList<String>();

        ligne1.addElement(getEmployeeRegNumberBlock());
        idContainer.addElement(ligne1);
        listOptClasses.add("RegNumber");

        ligne2.addElement(getEmployeeSucurityNumberBlock());
        idContainer.addElement(ligne2);
        listOptClasses.add("ESSN");

        ligne3.addElement(getEmployeeDateStartBlock());
        idContainer.addElement(ligne3);
        listOptClasses.add("DateStart");

        ligne4.addElement(getJobBlock());
        idContainer.addElement(ligne4);
        listOptClasses.add("Job");

        ligne5.addElement(getQualifBlock());
        idContainer.addElement(ligne5);
        listOptClasses.add("Qualif");

        ligne6.addElement(getCoeffBlock());
        idContainer.addElement(ligne6);
        listOptClasses.add("Coeff");

        ligne7.addElement(getSocialSecurityCeilingBlock());
        idContainer.addElement(ligne7);
        listOptClasses.add("SSCN");

        ligne8.addElement(getContratBlock());
        idContainer.addElement(ligne8);
        listOptClasses.add("contract");
        return idContainer;
    }

    public TableRowBox getEmployeeInformationTable3() throws  Exception
    {
        float[] configRow = {150f, 150f,150f};
        listOptClasses = new ArrayList<String>();
        TableRowBox firstPart2 = new TableRowBox(configRow, 0, 0);
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getEmployeeRegNumberBlock(),getEmployeeDateStartBlock(),getJobBlock(),getQualifBlock()})), false);
        listOptClasses.add("DateStart");
        listOptClasses.add("Job");
        listOptClasses.add("Qualif");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                emptyBox ,emptyBox,emptyBox,getClassificationBlock()})), false);
        listOptClasses.add("Classification");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getSocialSecurityCeilingBlock() ,emptyBox,getDateSeniorityBlock(),getCoeffBlock()})), false);
        listOptClasses.add("SSCN");
        listOptClasses.add("DateSeniority");
        listOptClasses.add("Coeff");
        return firstPart2;
    }

    public TableRowBox getEmployeeInformationTable4() throws  Exception
    {
        float[] configRow = {120f, 120f,120f,120f};
        listOptClasses = new ArrayList<String>();
        TableRowBox firstPart2 = new TableRowBox(configRow, 0, 0);
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getEmployeeDateStartBlock(),getJobBlock(),getQualifBlock()})), false);
        listOptClasses.add("DateStart");
        listOptClasses.add("Job");
        listOptClasses.add("Qualif");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getReleaseDateBlock() ,getHourlyRateBlock(),getClassificationBlock()})), false);
        listOptClasses.add("ReleaseDate");
        listOptClasses.add("HourlyRate");
        listOptClasses.add("Classification");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                emptyBox ,emptyBox,getCoeffBlock()})), false);
        listOptClasses.add("Coeff");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                emptyBox ,emptyBox,getSocialSecurityCeilingBlock()})), false);
        listOptClasses.add("SSCN");
        return firstPart2;
    }


    public TableRowBox getEmployeeInformationTable5() throws  Exception
    {
        float[] configRow = {150f, 150f,150f};
        listOptClasses = new ArrayList<String>();
        TableRowBox firstPart2 = new TableRowBox(configRow, 0, 0);
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getEmployeeRegNumberBlock(),getEmployeeDateStartBlock(),getJobBlock(),getQualifBlock()})), false);
        listOptClasses.add("RegNumber");
        listOptClasses.add("DateStart");
        listOptClasses.add("Job");
        listOptClasses.add("Qualif");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getEmployeeSucurityNumberBlock() ,emptyBox,getDateSeniorityBlock(),getClassificationBlock()})), false);
        listOptClasses.add("ESSN");
        listOptClasses.add("DateSeniority");
        listOptClasses.add("Classification");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                emptyBox ,emptyBox,getCoeffBlock()})), false);
        listOptClasses.add("Coeff");
        return firstPart2;
    }

    public TableRowBox getEmployeeInformationTable6() throws  Exception
    {
        float[] configRow = {150f, 150f,150f};
        listOptClasses = new ArrayList<String>();
        TableRowBox firstPart2 = new TableRowBox(configRow, 0, 0);
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getAssignementBlock(),getJobBlock(),getCoeffBlock(),getClassificationBlock()})), false);
        listOptClasses.add("Assignement");
        listOptClasses.add("Job");
        listOptClasses.add("Coeff");
        listOptClasses.add("Classification");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getCoefMinBlock() ,getMonthlyPayBlock(),emptyBox,emptyBox})), false);
        listOptClasses.add("CoeffMin");
        listOptClasses.add("MonthlyPay");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getTimeTableBlock() ,getHourlyRateBlock(),getMonthlyPayRefBlock()})), false);
        listOptClasses.add("TimeTable");
        listOptClasses.add("HourlyRate");
        listOptClasses.add("MonthlyPayRef");
        return firstPart2;
    }

    public TableRowBox getEmployeeInformationTable7() throws  Exception
    {
        float[] configRow = {200f, 200f,200f};
        listOptClasses = new ArrayList<String>();
        TableRowBox firstPart2 = new TableRowBox(configRow, 0, 0);
        SimpleTextBox emptyBox= new SimpleTextBox(font, fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);

        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{new HorizontalLineBox(0,0, 550f, 0),
                getAssignementBlock(),getJobBlock(),getCoeffBlock(),getClassificationBlock(),new HorizontalLineBox(0,0, 550f, 0),})), false);
        listOptClasses.add("Assignement");
        listOptClasses.add("Job");
        listOptClasses.add("Classification");
        listOptClasses.add("Assignement");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getCoefMinBlock() ,getMonthlyPayBlock(),emptyBox,emptyBox})), false);
        listOptClasses.add("CoeffMin");
        listOptClasses.add("MonthlyPay");
        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                getTimeTableBlock() ,getHourlyRateBlock(),getMonthlyPayRefBlock()})), false);
        listOptClasses.add("TimeTable");
        listOptClasses.add("Hourlyrate");
        listOptClasses.add("MonthlyPayRef");
        return firstPart2;
    }

    public TableRowBox getEmployeeInformationTable8() throws  Exception
    {
        float[] configRowT = {510f};
        listOptClasses = new ArrayList<String>();
        TableRowBox firstPart2 = new TableRowBox(configRowT, 0, 0);

        float[] configRow = {50f,90f,90f,70f, 60f, 80f, 80f};

        VerticalContainer idContainer = new VerticalContainer(0,0,0);
        HorizontalContainer ligne2 = new HorizontalContainer(0, 0);
        HorizontalContainer ligne1 = new HorizontalContainer(0, 0);


        idContainer.addElement(new HorizontalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]+configRow[4]+configRow[5]+configRow[6]), 0));

        // Ecquis
        SimpleTextBox Label4 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMinCoeffLabel() );
        Label4.setPadding(0, 0, 2, 0);
        Label4.setWidth(configRow[0]);
        ligne1.addElement(Label4);
        listOptClasses.add("MinCoeff");

        SimpleTextBox Value = new SimpleTextBox(font, fontSize, 0, 0,  model.getEmployeeInformation().getSecurityNumberLabel());
        Value.setPadding(0, 0, 3, 0);
        //Value.setEntityName("SSNH");
        Value.setWidth(configRow[1]);
        ligne1.addElement(Value);
        listOptClasses.add("ESSN");

        SimpleTextBox Value1 = new SimpleTextBox(font, fontSize, 0, 0,  model.getEmployeeInformation().getCategoryLabel());
        //Value1.setEntityName("EEMH");
        Value1.setPadding(0, 0, 3, 0);
        Value1.setWidth(configRow[2]);
        ligne1.addElement(Value1);
        listOptClasses.add("Category");

        SimpleTextBox Value2 = new SimpleTextBox(font, fontSize, 0, 0,  model.getEmployeeInformation().getEchelonLabel());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value2.setPadding(0, 0, 3, 0);
        Value2.setWidth(configRow[3]);
        ligne1.addElement(Value2);
        listOptClasses.add("Echelon");

        SimpleTextBox Value3 = new SimpleTextBox(font, fontSize, 0, 0,  model.getEmployeeInformation().getTimeTableLabel());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value3.setPadding(0, 0, 3, 0);
        Value3.setWidth(configRow[4]);
        ligne1.addElement(Value3);
        listOptClasses.add("TimeTable");

        SimpleTextBox Value4 = new SimpleTextBox(font, fontSize, 0, 0,  model.getEmployeeInformation().getHourlyRateLabel());
        // Value.setEntityName("S" + idName.toUpperCase());
        Value4.setPadding(0, 0, 3, 0);
        Value4.setWidth(configRow[5]);
        ligne1.addElement(Value4);
        listOptClasses.add("HourlyRate");

        SimpleTextBox Value5 = new SimpleTextBox(font, fontSize, 0, 0,  "temps partiel");
        // Value.setEntityName("S" + idName.toUpperCase());
        Value5.setPadding(0, 0, 3, 0);
        Value4.setWidth(configRow[6]);
        ligne1.addElement(Value5);
        listOptClasses.add("HalfTime");

        idContainer.addElement(ligne1);
        idContainer.addElement(new HorizontalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]+configRow[4]+configRow[5]+configRow[6]), 0));

        // Encours
        SimpleTextBox Label5 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getMincoef());
        Label5.setPadding(0, 0, 2, 0);
        Label5.setWidth(configRow[0]);
        ligne2.addElement(Label5);

        SimpleTextBox Value6 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getSocialSecurityNumber());
        Value6.setEntityName("SSN");
        Value6.setPadding(0, 0, 3, 0);
        Value6.setWidth(configRow[1]);
        ligne2.addElement(Value6);

        SimpleTextBox Value7 = new SimpleTextBox(font, fontSize, 0, 0,model.getEmployeeInformation().getEmployment());
        Value7.setEntityName("EEM");
        Value7.setPadding(0, 0, 3, 0);
        Value7.setWidth(configRow[2]);
        ligne2.addElement(Value7);

        SimpleTextBox Value8 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getEchelon());
        //Value8.setEntityName("S" + idName.toUpperCase());
        Value8.setPadding(0, 0, 3, 0);
        Value8.setWidth(configRow[3]);
        ligne2.addElement(Value8);

        SimpleTextBox Value9 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getTimetable());
        //Value8.setEntityName("S" + idName.toUpperCase());
        Value9.setPadding(0, 0, 3, 0);
        Value9.setWidth(configRow[4]);
        ligne2.addElement(Value9);

        SimpleTextBox Value10 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployeeInformation().getHourlyRate());
        //Value10.setEntityName("HR");
        Value10.setPadding(0, 0, 3, 0);
        Value10.setWidth(configRow[5]);
        ligne2.addElement(Value10);

        SimpleTextBox Value11 = new SimpleTextBox(font, fontSize, 0, 0, Integer.toString(model.getRandom().nextInt(35)));
        //Value8.setEntityName("S" + idName.toUpperCase());
        Value11.setPadding(0, 0, 3, 0);
        Value11.setWidth(configRow[6]);
        ligne2.addElement(Value11);

        idContainer.addElement(ligne2);
        idContainer.addElement(new HorizontalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]+configRow[4]+configRow[5]+configRow[6]), 0));
        idContainer.addElement(new VerticalLineBox(0,0, 0, idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, configRow[0], idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, (configRow[0]+configRow[1]), idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]), idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]), idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]+configRow[4]), idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]+configRow[4]+configRow[5]), idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()
        idContainer.addElement(new VerticalLineBox(0,0, (configRow[0]+configRow[1]+configRow[2]+configRow[3]+configRow[4]+configRow[5]+configRow[6]), idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()

     //   idContainer.addElement(new VerticalLineBox(0,0,  510f, idContainer.getBBox().getHeight())); //  sumUp.getBBox().getPosY()

        firstPart2.addElement( new EmployeeInfoPayslipBox(concatContainersVertically(new ElementBox[]{
                idContainer})),false);

        return firstPart2;
    }
    public VerticalContainer concatContainersVertically(ElementBox[] parts) throws  Exception
    {   int x = 1;
        VerticalContainer result = new VerticalContainer(0,0,0);
        for (ElementBox part: parts)
        {
            result.addElement(part);
        }
        return result;
    }

    @Override
    public BoundingBox getBBox() {
        if(container!=null)
        return container.getBBox();
        return hcontainer.getBBox();
    }

    @Override
    public void setWidth(float width) throws Exception {
        if(container==null)
            hcontainer.getBBox().setWidth(width);
        else
            container.getBBox().setWidth(width);
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        if(container==null)
            hcontainer.translate(offsetX,offsetY);
        else
        container.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if(container==null)
            hcontainer.build(stream,writer);
        else
        container.build(stream, writer);
    }
}
