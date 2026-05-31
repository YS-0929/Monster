package monster.dictionary.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * ファイル読み書きのユーティリティクラス。
 *
 * @author Suzuki
 * @version 1.0
 */
public class FileUtil {
	
	/**
     * ユーティリティクラスであるため、インスタンス化を禁止する。
     */
    private FileUtil() {}

    /**
     * 指定されたパスのファイルからすべての行を読み込む。
     * <p>ファイルが存在しない場合は空のリストを返す。</p>
     *
     * @param path 読み込み対象のファイルパス
     * @return ファイルの各行を含む文字列のリスト（変更不可能）
     * @throws RuntimeException ファイル読み込み中にI/Oエラーが発生した場合
     */
    public static List<String> readLines(Path path) {
        try {
            if (!Files.exists(path)) return List.of();
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("ファイル読み込み失敗: " + path, e);
        }
    }

    /**
     * 指定されたパスのファイルに文字列のリストを行単位で書き込む。
     * <p>親ディレクトリが存在しない場合は自動的に作成する。</p>
     *
     * @param path  書き込み対象のファイルパス
     * @param lines 書き込む文字列のリスト
     * @throws RuntimeException ファイル書き込み中、またはディレクトリ作成中にI/Oエラーが発生した場合
     */
    public static void writeLines(Path path, List<String> lines) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, lines);
        } catch (IOException e) {
            throw new RuntimeException("ファイル書き込み失敗: " + path, e);
        }
    }
}
