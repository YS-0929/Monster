package battle.service;

import java.util.List;

import battle.domain.Battle;
import battle.domain.BattleCommand;
import battle.domain.BattleMonster;
import monster.dictionary.domain.Move;
import monster.dictionary.domain.MoveCategory;
import monster.dictionary.domain.StatChange;
import monster.dictionary.domain.StatType;
import monster.dictionary.domain.type.Effectiveness;
import monster.dictionary.domain.type.Type;
import monster.instance.domain.Stats;

public class BattleService {

    /**
     * 1ターン分の処理を行う
     */
    public void executeTurn(
            Battle battle,
            BattleCommand playerCmd,
            BattleCommand enemyCmd
    ) {
        BattleMonster p = battle.getPlayerActive();
        BattleMonster e = battle.getEnemyActive();
        
        // ===== 行動順決定 =====
        boolean playerFirst = decideOrder(p, e, playerCmd, enemyCmd);

        if (playerFirst) {
            act(battle, true, playerCmd);
            if (!battle.getEnemyActive().isFainted()) {
                act(battle, false, enemyCmd);
            }
        } else {
            act(battle, false, enemyCmd);
            if (!battle.getPlayerActive().isFainted()) {
                act(battle, true, playerCmd);
            }
        }

        battle.nextTurn();

    }
    

    /**
     * 素早さで行動順を決める
     */
    public boolean decideOrder(BattleMonster p, BattleMonster e,
            BattleCommand pCmd, BattleCommand eCmd) {

    	int pPrio = pCmd.getPriority(p);
    	int ePrio = eCmd.getPriority(e);

    	// 1. 優先度比較
    	if (pPrio != ePrio) {
    		return pPrio > ePrio;
    	}

    	// 2. 素早さ比較（ランク補正込み）
    	double pFinal = p.getInstance().getActualStats().getSpeed()
    			* stageMultiplier(p.getSpeStage());

    	double eFinal = e.getInstance().getActualStats().getSpeed()
    			* stageMultiplier(e.getSpeStage());

    	if (pFinal != eFinal) {
    		return pFinal > eFinal;
    	}

    	// 3. 同速ならプレイヤー先行
    	return true;
    }
    
    /**
     * 行動を実行する
     */
    private void act(Battle battle, boolean isPlayer, BattleCommand cmd) {

        BattleMonster self   = isPlayer ? battle.getPlayerActive() : battle.getEnemyActive();
        BattleMonster target = isPlayer ? battle.getEnemyActive() : battle.getPlayerActive();

        switch (cmd.getType()) {

            case MOVE -> {
                Move move = self.getInstance().getMoves().get(cmd.getMoveIndex());
                executeMove(self, target, move);
            }

            case SWITCH -> {
                executeSwitch(battle, isPlayer, cmd.getSwitchIndex());
            }
        }
    }

    /**
     * 技の実行
     */
    public int executeMove(BattleMonster self, BattleMonster target, Move move) {

        // 命中判定
        if (!checkHit(self, target, move)) {
            applyStatChanges(self, target, move); // 外れてもランク変化は発生
            return 0;
        }

        // ダメージ計算
        int damage = calculateDamage(self, target, move);
        target.damage(damage);

        // ランク変化
        applyStatChanges(self, target, move);
        return damage;
    }

    /**
     * 交代処理
     */
    public void executeSwitch(Battle battle, boolean isPlayer, int switchIndex) {

        if (isPlayer) {
            var inst = battle.getPlayerParty().get(switchIndex);
            battle.setPlayerActive(inst);
        } else {
            var inst = battle.getEnemyParty().get(switchIndex);
            battle.setEnemyActive(inst);
        }
    }


