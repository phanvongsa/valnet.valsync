package au.gov.nsw.lpi.dao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Types;

@Component
public class ComponentDaoImpl extends BaseDaoImp implements ComponentDao {

    private final String upsert_procedurename = "COMPONENT_SEND_JSON_DATA";

    public ComponentDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String upsert(String payload){
      return runStandardProcedure(this.upsert_procedurename,payload);
    }

    @Override
    public String retrieve(String payload){
        return runStandardProcedure(this.upsert_procedurename,payload);
    }

}
