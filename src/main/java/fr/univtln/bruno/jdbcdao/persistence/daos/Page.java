package fr.univtln.bruno.jdbcdao.persistence.daos;

import java.util.List;

public record Page<T>(int pageNumber, int pageSize, List<T> resultList) {
}