package com.team.messaging_service.exceptions;

public class CommentExceptions {

    public static class InvalidCommentDataException extends RuntimeException {
        public InvalidCommentDataException(String message) {
            super(message);
        }
    }

    public static class CommentCreationException extends RuntimeException {
        public CommentCreationException(String message) {
            super(message);
        }
    }

    public static class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException(String message) {
            super(message);
        }
    }
}