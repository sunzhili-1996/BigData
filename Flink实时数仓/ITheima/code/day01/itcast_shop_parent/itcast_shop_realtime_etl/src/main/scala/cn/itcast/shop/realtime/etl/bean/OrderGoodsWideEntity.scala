package cn.itcast.shop.realtime.etl.bean

import scala.beans.BeanProperty

// 订单明细拉宽数据
case class OrderGoodsWideEntity(@BeanProperty ogId:Long,                //订单明细id
                                @BeanProperty orderId:Long,             //订单id
                                @BeanProperty goodsId:Long,             //商品id
                                @BeanProperty goodsNum:Long,            //商品数量
                                @BeanProperty goodsPrice:Double,        //商品价格
                                @BeanProperty goodsName:String,         //商品名称
                                @BeanProperty shopId:Long,              //拓宽后的字段-》商品表：店铺id
                                @BeanProperty goodsThirdCatId:Int,      //拓宽后的字段->商品表：商品三级分类id
                                @BeanProperty goodsThirdCatName:String, //拓宽后的字段->商品分类表：商品三级分类名称
                                @BeanProperty goodsSecondCatId:Int,     //拓宽后的字段-》商品分类表：商品二级分类id
                                @BeanProperty goodsSecondCatName:String,  //拓宽后的字段-》商品分类表：商品二级分类名称
                                @BeanProperty goodsFirstCatId:Int,        //拓宽后的字段-》商品分类表：商品一级分类id
                                @BeanProperty goodsFirstCatName:String,   //拓宽后的字段-》商品分类表：商品一级分类名称
                                @BeanProperty areaId:Int,                 //拓宽后的字段-》店铺表：区域id
                                @BeanProperty shopName:String,            //拓宽后的字段-》店铺表：店铺名称
                                @BeanProperty shopCompany:String,         //拓宽后的字段-》店铺表：店铺所属公司
                                @BeanProperty cityId:Int,                 //拓宽后的字段-》组织机构表：城市id
                                @BeanProperty cityName:String,            //拓宽后的字段-》组织机构表：城市名称
                                @BeanProperty regionId:Int,               //拓宽后的字段-》组织机构表：大区id
                                @BeanProperty regionName:String)          //拓宽后的字段-》组织机构表：大区名称
