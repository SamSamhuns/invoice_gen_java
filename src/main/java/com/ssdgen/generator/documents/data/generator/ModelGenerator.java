package com.ssdgen.generator.documents.data.generator;

public interface ModelGenerator<T> {

    T generate(GenerationContext ctx);

}
