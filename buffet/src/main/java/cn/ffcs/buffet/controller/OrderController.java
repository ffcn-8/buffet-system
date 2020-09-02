package cn.ffcs.buffet.controller;

import cn.ffcs.buffet.common.annotation.AvoidRepeatableCommit;
import cn.ffcs.buffet.common.annotation.PassToken;
import cn.ffcs.buffet.common.dto.Page;
import cn.ffcs.buffet.common.dto.Result;
import cn.ffcs.buffet.common.util.TokenUtil;
import cn.ffcs.buffet.model.ao.AddOrderAO;
import cn.ffcs.buffet.model.ao.EditOrderStatusAO;
import cn.ffcs.buffet.model.ao.PayOrderAO;
import cn.ffcs.buffet.model.po.OrderPO;
import cn.ffcs.buffet.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: order
 * @Author: huang.zhao
 * @Date: 2020/7/30 16:18
 */
@Api(value = "/api/order", tags = "order服务")
@RestController
@RequestMapping(value = "/api/order")
@Validated
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 获取某个用户的所有订单信息,分页形式
     * @param userId 用户id
     * @param orderStatus 订单状态
     * @param page 分页数据
     * @param orderId 订单流水号
     * @return
     */
    @ApiOperation(value = "获取订单列表，可选择的条件有用户id、订单流水号、订单状态。查询为分页查询。(后台使用)")
    @GetMapping(path = "/listOrder")
    public Result listOrder(Integer userId, String orderStatus, Page<OrderPO> page, String orderId) {
        return orderService.listOrder(userId, orderStatus, page, orderId);
    }

    /**
     * 获取某个用户的所有订单信息,分页形式
     * @param page 分页数据
     * @return
     */
    @ApiOperation(value = "获取订单列表，可选择的条件有用订单流水号。查询为分页查询。(前台使用)")
    @GetMapping(path = "/listOrderByCurrentUser")
    public Result listOrderByCurrentId(Page<OrderPO> page) {
        Integer userId = TokenUtil.getUserIdAndUserTelOfToken().getUserId();
        return orderService.listOrderByCurrentId(userId, page);
    }

    /**
     * 获取指定订单信息
     * @param id 订单id
     * @return
     */
    @ApiOperation(value = "获取指定订单信息， 参数： 订单orderPO的id。(前台使用)")
    @GetMapping(path = "/getOrderById")
    public Result getOrderById(Long id) {
        Integer userId = TokenUtil.getUserIdAndUserTelOfToken().getUserId();
        return orderService.getOrderById(id);
    }

    /**
     * 删除指定订单id的订单，批量形式
     * @param idList idList
     * @return
     */
    @AvoidRepeatableCommit
    @ApiOperation(value = "删除指定订单id的订单，批量形式(后台使用)")
    @PostMapping(path = "/deleteOrderByIdList")
    @ResponseBody
    public Result deleteOrderByIdList(@RequestParam(required = false, value = "idList[]") Integer[] idList) {
        if(idList == null || idList.length == 0) {
            return Result.fail("空数组");
        }
        List<Integer> list = java.util.Arrays.asList(idList);
        return orderService.deleteOrderByOrderId(list);
    }

    /**
     * 修改订单状态，批量形式
     * @param idList idList
     * @param orderStatus 订单状态
     * @return
     */
    @AvoidRepeatableCommit
    @ApiOperation(value = "修改订单状态，后台web端使用，批量形式(用于后台的管理员批量派送、批量取消订单等功能)")
    @PostMapping(path = "/updateOrderByIdList")
    @ResponseBody
    public Result updateOrderByIdList(@RequestParam(required = false, value = "idList[]") Integer[] idList, String orderStatus) {
        if(idList == null || idList.length == 0) {
            return Result.fail("空数组");
        }
        List<Integer> list = java.util.Arrays.asList(idList);
        return orderService.updateOrderByIdList(list, orderStatus);
    }

    /**
     * 查询订单总数、总交易额
     * @return
     */
    @ApiOperation(value = "查询订单总数、总交易额")
    @GetMapping(value = "/getTotalNumberAndMoney")
    @ResponseBody
    public Result getTotalNumberAndMoney() {
        return orderService.getTotalNumberAndMoney();
    }

    /**
     * 生成订单，订单未待支付状态
     * @param addOrderAO 订单生成实体类
     * @return
     */
    @AvoidRepeatableCommit
    @ApiOperation(value = "生成订单，订单未待支付状态.商品规格id集合、总价、各类商品价格集合、地址id、各类商品数量集合")
    @PostMapping(path = "/addOrder")
    public Result addOrder(@RequestBody AddOrderAO addOrderAO) {
        return orderService.addOrder(addOrderAO.getIdList(), addOrderAO.getTotalMoney(), addOrderAO.getTotalMoneyList()
                , addOrderAO.getAddressId(), addOrderAO.getGoodCountList());
    }

    /**
     * 修改订单状态订单
     * @param editOrderStatusAO 订单状态改变AO
     * @return
     */
    @AvoidRepeatableCommit
    @ApiOperation(value = "修改订单状态，app端使用,传id（订单id）与需要修改成的状态(orderStatus)（String类型）。订单状态：'2'为待接单（待支付的下一个状态）、" +
            "'3'为制作中、'4'为派送中、'5'为已收货、6为已评价, 7为取消中, 8:超时未接单")
    @PostMapping(path = "/editOrderStatus")
    public Result editOrderStatus(@RequestBody EditOrderStatusAO editOrderStatusAO) {
        return orderService.editOrderStatus(editOrderStatusAO.getId(), editOrderStatusAO.getOrderStatus());
    }

    /**
     * 支付订单
     * @param payOrderAO 支付订单AO
     * @return
     */
    @AvoidRepeatableCommit
    @ApiOperation(value = "支付订单，用于app端支付订单操作, id:订单的id； idList：商品规格idList； goodCountList： 商品对应数量List")
    @PostMapping(path = "/payOrder")
    @ResponseBody
    public Result payOrder(@RequestBody PayOrderAO payOrderAO) {
        return orderService.payOrder(payOrderAO.getId(), payOrderAO.getIdList(), payOrderAO.getGoodCountList());
    }

//    /**
//     * 确认取消订单
//     * @param id 订单id
//     * @return
//     */
//    @AvoidRepeatableCommit
//    @ApiOperation(value = "取消订单，用于app端取消订单")
//    @PostMapping(path = "/cancelOrder")
//    @ResponseBody
//    public Result cancelOrder(Long id) {
//        return orderService.cancelOrder(id);
//    }

}
