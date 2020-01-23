package com.example.bookshelf.storage.impl;

import com.example.bookshelf.storage.BookStorage;
import com.example.bookshelf.type.Book;

import java.util.ArrayList;
import java.util.List;

public class StaticListBookStorageImpl implements BookStorage {

    private static List<Book> bookStorage = new ArrayList<>();

    @Override
    public Book getBook(long id) {
        for (Book book : bookStorage) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookStorage;
    }

    @Override
    public void addBook(Book book) {
        bookStorage.add(book);
    }
}
