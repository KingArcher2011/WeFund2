package cn.edu.pku.kingarcher.wefund.model;

/**
 * Created by xtrao on 2016/3/26.
 */
public class BaseFundBean {

    private String base_fund_id;
    private String base_fund_nm;
    private String price;
    private String fund_company_nm;
    private String base_est_dis_rt;
    private String market;
    private String fund_manager;

    public String getBaseFundId() {
        return base_fund_id;
    }
    public String getBaseFundNm() {
        return base_fund_nm;
    }
    public String getPrice() {
        return price;
    }
    public String getFund_company_nm() {
        return fund_company_nm;
    }
    public String getBaseEstDisRt() {
        return base_est_dis_rt;
    }
    public String getMarket() {
        return market;
    }
    public String getFund_manager() {
        return fund_manager;
    }

}
