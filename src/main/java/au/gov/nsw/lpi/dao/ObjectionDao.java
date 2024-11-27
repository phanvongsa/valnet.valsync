package au.gov.nsw.lpi.dao;

public interface ObjectionDao{
    String request_for_information_kit(String payload);

    String get_sales_analysis_report(String payload);
}