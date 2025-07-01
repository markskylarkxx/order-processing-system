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
  currentPage = 0;
  totalPages = 0;

  constructor(
    private http: HttpClient,
    private orderNotificationService: OrderNotificationService
  ) {}

  ngOnInit() {
    this.fetchOrders(this.currentPage);
    this.orderNotificationService.connect();

    this.orderNotificationService.orderUpdates$.subscribe((update) => {
      if (update) {
        console.log('[OrderList] New order update received', update);
        this.fetchOrders(this.currentPage);
      }
    });
  }

  ngOnDestroy() {
    this.orderNotificationService.disconnect();
  }

  fetchOrders(page: number = 0) {
    this.http
      .get<any>(`http://localhost:8081/orders?page=${page}&size=10`)
      .subscribe({
        next: (data) => {
          this.orders = data.content;
          this.currentPage = data.number;
          this.totalPages = data.totalPages;
        },
        error: (err) => {
          console.error(err);
          alert('Failed to load orders');
        },
      });
  }

  goToPage(page: number) {
    this.fetchOrders(page);
  }
}
1