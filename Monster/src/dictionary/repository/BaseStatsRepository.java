package dictionary.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dictionary.util.FileUtil;

/**
 * 種族値（BaseStats）を CSV で保存・読み込みするリポジトリ。
 *
 * @author Suzuki
 * @version 1.1
 */
public class BaseStatsRepository {
	
	/** 保存先ファイルのパス */
    private final Path filePath = Path.of(FilePathConfig.BASE_STATS_FILE);

    /**
     * CSVファイルからすべてのベース種族値データを生の文字列配列リストとして読み込む。
     * <p>ヘッダー行（idから始まる行）および空行は自動的に除外される。</p>
     *
     * @return カンマで分割された文字列配列（行）のリスト（変更不可能）
     */
    public List<String[]> findAllRaw() {
        List<String> lines = FileUtil.readLines(filePath);
        List<String[]> result = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank() || line.startsWith("id")) continue;
            result.add(line.split(","));
        }
        return List.copyOf(result);
    }

    /**
     * 指定された生のベース種族値データ（文字列配列リスト）をCSVファイルに保存する。
     * <p>書き込み時は常に新規のヘッダー行（id,hp,attack,defense,spAttack,spDefense,speed）が先頭に付与される。</p>
     *
     * @param rows 保存するベース種族値データの文字列配列リスト
     */
    public void saveAllRaw(List<String[]> rows) {
        List<String> lines = new ArrayList<>();
        lines.add("id,hp,attack,defense,spAttack,spDefense,speed");

        for (String[] row : rows) {
            lines.add(String.join(",", row));
        }

        FileUtil.writeLines(filePath, lines);
    }
}
