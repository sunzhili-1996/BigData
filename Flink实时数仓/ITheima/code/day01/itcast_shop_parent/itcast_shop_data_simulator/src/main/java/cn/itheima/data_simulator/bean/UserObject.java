package cn.itheima.data_simulator.bean;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;

public class UserObject {
    // 用户id
    private Integer userId;
    // 登录名称
    private String loginName;
    // 登录密钥
    private Integer loginSecret = RandomUtils.nextInt(1000, 10000);
    // 登录密码
    private String loginPwd = "81dc9bdb52d04dc20036dbd8313ed055";
    // 用户类型
    private Integer userType = 1;
    // 用户性别
    private Integer userSex = RandomUtils.nextInt(0,2);
    // 用户名
    private String userName;
    // 真实姓名
    private String trueName;
    // 生日
    private Date brithday;
    // 用户照片
    private String userPhoto;
    // 用户QQ号
    private String userQQ = RandomStringUtils.randomNumeric(7,14);
    // 用户电话
    private String userPhone = RandomStringUtils.randomNumeric(11);
    // 用户邮箱
    private String userEmail;
    // 用户当前积分
    private Integer userScore = Integer.parseInt(RandomStringUtils.randomNumeric(0, 5));
    // 用户总积分
    private Integer userTotalScore = Integer.parseInt(RandomStringUtils.randomNumeric(0, 5));
    // 用户登录ip
    private String lastIP;
    // 最后登录时间
    private Date lastTime;
    // 用户来源
    private Integer userFrom;
    // 用户余额
    private Double userMoney;
    private Double lockMoney;
    // 用户状态
    private Integer userStatus;
    // 数据状态
    private Integer dataFlag;
    // 创建时间
    private Date createTime;
    // 支付密码
    private String payPwd;
    // 充值金额
    private Double rechargeMoney;
    // 是否通知
    private Integer isInform;

    private Map<Integer, List<Integer>> adds;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getLoginSecret() {
        return loginSecret;
    }

    public void setLoginSecret(Integer loginSecret) {
        this.loginSecret = loginSecret;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public Date getBrithday() {
        return brithday;
    }

    public void setBrithday(Date brithday) {
        this.brithday = brithday;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserQQ() {
        return userQQ;
    }

    public void setUserQQ(String userQQ) {
        this.userQQ = userQQ;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getUserScore() {
        return userScore;
    }

    public void setUserScore(Integer userScore) {
        this.userScore = userScore;
    }

    public Integer getUserTotalScore() {
        return userTotalScore;
    }

    public void setUserTotalScore(Integer userTotalScore) {
        this.userTotalScore = userTotalScore;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(Integer userFrom) {
        this.userFrom = userFrom;
    }

    public Double getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(Double userMoney) {
        this.userMoney = userMoney;
    }

    public Double getLockMoney() {
        return lockMoney;
    }

    public void setLockMoney(Double lockMoney) {
        this.lockMoney = lockMoney;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getDataFlag() {
        return dataFlag;
    }

    public void setDataFlag(Integer dataFlag) {
        this.dataFlag = dataFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    public Double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(Double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public Integer getIsInform() {
        return isInform;
    }

    public void setIsInform(Integer isInform) {
        this.isInform = isInform;
    }

    public void setAdds(Integer userId){
        if(adds == null){
            adds = new HashMap<Integer, List<Integer>>();
        }

        this.adds.put(userId,new ArrayList<Integer>());
    }

    public List<Integer> getAddsByUserId(Integer userId){
        return this.adds.get(userId);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
