
-- 按比例修改订单状态
update itcast_orders set orderStatus=(rand()*90 + 10) where orderId>1 and orderStatus>11;
update itcast_orders set orderStatus=1 where orderStatus>=11 and orderStatus<15;
update itcast_orders set orderStatus=2 where orderStatus>=15 and orderStatus<17;
update itcast_orders set orderStatus=3 where orderStatus>=17 and orderStatus<23;
update itcast_orders set orderStatus=4 where orderStatus>=23 and orderStatus<24;
update itcast_orders set orderStatus=5 where orderStatus>=24 and orderStatus<26;
update itcast_orders set orderStatus=6 where orderStatus>=26 and orderStatus<31;
update itcast_orders set orderStatus=7 where orderStatus>=31 and orderStatus<36;
update itcast_orders set orderStatus=8 where orderStatus>=36 and orderStatus<51;
update itcast_orders set orderStatus=9 where orderStatus>=51 and orderStatus<96;
update itcast_orders set orderStatus=10 where orderStatus>=96 and orderStatus<98;
update itcast_orders set orderStatus=11 where orderStatus>=98;

-- 生成退款表数据
insert into itcast_order_refunds(orderId,goodsId,createTime)
select og.orderId,goodsId,'2019-09-06' from itcast_order_goods og
right outer join itcast_orders orders on  og.orderId=orders.orderId
where orders.orderStatus in (10,11) ;

-- 修改退款单创建时间
update itcast_order_refunds r,itcast_orders o
set  r.createTime=FROM_UNIXTIME((UNIX_TIMESTAMP(o.createTime) + rand()*604800),'%Y-%m-%d %H:%i:%s')
where r.orderId=o.orderId;

-- 修改退款时间
update itcast_order_refunds set refundTime=FROM_UNIXTIME((UNIX_TIMESTAMP(createTime) + rand()*604800),'%Y-%m-%d %H:%i:%s') where id>4 ;

-- 修改推荐金额
update itcast_order_refunds r,itcast_order_goods g
set r.backMoney=g.payPrice
where r.orderId=g.orderId and r.goodsId = g.goodsId
;
