package au.gov.nsw.lpi.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

//    private DataSource dataSource;
//    public DatabaseManager(DbConfigDao db){
//        this.dataSource = db.getDataSource();
//    }
//
//    public String testConnection(){
//        String connection_response;// = "Connection Error";
//        try {
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            jdbcTemplate.execute("SELECT 1 FROM DUAL");
//            connection_response = "OK";
//        } catch (Exception e) {
//            connection_response = "FAIL - "+e.getMessage();
//        }
//
//        return connection_response;
//
//    }
}
