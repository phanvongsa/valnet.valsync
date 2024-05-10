package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ApportionmentDaoImpl extends BaseDaoImp implements ApportionmentDao{

    public ApportionmentDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String upsert(String payload) {
        String sql = String.format("{ ? = call %s.APPORTIONMENT DAO TO BE IMPLEMENETED(?) }",this.catalogName);
        logger.debug(sql);
        /*
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
        */
        return "APPORTIONMENTDAO TO BE IMPLEMENTED IN DB FUNCTION";
    }
}
