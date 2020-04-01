public class DWAAura extends Aura {

    public DWAAura(AuraBoard auraBoard, Minion auraGiver, int attackBuff) {
        super(auraBoard, auraGiver, attackBuff, 0);
        affectedMinions.add(new Minion());
        affectedMinions.add(new Minion());
    }



    public void applyAuraTo(Minion minion, int pos) {
        if (pos == myAuraBoard.getBoardMinions().indexOf(auraGiver)) {
            affectedMinions.get(0).addAttack((-1) * attackBuff);
            affectedMinions.remove(0);
            affectedMinions.add(0, minion);
            minion.addAttack(attackBuff);
        } else if (pos == myAuraBoard.getBoardMinions().indexOf(auraGiver) + 1) {
            affectedMinions.get(1).addAttack((-1) * attackBuff);
            affectedMinions.remove(1);
            affectedMinions.add(1, minion);
            minion.addAttack(attackBuff);
        }
    }

    @Override
    public void applyAuraTo(Minion minion) {
        if (myAuraBoard.getBoardMinions().indexOf(minion) == myAuraBoard.getBoardMinions().indexOf(auraGiver) - 1) {
            affectedMinions.get(0).addAttack((-1) * attackBuff);
            affectedMinions.remove(0);
            affectedMinions.add(0, minion);
            minion.addAttack(attackBuff);
        } else if (myAuraBoard.getBoardMinions().indexOf(minion) == myAuraBoard.getBoardMinions().indexOf(auraGiver) + 1) {
            affectedMinions.get(1).addAttack((-1) * attackBuff);
            affectedMinions.remove(1);
            affectedMinions.add(1, minion);
            minion.addAttack(attackBuff);
        }
    }

    @Override
    public void removeAuraFrom(Minion minion) {
        minion.addAttack((-1) * attackBuff);
        affectedMinions.remove(minion);

        if (myAuraBoard.getBoardMinions().indexOf(minion) == myAuraBoard.getBoardMinions().indexOf(auraGiver) - 1) {
            if (myAuraBoard.getBoardMinions().indexOf(auraGiver) - 2 >= 0) {
                Minion newOne = myAuraBoard.getBoardMinion(myAuraBoard.getBoardMinions().indexOf(auraGiver) - 2);
                newOne.addAttack(attackBuff);
                affectedMinions.add(0, newOne);
            } else {
                affectedMinions.add(0, new Minion());
            }
        } else if (myAuraBoard.getBoardMinions().indexOf(minion) == myAuraBoard.getBoardMinions().indexOf(auraGiver) + 1) {
            if (myAuraBoard.getBoardMinions().indexOf(auraGiver) + 2 <= myAuraBoard.getBoardSize()) {
                Minion newOne = myAuraBoard.getBoardMinion(myAuraBoard.getBoardMinions().indexOf(auraGiver) + 2);
                newOne.addAttack(attackBuff);
                affectedMinions.add(1, newOne);
            } else {
                affectedMinions.add(1, new Minion());
            }
        }
    }

}
