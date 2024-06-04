package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Types;

@Component
public class DealingDaoImpl extends BaseDaoImp implements DealingDao {
    public DealingDaoImpl(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public String upsert(String payload) {
        String sql = String.format("{ ? = call %s.PARSE_DEALING_FROM_VALIQ(?) }",this.catalogName);
        logger.debug(sql);
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
