package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ApportionmentDaoImpl extends BaseDaoImp implements ApportionmentDao{

    private final String upsert_procedurename = "APPORTIONMENT_GET_JSON_DATA";

    public ApportionmentDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String upsert(String payload) {
      return runStandardProcedure(this.upsert_procedurename,payload);
    }
}
