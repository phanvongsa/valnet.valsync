package au.gov.nsw.lpi.dao;

public interface ApportionmentDao extends BaseDao {
    String upsert(String payload);
}
