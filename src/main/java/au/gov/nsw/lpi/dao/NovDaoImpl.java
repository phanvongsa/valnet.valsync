package au.gov.nsw.lpi.dao;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class NovDaoImpl extends BaseDaoImp implements NovDao{
    private final String get_preferences_procedurename = "NOV_PREF_SEND_JSON_DATA";

    private final String update_preferences_procedurename = "FUNC_PLACEHOLDER";

    public NovDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String get_preferences(String payload) {
        return runStandardProcedure(this.get_preferences_procedurename,payload);
    }

    @Override
    public String update_preferences(String payload) {
        return runStandardProcedure(this.update_preferences_procedurename,payload);
    }
}
