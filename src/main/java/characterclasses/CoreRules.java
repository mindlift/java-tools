package characterclasses;

public final class CoreRules {

    // core rules - https://tools.ietf.org/html/rfc5234#appendix-B.1
    public static final String ALPHA    = "[\\x41-\\x5A\\x61-\\x7A]"; // A-Z / a-z
    public static final String BIT      = "[\\x30\\x31]";
    public static final String CHAR     = "[\\x01-\\x7F]";  // any 7-bit US-ASCII character, excluding NUL
    public static final String CR       = "\\x0D";  // carriage return
    public static final String CTL      = "[\\x00-\\x1F\\x7F]";  // controls
    public static final String DIGIT    = "[\\x30-\\x39]";  // 0-9
    public static final String DQUOTE   = "\\x22";  // " (Double Quote)
    public static final String HEXDIG   = "[" + DIGIT + "[\\x41-\\x46\\x61-\\x66]]";
    public static final String HTAB     = "\\x09";  // horizontal tab
    public static final String LF       = "\\x0A";  // linefeed
    public static final String CRLF     = CR + LF;  // Internet standard newline
    public static final String SP       = "\\x20";  // space character
    public static final String WSP      = "[" + SP + HTAB + "]";  // white space
    /*
    ; Use of this linear-white-space rule
    ;  permits lines containing only white
    ;  space that are no longer legal in
    ;  mail headers and have caused
    ;  interoperability problems in other
    ;  contexts.
    ;  Do not use when defining mail
    ;  headers and use with caution in
    ;  other contexts.
     */
    public static final String LWSP = "^(?:" + WSP + "|" + CRLF + WSP + ")*?";  // linear white space *(WSP / CRLF WSP)
    public static final String OCTET = "[\\x00-\\xFF]";   // any 8 bits of data
    public static final String VCHAR = "[\\x21-\\x7E]";   // visible (printing) characters
}
