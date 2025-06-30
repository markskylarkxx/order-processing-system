import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-create-order',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-order.html',
  styleUrls: ['./create-order.css'],
})
export class CreateOrder implements OnInit {
  products: any[] = [];

  order = {
    productId: 0,
    quantity: 0,
  };

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.loadProducts();
  }

 loadProducts() {
  this.http.get<{ data: any[] }>('http://localhost:8082/products')
    .subscribe({
      next: (response) => {
        this.products = response.data;
      },
      error: (err) => {
        console.error(err);
        alert('Failed to load products');
      }
    });
}

  submitOrder() {
    this.http.post('http://localhost:8081/orders', this.order)
      .subscribe({
        next: () => {
          alert('Order Created Successfully');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error(err);
          alert('Error creating order');
        },
      });
  }
}