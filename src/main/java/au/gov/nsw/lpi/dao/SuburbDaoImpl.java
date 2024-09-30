package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SuburbDaoImpl extends BaseDaoImp implements SuburbDao {
  
  private final String street_upsert_procedurename = "SUBURB_STREET_GET_JSON_DATA";
  private final String district_upsert_procedurename = "SUBURB_DISTRICT_GET_JSON_DATA";

  public SuburbDaoImpl(DataSource dataSource) {
    super(dataSource);
  }
  @Override
  public String street_upsert(String payload) {
    return runStandardProcedure(this.street_upsert_procedurename,payload);      
  }

  @Override
  public String district_upsert(String payload) {
    return runStandardProcedure(this.district_upsert_procedurename,payload);            
  }
}
