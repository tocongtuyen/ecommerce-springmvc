package com.vietshop.Service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vietshop.Entity.Order;
import com.vietshop.Service.iOrderService;
import com.vietshop.dto.OrderDTO;
import com.vietshop.repository.OrderRepository;
import com.vietshop.util.SecurityUtils;

@Service // Để class có thể thực hiện cơ chế DI và IOC
public class OrderService implements iOrderService{
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public <S extends Order> S save(S entity) {
		return orderRepository.save(entity);
	}
	
	@Override
	public <S extends Order> S findOne(Example<S> example) {
		return orderRepository.findOne(example);
	}

	@Override
	public Page<Order> findAll(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}


	@Override
	public OrderDTO findOne(Long id) {
		Order order = orderRepository.findOne(id);
		OrderDTO orderDTO = new OrderDTO();
		BeanUtils.copyProperties(order, orderDTO);
		return orderDTO;
	}


	@Override
	public boolean exists(Long id) {
		return orderRepository.exists(id);
	}

	@Override
	public <S extends Order> List<S> save(Iterable<S> entities) {
		return orderRepository.save(entities);
	}

	@Override
	public void flush() {
		orderRepository.flush();
	}

	@Override
	public <S extends Order> S saveAndFlush(S entity) {
		return orderRepository.saveAndFlush(entity);
	}

	@Override
	public long count() {
		return orderRepository.count();
	}

	@Override
	public void deleteInBatch(Iterable<Order> entities) {
		orderRepository.deleteInBatch(entities);
	}

	@Override
	public <S extends Order> Page<S> findAll(Example<S> example, Pageable pageable) {
		return orderRepository.findAll(example, pageable);
	}

	@Override
	public void delete(Long id) {
		orderRepository.delete(id);
	}

	@Override
	public void deleteAllInBatch() {
		orderRepository.deleteAllInBatch();
	}

	@Override
	public void delete(Order entity) {
		orderRepository.delete(entity);
	}

	@Override
	public Order getOne(Long id) {
		return orderRepository.getOne(id);
	}

	@Override
	public <S extends Order> long count(Example<S> example) {
		return orderRepository.count(example);
	}

	@Override
	public void delete(Iterable<? extends Order> entities) {
		orderRepository.delete(entities);
	}

	

	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public void deleteAll() {
		orderRepository.deleteAll();
	}

	@Override
	public <S extends Order> boolean exists(Example<S> example) {
		return orderRepository.exists(example);
	}


	@Override
	public Order getOrderByUserName(String username) {
		return orderRepository.getOrderByUserName(username);
	}

	public Page<Order> findRecentOrder(Pageable page) {
		return orderRepository.findRecentOrder(page);
	}

	

	
	public Page<Order> findRecentOrderByUsername(Pageable page, String username) {
		return orderRepository.findRecentOrderByUsername(page, username);
	}
	
	
	public Page<OrderDTO> getLastOrder(){
	PageRequest page_req = new PageRequest(0, 5);
    Pageable page = page_req;
    
    Page<OrderDTO> orderyDTOpage = orderRepository.findRecentOrder(page).map(Order -> {
    	OrderDTO orderDTO = new OrderDTO();
		BeanUtils.copyProperties(Order, orderDTO);
		return orderDTO;
	});
    return orderyDTOpage;
	}
	
	public void pendingStatus(Long idOrder) {
		Order order = orderRepository.findOne(idOrder);
		order.setStatus("Thanh toán khi nhận hàng");
		orderRepository.save(order);
	}
	
	public void successStatus(Long idOrder) {
		Order order = orderRepository.findOne(idOrder);
		order.setStatus("Đã thanh toán");
		orderRepository.save(order);
	}
	
	
	public Page<OrderDTO> orderPage(Pageable pageable) {
		Page<OrderDTO> orderDTOpage = orderRepository.findAll(pageable).map(Order -> {
			OrderDTO orderDTO = new OrderDTO();
			BeanUtils.copyProperties(Order, orderDTO);
			return orderDTO;
		});
		return orderDTOpage;
	}
	
	
	public Page<OrderDTO> orderPage( Optional<String> keyword,Pageable pageable) {

		Page<OrderDTO> orderDTOpage = orderRepository.searchOrder(keyword.get(),pageable).map(Order -> {
			OrderDTO orderDTO = new OrderDTO();
			BeanUtils.copyProperties(Order, orderDTO);
			return orderDTO;
		});

		return orderDTOpage;
	}
	
	
	public Page<OrderDTO> mylistOrder(Optional<Integer> p){
		int currentPage = p.orElse(0);
		PageRequest page_req = new PageRequest(currentPage, 10);
		Pageable page = page_req;
		Page<OrderDTO> pageOrderDTO = orderRepository.findRecentOrderByUsername(page,
				SecurityUtils.getPrincipal().getUsername()).map(Order->{
					OrderDTO orderDTO = new OrderDTO();
					BeanUtils.copyProperties(Order, orderDTO);
					return orderDTO;
				});
		return pageOrderDTO;
	}
	
//	public List<OrderDetailsDTO> orderList(Long idOrder){ 
//		Order order = orderRepository.findOne(idOrder);
//	List<OrderDetailsDTO> orderDetailsDTOlist = order.getOrderDetailsList().map(OrderDetails -> {
//		OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
//		BeanUtils.copyProperties(Category, categoryDTO);
//		return categoryDTO;
//	});
//	return orderDetailsDTOlist;
//	}
}
