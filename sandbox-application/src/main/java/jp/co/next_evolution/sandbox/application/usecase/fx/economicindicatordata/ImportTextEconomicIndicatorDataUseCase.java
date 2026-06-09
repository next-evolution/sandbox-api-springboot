package jp.co.next_evolution.sandbox.application.usecase.fx.economicindicatordata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jp.co.next_evolution.sandbox.application.config.SandboxStorageProperties;
import jp.co.next_evolution.sandbox.application.dto.fx.FileImportResult;
import jp.co.next_evolution.sandbox.domain.exception.SandboxApiException;
import jp.co.next_evolution.sandbox.domain.model.fx.Country;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicator;
import jp.co.next_evolution.sandbox.domain.model.fx.EconomicIndicatorData;
import jp.co.next_evolution.sandbox.domain.repository.fx.CountryRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorDataRepository;
import jp.co.next_evolution.sandbox.domain.repository.fx.EconomicIndicatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportTextEconomicIndicatorDataUseCase {

  private final EconomicIndicatorDataParser parser;

  private final SandboxStorageProperties sandboxStorageProperties;

  private final EconomicIndicatorDataRepository economicIndicatorDataRepository;

  private final EconomicIndicatorRepository economicIndicatorRepository;

  private final CountryRepository countryRepository;

  @Transactional
  public List<FileImportResult> execute(List<FileEntry> files, String userSub) {

    HashMap<String, String> countryMap = buildCountryMap();
    HashMap<String, HashMap<String, EconomicIndicator>> indicatorMap =
        buildIndicatorMap(countryMap);

    List<FileImportResult> resultList = new ArrayList<>();

    for (FileEntry entry : files) {
      resultList.add(processFile(entry, countryMap, indicatorMap, userSub));
    }

    return resultList;

  }

  private FileImportResult processFile(FileEntry entry,
      HashMap<String, String> countryMap,
      HashMap<String, HashMap<String, EconomicIndicator>> indicatorMap,
      String userSub) {

    Path savedPath = saveFile(entry.fileName(), entry.inputStream(), userSub);

    List<EconomicIndicatorData> dataList;
    try {
      dataList = parser.parseFile(savedPath, entry.fileName(), countryMap, indicatorMap);
    } catch (Exception e) {
      throw new SandboxApiException(e.getMessage());
    }

    int deleteCount = economicIndicatorDataRepository.deleteLoad();

    for (EconomicIndicatorData data : dataList) {
      economicIndicatorDataRepository.insertLoad(data);
    }

    int insertFromLoad = 0;
    int diffCount = 0;

    List<EconomicIndicatorData> diffList = economicIndicatorDataRepository.loadDiff();
    if (CollectionUtils.isEmpty(diffList)) {
      insertFromLoad = economicIndicatorDataRepository.insertFromLoad();
    } else {
      diffCount = diffList.size();
      log.warn("---------- load diff ----------");
      for (EconomicIndicatorData d : diffList) {
        log.warn("{}", d);
      }
    }

    log.info("delete={} | insert={}/{} | insertFromLoad={} | diffCount={}",
        deleteCount, dataList.size(), dataList.size(), insertFromLoad, diffCount);

    return FileImportResult.builder()
        .fileName(entry.fileName())
        .fileSize(entry.fileSize())
        .readCount(dataList.size())
        .resultStatus("OK")
        .build();

  }

  private Path saveFile(String fileName, InputStream inputStream, String userSub) {

    try {
      String bucket = sandboxStorageProperties.getBucket();
      String fx = sandboxStorageProperties.getFx();
      Path uploadDir = Paths.get(bucket, fx, "EconomicIndicatorDataService", userSub);
      Files.createDirectories(uploadDir);
      Path savedFile = uploadDir.resolve(fileName);
      Files.copy(inputStream, savedFile, StandardCopyOption.REPLACE_EXISTING);
      return savedFile;
    } catch (IOException e) {
      throw new SandboxApiException("ファイル保存に失敗しました: " + fileName, e);
    }

  }

  private HashMap<String, String> buildCountryMap() {
    HashMap<String, String> map = new HashMap<>();
    for (Country country : countryRepository.countryList()) {
      map.put(country.getName(), country.getCode());
    }
    return map;
  }

  private HashMap<String, HashMap<String, EconomicIndicator>> buildIndicatorMap(
      HashMap<String, String> countryMap) {
    HashMap<String, HashMap<String, EconomicIndicator>> result = new HashMap<>();
    for (String countryName : countryMap.keySet()) {
      String countryCode = countryMap.get(countryName);
      HashMap<String, EconomicIndicator> indicatorByName = new HashMap<>();
      for (EconomicIndicator indicator : economicIndicatorRepository.getEconomicIndicatorList(
          countryCode)) {
        indicatorByName.put(indicator.getName(), indicator);
      }
      result.put(countryName, indicatorByName);
    }
    return result;
  }

  public record FileEntry(String fileName, InputStream inputStream, long fileSize) {

  }

}