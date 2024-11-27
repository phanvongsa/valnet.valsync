package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ObjectionDaoImpl extends BaseDaoImp implements ObjectionDao{

    private final String request_for_information_kit_procedurename = "INFORMATION_KIT_GET_JSON_DATA";

    private final String get_sales_analysis_report_procedurename = "FUNC_PLACEHOLDER";

    public ObjectionDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String request_for_information_kit(String payload) {
        return runStandardProcedure(this.request_for_information_kit_procedurename,payload);
    }

    @Override
    public String get_sales_analysis_report(String payload) {
        return runStandardProcedure(this.get_sales_analysis_report_procedurename,payload);
    }


}
