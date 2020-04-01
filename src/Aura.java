import java.util.LinkedList;

public class Aura {

    protected AuraBoard myAuraBoard;
    protected Minion auraGiver;
    protected int attackBuff;
    protected int healthBuff;
    protected LinkedList<Minion> affectedMinions = new LinkedList<>();


    public Aura(AuraBoard auraBoard, Minion auraGiver, int attackBuff, int healthBuff) {
        this.myAuraBoard = auraBoard;
        this.auraGiver = auraGiver;
        this.attackBuff = attackBuff;
        this.healthBuff = healthBuff;
    }


    public void applyAuraTo(Minion minion) {
        if (affects(minion)) return;
        affectedMinions.add(minion);
        minion.addAttack(attackBuff);
        minion.addHealth(healthBuff);
    }

    public void removeAuraFrom(Minion minion) {
        minion.addAttack((-1) * attackBuff);

        int itsBaseHealth = myAuraBoard.getUnaffectedHealth(minion);
        if (minion.getHealth() > itsBaseHealth) {
            if (minion.getHealth() - healthBuff > itsBaseHealth) {
                minion.addHealth((-1) * healthBuff);
            } else {
                minion.setHealth(itsBaseHealth);
            }
        }

        affectedMinions.remove(minion);
    }

    public void removeAuraFromAllAffectedMinions() {
        for (Minion minion : affectedMinions) {
            minion.addAttack((-1) * attackBuff);

            int itsBaseHealth = myAuraBoard.getUnaffectedHealth(minion);
            if (minion.getHealth() > itsBaseHealth) {
                if (minion.getHealth() - healthBuff > itsBaseHealth) {
                    minion.addHealth((-1) * healthBuff);
                } else {
                    minion.setHealth(itsBaseHealth);
                }
            }
        }
        affectedMinions.clear();
    }

    public boolean affects(Minion minion) {
        if (affectedMinions.contains(minion)) return true;
        return false;
    }


    public LinkedList<Minion> getAffectedMinions() {
        return affectedMinions;
    }

    public Minion getAuraGiver() {
        return auraGiver;
    }

    public int getAttackBuff() {
        return attackBuff;
    }

    public void setAttackBuff(int attackBuff) {
        this.attackBuff = attackBuff;
    }

    public int getHealthBuff() {
        return healthBuff;
    }

    public void setHealthBuff(int healthBuff) {
        this.healthBuff = healthBuff;
    }
}
