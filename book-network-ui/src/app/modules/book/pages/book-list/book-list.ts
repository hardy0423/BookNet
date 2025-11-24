import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { BookService } from '../../../../services/services/book.service';
import {BookResponse} from '../../../../services/models/book-response';
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookCard } from '../../components/book-card/book-card';

@Component({
  selector: 'app-book-list',
  imports: [CommonModule, FormsModule, RouterModule, BookCard],
  templateUrl: './book-list.html',
  styleUrls: ['./book-list.scss'],
})

export class BookList implements OnInit {

  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 5;
  pages: number[] = [];
  message = '';
  level: 'success' | 'error' = 'success';

  constructor(
    private bookService: BookService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.findAllBooks();
  }

  // ===============================
  //           LOAD BOOKS
  // ===============================
  async findAllBooks() {
    try {
      const books = await this.bookService.findAllBooks({
        page: this.page,
        size: this.size
      });

      this.bookResponse = books;
      this.pages = Array(books.totalPages ?? 0)
        .fill(0)
        .map((_, i) => i);

    } catch (err) {
      console.error('Error loading books: ', err);
      this.message = 'Unable to load books';
      this.level = 'error';
    }
  }

  // ===============================
  //            PAGINATION
  // ===============================
  async gotToPage(page: number) {
    this.page = page;
    await this.findAllBooks();
  }

  async goToFirstPage() {
    this.page = 0;
    await this.findAllBooks();
  }

  async goToPreviousPage() {
    if (this.page > 0) {
      this.page--;
      await this.findAllBooks();
    }
  }

  async goToLastPage() {
    if (this.bookResponse.totalPages) {
      this.page = this.bookResponse.totalPages - 1;
      await this.findAllBooks();
    }
  }

  async goToNextPage() {
    if (this.bookResponse.totalPages && this.page < this.bookResponse.totalPages - 1) {
      this.page++;
      await this.findAllBooks();
    }
  }

  get isLastPage() {
    return this.bookResponse.totalPages
      ? this.page === this.bookResponse.totalPages - 1
      : true;
  }

  // ===============================
  //         BORROW A BOOK
  // ===============================
  async borrowBook(book: BookResponse) {
    this.message = '';
    this.level = 'success';

    try {
      await this.bookService.borrowBook({
        'book-id': book.id!
      });

      this.level = 'success';
      this.message = 'Book successfully added to your list';

    } catch (err: any) {
      console.error(err);
      this.level = 'error';
      this.message = err?.error?.error || 'Unable to borrow this book';
    }
  }

  // ===============================
  //         BOOK DETAILS
  // ===============================
  displayBookDetails(book: BookResponse) {
    this.router.navigate(['books', 'details', book.id]);
  }
}
