package battle.domain;

public class BattleCommand {
    public final BattleCommandType type;
    public final int moveIndex;   // MOVE のとき
    public final int switchIndex; // SWITCH のとき

    private BattleCommand(BattleCommandType type, int moveIndex, int switchIndex) {
        this.type = type;
        this.moveIndex = moveIndex;
        this.switchIndex = switchIndex;
    }
    
    public BattleCommandType getType() { return type; }
    public int getMoveIndex() { return moveIndex; }
    public int getSwitchIndex() { return switchIndex; }

    public static BattleCommand move(int moveIndex) {
        return new BattleCommand(BattleCommandType.MOVE, moveIndex, -1);
    }

    public static BattleCommand change(int switchIndex) {
        return new BattleCommand(BattleCommandType.SWITCH, -1, switchIndex);
    }
    
    public int getPriority(BattleMonster bm) {
        if (type == null) return 0;

        switch (type) {
            case MOVE -> {
                // 安全チェックを追加
                var moves = bm.getInstance().getMoves();
                if (moveIndex < 0 || moveIndex >= moves.size()) {
                    // 無効なインデックスならデフォルト優先度 0 を返すかログ出力
                    System.out.println("Warning: invalid moveIndex=" + moveIndex + " for " + bm.getInstance().getNickname());
                    return 0;
                }
                return moves.get(moveIndex).getPriority();
            }
            case SWITCH -> {
                return 6; // 交代は優先度 +6
            }
            default -> {
                return 0;
            }
        }
    }
}
