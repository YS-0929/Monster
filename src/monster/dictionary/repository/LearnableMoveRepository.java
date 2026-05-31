package monster.dictionary.repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import monster.dictionary.util.FileUtil;

/**
 * モンスターがレベルアップで覚える技を CSV で保存・読み込みするリポジトリ。
 *
 * @author Suzuki
 * @version 1.0
 */
public class LearnableMoveRepository {
	
	/** 保存先ファイルのパス */
    private final Path filePath = Path.of(FilePathConfig.LEARNABLE_MOVE_FILE);

    /**
     * CSVファイルからすべてのレベルアップ習得技データを生の文字列配列リストとして読み込む。
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
     * 指定された生のレベルアップ習得技データ（文字列配列リスト）をCSVファイルに保存する。
     * <p>書き込み時は常に新規のヘッダー行（monsterId,moveName,level）が先頭に付与される。</p>
     *
     * @param rows 保存するレベルアップ習得技データの文字列配列リスト
     */
    public void saveAllRaw(List<String[]> rows) {
        List<String> lines = new ArrayList<>();
        lines.add("monsterId,moveName,level");

        for (String[] row : rows) {
            lines.add(String.join(",", row));
        }

        FileUtil.writeLines(filePath, lines);
    }
}
