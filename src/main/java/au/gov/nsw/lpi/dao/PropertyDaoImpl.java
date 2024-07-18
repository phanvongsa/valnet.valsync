package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Types;

@Component
public class PropertyDaoImpl extends BaseDaoImp implements PropertyDao {

    public PropertyDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String upsert(String payload) {
        String sql = String.format("{ ? = call %s.PROPERTY_GET_JSON_DATA(?) }",this.catalogName);
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
    public String upsert_supplementary_value(String payload) {
        String sql = String.format("{ ? = call %s.SUPPLEMENTRY_VAL_GET_JSON_DATA(?) }",this.catalogName);
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
}
