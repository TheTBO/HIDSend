package com.example.hidsend

object Characters {
    val conversionMap: Map<Char, String> = mapOf(
        '`' to "backquote",
        '~' to "tilde",
        '!' to "left-shift 1",
        '@' to "left-shift 2",
        '#' to "left-shift 3",
        '$' to "left-shift 4",
        '%' to "left-shift 5",
        '^' to "left-shift 6",
        '&' to "left-shift 7",
        '*' to "left-shift 8",
        '(' to "left-shift 9",
        ')' to "left-shift 0",
        '-' to "minus",
        '_' to "left-shift minus",
        '=' to "equal",
        '+' to "left-shift equal",
        '.' to "period",
        '>' to "left-shift period",
        ',' to "comma",
        '<' to "left-shift comma",
        ';' to "semicolon",
        ':' to "left-shift semicolon",
        '\'' to "quote",
        '[' to "lbracket",
        ']' to "rbracket",
        '{' to "left-shift lbracket",
        '}' to "left-shift rbracket",
        '\\' to "backslash",
        '/' to "slash",
        '?' to "left-shift slash"
    )

}