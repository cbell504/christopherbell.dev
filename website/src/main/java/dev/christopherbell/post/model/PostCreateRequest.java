package dev.christopherbell.post.model;

import lombok.Builder;

/**
 * Request payload for creating a new post or reply.
 *
 * @param text the tweet‑like content to publish (trimmed, required, ≤ 280 chars)
 * @param parentId optional id of the parent post (when replying);
 *                 when provided, the new post becomes a child in that thread
 */
@Builder
public record PostCreateRequest(
    String text,
    String parentId
) {}
