package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import jp.co.next_evolution.sandbox.application.config.SandboxAppProperties;
import jp.co.next_evolution.sandbox.domain.exception.SandboxApiException;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EconomicIndicatorDataParser {

  private static final DateTimeFormatter DTF_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DTF_PUBLICATION =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private static final Pattern PTN_DATE = Pattern.compile("^[0-9]{1,2}/[0-9]{1,2}\\(");
  private static final Pattern PTN_TIME = Pattern.compile("^[0-9]{2}:[0-9]{2}");

  private final SandboxAppProperties sandboxAppProperties;

  public List<EconomicIndicatorData> parseFile(Path path, String fileName,
      HashMap<String, String> countryMap,
      HashMap<String, HashMap<String, EconomicIndicator>> indicatorMap) throws Exception {

    String importance = getImportance(fileName);
    int year = getYear(fileName);
    String baseDate = "";
    int errorCount = 0;
    List<EconomicIndicatorData> resultList = new ArrayList<>();

    for (String line : readLines(path)) {
      line = applyStrip(line);
      if (PTN_DATE.matcher(line).find()) {
        baseDate = toDate(year, line);
      } else {
        try {
          EconomicIndicatorData data =
              parseDataLine(baseDate, importance, line, countryMap, indicatorMap);
          if (data != null) {
            resultList.add(data);
          }
        } catch (SandboxApiException e) {
          errorCount++;
        } catch (Exception e) {
          log.error(line);
          log.error(e.getMessage());
        }
      }
    }

    if (errorCount > 0) {
      throw new Exception("parseFile errorCount=" + errorCount);
    }

    return resultList;

  }

  private EconomicIndicatorData parseDataLine(String baseDate, String importance, String line,
      HashMap<String, String> countryMap,
      HashMap<String, HashMap<String, EconomicIndicator>> indicatorMap) throws Exception {

    if (isSkip(line)) {
      return null;
    }

    if (!PTN_TIME.matcher(line).find()) {
      if (line.contains("日本")) {
        line = "12:00\t" + line;
      } else if (line.contains("中国")) {
        line = "10:00\t" + line;
      } else if (line.contains("インド")) {
        line = "21:00\t" + line;
      }
    }

    if (!PTN_TIME.matcher(line).find()) {
      return null;
    }

    String[] elem = line.split("\t", -1);

    if (!countryMap.containsKey(elem[1])) {
      log.error("country not found: {}", line);
      throw new Exception("country [" + elem[1] + "] not found.");
    }

    String subTitle = getSubTitle(elem[2]);
    String name = normalizeIndicatorName(
        StringUtils.hasText(subTitle) ? elem[2].replace(subTitle, "") : elem[2]);

    HashMap<String, EconomicIndicator> countryIndicators = indicatorMap.get(elem[1]);
    if (countryIndicators == null || !countryIndicators.containsKey(name)) {
      log.error("economic-indicator not found: {}", line);
      throw new Exception("economic-indicator [" + name + "] not found.");
    }

    EconomicIndicator indicator = countryIndicators.get(name);

    if (!importance.equals(indicator.getImportance())) {
      log.warn("diff importance file={}|db={} -> baseDate={}: {}",
          importance, indicator.getImportance(), baseDate, line);
    }

    String unitOfValue = extractUnitOfValue(elem[6]);

    return EconomicIndicatorData.builder()
        .code(indicator.getCode())
        .countryCode(indicator.getCountryCode())
        .publication(toPublication(baseDate, elem[0]))
        .subTitle(subTitle)
        .previousValue(removeUnitOfValue(elem[4], unitOfValue))
        .forecastValue(removeUnitOfValue(elem[5], unitOfValue))
        .resultValue(removeUnitOfValue(
            StringUtils.hasText(elem[6]) ? elem[6] : "-", unitOfValue))
        .build();

  }

  private String applyStrip(String line) {
    for (String keyword : sandboxAppProperties.getIndicatorStripList()) {
      line = line.replace(keyword, "");
    }
    return line;
  }

  private boolean isSkip(String line) {
    for (String keyword : sandboxAppProperties.getIndicatorExcludeList()) {
      if (line.contains(keyword)) {
        return true;
      }
    }
    return false;
  }

  private String getImportance(String fileName) {
    return fileName.split("_")[2].split("\\.")[0];
  }

  private int getYear(String fileName) {
    return Integer.parseInt(fileName.split("_")[0]);
  }

  private String toDate(int year, String dateStr) {
    String[] parts = dateStr.split("\\(")[0].split("/");
    return LocalDate.of(year, Integer.parseInt(parts[0]),
        Integer.parseInt(parts[1])).format(DTF_YMD);
  }

  private LocalDateTime toPublication(String baseDate, String timeStr) {
    int hour = Integer.parseInt(timeStr.split(":")[0]);
    int minute = Integer.parseInt(timeStr.split(":")[1]);
    if (hour > 23) {
      return LocalDateTime.parse(
          String.format("%s %02d:%02d", baseDate, hour - 24, minute), DTF_PUBLICATION)
          .plusDays(1);
    }
    return LocalDateTime.parse(
        String.format("%s %02d:%02d", baseDate, hour, minute), DTF_PUBLICATION);
  }

  private String normalizeIndicatorName(String name) {
    return Normalizer.normalize(
        name.replaceAll("、", "").replaceAll("､", "")
            .replaceAll("・", "").replaceAll("　", "")
            .replaceAll(" ", ""),
        Normalizer.Form.NFKC);
  }

  private String extractUnitOfValue(String value) {
    return StringUtils.hasText(value)
        ? value.replaceAll("-", "").replaceAll("\\+", "")
               .replaceAll("[0-9]", "").replaceAll("\\.", "")
               .replaceAll(" ", "").replaceAll("\\(", "")
               .replaceAll("\\)", "")
        : "";
  }

  private String removeUnitOfValue(String value, String unit) {
    if (!StringUtils.hasText(unit)) {
      return value;
    }
    if (!StringUtils.hasText(value)) {
      return null;
    }
    return Normalizer.normalize(
        value.replaceAll(unit, "").replaceAll("％", "")
             .replaceAll("億円", "").replaceAll("億元", ""),
        Normalizer.Form.NFKC);
  }

  private String getSubTitle(String indicatorName) {
    Pattern ptnPeriod = Pattern.compile("^[0-9]{1,2}-[0-9]{1,2}月期");
    String elem = ptnPeriod.matcher(indicatorName).find()
        ? indicatorName.replaceAll("([0-9]{1,2}-[0-9]{1,2}月期)", "$1\t")
        : indicatorName.replaceAll("([0-9]{1,2}月)", "$1\t");
    return elem.split("\t").length == 1 ? null : elem.split("\t")[0];
  }

  private List<String> readLines(Path path) throws Exception {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(Files.newInputStream(path)))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("(")) {
          lines.set(lines.size() - 1, lines.getLast() + line);
        } else {
          lines.add(line);
        }
      }
    } catch (IOException e) {
      log.error("{}: readLines error.", path.getFileName());
      throw new Exception(e.getMessage());
    }
    return lines;
  }

}