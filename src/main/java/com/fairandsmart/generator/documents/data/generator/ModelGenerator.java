package com.fairandsmart.generator.documents.data.generator;

public interface ModelGenerator<T> {

    T generate(GenerationContext ctx);

}
