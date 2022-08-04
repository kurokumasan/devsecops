package com.example.simpleshopmysql.controllers;

import com.example.simpleshopmysql.models.LineItem;
import com.example.simpleshopmysql.models.MessageResponse;
import com.example.simpleshopmysql.models.SaleOrder;
import com.example.simpleshopmysql.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping(value="api/v1")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(value="/order",method = RequestMethod.GET)
    public ResponseEntity<List<SaleOrder>> getAllOrders(HttpSession session) {
        try {
            List<SaleOrder> orders = orderService.getAllOrders(session);
            //List<SaleOrder> orders = (LIST<SaleOrder>)session.getAttribute("order_list");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order",method = RequestMethod.POST)
    public ResponseEntity<SaleOrder> createOrders(@RequestBody SaleOrder saleOrder, HttpSession session) {
        try {
            //List<SaleOrder> orders = (LIST<SaleOrder>)session.getAttribute("order_list");
            SaleOrder generatedOrder = orderService.createOrders(saleOrder, session);
	    //session.setAttribute("order_list", orders.add(generatedOrder));
            return ResponseEntity.ok(generatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.GET)
    public ResponseEntity<SaleOrder> getOrder(@PathVariable("orderid") Integer orderid, HttpSession session) {
        try {
            //System.out.println(session);	
            SaleOrder order = orderService.getOrder(orderid, session);
            //SaleOrder order = (SaleOrder) session.getAttribute("order");
            if(order!=null)
                return ResponseEntity.ok(order);
            else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.POST)
    public ResponseEntity<LineItem> createOrderLineItem(@PathVariable("orderid") Integer orderid, @RequestBody LineItem lineItem,HttpSession session) {
        try {
            LineItem savedLineItem = orderService.createOrderLineItem(orderid,lineItem,session);
            return ResponseEntity.ok(savedLineItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteOrder(@PathVariable("orderid") Integer orderid, HttpSession session) {
        try {
            orderService.deleteOrder(orderid, session);
            return ResponseEntity.ok(new MessageResponse(200,"delete order success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @RequestMapping(value="/order/{orderid}/{lineitemid}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> deleteOrderLineItem(@PathVariable("orderid") Integer orderid, @PathVariable("lineitemid") Integer lineitemid, HttpSession session) {
        try {
            orderService.deleteOrderLineItem(orderid,lineitemid,session);
            return ResponseEntity.ok(new MessageResponse(200,"delete lineitem success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value="/order/{orderid}/checkout", method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> checkout(@PathVariable("orderid") Integer orderid, HttpSession session) {
        try {
            int state = orderService.checkout(orderid,session);
            if(state==1)
                return ResponseEntity.ok(new MessageResponse(200,"checkouted"));
            else if(state==0)
                return ResponseEntity.ok(new MessageResponse(404,"order id not found"));
            else {
                return ResponseEntity.ok(new MessageResponse(500,"order was checked out "));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse(500,"no inventory"));
        }
    }

}

