package dictionary.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dictionary.util.FileUtil;

/**
 * evolution.csv を読み書きするデータアクセスクラス（リポジトリ）。
 * <p>fromId, toId, conditionType, conditionValue の4列構造を持つCSVファイルと直接対話し、
 * 文字列配列のリスト形式でデータの全件取得や全件上書き保存を行う。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class EvolutionRepository {
	
	/** 読み書き対象となるCSVファイルのパス */
	private final Path filePath = Path.of(FilePathConfig.EVOLUTION_FILE);

    /**
     * CSVファイルを一行ずつ読み込み、カンマで分割した文字列配列のリストとして全件取得する。
     * <p>空白行は自動的にスキップされる。</p>
     *
     * @return CSVの各行をカンマで分割した文字列配列のリスト
     */
    public List<String[]> findAllRaw() {
        List<String[]> rows = new ArrayList<>();

        for (String line : FileUtil.readLines(filePath)) {
        	if (line.isBlank() || line.startsWith("fromId")) continue;
            if (line.trim().isEmpty()) {
                continue;
            }
            rows.add(line.split(","));
        }

        return rows;
    }

    /**
     * 指定された文字列配列のリストを、それぞれカンマ区切りのデータに変換してCSVファイルへ全件上書き保存する。
     *
     * @param rows 保存対象となる文字列配列のリスト
     */
    public void saveAllRaw(List<String[]> rows) {
        List<String> lines = new ArrayList<>();

        for (String[] row : rows) {
            lines.add(String.join(",", row));
        }

        FileUtil.writeLines(filePath, lines);
    }
}
