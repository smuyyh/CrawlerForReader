package com.qy.reader.crawler.xpath.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmMap {
    private static EmMap ourInstance = new EmMap();
    public Map<String, ScopeEm> scopeEmMap = new HashMap<String, ScopeEm>();
    public Map<String, OpEm> opEmMap = new HashMap<String, OpEm>();
    public Set<Character> commOpChar = new HashSet<Character>();

    private EmMap() {
        scopeEmMap.put("/", ScopeEm.INCHILREN);
        scopeEmMap.put("//", ScopeEm.RECURSIVE);
        scopeEmMap.put("./", ScopeEm.CUR);
        scopeEmMap.put(".//", ScopeEm.CURREC);

        opEmMap.put("+", OpEm.PLUS);
        opEmMap.put("-", OpEm.MINUS);
        opEmMap.put("=", OpEm.EQ);
        opEmMap.put("!=", OpEm.NE);
        opEmMap.put(">", OpEm.GT);
        opEmMap.put("<", OpEm.LT);
        opEmMap.put(">=", OpEm.GE);
        opEmMap.put("<=", OpEm.LE);
        opEmMap.put("^=", OpEm.STARTWITH);
        opEmMap.put("$=", OpEm.ENDWITH);
        opEmMap.put("*=", OpEm.CONTAIN);
        opEmMap.put("~=", OpEm.REGEX);
        opEmMap.put("!~", OpEm.NOTMATCH);

        commOpChar.add('+');
        commOpChar.add('-');
        commOpChar.add('=');
        commOpChar.add('*');
        commOpChar.add('^');
        commOpChar.add('$');
        commOpChar.add('~');
        commOpChar.add('>');
        commOpChar.add('<');
        commOpChar.add('!');
    }

    public static EmMap getInstance() {
        return ourInstance;
    }
}
