package au.gov.nsw.lpi.dao;

public interface DealingDao extends BaseDao{
    String upsert(String payload);
}
