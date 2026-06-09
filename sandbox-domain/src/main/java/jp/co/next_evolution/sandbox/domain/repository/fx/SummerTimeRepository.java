package jp.co.next_evolution.sandbox.domain.repository.fx;

import java.util.List;
import java.util.Optional;
import jp.co.next_evolution.sandbox.domain.model.fx.SummerTime;

public interface SummerTimeRepository {

  int count();

  List<SummerTime> search(int page, int size);

  Optional<SummerTime> get(short targetYear);

  boolean exists(short targetYear);

  int add(SummerTime summerTime);

  int update(SummerTime summerTime);

  int update(SummerTime summerTime, int targetYear);

}
