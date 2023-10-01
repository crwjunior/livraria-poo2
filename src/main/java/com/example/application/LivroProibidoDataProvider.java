package com.example.application;

import com.example.application.database.LivroDAO;
import com.example.application.database.LivroProibidoDAO;
import com.example.application.dto.LivroDTO;
import com.example.application.dto.LivrosProibidosDTO;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;

public class LivroProibidoDataProvider extends AbstractBackEndDataProvider<LivrosProibidosDTO, CrudFilter>{
    final List<LivrosProibidosDTO> DATABASE = new ArrayList<>(getLivros());

    public List<LivrosProibidosDTO> getLivros(){
        try {
            return LivroProibidoDAO.listarLivros();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Consumer<Long> sizeChangeListener;

    @Override
    protected Stream<LivrosProibidosDTO> fetchFromBackEnd(Query<LivrosProibidosDTO, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<LivrosProibidosDTO> stream = DATABASE.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<LivrosProibidosDTO, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    private static Predicate<LivrosProibidosDTO> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<LivrosProibidosDTO>) livro -> {
                    try {
                        Object value = valueOf(constraint.getKey(), livro);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<LivrosProibidosDTO> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<LivrosProibidosDTO> comparator = Comparator.comparing(
                        livro -> (Comparable) valueOf(sortClause.getKey(),
                                livro));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<LivrosProibidosDTO>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, LivrosProibidosDTO livro) {
        try {
            Field field = LivrosProibidosDTO.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(livro);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void persist(LivrosProibidosDTO item) {
        if (item.getId() == null) {
            item.setId(DATABASE.stream().map(LivrosProibidosDTO::getId).max(naturalOrder())
                    .orElse(0) + 1);
        }

        final Optional<LivrosProibidosDTO> existingItem = find(item.getId());
        if (existingItem.isPresent()) {
            int position = DATABASE.indexOf(existingItem.get());
            DATABASE.remove(existingItem.get());
            DATABASE.add(position, item);
            LivrosProibidosDTO livro;
            try {
                livro = LivroProibidoDAO.buscarLivroProibidoPorId(item.getId());
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao buscar livro proibido", e);
            }
            try {
                LivroProibidoDAO.atualizarLivroProibido(item.getId(), livro);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao atualizar livro proibido", e);
            }

        } else {
            DATABASE.add(item);
            try {
                LivroProibidoDAO.adicionarLivro(item);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao adicionar livro proibido", e);
            }
        }
    }

    Optional<LivrosProibidosDTO> find(Integer id) {
        return DATABASE.stream().filter(entity -> entity.getId().equals(id))
                .findFirst();
    }

    public void delete(LivrosProibidosDTO item) {
        try {
            LivroProibidoDAO.deletarLivro(item.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar livro proibido", e);
        }
        DATABASE.removeIf(entity -> entity.getId().equals(item.getId()));
    }
}
