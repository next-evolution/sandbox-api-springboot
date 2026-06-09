package jp.co.next_evolution.sandbox.application.dto.fx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FileImportResult {

  private String fileName;

  private long fileSize;

  private String resultStatus;

  private int readCount;

  private String message;

}
