package glowie.glowie_app.launcher.domain

import kotlin.text.Regex

/**
 * Make a lua script more compact by removing comments and extra whitespace
 *
 * This is a naive and wrong implementation without proper lexing
 * and will fail in some cases, e.g. it does not tokenize literal strings
 * and will remove whitespace and comments from them. It may also remove
 * whitespace between tokens that need it, although hopefully only in places
 * with wrong syntax (e.g. < = would turn into <=)
 */
fun compactScript(script: String) : String {
    var script = script

    // Replace block comments with a space, as token on both sides of the comment shall be kept separate
    script = Regex("""--\[\[.*\]\]""", RegexOption.DOT_MATCHES_ALL)
        .replace(script, " ")

    // Remove line comments
    script = Regex("""--.*""")
        .replace(script, " ")

    // Turn multiple whitespace into a single space
    script = Regex("""\s+""")
        .replace(script, " ")

    // Remove whitespace around characters that (probably) don't need it
    script = Regex(""" ?([-=+*/(){}\[\],;]) ?""")
        .replace(script, "$1")

    // Trim any whitespace left at the beginning or end of the script
    script = script.trim()

    return script
}
