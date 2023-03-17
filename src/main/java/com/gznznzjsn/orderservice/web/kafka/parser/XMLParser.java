package com.gznznzjsn.orderservice.web.kafka.parser;

import com.jcabi.xml.XML;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class XMLParser {

    private final XML xml;

    public String parse(String node) {
        return this.xml.nodes("//" + node)
                .get(0)
                .xpath("text()")
                .get(0);
    }

}