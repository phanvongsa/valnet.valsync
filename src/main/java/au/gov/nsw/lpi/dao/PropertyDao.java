package au.gov.nsw.lpi.dao;

public interface PropertyDao extends BaseDao{
    String upsert(String payload);

    String upsert_supplementary_value(String payload);
}