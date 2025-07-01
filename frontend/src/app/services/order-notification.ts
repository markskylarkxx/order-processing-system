import { Injectable } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject } from 'rxjs';
import environment from '../environment'; 

export interface OrderNotification {
  orderId: number;
  status: string;
  productId: number;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderNotificationService {
  private stompClient?: Stomp.Client;
  private orderSubject = new BehaviorSubject<OrderNotification | null>(null);
  public orderUpdates$ = this.orderSubject.asObservable();

  connect() {
    const socket = new SockJS(environment.socketUrl);
    this.stompClient = new Stomp.Client({
      webSocketFactory: () => socket as any,
      debug: (msg) => console.log('[STOMP]', msg),
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      this.stompClient?.subscribe('/topic/orders', (message) => {
        const notification: OrderNotification = JSON.parse(message.body);
        this.orderSubject.next(notification);
      });
    };

    this.stompClient.activate();
  }
  disconnect() {
    this.stompClient?.deactivate();
  }
}
