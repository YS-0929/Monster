package battle.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import battle.domain.Battle;
import battle.domain.BattleCommand;
import battle.domain.BattleCommandType;
import battle.domain.BattleMonster;
import battle.service.BattleService;
import monster.dictionary.domain.Move;
import monster.instance.domain.MonsterInstance;

public class BattleUI {

    private final Scanner scanner = new Scanner(System.in);

    public void start(Battle battle, BattleService service) {

        System.out.println("=== バトル開始！ ===");

        while (true) {

            BattleMonster player = battle.getPlayerActive();
            BattleMonster enemy  = battle.getEnemyActive();

            // HP 表示
            System.out.println();
            System.out.println("あなたのモンスター: " + player.getInstance().getNickname()
                    + " (HP: " + player.getCurrentHp() + ")");
            System.out.println("相手のモンスター: " + enemy.getInstance().getNickname()
                    + " (HP: " + enemy.getCurrentHp() + ")");
            System.out.println();

            // プレイヤーの行動選択
            BattleCommand playerCmd = selectPlayerCommand(player, battle);

            // 敵の行動選択（AI）
            BattleCommand enemyCmd = selectEnemyCommand(battle);

            // 先攻後攻を決める（BattleService に任せる）
            boolean playerFirst = service.decideOrder(battle.getPlayerActive(), battle.getEnemyActive(), playerCmd, enemyCmd);

            // 行動実行（先攻→後攻）
            if (playerFirst) {
                performAction(service, battle, true, playerCmd);
                // 相手がまだ生きていれば敵の行動
                if (!battle.getEnemyActive().isFainted()) {
                    performAction(service, battle, false, enemyCmd);
                }
            } else {
                performAction(service, battle, false, enemyCmd);
                if (!battle.getPlayerActive().isFainted()) {
                    performAction(service, battle, true, playerCmd);
                }
            }

            // プレイヤーのアクティブが倒れたら強制交代（プレイヤーに選ばせる）
            if (battle.getPlayerActive().isFainted()) {
                System.out.println();
                System.out.println(battle.getPlayerActive().getInstance().getNickname() + " は倒れた！");
                System.out.println("あなたのモンスターは倒れた。交代してください。");
                BattleCommand forced = selectSwitch(battle); // 生きているモンスターを選ばせる
                if (forced != null) {
                    // 交代演出
                    BattleMonster prev = battle.getPlayerActive();
                    System.out.println("トレーナー " + battle.getPlayer().getName() + " は " + prev.getInstance().getNickname() + " をひっこめた！");
                    service.executeSwitch(battle, true, forced.getSwitchIndex());
                    System.out.println("トレーナー " + battle.getPlayer().getName() + " は " + battle.getPlayerActive().getInstance().getNickname() + " をくりだした！");
                } else {
                    System.out.println("交代できるモンスターがいない。あなたの負けです。");
                    break;
                }
            }

            // 敵のアクティブが倒れていたら自動交代（AI）
            if (battle.getEnemyActive().isFainted()) {
                System.out.println();
                System.out.println(battle.getEnemyActive().getInstance().getNickname() + " は倒れた！");
                List<Integer> alive = new ArrayList<>();
                for (int i = 0; i < battle.getEnemyParty().size(); i++) {
                    if (!battle.getEnemyParty().get(i).isFainted()) alive.add(i);
                }
                if (alive.isEmpty()) {
                    System.out.println("相手は交代できるモンスターがいない。あなたの勝ちです。");
                    break;
                } else {
                    int idx = alive.get((int)(Math.random() * alive.size()));
                    System.out.println("トレーナー " + battle.getEnemy().getName() + " は " + battle.getEnemyActive().getInstance().getNickname() + " をひっこめた！");
                    service.executeSwitch(battle, false, idx);
                    System.out.println("トレーナー " + battle.getEnemy().getName() + " は " + battle.getEnemyActive().getInstance().getNickname() + " をくりだした！");
                }
            }

            // 勝敗判定
            if (battle.isPlayerAllFainted()) {
                System.out.println();
                System.out.println("あなたのモンスターは全て倒れた…");
                System.out.println("=== あなたの負け… ===");
                break;
            }

            if (battle.isEnemyAllFainted()) {
                System.out.println();
                System.out.println("相手のモンスターは全て倒れた！");
                System.out.println("=== あなたの勝ち！ ===");
                break;
            }
        }
    }

