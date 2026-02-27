package com.gamesUP.gamesUP.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class GameSearchRequest {

    private String keyword;
    private Long categoryId;
    private Long publisherId;
    private Long authorId;
    private String genre;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Double minRating;
    private Boolean inStock;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releasedAfter;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releasedBefore;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public LocalDate getReleasedAfter() {
        return releasedAfter;
    }

    public void setReleasedAfter(LocalDate releasedAfter) {
        this.releasedAfter = releasedAfter;
    }

    public LocalDate getReleasedBefore() {
        return releasedBefore;
    }

    public void setReleasedBefore(LocalDate releasedBefore) {
        this.releasedBefore = releasedBefore;
    }
}
