package au.gov.nsw.lpi.dao;

public interface SuburbDao {
    String street_upsert(String paylod);
    String district_upsert(String paylod);
}