    /**
     * 行動を実行して演出を表示するユーティリティ
     * - MOVE: executeMove を呼び、戻り値（与えたダメージ）を受け取って表示
     * - SWITCH: executeSwitch を呼んで演出表示（実際の交代は service に任せる）
     */
    private void performAction(BattleService service, Battle battle, boolean isPlayer, BattleCommand cmd) {
        if (cmd == null || cmd.getType() == null) {
            // 無効コマンドはスキップ
            return;
        }

        if (cmd.getType() == BattleCommandType.MOVE) {
            BattleMonster attacker = isPlayer ? battle.getPlayerActive() : battle.getEnemyActive();
            BattleMonster defender = isPlayer ? battle.getEnemyActive() : battle.getPlayerActive();
            MonsterInstance atkInst = attacker.getInstance();
            Move mv = atkInst.getMoves().get(cmd.getMoveIndex());

            // 攻撃アナウンス
            System.out.println(atkInst.getNickname() + " の " + mv.getName() + "！！");

            // ダメージ実行（service.executeMove は与えたダメージを返す想定）
            int damage = service.executeMove(attacker, defender, mv);

            // 効果表示
            if (damage > 0) {
                System.out.println(defender.getInstance().getNickname() + " に " + damage + " のダメージ！");
            } else {
                System.out.println("しかし、ダメージを与えられなかった！");
            }

            // 相手が倒れたか
            if (defender.isFainted()) {
                System.out.println(defender.getInstance().getNickname() + " は倒れた！");
            }

            // 少し間を置く（演出）
            sleepShort();
        } else if (cmd.getType() == BattleCommandType.SWITCH) {
            // 交代コマンド（通常は UI 側で演出するが、ここでも簡単に表示）
            int idx = cmd.getSwitchIndex();
            List<BattleMonster> party = isPlayer ? battle.getPlayerParty() : battle.getEnemyParty();
            if (idx < 0 || idx >= party.size()) {
                System.out.println("しかし交代に失敗した。");
                return;
            }
            BattleMonster next = party.get(idx);
            if (next.isFainted()) {
                System.out.println(next.getInstance().getNickname() + " は倒れているため出せない！");
                return;
            }
            String trainerName = isPlayer ? battle.getPlayer().getName() : battle.getEnemy().getName();
            // ひっこめる演出は呼び出し元で行うことが多いのでここでは出すだけ
            System.out.println("トレーナー " + trainerName + " は " + next.getInstance().getNickname() + " をくりだした！");
            service.executeSwitch(battle, isPlayer, idx);
            sleepShort();
        } else {
            // その他（アイテム等）を将来扱う場合のフォールバック
            System.out.println("その行動はまだ実装されていません。");
        }
    }

    private BattleCommand selectPlayerCommand(BattleMonster player, Battle battle) {

        while (true) {
            System.out.println();
            System.out.println("行動を選択してください：");
            System.out.println("1. 技を使う");
            System.out.println("2. モンスターを交代する");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    return selectMove(player);
                }
                case "2" -> {
                    return selectSwitch(battle);
                }
                default -> System.out.println("無効な入力です。");
            }
        }
    }

    private BattleCommand selectMove(BattleMonster player) {

        MonsterInstance inst = player.getInstance();

        System.out.println("技を選択してください：");

        for (int i = 0; i < inst.getMoves().size(); i++) {
            System.out.println((i + 1) + ". " + inst.getMoves().get(i).getName());
        }

        while (true) {
            String input = scanner.nextLine();
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < inst.getMoves().size()) {
                    return BattleCommand.move(idx);
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("無効な入力です。");
        }
    }

    private BattleCommand selectSwitch(Battle battle) {

        var party = battle.getPlayerParty();

        // 表示用に「表示番号 -> 実インデックス」を作る
        List<Integer> selectableIndexes = new ArrayList<>();

        System.out.println("交代するモンスターを選択してください：");

        for (int i = 0; i < party.size(); i++) {
            BattleMonster bm = party.get(i);
            String status = bm.isFainted() ? "（倒れている）" : "(HP: " + bm.getCurrentHp() + ")";
            System.out.println((i + 1) + ". " + bm.getInstance().getNickname() + " " + status);
            if (!bm.isFainted()) {
                selectableIndexes.add(i); // 選択可能な実インデックスを保存
            }
        }

        if (selectableIndexes.isEmpty()) {
            System.out.println("交代可能なモンスターがいません。");
            return null;
        }

        while (true) {
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < party.size()) {
                    if (!party.get(choice).isFainted()) {
                        return BattleCommand.change(choice);
                    } else {
                        System.out.println("そのモンスターは倒れているため選べません。");
                    }
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("無効な入力です。");
        }
    }

    /**
     * 敵の行動選択（簡易AI）
     * - アクティブが倒れているなら交代
     * - ばつぐん技があればそれを使う
     * - それ以外はランダム技
     */
    private BattleCommand selectEnemyCommand(Battle battle) {

        BattleMonster enemyActive = battle.getEnemyActive();
        BattleMonster playerActive = battle.getPlayerActive();

        // 敵アクティブが倒れているなら交代
        if (enemyActive.isFainted()) {
            List<Integer> alive = new ArrayList<>();
            for (int i = 0; i < battle.getEnemyParty().size(); i++) {
                if (!battle.getEnemyParty().get(i).isFainted()) alive.add(i);
            }
            if (alive.isEmpty()) return BattleCommand.move(0);
            int idx = alive.get((int)(Math.random() * alive.size()));
            return BattleCommand.change(idx);
        }

        // 1) ばつぐん技を探す
        List<Move> moves = enemyActive.getInstance().getMoves();
        double bestScore = -1.0;
        int bestMoveIndex = -1;

        for (int i = 0; i < moves.size(); i++) {
            Move mv = moves.get(i);
            double multiplier = BattleService.calcTypeEffectiveness(mv.getType(), playerActive.getInstance().getSpecies().getTypes());
            if (multiplier > 1.0) {
                double score = multiplier * mv.getPower();
                if (score > bestScore) {
                    bestScore = score;
                    bestMoveIndex = i;
                }
            }
        }

        if (bestMoveIndex != -1) {
            return BattleCommand.move(bestMoveIndex);
        }

        // 3) ランダム
        int moveCount = moves.size();
        if (moveCount == 0) return BattleCommand.move(0);
        int randIdx = (int)(Math.random() * moveCount);
        return BattleCommand.move(randIdx);
    }

    // 演出用の短い待ち（例: 300ms）
    private void sleepShort() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}
    }
}