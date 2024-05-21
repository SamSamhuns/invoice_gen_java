package com.ssdgen.generator.documents.common;

import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

public class VerifCharEncoding extends WinAnsiEncoding {

    public static String remove(String test) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < test.length(); i++) {
            if(test.charAt(i)=='\u00A0' || test.charAt(i)=='\u009D' || test.charAt(i) == '\u0095' || test.charAt(i) == '\u008A'){
                b.append(' ');
            }
            else if (WinAnsiEncoding.INSTANCE.contains(test.charAt(i))) {
                b.append(test.charAt(i));
            }
        }
        return b.toString();
    }

}
