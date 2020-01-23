package com.example.bookshelf.controller;

import com.example.bookshelf.storage.BookStorage;
import com.example.bookshelf.storage.impl.PostgresBookStorageImpl;
import com.example.bookshelf.type.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import java.util.List;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Response.Status.*;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class BookController {

    private final static String BOOK_ID_PARAM_NAME = "bookId";

    private BookStorage bookStorage = new PostgresBookStorageImpl();

    public Response serveGetBookRequest(IHTTPSession session) {
        Map<String, List<String>> requestParameters = session.getParameters();
        if (requestParameters.containsKey(BOOK_ID_PARAM_NAME)) {
            List<String> bookIdParams = requestParameters.get(BOOK_ID_PARAM_NAME);
            String bookIdParam = bookIdParams.get(0);
            long bookId = 0;

            try {
                bookId = Long.parseLong(bookIdParam);
            } catch (NumberFormatException nfe) {
                System.err.println("Error during convert request param: \n" + nfe);
                return newFixedLengthResponse(BAD_REQUEST,"text/plain","Request param 'bookId' have to be a number");
            }

            Book book = bookStorage.getBook(bookId);
            if (book != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String response = objectMapper.writeValueAsString(book);
                    return newFixedLengthResponse(OK,"application/json", response);
                } catch (JsonProcessingException e) {
                    System.err.println("Error during process request: \n" + e);
                    return newFixedLengthResponse(INTERNAL_ERROR,"text/plain","Internal error can't read all book");
                }
            }
            return newFixedLengthResponse(NOT_FOUND,"application/json","");
        }
        return newFixedLengthResponse(BAD_REQUEST,"text/plain","Incorrect request params");
    }

    public Response serveGetBooksRequest(IHTTPSession session) {

        ObjectMapper objectMapper = new ObjectMapper();
        String response;

        try {
            response = objectMapper.writeValueAsString(bookStorage.getAllBooks());
        } catch (JsonProcessingException e) {
            System.err.println("Error while processing your request: \n" + e);
            return newFixedLengthResponse(INTERNAL_ERROR,"text/plain","Internal error cannot read all book");
        }
        return newFixedLengthResponse(OK, "application/json", response);
    }

    public Response serveAddBookRequest(IHTTPSession session) {

        ObjectMapper objectMapper = new ObjectMapper();
        long randomBookId = System.currentTimeMillis();

        String lengthHeader = session.getHeaders().get("content-length");
        int contentLength = Integer.parseInt(lengthHeader);
        byte[] buffer = new byte[contentLength];

        try {
            session.getInputStream().read(buffer,0, contentLength);
            String requestBody = new String(buffer).trim();
            Book requestBook = objectMapper.readValue(requestBody, Book.class);
            requestBook.setId(randomBookId);

            bookStorage.addBook(requestBook);

        } catch (Exception e) {
            System.err.println("Error during process request: \n" + e);
            return newFixedLengthResponse(INTERNAL_ERROR,"text/plain","Internal error book hasn't been added");
        }
        return newFixedLengthResponse(OK,"text/plain","Book has been successfully added, id=" + randomBookId);
    }
}