    /**
     * ダメージ計算
     */
    private int calculateDamage(BattleMonster attacker, BattleMonster defender, Move move) {
    	
    	 MoveCategory category = move.getCategory();

    	    // 変化技はダメージ 0
    	 if (category == MoveCategory.STATUS) {
    		 return 0;
    	 }
    	
    	int level = attacker.getInstance().getLevel();
        Stats atkStats = attacker.getInstance().getActualStats();
        Stats defStats = defender.getInstance().getActualStats();

        int power = move.getPower();
        
     // ===== 物理 or 特殊 =====
        int attack;
        int defense;

        if (category == MoveCategory.PHYSICAL) {
            attack = (int) (atkStats.getAttack()  * stageMultiplier(attacker.getAtkStage()));
            defense = (int) (defStats.getDefense() * stageMultiplier(defender.getDefStage()));
        } else { // SPECIAL
            attack = (int) (atkStats.getSpAttack()  * stageMultiplier(attacker.getSpAStage()));
            defense = (int) (defStats.getSpDefense() * stageMultiplier(defender.getSpDStage()));
        }

        // ===== 1. 基本ダメージ =====
        double baseDamage =
                (((2.0 * level / 5 + 2) * power * attack / defense) / 50) + 2;

        // ===== 2. STAB =====
        boolean stab = attacker.getInstance().getSpecies().getTypes().contains(move.getType());
        double stabBonus = stab ? 1.5 : 1.0;

        // ===== 3. タイプ相性（あなたの Effectiveness を使用） =====
        double typeEffect = calcTypeEffectiveness(
                move.getType(),
                defender.getInstance().getSpecies().getTypes()
        );

     // ===== 4. 急所 =====
        double critRate = move.getCriticalRate();  // Move クラスの値を取得

        // criticalRate が 0 の場合はデフォルト 5% を使う
        if (critRate <= 0) {
            critRate = 0.05;
        }

        double critical = Math.random() < critRate ? 1.5 : 1.0;

        // ===== 5. 乱数補正 =====
        double random = 0.85 + (Math.random() * 0.15);

        // ===== 6. 最終ダメージ =====
        double finalDamage = baseDamage * stabBonus * typeEffect * critical * random;

        return Math.max(1, (int) finalDamage);
    }
    
    /**
	 * タイプ相性の計算
	 */
    public static double calcTypeEffectiveness(Type moveType, List<Type> defenderTypes) {

        double result = 1.0;

        for (Type def : defenderTypes) {
        	
        	Effectiveness effective = moveType.getEffectiveness(def); 

            if (effective == Effectiveness.GOOD) {
                result *= 2.0;
            }
            else if (effective == Effectiveness.BAD) {
                result *= 0.5;
            }
            else if (effective == Effectiveness.NONE) {
                result *= 0.0;
            }
            else {
                result *= 1.0;
            }
        }

        return result;
    }
    
    private void applyStatChanges(BattleMonster self, BattleMonster target, Move move) {

        // 自分にかかるランク変化
        for (StatChange sc : move.getStatChanges()) {
            applySingleStatChange(self, sc);
        }

    }
    
    private void applySingleStatChange(BattleMonster bm, StatChange sc) {

        StatType type = sc.getStat();
        int delta = sc.getStages();

        switch (type) {
            case ATTACK -> bm.changeAtkStage(delta);
            case DEFENSE -> bm.changeDefStage(delta);
            case SP_ATTACK -> bm.changeSpAStage(delta);
            case SP_DEFENSE -> bm.changeSpDStage(delta);
            case SPEED -> bm.changeSpeStage(delta);
            case ACCURACY -> bm.changeAccStage(delta);
            case EVASION -> bm.changeEvaStage(delta);
        }
    }
    
    /**
	 * ランク補正の計算
	 */
    private double stageMultiplier(int stage) {
        if (stage >= 0) {
            return (2.0 + stage) / 2.0;
        } else {
            return 2.0 / (2.0 - stage);
        }
    }
    
    private boolean checkHit(BattleMonster attacker, BattleMonster defender, Move move) {

        // 命中率 0 の技は必中扱い（例：つるぎのまい）
        if (move.getAccuracy() <= 0) return true;

        double baseAcc = move.getAccuracy() / 100.0;

        // ランク補正
        double accMul = stageMultiplier(attacker.getAccStage());
        double evaMul = stageMultiplier(defender.getEvaStage());

        double finalAcc = baseAcc * accMul / evaMul;

        // 上限 1.0
        finalAcc = Math.min(1.0, finalAcc);

        return Math.random() < finalAcc;
    }
    
  
}