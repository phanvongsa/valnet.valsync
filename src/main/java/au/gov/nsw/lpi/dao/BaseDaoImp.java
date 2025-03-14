package au.gov.nsw.lpi.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Types;

import javax.sql.DataSource;

public abstract class BaseDaoImp implements BaseDao {
    protected static final Logger logger = LoggerFactory.getLogger(BaseDaoImp.class);
    protected final DataSource dataSource;
    protected final String catalogName = "VN_PEGA_INTEGRATION_API";

    public BaseDaoImp(DataSource dataSource){
        logger.debug("BaseDao() <- "+ this.getClass().getName()+"()");
        this.dataSource = dataSource;
    }

    public String getExceptionResponse(Exception e){
        return e.getMessage()==null?"Error EXCEPTION":e.getMessage();
    }

    protected String runStandardProcedure(String procedure_name, String payload){
        logger.debug("Calling procedure: "+procedure_name);
        String sql = String.format("{ ? = call %s.%s(?) }",this.catalogName, procedure_name);
        try (Connection connection = dataSource.getConnection()){
            try (CallableStatement cs = connection.prepareCall(sql)) {
                Clob clob = connection.createClob();
                clob.setString(1, payload);
                cs.setClob(2, clob);
                cs.registerOutParameter(1, Types.CLOB);
                cs.execute();
                Clob clobResponse = cs.getClob(1);
                return (clobResponse != null) ? clobResponse.getSubString(1, (int) clobResponse.length()) : "";
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return getExceptionResponse(e);
        }
    }
}
