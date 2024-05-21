package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.mifmif.common.regex.Generex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class PayslipModel extends Model {
    private PayslipDate date;
    private Company employer;
    private Employee employee;
    private SalaryCotisationTable salaryTable;
    private EmployeeInformation employeeInformation;
    private LeaveInformation leaveInformation;
    private SumUpSalary sumUpSalary;
    private String headTitle;


    public PayslipModel() {}

    public String getHeadTitle() {
        return headTitle;
    }

    public void setHeadTitle(String headTitle) {
        this.headTitle = headTitle;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public PayslipDate getDate() {
        return date;
    }

    public void setDate(PayslipDate date) {
        this.date = date;
    }

    public void setEmployer(Company employer) {
        this.employer = employer;
    }

    public void setSalaryTable(SalaryCotisationTable salaryTable) {
        this.salaryTable = salaryTable;
    }

    public void setEmployeeInformation(EmployeeInformation employeeInformation) {
        this.employeeInformation = employeeInformation;
    }

    public Company getEmployer() {
        return employer;
    }

    public SalaryCotisationTable getSalaryTable() {
        return salaryTable;
    }

    public EmployeeInformation getEmployeeInformation() {
        return employeeInformation;
    }

    public LeaveInformation getLeaveInformation() {
        return leaveInformation;
    }

    public void setLeaveInformation(LeaveInformation leaveInformation) {
        this.leaveInformation = leaveInformation;
    }

    public SumUpSalary getSumUpSalary() {
        return sumUpSalary;
    }

    public void setSumUpSalary(SumUpSalary sumUpSalary) {
        this.sumUpSalary = sumUpSalary;
    }

    @Override
    public String toString() {
        return "PayslipModel{" +
                "date=" + getDate() +
                ", employer=" + getEmployer() +
                ", employee=" + getEmployee() +
                ", salaryTable=" + getSalaryTable() +
                ", employeeInformation=" + getEmployeeInformation() +
                ", leaveInformation=" + getLeaveInformation() +
                ", headTitle='" + headTitle + '\'' +
                '}';
    }


    public static class Generator implements ModelGenerator<PayslipModel> {
        private static final Map<String, String> headerLabels = new HashMap<>();

        {
            headerLabels.put("Payslip", "en");

            headerLabels.put("Bulletin de paie", "fr");
            headerLabels.put("Fiche de paie", "fr");
            headerLabels.put("Bulletin de salaire", "fr");
        }

        @Override
        public PayslipModel generate(GenerationContext ctx) {
            PayslipModel model = new PayslipModel();
            model.setDate(new PayslipDate.Generator().generate(ctx));
            model.setLang(ctx.getLanguagePayslip());
            model.setPaymentInfo(new PaymentInfo.Generator().generate(ctx));
            model.setCompany(new Company.Generator().generate(ctx));
            model.setEmployee(new Employee.Generator().generate(ctx));
            model.setEmployeeInformation(new EmployeeInformation.Generator().generate(ctx));
            model.setSalaryTable(new SalaryCotisationTable.Generator().generate(ctx,model.getEmployeeInformation().getMonthlyPay()));
            model.setLeaveInformation(new LeaveInformation.Generator().generate(ctx));
            model.setSumUpSalary(new SumUpSalary.Generator().generate(ctx));
            List<String> localizedHeaderLabel = headerLabels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguagePayslip())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idxvL = new Random().nextInt(localizedHeaderLabel.size());
            Generex generex = new Generex(localizedHeaderLabel.get(idxvL));
            model.setHeadTitle(generex.random());
            return model;
        }
    }
}
