package org.tracker.configuration;

import java.util.UUID;

public record UserPrincipal(UUID id, String email) {
}
