package com.llamagen.param;

import java.util.LinkedHashMap;
import java.util.Map;

abstract class BaseParamsBuilder<T extends BaseParamsBuilder<T>> {
    protected final Map<String, Object> extraParams = new LinkedHashMap<>();

    public T putExtraParam(String key, Object value) {
        extraParams.put(key, value);
        return self();
    }

    protected abstract T self();
}
