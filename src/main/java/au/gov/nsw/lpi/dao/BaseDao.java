package au.gov.nsw.lpi.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public abstract class BaseDao implements IBaseDao{
    protected static final Logger logger = LoggerFactory.getLogger(BaseDao.class);
    protected final DataSource dataSource;
    protected final String catalogName = "VN_PEGA_INTEGRATION_API";

    public BaseDao(DataSource dataSource){
        logger.debug("BaseDao()");
        this.dataSource = dataSource;
    }

    public String getExceptionResponse(Exception e){
        return e.getMessage()==null?"Error EXCEPTION":e.getMessage();
    }

}
