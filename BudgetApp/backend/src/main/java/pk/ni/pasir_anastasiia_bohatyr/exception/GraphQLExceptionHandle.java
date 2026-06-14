package pk.ni.pasir_anastasiia_bohatyr.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GraphQLExceptionHandle implements DataFetcherExceptionResolver {

    @Override
    public @NonNull Mono<List<GraphQLError>> resolveException(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {

        if (ex instanceof ConstraintViolationException validationEx) {
            List<GraphQLError> errors = validationEx.getConstraintViolations()
                    .stream()
                    .map(v -> GraphqlErrorBuilder.newError(env)
                            .message("Błąd walidacji: " + v.getMessage())
                            .build())
                    .collect(Collectors.toList());

            return Mono.just(errors);
        }

        GraphQLError error = GraphqlErrorBuilder.newError(env)
                .message("Wystąpił błąd: " + ex.getMessage())
                .build();

        return Mono.just(List.of(error));
    }
}

