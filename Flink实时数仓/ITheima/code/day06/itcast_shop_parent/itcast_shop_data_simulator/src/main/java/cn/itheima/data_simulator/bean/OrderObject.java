package cn.itheima.data_simulator.bean;

public class OrderObject {

    private Long orderId;

    private Double totalMoney;

    public OrderObject(Long orderId,Double totalMoney){
        this.orderId = orderId;
        this.totalMoney = totalMoney;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }
}
