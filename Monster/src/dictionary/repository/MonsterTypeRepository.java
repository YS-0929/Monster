package dictionary.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dictionary.util.FileUtil;

/**
 * モンスターのタイプ情報を CSV で保存・読み込みするリポジトリ。
 * <p>monster_type.csv はモンスターとタイプの多対多の関係を表現する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterTypeRepository {
	
	/** 保存先ファイルのパス */
    private final Path filePath = Path.of(FilePathConfig.MONSTER_TYPE_FILE);

    /**
     * CSVファイルからすべてのモンスター・タイプ紐付けデータを生の文字列配列リストとして読み込む。
     * <p>ヘッダー行（monsterIdから始まる行）および空行は自動的に除外される。</p>
     *
     * @return カンマで分割された文字列配列（行）のリスト（変更不可能）
     */
    public List<String[]> findAllRaw() {
        List<String> lines = FileUtil.readLines(filePath);
        List<String[]> result = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank() || line.startsWith("monsterId")) continue;
            result.add(line.split(","));
        }
        return List.copyOf(result);
    }

    /**
     * 指定された生のモンスター・タイプ紐付けデータ（文字列配列リスト）をCSVファイルに保存する。
     * <p>書き込み時は常に新規のヘッダー行（monsterId,type）が先頭に付与される。</p>
     *
     * @param rows 保存するモンスター・タイプ紐付けデータの文字列配列リスト
     */
    public void saveAllRaw(List<String[]> rows) {
        List<String> lines = new ArrayList<>();
        lines.add("monsterId,type");

        for (String[] row : rows) {
            lines.add(String.join(",", row));
        }

        FileUtil.writeLines(filePath, lines);
    }
}
