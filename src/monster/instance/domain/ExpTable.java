package monster.instance.domain;

/**
 * 経験値テーブルを表す値オブジェクト。
 * レベルごとの必要経験値を返す。
 *
 * 現在は「中速（Medium Fast）」成長曲線を採用。
 * 必要経験値 = level^3
 *
 * @author Suzuki
 * @version 1.0
 */
record ExpTable() {
	
	/**
	 * 指定されたレベルに必要な経験値を返す。
	 *
	 * @param level レベル（1以上100以下）
	 * @return 必要経験値
	 * @throws IllegalArgumentException レベルが1未満または100を超える場合
	 */
	public static int requiredExp(int level) {
		if (level < 1 || level > 100) {
			throw new IllegalArgumentException("レベルは1以上100以下でなければなりません。");
		}
		return (int) Math.pow(level, 3);
	}
}
