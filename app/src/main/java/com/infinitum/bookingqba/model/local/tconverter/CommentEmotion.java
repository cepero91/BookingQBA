package com.infinitum.bookingqba.model.local.tconverter;

import android.arch.persistence.room.TypeConverter;

public enum CommentEmotion {
    AWFUL(0), GOOD(1), EXCELLENT(2);

    private final int emotionLevel;

    @TypeConverter
    public static CommentEmotion fromLevel(Integer level) {
        for (CommentEmotion emotion : values()) {
            if (emotion.emotionLevel == level) {
                return (emotion);
            }
        }
        return (null);
    }

    @TypeConverter
    public static Integer fromEmotion(CommentEmotion emotion) {
        return (emotion.emotionLevel);
    }

    CommentEmotion(int level) {
        this.emotionLevel = level;
    }
}
