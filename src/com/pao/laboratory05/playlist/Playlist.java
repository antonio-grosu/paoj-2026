package com.pao.laboratory05.playlist;

import java.util.Arrays;

public class Playlist {
    private String name;
    private Song[] songs = new Song[0];


    // Playlist(String name) — constructor
    Playlist(String name) {
        this.name = name;
    }


    String getName(){
        return name;
    }
    // void addSong(Song song) — adaugă cu pattern-ul de resize (System.arraycopy)

    void addSong(Song song){
        Song[] newArray = new Song[songs.length + 1];
        System.arraycopy(songs, 0, newArray, 0, songs.length);
        newArray[songs.length] = song;
        songs = newArray;
    }


    // void printSortedByTitle() — clonează array-ul, Arrays.sort(copy), afișează


    void printSortedByTitle(){
        Song[] copy = Arrays.copyOf(songs, songs.length);
        Arrays.sort(copy);
        for (Song song : copy) {
            System.out.println(song);
        }
    }

  
    // void printSortedByDuration() — clonează, Arrays.sort(copy, new SongDurationComparator()), afișează

    void printSortedByDuration(){
        Song[] copy = Arrays.copyOf(songs, songs.length);
        Arrays.sort(copy, new SongDurationComparator());
        for (Song song : copy) {
            System.out.println(song);
        }
    }


    
    // int getTotalDuration() — suma durationSeconds din toate song-urile

    int getTotalDuration(){
        int total = 0;
        for (Song song : songs) {
            total += song.durationSeconds();
        }

        return total;
    }
}
