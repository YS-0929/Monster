package dictionary.util;

import java.util.ArrayList;
import java.util.List;

/**
 * クォート対応の CSV パーサ。
 * Java 標準ライブラリのみで安全に CSV を扱うためのユーティリティ。
 *
 * @author Suzuki
 */
public class CsvUtil {
	
	/**
     * ユーティリティクラスであるため、インスタンス化を禁止する。
     */
    private CsvUtil() {}

    /**
     * 1 行の CSV 文字列を正確にパースする（カンマ・クォート対応）。
     * <p>ダブルクォーテーションで囲まれた範囲のカンマは区切り文字として扱わず、
     * 連続する2つのダブルクォーテーション（""）は1つの文字として復元する。</p>
     *
     * @param line CSV の 1 行（nullは不可）
     * @return 分割されたフィールドの配列
     */
    public static String[] parseLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // "" → " として扱う
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        result.add(sb.toString());
        
        return result.toArray(new String[0]);
    }

    /**
     * CSV のフィールド文字列を安全に書き出せるようエスケープ処理を行う。
     * <p>文字列内にカンマ（,）またはダブルクォーテーション（"）が含まれる場合、
     * 全体をクォートで囲み、内部のクォートを2つの連続するクォート（""）に置換する。</p>
     *
     * @param field エスケープ対象の文字列
     * @return CSV出力に適したエスケープ済みの文字列
     */
    public static String escape(String field) {
        if (field == null) {
            return "";
        }
        
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        
        return field;
    }
}
