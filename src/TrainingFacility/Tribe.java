package TrainingFacility;

public enum Tribe {
    NONE, ALL, BEAST, DEMON, DRAGON, MECH, MURLOC;

    public static Tribe parseTribe(String s) {
        if(s.toLowerCase().equals("none")) return Tribe.NONE;
        else if(s.toLowerCase().equals("all")) return Tribe.ALL;
        else if(s.toLowerCase().equals("beast")) return Tribe.BEAST;
        else if(s.toLowerCase().equals("demon")) return Tribe.DEMON;
        else if(s.toLowerCase().equals("dragon")) return Tribe.DRAGON;
        else if(s.toLowerCase().equals("mech")) return Tribe.MECH;
        else if(s.toLowerCase().equals("murloc")) return Tribe.MURLOC;
        return Tribe.NONE;
    }
}
