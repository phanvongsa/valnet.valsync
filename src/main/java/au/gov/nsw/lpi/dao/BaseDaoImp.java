package au.gov.nsw.lpi.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public abstract class BaseDaoImp implements BaseDao {
    protected static final Logger logger = LoggerFactory.getLogger(BaseDaoImp.class);
    protected final DataSource dataSource;
    protected final String catalogName = "VN_PEGA_INTEGRATION_API";

    public BaseDaoImp(DataSource dataSource){
        logger.debug("BaseDao()");
        this.dataSource = dataSource;
    }

    public String getExceptionResponse(Exception e){
        return e.getMessage()==null?"Error EXCEPTION":e.getMessage();
    }

}
