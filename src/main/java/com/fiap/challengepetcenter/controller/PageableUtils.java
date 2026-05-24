package com.fiap.challengepetcenter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class PageableUtils {

    private PageableUtils() {
    }

    static Pageable comOrdenacaoPadrao(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        for (Sort.Order order : pageable.getSort()) {
            if (!order.getProperty().matches("[A-Za-z0-9_.]+")) {
                return PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Direction.ASC, "id")
                );
            }
        }

        return pageable;
    }
}
