package au.gov.nsw.lpi.dao;

import javax.sql.DataSource;

import au.gov.nsw.lpi.controllers.YugController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Map;

@Component
public class ComponentDao {
    private static final Logger logger = LoggerFactory.getLogger(ComponentDao.class);
    private final DataSource dataSource;
    private final String catalogName = "VN_PEGA_INTEGRATION_API";

    public ComponentDao(DataSource dataSource) {
        this.dataSource = dataSource;
        //this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public String update(String payload){
        try (Connection connection = dataSource.getConnection()){
            try (CallableStatement cs = connection.prepareCall("{ ? = call VN_PEGA_INTEGRATION_API.COMPONENT_GET_JSON_DATA(?) }")) {
                Clob clob = connection.createClob();
                clob.setString(1, payload);
                cs.setClob(2, clob);
                cs.registerOutParameter(1, Types.VARCHAR);
                cs.execute();
                return cs.getString(1);
            }
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String retrieve(String payload){
        try{
            JsonObject jo = JsonParser.parseString(payload).getAsJsonObject();
            CallableStatement cs = dataSource.getConnection().prepareCall("{ ? = call VN_PEGA_INTEGRATION_API.COMPONENT_SEND_JSON_DATA(?, ?)}");
            cs.registerOutParameter(1, java.sql.Types.CLOB);
            cs.setInt(2, jo.get("component_id").getAsInt());
            cs.setString(3, jo.get("mode").getAsString());
            cs.execute();
            return cs.getString(1);
        }catch (Exception e){
            return e.getMessage();
        }

    }
}