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
    EMAIL_ALREADY_EXISTS("Email already exists"),
    BOOKING_START_DATE_LATER_END_DATE("Booking start date later than the end date"),
    ITEM_UNAVAILABLE("Item unavailable"),
    USER_RESERVE_OWN_ITEM("You can't reserve your item"),
    BOOKING_NOT_FOUND("Booking not found"),
    BOOKING_ALREADY_CONFIRMED("Booking already confirmed"),
    NO_MATCHING_BOOKINGS("No matching bookings"),
    FORBIDDEN_TO_ADD_COMMENTS("Forbidden to add comments");

    private final String title;

    ExceptionDescriptions(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}