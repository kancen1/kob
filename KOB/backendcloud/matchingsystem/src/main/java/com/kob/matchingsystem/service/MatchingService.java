package com.kob.matchingsystem.service;

public interface MatchingService {
    String addPlayer(int userId, int rating);
    String removePlayer(int userId);
}
