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

    public ComponentDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String upsert(String payload){
        String sql = String.format("{ ? = call %s.COMPONENT_GET_JSON_DATA(?) }",this.catalogName);
//        logger.debug(sql);
        try (Connection connection = dataSource.getConnection()){
            try (CallableStatement cs = connection.prepareCall(sql)) {
                Clob clob = connection.createClob();
                clob.setString(1, payload);
                cs.setClob(2, clob);
                cs.registerOutParameter(1, Types.VARCHAR);
                cs.execute();
                return cs.getString(1);
            }
        }catch (Exception e){
            return getExceptionResponse(e);
        }
    }

    @Override
    public String retrieve(String payload){
        String sql = String.format("{ ? = call %s.COMPONENT_SEND_JSON_DATA(?, ?)}", this.catalogName);
//        logger.debug(sql);
        try (Connection connection = dataSource.getConnection()){
            try (CallableStatement cs = connection.prepareCall(sql)) {
                JsonObject jo = JsonParser.parseString(payload).getAsJsonObject();
                cs.registerOutParameter(1, Types.CLOB);
                cs.setInt(2, jo.get("component_id").getAsInt());
                cs.setString(3, jo.get("mode").getAsString());
                cs.execute();
                return cs.getString(1);
            }
        }catch (Exception e){
            return getExceptionResponse(e);
        }
    }

}
