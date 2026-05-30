package dictionary.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dictionary.util.CsvUtil;
import dictionary.util.FileUtil;

/**
 * モンスター基本情報を CSV で保存・読み込みするリポジトリ。
 * * @author Suzuki
 * @version 1.0
 */
public class MonsterRepository {
	
	/** 保存先ファイルのパス */
    private final Path filePath = Path.of(FilePathConfig.MONSTER_FILE);
	
	/**
     * CSVファイルからすべてのモンスターデータを生の文字列配列リストとして読み込む。
     * <p>ヘッダー行（idから始まる行）および空行は自動的に除外される。</p>
     *
     * @return カンマで分割された文字列配列（行）のリスト
     */
    public List<String[]> findAllRaw() {
        List<String> lines = FileUtil.readLines(filePath);
        List<String[]> result = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank() || line.startsWith("id")) continue;
            result.add(CsvUtil.parseLine(line)); // ← 修正
        }
        return result;
    }

    /**
     * 指定された生のモンスターデータ（文字列配列リスト）をCSVファイルに保存する。
     * <p>書き込み時は常に新規のヘッダー行（id,name,description）が先頭に付与される。</p>
     *
     * @param rows 保存するモンスターデータの文字列配列リスト
     */
    public void saveAllRaw(List<String[]> rows) {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,description");

        for (String[] row : rows) {
            lines.add(
                CsvUtil.escape(row[0]) + "," +
                CsvUtil.escape(row[1]) + "," +
                CsvUtil.escape(row[2])   // ← カンマ対応
            );
        }

        FileUtil.writeLines(filePath, lines);
    }
}
