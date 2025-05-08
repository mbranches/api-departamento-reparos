package com.branches.exception;

import java.util.List;
import java.util.Map;

public record ValidationErrorMessage(int status, String message, Map<String, List<String>> errors) {
}
