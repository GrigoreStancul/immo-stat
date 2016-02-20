package com.gst.immo.utils.functions;

import java.io.IOException;

@FunctionalInterface
public interface CheckedConsumer<T> {
	void accept(T t) throws IOException;
}
