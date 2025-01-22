package au.gov.nsw.lpi.dao;

public interface PropertyDao extends BaseDao{
    String upsert(String payload);

    String get_sales_analysis(String payload);

    String upsert_supplementary_value(String payload);

    String cancel_supplementary_value(String payload);

    String remove_supplementary_value(String payload);
}