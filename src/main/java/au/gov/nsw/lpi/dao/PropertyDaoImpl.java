package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class PropertyDaoImpl extends BaseDaoImp implements PropertyDao {

    private String upsert_procedurename = "PROPERTY_GET_JSON_DATA";
    private String upsert_supplementary_value_procedurename = "SUPPLEMENTRY_VAL_GET_JSON_DATA";
    private String cancel_supplementary_value_procedurename = "CANCEL_SUPP_VAL_GET_JSON_DATA";
    private String remove_supplementary_value_procedurename = "REMOVE_PROPERTY_GET_JSON_DATA";
    private String get_sales_analysis_procedurename = "FUNC_PLACEHOLDER";

    public PropertyDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String upsert(String payload) {
        return runStandardProcedure(this.upsert_procedurename,payload);
    }

    @Override
    public String get_sales_analysis(String payload) {
        return runStandardProcedure(this.get_sales_analysis_procedurename,payload);
    }

    @Override
    public String upsert_supplementary_value(String payload) {
        return runStandardProcedure(this.upsert_supplementary_value_procedurename,payload);
    }

    @Override
    public String cancel_supplementary_value(String payload) {
      return runStandardProcedure(this.cancel_supplementary_value_procedurename,payload);
    }

    @Override
    public String remove_supplementary_value(String payload) {
      return runStandardProcedure(this.remove_supplementary_value_procedurename,payload);      
    }
    
}
