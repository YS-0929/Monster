package monster.dictionary.service;

import java.util.ArrayList;
import java.util.List;

import monster.dictionary.domain.Move;
import monster.dictionary.domain.StatChange;
import monster.dictionary.repository.MoveRepository;

/**
 * Move ドメインオブジェクトを分解し、CSV ファイルに保存するセーバー。
 * <p>オブジェクトとしてカプセル化されている技の各種ステータスを文字列配列に平坦化する。
 * 特に能力変化リストのようなネストされたデータ構造は、独自の複合文字列にフォーマットした上で永続化を行う。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MoveSaver {
	
	/** 技データのリポジトリ */
    private final MoveRepository moveRepo;

    /**
     * コンストラクタ。
     *
     * @param moveRepo 技データのリポジトリ
     */
    public MoveSaver(MoveRepository moveRepo) {
        this.moveRepo = moveRepo;
    }

    /**
     * 指定されたすべての Move ドメインオブジェクトを平坦化し、CSVファイルへ一括保存する。
     *
     * @param moves 保存対象の Move オブジェクトのリスト
     */
    public void saveAll(List<Move> moves) {
        List<String[]> rows = new ArrayList<>();

        for (Move m : moves) {
            rows.add(new String[]{
                    m.getName(),
                    m.getType().name(),
                    m.getCategory().name(),
                    String.valueOf(m.getPower()),
                    String.valueOf(m.getAccuracy()),
                    String.valueOf(m.getCriticalRate()),
                    String.valueOf(m.getPriority()),
                    formatStatChanges(m.getStatChanges()),
                    m.getStatusEffect().name(),
                    String.valueOf(m.getStatusChance())
            });
        }

        moveRepo.saveAllRaw(rows);
    }

    /**
     * 能力変化（バフ・デバフ）のリストを、CSV保存用の特殊な複合文字列にフォーマットする。
     * <p>各要素を「STAT_NAME:STAGES」の形式に変換し、それらをカンマで連結した1つの文字列を生成する。
     * リストが空の場合は空文字（""）を返す。</p>
     *
     * @param list フォーマット対象の StatChange オブジェクトのリスト
     * @return カンマで連結された能力変化の文字列表現
     */
    private String formatStatChanges(List<StatChange> list) {
        if (list.isEmpty()) return "";
        
        List<String> parts = new ArrayList<>();
        
        for (StatChange sc : list) {
            parts.add(sc.getStat().name() + ":" + sc.getStages());
        }
        
        return String.join(";", parts);
    }
}
