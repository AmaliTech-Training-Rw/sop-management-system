package com.team.messaging_service.exceptions;

public class CommentExceptions {
    public static class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException(String id) {
            super("Comment not found with id: " + id);
        }
    }

    public static class CommentCreationException extends RuntimeException {
        public CommentCreationException(String message) {
            super(message);
        }
    }

    public static class InvalidCommentDataException extends RuntimeException {
        public InvalidCommentDataException(String message) {
            super(message);
        }
    }

    public static class CommentFetchException extends RuntimeException {
        public CommentFetchException(String message) {
            super(message);
        }
    }

    public static class InvalidPaginationParametersException extends RuntimeException {
        public InvalidPaginationParametersException(String message) {
            super(message);
        }
    }

    public static class InvalidCommentIdException extends RuntimeException {
        public InvalidCommentIdException(String message) {
            super(message);
        }
    }

    public static class InvalidSopDocumentIdException extends RuntimeException {
        public InvalidSopDocumentIdException(String message) {
            super(message);
        }
    }

    public static class InvalidSearchParameterException extends RuntimeException {
        public InvalidSearchParameterException(String message) {
            super(message);
        }
    }

    public static class CommentSearchException extends RuntimeException {
        public CommentSearchException(String message) {
            super(message);
        }
    }
}