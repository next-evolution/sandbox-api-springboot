package jp.co.next_evolution.sandbox.api.dto.response;

import java.util.List;
import jp.co.next_evolution.sandbox.domain.model.KeyValue;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeyValueResponse {

  private List<KeyValue> list;

}
