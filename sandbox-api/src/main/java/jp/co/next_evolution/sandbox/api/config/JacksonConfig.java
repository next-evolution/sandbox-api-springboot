package jp.co.next_evolution.sandbox.api.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  private static final ZoneOffset JST = ZoneOffset.ofHours(9);
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder
        .serializers(new LocalDateTimeJstSerializer())
        .deserializers(new LocalDateTimeJstDeserializer());
  }

  static class LocalDateTimeJstSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public Class<LocalDateTime> handledType() {
      return LocalDateTime.class;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeString(value.atOffset(JST).format(FORMATTER));
    }
  }

  static class LocalDateTimeJstDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public Class<?> handledType() {
      return LocalDateTime.class;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      return OffsetDateTime.parse(p.getText(), FORMATTER).toLocalDateTime();
    }
  }

}
