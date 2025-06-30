import { TestBed } from '@angular/core/testing';

import { OrderNotification } from './order-notification';

describe('OrderNotification', () => {
  let service: OrderNotification;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderNotification);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
