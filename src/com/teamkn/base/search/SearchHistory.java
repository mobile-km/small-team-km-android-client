package com.teamkn.base.search;

import com.google.common.base.Joiner;
import com.teamkn.Logic.TeamknPreferences;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class SearchHistory {
    private static String get_history_string() {
        return TeamknPreferences.PREFERENCES.getString("search_history", "");
    }

    private static <E> LinkedList<E> uniquify_list(List<E> list) {
        return new LinkedList<E>(new LinkedHashSet<E>(list));
    }

    private static <E> LinkedList<E> trim_list(LinkedList<E> list, int size) {
        if (list.size() >= size) {
            list.subList(10, list.size())
                    .clear();
            return list;
        }

        return list;
    }


    public static LinkedList<String> put(String query_string) {
        LinkedList<String> unique_records = uniquify_list(get());

        if (query_string.length() > 0) {
            unique_records.addFirst(query_string);
        }

        unique_records = uniquify_list(unique_records);
        LinkedList<String> history_cache = trim_list(unique_records, 10);
        TeamknPreferences.put_string("search_history",
                                     Joiner.on(",").join(history_cache));
        return history_cache;
    }

    public static LinkedList<String> get() {
        if (get_history_string().equals("")) {
            return new LinkedList<String>();
        }

        return new LinkedList<String>(Arrays.asList(get_history_string().split(",")));
    }

}