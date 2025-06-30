

import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderNotificationService } from '../services/order-notification';
@Component({
  selector: 'app-order-list',
  standalone: true,
  templateUrl: './order-list.html',
  styleUrls: ['./order-list.css'],
  imports: [CommonModule, RouterModule],
})
export class OrderList implements OnInit, OnDestroy {
  orders: any[] = [];

  constructor(
    private http: HttpClient,
    private orderNotificationService: OrderNotificationService
  ) {}

  ngOnInit() {
    this.fetchOrders();
    this.orderNotificationService.connect();

    this.orderNotificationService.orderUpdates$.subscribe((update) => {
      if (update) {
        console.log('[OrderList] New order update received', update);
        this.fetchOrders();
      }
    });
  }

  ngOnDestroy() {
    this.orderNotificationService.disconnect();
  }
  fetchOrders() {
    this.http.get<any[]>('http://localhost:8081/orders')
      .subscribe({
        next: (data) => {
          this.orders = data;
        },
        error: (err) => {
          console.error(err);
          alert('Failed to load orders');
        }
      });
  }
}
