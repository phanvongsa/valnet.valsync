package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DealingDaoImpl extends BaseDaoImp implements DealingDao {
    
  private String upsert_procedurename = "DEALING_GET_JSON_DATA";
  public DealingDaoImpl(DataSource dataSource) {
        super(dataSource);
  }

  @Override
  public String upsert(String payload) {
    return runStandardProcedure(this.upsert_procedurename,payload);      
  }
}
