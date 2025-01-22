package au.gov.nsw.lpi.dao;

public interface NovDao extends BaseDao{
    String get_preferences(String payload);

    String update_preferences(String payload);
}
