import java.util.LinkedList;

public class AuraBoard implements IBoard {

    private BoardState myBoardState = new BoardState();
    private LinkedList<Integer> boardMinionsUnaffectedHealth = new LinkedList<>();

    private LinkedList<Aura> auras = new LinkedList<>();


    @Override
    public void playMinion(Minion minion, int pos, int uselessTargetedPos) {
        boardMinionsUnaffectedHealth.add(pos, minion.getHealth());
        boolean alreadyPlayed = false;

        for (Aura a : auras) {
            if (a instanceof DWAAura) ((DWAAura) a).applyAuraTo(minion, pos);
        }
        if(minion.getName().equals("Dire Wolf Alpha")) {
            myBoardState.playMinion(minion, pos, uselessTargetedPos);
            alreadyPlayed = true;
            Aura dwaAura = new DWAAura(this, minion, 1);
            if (minion.isGolden()) {
                dwaAura.setAttackBuff(2);
            }
            auras.add(dwaAura);

            for (Minion m : getBoardMinions()) dwaAura.applyAuraTo(m);
        }


        if (minion.getTribe() == Tribe.MURLOC || minion.getTribe() == Tribe.ALL) {
            for (Aura a : auras) {
                if (a.getAuraGiver().getName().equals("Murloc Warleader")) a.applyAuraTo(minion);
            }
        }
        if(minion.getName().equals("Murloc Warleader")) {
            Aura mwlAura = new Aura(this, minion, 2, 0);
            if (minion.isGolden()) {
                mwlAura.setAttackBuff(4);
            }
            auras.add(mwlAura);

            for (Minion m : getBoardMinions()) {
                if (m.getTribe() == Tribe.MURLOC || m.getTribe() == Tribe.ALL) mwlAura.applyAuraTo(m);
            }
        }


        if (minion.getTribe() == Tribe.DEMON || minion.getTribe() == Tribe.ALL) {
            for (Aura a : auras) {
                if (a.getAuraGiver().getName().equals("Mal'Ganis")) a.applyAuraTo(minion);
            }
        }
        if(minion.getName().equals("Mal'Ganis")) {
            Aura mgAura = new Aura(this, minion, 2, 2);
            if (minion.isGolden()) {
                mgAura.setAttackBuff(4);
                mgAura.setHealthBuff(4);
            }
            auras.add(mgAura);

            for (Minion m : getBoardMinions()) {
                if (m.getTribe() == Tribe.DEMON || m.getTribe() == Tribe.ALL) mgAura.applyAuraTo(m);
            }
        }


        if (minion.getTribe() == Tribe.DEMON || minion.getTribe() == Tribe.ALL) {
            for (Aura a : auras) {
                if (a.getAuraGiver().getName().equals("Siegebreaker")) a.applyAuraTo(minion);
            }
        }
        if(minion.getName().equals("Siegebreaker")) {
            Aura sbAura = new Aura(this, minion, 1, 0);
            if (minion.isGolden()) {
                sbAura.setAttackBuff(2);
            }
            auras.add(sbAura);

            for (Minion m : getBoardMinions()) {
                if (m.getTribe() == Tribe.DEMON || m.getTribe() == Tribe.ALL) sbAura.applyAuraTo(m);
            }
        }



        if (!alreadyPlayed) myBoardState.playMinion(minion, pos, uselessTargetedPos);
    }


    @Override
    public void moveMinion(int fromPos, int toPos) {
        //rerout for correct aura handling
        Minion save = myBoardState.getBoardMinion(fromPos);
        removeMinion(fromPos);
        playMinion(save, toPos, -1);    }


    @Override
    public void removeMinion(int pos) {
        Minion m = getBoardMinion(pos);

        //wurde von aura affected? TODO proglem mit fiendish und rat
        removeAllAurasFrom(m);

        //war aura giver?
        Aura minionsAura = null;
        for (Aura a : auras) {
            if (a.getAuraGiver() == m) minionsAura = a;
        }
        //wenn aura giver -> clear die aura und entfern sie
        if (minionsAura != null) {
            minionsAura.removeAuraFromAllAffectedMinions();
            auras.remove(minionsAura);
        }

        boardMinionsUnaffectedHealth.remove(pos);
        myBoardState.removeMinion(pos);
    }


    public void addHealthTo(Minion minion, int howMuch) {
        minion.addHealth(howMuch);
        int index = getBoardMinions().indexOf(minion);
        if (index != -1) {
            boardMinionsUnaffectedHealth.set(index, boardMinionsUnaffectedHealth.get(index) + howMuch);
        }
    }

    public void removeAllAurasFrom(Minion m) {
        for (Aura a : auras) {
            if (a.affects(m)) a.removeAuraFrom(m);
        }
    }

    @Override
    public LinkedList<Minion> getBoardMinions() {
        return myBoardState.getBoardMinions();
    }

    @Override
    public int getBoardSize() {
        return myBoardState.getBoardSize();
    }

    @Override
    public Minion getBoardMinion(int pos) {
        return myBoardState.getBoardMinion(pos);
    }

    @Override
    public LinkedList<Minion> contains(String minionName) {
        return myBoardState.contains(minionName);
    }

    public AuraBoard getDeepCopyOfThis() {
        AuraBoard a = new AuraBoard();
        for (int i = 0; i < myBoardState.getBoardSize(); i++) {
            Minion saveMinion = getBoardMinion(i);
            removeMinion(i);
            //removeAllAurasFrom(saveMinion);
            Minion copy = new Minion(saveMinion);
            playMinion(saveMinion, i, -1);

            a.playMinion(copy, i, -1);
        }
        return a;
    }

    public int getUnaffectedHealth(Minion minion) {
        if (getBoardMinions().indexOf(minion) != -1) return boardMinionsUnaffectedHealth.get(getBoardMinions().indexOf(minion));
        return 0;       //necessary for Dire Wolf Alpha's Dummies
    }

    public BoardState getMyBoardState() {
        return myBoardState;
    }

    public void setMyBoardState(BoardState myBoardState) {
        this.myBoardState = myBoardState;
    }

    public LinkedList<Aura> getAuras() {
        return auras;
    }

    public void setAuras(LinkedList<Aura> auras) {
        this.auras = auras;
    }
}
