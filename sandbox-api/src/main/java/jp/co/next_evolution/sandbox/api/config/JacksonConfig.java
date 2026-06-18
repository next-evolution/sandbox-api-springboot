package jp.co.next_evolution.sandbox.api.config;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

  private static final ZoneOffset JST = ZoneOffset.ofHours(9);
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

  @Bean
  public JsonMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> {
      SimpleModule module = new SimpleModule();
      module.addSerializer(new LocalDateTimeJstSerializer());
      module.addDeserializer(LocalDateTime.class, new LocalDateTimeJstDeserializer());
      builder.addModule(module);
    };
  }

  static class LocalDateTimeJstSerializer extends ValueSerializer<LocalDateTime> {

    @Override
    public Class<LocalDateTime> handledType() {
      return LocalDateTime.class;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException {
      gen.writeString(value.atOffset(JST).format(FORMATTER));
    }
  }

  static class LocalDateTimeJstDeserializer extends ValueDeserializer<LocalDateTime> {

    @Override
    public Class<?> handledType() {
      return LocalDateTime.class;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException {
      return OffsetDateTime.parse(p.getString(), FORMATTER).toLocalDateTime();
    }
  }

}
