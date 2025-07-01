package com.order_service.domain;

import com.order_service.application.dto.OrderDto;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository  extends JpaRepository<Order,Long> {


}
