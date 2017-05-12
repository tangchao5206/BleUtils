package cn.me.com.blelib.ble.bean;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/10/27 14:26
 * 修改时间：
 * 修改备注：
 */
public class BloodO2Bean {

    /**
     * 血氧
     */
    private int bloodO2Data;

    /**
     * 脉率
     */
    private int pulseData;

    /**
     * 血氧单位
     */
    private String bloodO2UnitText = "%";

    /**
     * 脉率单位
     */
    private String pulseUnitText = "博/分";

    /**
     * 是否测量中数据
     */
    private boolean isTestingData = true;


    public int getBloodO2Data() {
        return bloodO2Data;
    }



    public void setBloodO2Data(int bloodO2Data) {
        this.bloodO2Data = bloodO2Data;
    }



    public int getPulseData() {
        return pulseData;
    }



    public void setPulseData(int pulseData) {
        this.pulseData = pulseData;
    }



    public String getBloodO2UnitText() {
        return bloodO2UnitText;
    }



    public void setBloodO2UnitText(String bloodO2UnitText) {
        this.bloodO2UnitText = bloodO2UnitText;
    }



    public String getPulseUnitText() {
        return pulseUnitText;
    }



    public void setPulseUnitText(String pulseUnitText) {
        this.pulseUnitText = pulseUnitText;
    }



    public boolean isTestingData() {
        return isTestingData;
    }



    public void setTestingData(boolean isTestingData) {
        this.isTestingData = isTestingData;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BloodO2,");
        sb.append("isTestingData : ");
        sb.append(this.isTestingData);

        sb.append(" ,bloodO2Data:");
        sb.append(this.bloodO2Data);
        sb.append(this.bloodO2UnitText);

        sb.append(" ,pulse:");
        sb.append(this.pulseData);
        sb.append(this.pulseUnitText);

        return sb.toString();
    }
}
