package battle.domain;

import java.util.List;

import trainer.domain.Trainer;

public class Battle {
	
	private final Trainer player;
    private final Trainer enemy;
    
    private final List<BattleMonster> playerParty;
    private final List<BattleMonster> enemyParty;
    
    private BattleMonster playerActive;
    private BattleMonster enemyActive;

    private int turn = 1;

    public Battle(Trainer player, Trainer enemy) {
        this.player = player;
        this.enemy = enemy;
        
     // MonsterInstance → BattleMonster に変換
        this.playerParty = player.getParty().stream()
                .map(BattleMonster::new)
                .toList();

        this.enemyParty = enemy.getParty().stream()
                .map(BattleMonster::new)
                .toList();
        

        this.playerActive = playerParty.get(0); // 最初のモンスターをアクティブに
        this.enemyActive = enemyParty.get(0);   // 最初のモンスターをアクティブに
    }
    
    public List<BattleMonster> getPlayerParty() { return playerParty; }
    public List<BattleMonster> getEnemyParty() { return enemyParty; }
    
    public void setPlayerActive(BattleMonster bm) {
        this.playerActive = bm;
    }

    public void setEnemyActive(BattleMonster bm) {
        this.enemyActive = bm;
    }
    
    public Trainer getPlayer() { return player; }
    public Trainer getEnemy() { return enemy; }
    public BattleMonster getPlayerActive() { return playerActive; }
    public BattleMonster getEnemyActive() { return enemyActive; }

    public int getTurn() { return turn; }
    public void nextTurn() { turn++; }
    
    public boolean isPlayerAllFainted() {
        return playerParty.stream().allMatch(BattleMonster::isFainted);
    }
    
    public boolean isEnemyAllFainted() {
		return enemyParty.stream().allMatch(BattleMonster::isFainted);
	}
}
