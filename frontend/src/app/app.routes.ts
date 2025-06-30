import { Routes } from '@angular/router';
import { CreateOrder } from './create-order/create-order';
import { OrderList } from './order-list/order-list';

export const routes: Routes = [
  { path: '', component: OrderList },
  { path: 'create-order', component: CreateOrder },
];
