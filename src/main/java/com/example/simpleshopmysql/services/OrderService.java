package com.example.simpleshopmysql.services;

import com.example.simpleshopmysql.models.LineItem;
import com.example.simpleshopmysql.models.Product;
import com.example.simpleshopmysql.models.SaleOrder;
import com.example.simpleshopmysql.repo.LineItemRepository;
import com.example.simpleshopmysql.repo.ProductRepository;
import com.example.simpleshopmysql.repo.SaleOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class OrderService {
    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Integer orderid;

    public List<SaleOrder> getAllOrders(HttpSession session) {
        //List<SaleOrder> orders= saleOrderRepository.findAll();
	List<SaleOrder> orders = new ArrayList<SaleOrder>();
	if (session.isNew()) {
	    //List<SaleOrder> orders = new ArrayList<SaleOrder>();
	    session.setAttribute("order_list", orders);
	    orderid = 0;
	} else { /*List<SaleOrder>*/ orders = (List<SaleOrder>) session.getAttribute("order_list"); }
        return orders;
    }

    public SaleOrder createOrders(SaleOrder saleOrder, HttpSession session) {
        //SaleOrder generatedOrder = saleOrderRepository.save(saleOrder);
        List<SaleOrder> orders = (List<SaleOrder>) session.getAttribute("order_list");
        saleOrder.setIid(++orderid);
	orders.add(saleOrder);
	session.setAttribute("order_list",orders);
        return saleOrder;//generatedOrder;
    }

    public SaleOrder getOrder(Integer orderid, HttpSession session) {
        /*Optional<SaleOrder> order = saleOrderRepository.findById(orderid);
        if(order.isPresent()) {
            return order.get();
        } else {
            return null;
        }*/
        List<SaleOrder> orders = (List<SaleOrder>) session.getAttribute("order_list");
	for (SaleOrder order : orders) {
	    if (order.getIid()==orderid) 
                return order;
	}
	return null;
    }

    public LineItem createOrderLineItem(Integer orderid, LineItem lineItem, HttpSession session) {
        if(lineItem==null)
            throw new RuntimeException("no lineitem data");
        //Optional<SaleOrder> temporaryorder = saleOrderRepository.findById(orderid);
        //if(temporaryorder.isEmpty())
        SaleOrder temporaryorder = this.getOrder(orderid, session);
        if(temporaryorder==null)
            throw new RuntimeException("no order id");
        Optional<Product> temporaryproduct = productRepository.findById(lineItem.getSku());
        if(temporaryproduct.isEmpty())
            throw new RuntimeException("no order");
        Product product = temporaryproduct.get();
        if(lineItem.getQuantity()>product.getQuantity())
            throw new RuntimeException("no inventory");
        //lineItem.setSaleOrder(temporaryorder.get());
        lineItem.setSaleOrder(temporaryorder);
        LineItem savedLineItem = lineItemRepository.save(lineItem);
        return savedLineItem;
    }

    public void deleteOrder(Integer orderid, HttpSession session) {
        //if(saleOrderRepository.existsById(orderid))
        //    saleOrderRepository.deleteById(orderid);
        List<SaleOrder> orders = (List<SaleOrder>) session.getAttribute("order_list");
	for (SaleOrder order : orders) {
	    if (order.getIid()==orderid) {
                orders.remove(order);
	        session.setAttribute("order_list", orders);
		return;
	    }
	}	
    }


    public void deleteOrderLineItem(Integer orderid, Integer lineitemid, HttpSession session) {
        //if(saleOrderRepository.existsById(orderid))
        //    if(lineItemRepository.existsById(lineitemid))
        //        lineItemRepository.deleteById(lineitemid);
	SaleOrder temporaryorder = this.getOrder(orderid, session);
	if(temporaryorder!=null)
            if(lineItemRepository.existsById(lineitemid))
                lineItemRepository.deleteById(lineitemid);
	    
    }

    public int checkout(Integer orderid, HttpSession session) {
        //Optional<SaleOrder> temporaryorder = saleOrderRepository.findById(orderid);
	SaleOrder temporaryorder = this.getOrder(orderid, session);
        //if(temporaryorder.isPresent()) {
        if(temporaryorder!=null) {
            //SaleOrder saleOrder = temporaryorder.get();
            SaleOrder saleOrder = temporaryorder;
            if(saleOrder.getState().equals("purchased"))
                return -1;
            List<LineItem> items = saleOrder.getLineItems();
            for(LineItem item : items) {
                Optional<Product> oproduct = productRepository.findById(item.getSku());
                if(oproduct.isPresent()) {
                    Product product = oproduct.get();
                    int quantity = product.getQuantity();
                    if(quantity>=item.getQuantity()) {
                        product.setQuantity(quantity-item.getQuantity());
                        productRepository.save(product);
                    } else {
                        throw new RuntimeException("inventory not enough");
                    }
                }
            }
            saleOrder.setCheckoutDate(LocalDateTime.now());
            saleOrder.setState("purchased");
            saleOrderRepository.save(saleOrder);
            return 1;
        } else {
            return 0;
        }
    }

}
