package com.example.bookshelf;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class BookshelfApp extends NanoHTTPD {

    RequestUtlMapper requestUtlMapper = new RequestUtlMapper();

    public BookshelfApp(int port) throws IOException {
        super(port);
        start(5000, false);
        System.out.println("Server has been initialized");
    }

    public static void main(String[] args) {
        try {
            new BookshelfApp(8080);
        } catch (IOException e) {
            System.err.println("Server cannot be initialized due to error: \n" + e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        return requestUtlMapper.delegateRequest(session);
    }
}
