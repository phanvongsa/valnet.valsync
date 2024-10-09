package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ObjectionDaoImpl extends BaseDaoImp implements ObjectionDao{

    private final String request_for_information_kit_procedurename = "FUNC_PLACEHOLDER";

    public ObjectionDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String request_for_information_kit(String payload) {
        return runStandardProcedure(this.request_for_information_kit_procedurename,payload);
    }
}
