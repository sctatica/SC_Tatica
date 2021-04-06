package com.sang.sc_tatica.Ranks;

import com.sang.sc_tatica.R;

public class Rank {

    private String rank_type;
    private String rank_description;
    private int rank_image;

    public Rank(String rank_type, String rank_description, int rank_image) {
        this.rank_type = rank_type;
        this.rank_description = rank_description;
        this.rank_image = rank_image;
    }

    public static final Rank[] ranksDetails = {
            new Rank("Bronze Rank", "  - Learn about local / local short-term events and classes organized by the founding group.\n" +
                    "  - Choosing to become a financial donor (by accumulating time and converting money from sponsors)", R.drawable.rank_1_image),
            new Rank("Silver Rank", "  - Unlock to join or become a collaborator for short-term events and classes held locally / domestically organized by the founding team.\n" +
                    "  - Choosing to become a financial donor (by accumulating time and converting money from sponsors)\n" +
                    "  - For members living outside of Vietnam, they can actively form groups and organize events and classes on behalf of the founding group under the supervision of the founding group, provided that the legality can be proved and necessary of those events / classes.", R.drawable.rank_2_image),
            new Rank("Gold Rank", "    - Unlocked, learn about domestic and foreign community activities.\n" +
                    "  - Choose to become a financial donor (by accumulating time and redeeming money from donors) or certify senior membership and participate as community activities collaborators of teams partnership outside the living area.\n" +
                    "  - Participating in Online Workshop or short-term sightseeing courses.", R.drawable.rank_3_image)
    };

    public int getRank_image() {
        return rank_image;
    }

    public String getRank_type() {
        return rank_type;
    }

    public String getRank_description() {
        return rank_description;
    }
}
