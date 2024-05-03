package au.gov.nsw.lpi.dao;

public interface ComponentDao {
    String upsert(String paylod);

    String retrieve(String paylod);
}