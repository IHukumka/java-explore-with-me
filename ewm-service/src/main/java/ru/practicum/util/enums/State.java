package ru.practicum.util.enums;

import javax.validation.ValidationException;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

	public static State getStateValue(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}