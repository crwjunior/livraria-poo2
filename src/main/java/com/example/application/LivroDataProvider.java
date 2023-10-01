package com.example.application;

import com.example.application.database.LivroDAO;
import com.example.application.dto.LivroDTO;
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

public class LivroDataProvider extends AbstractBackEndDataProvider<LivroDTO, CrudFilter>{
    final List<LivroDTO> DATABASE = new ArrayList<>(getLivros());

    public List<LivroDTO> getLivros(){
        try {
            return LivroDAO.listarLivros();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Consumer<Long> sizeChangeListener;

    @Override
    protected Stream<LivroDTO> fetchFromBackEnd(Query<LivroDTO, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<LivroDTO> stream = DATABASE.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<LivroDTO, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    private static Predicate<LivroDTO> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<LivroDTO>) livro -> {
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

    private static Comparator<LivroDTO> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<LivroDTO> comparator = Comparator.comparing(
                        livro -> (Comparable) valueOf(sortClause.getKey(),
                                livro));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<LivroDTO>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, LivroDTO livro) {
        try {
            Field field = LivroDTO.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(livro);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void persist(LivroDTO item) {
        if (item.getId() == null) {
            item.setId(DATABASE.stream().map(LivroDTO::getId).max(naturalOrder())
                    .orElse(0) + 1);
        }

        final Optional<LivroDTO> existingItem = find(item.getId());
        if (existingItem.isPresent()) {
            LivroDTO livroDTO;
            int position = DATABASE.indexOf(existingItem.get());
            DATABASE.remove(existingItem.get());
            DATABASE.add(position, item);
            try {
                livroDTO = LivroDAO.buscarLivroPorId(item.getId());
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao buscar livro por id", e);
            }
            try {
                LivroDAO.atualizarLivro(item.getId(), livroDTO);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao atualizar livro", e);
            }
        } else {
            DATABASE.add(item);
            try {
                LivroDAO.adicionarLivro(item);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao adicionar livro", e);
            }
        }
    }

    Optional<LivroDTO> find(Integer id) {
        return DATABASE.stream().filter(entity -> entity.getId().equals(id))
                .findFirst();
    }

    public void delete(LivroDTO item) {
        DATABASE.removeIf(entity -> entity.getId().equals(item.getId()));
        try {
            LivroDAO.deletarLivro(item.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar livro", e);
        }
    }
}
