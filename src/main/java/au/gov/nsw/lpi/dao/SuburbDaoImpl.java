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
public class SuburbDaoImpl extends BaseDaoImp implements SuburbDao {
    public SuburbDaoImpl(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public String STREET_UPSERT(String payload) {
        String sql = String.format("{ ? = call %s.PARSE_STREET_SUBURB_FROM_VALIQ(?) }",this.catalogName);
        try (Connection connection = dataSource.getConnection()){
            try (CallableStatement cs = connection.prepareCall(sql)) {
                Clob clob = connection.createClob();
                clob.setString(1, payload);
                cs.setClob(2, clob);
                cs.registerOutParameter(1, Types.CLOB);
                cs.execute();
                return cs.getString(1);
            }
        }catch (Exception e){
            return getExceptionResponse(e);
        }
    }

    @Override
    public String DISTRICT_UPSERT(String payload) {
        String sql = String.format("{ ? = call %s.PARSE_SUB_DIST_VALIQ(?) }",this.catalogName);
        try (Connection connection = dataSource.getConnection()){
            try (CallableStatement cs = connection.prepareCall(sql)) {
                Clob clob = connection.createClob();
                clob.setString(1, payload);
                cs.setClob(2, clob);
                cs.registerOutParameter(1, Types.CLOB);
                cs.execute();
                return cs.getString(1);
            }
        }catch (Exception e){
            return getExceptionResponse(e);
        }
    }
}
