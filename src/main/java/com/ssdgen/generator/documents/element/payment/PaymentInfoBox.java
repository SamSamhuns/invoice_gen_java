package com.ssdgen.generator.documents.element.payment;

import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.data.model.InvoiceAnnotModel;
import com.ssdgen.generator.documents.data.model.PaymentInfo;
import com.ssdgen.generator.documents.data.model.Company;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.table.TableRowBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.element.container.HorizontalContainer;
import com.ssdgen.generator.documents.element.container.VerticalContainer;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Color;
import java.util.Random;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class PaymentInfoBox extends ElementBox {

    private final PDFont fontN;
    private final PDFont fontB;
    private final PDFont fontI;
    private final float fontSizeSmall;
    private final float fontSizeBig;
    private float width;
    private final Color lineStrokeColor;

    private final InvoiceModel model;
    private final InvoiceAnnotModel annot;
    private final Map<String, Boolean> proba;

    private VerticalContainer vContainer;
    private final Random rnd = new Random();

    public PaymentInfoBox(PDFont fontN, PDFont fontB, PDFont fontI,
            float fontSizeSmall, float fontSizeBig,
            float width, Color lineStrokeColor,
            InvoiceModel model, InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        this.fontN = fontN;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSizeSmall = fontSizeSmall;
        this.fontSizeBig = fontSizeBig;
        this.width = width;
        this.lineStrokeColor = lineStrokeColor;

        this.model = model;
        this.annot = annot;
        this.proba = proba;
        this.init();
    }

    private void init() throws Exception {
        PDFont fontNB = rnd.nextBoolean() ? fontN : fontB;
        Company company = model.getCompany();
        PaymentInfo payment = model.getPaymentInfo();
        annot.setPaymentto(new InvoiceAnnotModel.Paymentto());

        SimpleTextBox paymentHeader = new SimpleTextBox(fontB, fontSizeBig, 0, 0, payment.getAddressHeader(), "PH");
        SimpleTextBox bankNameLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0, payment.getLabelBankName() + ": ",
                "PBN");
        SimpleTextBox bankNameValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0, payment.getValueBankName(), "PBN");
        SimpleTextBox accountNameLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                payment.getLabelAccountName() + ": ", "PAName");
        SimpleTextBox accountNameValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0, payment.getValueAccountName(),
                "PAName");
        SimpleTextBox accountNumberLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                payment.getLabelAccountNumber() + ": ", "PANum");
        SimpleTextBox accountNumberValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0,
                payment.getValueAccountNumber(), "PANum");
        SimpleTextBox branchNameLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                payment.getLabelBranchName() + ": ", "PBName");
        SimpleTextBox branchNameValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0, payment.getValueBranchName(),
                "PBName");
        SimpleTextBox ibanNumberLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                payment.getLabelIBANNumber() + ": ", "PBNum");
        SimpleTextBox ibanNumberValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0, payment.getValueIBANNumber(),
                "PBNum");
        SimpleTextBox routingNumberLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                payment.getLabelRoutingNumber() + ": ", "PBNum");
        SimpleTextBox routingNumberValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0,
                payment.getValueRoutingNumber(), "PBNum");
        SimpleTextBox swiftCodeLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                payment.getLabelSwiftCode() + ": ", "PSNum");
        SimpleTextBox swiftCodeValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0, payment.getValueSwiftCode(),
                "PSNum");
        SimpleTextBox vatNumberLabel = new SimpleTextBox(fontNB, fontSizeSmall, 0, 0,
                company.getIdNumbers().getVatLabel() + ": ", "SVAT");
        SimpleTextBox vatNumberValue = new SimpleTextBox(fontN, fontSizeSmall, 0, 0,
                company.getIdNumbers().getVatValue(), "SVAT");

        if (proba.get("payment_address_tabular")) {
            List<Integer> labels = Arrays.asList(
                    payment.getLabelBankName().length(), payment.getLabelAccountName().length(),
                    payment.getLabelAccountNumber().length(), payment.getLabelBranchName().length(),
                    payment.getLabelIBANNumber().length(), payment.getLabelRoutingNumber().length(),
                    payment.getLabelSwiftCode().length(), company.getIdNumbers().getVatLabel().length());
            List<Integer> values = Arrays.asList(
                    payment.getValueBankName().length(), payment.getValueAccountName().length(),
                    payment.getValueAccountNumber().length(), payment.getValueBranchName().length(),
                    payment.getValueIBANNumber().length(), payment.getValueRoutingNumber().length(),
                    payment.getValueSwiftCode().length(), company.getIdNumbers().getVatValue().length());
            Collections.sort(labels);
            Collections.sort(values);
            // get max width of labels and values columns
            float maxLabelWidth = 12
                    + fontNB.getStringWidth("x".repeat(labels.get(labels.size() - 1) + 2)) / 1000 * fontSizeSmall;
            float maxValueWidth = 20
                    + fontN.getStringWidth("x".repeat(values.get(values.size() - 1))) / 1000 * fontSizeSmall;

            if ((maxLabelWidth + maxValueWidth) > width) {
                width += Math.min(30, maxLabelWidth + maxValueWidth - width + 2); // dont exceed an addition of 30 units
            }
            float[] configRow = { maxLabelWidth, maxValueWidth };

            vContainer = new VerticalContainer(0, 0, width);
            vContainer.addElement(paymentHeader);

            TableRowBox bankNameCont = new TableRowBox(configRow, 0, 0);
            bankNameCont.addElement(bankNameLabel);
            bankNameCont.addElement(bankNameValue);
            vContainer.addElement(bankNameCont);
            annot.getPaymentto().setBankName(payment.getValueBankName());

            TableRowBox accountNameCont = new TableRowBox(configRow, 0, 0);
            accountNameCont.addElement(accountNameLabel);
            accountNameCont.addElement(accountNameValue);
            vContainer.addElement(accountNameCont);
            annot.getPaymentto().setAccountName(payment.getValueAccountName());

            if (proba.get("payment_account_number")) {
                TableRowBox accountNumberCont = new TableRowBox(configRow, 0, 0);
                accountNumberCont.addElement(accountNumberLabel);
                accountNumberCont.addElement(accountNumberValue);
                vContainer.addElement(accountNumberCont);
                annot.getPaymentto().setAccountNumber(payment.getValueAccountNumber());
            }
            if (proba.get("payment_branch_name")) {
                TableRowBox branchNameCont = new TableRowBox(configRow, 0, 0);
                branchNameCont.addElement(branchNameLabel);
                branchNameCont.addElement(branchNameValue);
                vContainer.addElement(branchNameCont);
                annot.getPaymentto().setBranchAddress(payment.getValueBranchName());
            }

            TableRowBox ibanNumberCont = new TableRowBox(configRow, 0, 0);
            ibanNumberCont.addElement(ibanNumberLabel);
            ibanNumberCont.addElement(ibanNumberValue);
            vContainer.addElement(ibanNumberCont);
            annot.getPaymentto().setIbanNumber(payment.getValueIBANNumber());

            if (proba.get("payment_routing_number")) {
                TableRowBox routingNumberCont = new TableRowBox(configRow, 0, 0);
                routingNumberCont.addElement(routingNumberLabel);
                routingNumberCont.addElement(routingNumberValue);
                vContainer.addElement(routingNumberCont);
                annot.getPaymentto().setRoutingNumber(payment.getValueRoutingNumber());
            }
            if (proba.get("payment_swift_number")) {
                TableRowBox swiftCodeCont = new TableRowBox(configRow, 0, 0);
                swiftCodeCont.addElement(swiftCodeLabel);
                swiftCodeCont.addElement(swiftCodeValue);
                vContainer.addElement(swiftCodeCont);
                annot.getPaymentto().setSwiftCode(payment.getValueSwiftCode());
            }
            if (proba.get("payment_vendor_tax_number") && !proba.get("vendor_tax_number_top")) {
                TableRowBox vatNumberCont = new TableRowBox(configRow, 0, 0);
                vatNumberCont.addElement(vatNumberLabel);
                vatNumberCont.addElement(vatNumberValue);
                vContainer.addElement(vatNumberCont);
                annot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
            }
            if (proba.get("addresses_bordered")) {
                vContainer.setBorderColor(lineStrokeColor);
                vContainer.setBorderThickness(0.5f);
            }
        } else {
            vContainer = new VerticalContainer(0, 0, width);
            vContainer.addElement(paymentHeader);

            HorizontalContainer bankNameCont = new HorizontalContainer(0, 0);
            bankNameCont.addElement(bankNameLabel);
            bankNameCont.addElement(bankNameValue);
            vContainer.addElement(bankNameCont);
            annot.getPaymentto().setBankName(payment.getValueBankName());

            HorizontalContainer accountNameCont = new HorizontalContainer(0, 0);
            accountNameCont.addElement(accountNameLabel);
            accountNameCont.addElement(accountNameValue);
            vContainer.addElement(accountNameCont);
            annot.getPaymentto().setAccountName(payment.getValueAccountName());

            if (proba.get("payment_account_number")) {
                HorizontalContainer accountNumberCont = new HorizontalContainer(0, 0);
                accountNumberCont.addElement(accountNumberLabel);
                accountNumberCont.addElement(accountNumberValue);
                vContainer.addElement(accountNumberCont);
                annot.getPaymentto().setAccountNumber(payment.getValueAccountNumber());
            }
            if (proba.get("payment_branch_name")) {
                HorizontalContainer branchNameCont = new HorizontalContainer(0, 0);
                branchNameCont.addElement(branchNameLabel);
                branchNameCont.addElement(branchNameValue);
                vContainer.addElement(branchNameCont);
                annot.getPaymentto().setBranchAddress(payment.getValueBranchName());
            }

            HorizontalContainer ibanNumberCont = new HorizontalContainer(0, 0);
            ibanNumberCont.addElement(ibanNumberLabel);
            ibanNumberCont.addElement(ibanNumberValue);
            vContainer.addElement(ibanNumberCont);
            annot.getPaymentto().setIbanNumber(payment.getValueIBANNumber());

            if (proba.get("payment_routing_number")) {
                HorizontalContainer routingNumberCont = new HorizontalContainer(0, 0);
                routingNumberCont.addElement(routingNumberLabel);
                routingNumberCont.addElement(routingNumberValue);
                vContainer.addElement(routingNumberCont);
                annot.getPaymentto().setRoutingNumber(payment.getValueRoutingNumber());
            }
            if (proba.get("payment_swift_number")) {
                HorizontalContainer swiftCodeCont = new HorizontalContainer(0, 0);
                swiftCodeCont.addElement(swiftCodeLabel);
                swiftCodeCont.addElement(swiftCodeValue);
                vContainer.addElement(swiftCodeCont);
                annot.getPaymentto().setSwiftCode(payment.getValueSwiftCode());
            }
            if (proba.get("payment_vendor_tax_number") && !proba.get("vendor_tax_number_top")) {
                HorizontalContainer vatNumberCont = new HorizontalContainer(0, 0);
                vatNumberCont.addElement(vatNumberLabel);
                vatNumberCont.addElement(vatNumberValue);
                vContainer.addElement(vatNumberCont);
                annot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
            }
            if (proba.get("addresses_bordered")) {
                vContainer.setBorderColor(lineStrokeColor);
                vContainer.setBorderThickness(0.5f);
            }
        }
    }

    @Override
    public BoundingBox getBBox() {
        return vContainer.getBBox();
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
        vContainer.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        vContainer.build(stream, writer);
    }
}
