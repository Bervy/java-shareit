package ru.practicum.shareit.error;


public enum ExceptionDescriptions {
    AVAILABLE_NOT_FOUND("Available not found"),
    USER_NOT_FOUND("User not found"),
    EMPTY_NAME("Name must not be empty"),
    DESCRIPTION_NOT_FOUND("Description not found"),
    OWNER_NOT_FOUND("Owner not found"),
    ITEM_NOT_FOUND("Item not found"),
    ITEM_ALREADY_EXISTS("Item already exists"),
    USER_ALREADY_EXISTS("User already exists"),
    EMPTY_EMAIL("Empty email"),
    EMAIL_ALREADY_EXISTS("Email already exists");

    private final String title;

    ExceptionDescriptions(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}